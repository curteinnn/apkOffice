# Admin Task Management Sequence

**Tujuan:** Menjelaskan baca, tambah, ubah, dan hapus tugas dari menu Kelola Tugas Admin Desktop.

```mermaid
sequenceDiagram
    actor Admin
    participant UI as ListTugas dan PengajuanTugas
    participant API as TaskApiService ApiClient
    participant Filter as JWT filter + SecurityConfig
    participant C as TugasController
    participant S as TugasService
    participant Master as User/JenisTugas services dan repositories
    participant Generator as TugasIdGenerator
    participant Repo as TugasRepository
    participant DB as MariaDB

    Admin->>UI: Buka Kelola Tugas
    UI->>API: GET /api/tugas
    API->>Filter: Bearer JWT
    Filter->>C: Otorisasi ADMIN
    C->>S: getAllTugas(username, ADMIN)
    S->>Repo: findAll()
    Repo->>DB: SELECT tugas dan assignees
    DB-->>Repo: Daftar tugas
    Repo-->>S: Daftar tugas
    S-->>C: Daftar tugas
    C-->>API: JSON tugas
    API-->>UI: Tampilkan tabel

    alt Lihat detail
        Admin->>UI: Pilih atau klik ganda tugas
        UI->>UI: Tampilkan detail dari data yang sudah dimuat
    else Tambah tugas
        Admin->>UI: Buka form dan isi data serta tanggal rencana
        UI->>API: GET /api/users dan GET /api/jenis-tugas
        API->>Filter: Bearer JWT
        Filter->>Master: Otorisasi ADMIN dan ambil opsi form
        Master->>DB: SELECT user dan jenis tugas
        DB-->>Master: Daftar user dan jenis tugas
        Master-->>UI: JSON opsi, UI menyaring employee aktif
        Admin->>UI: Pilih satu atau lebih employee
        UI->>UI: Validasi field wajib dan format tanggal
        UI->>API: POST /api/tugas/bulk
        API->>Filter: Bearer JWT
        Filter->>C: Otorisasi ADMIN
        C->>S: createTugasForEmployees(request)
        S->>Master: Validasi ID, keberadaan, role employee dan jenis tugas
        Master->>DB: SELECT data referensi
        DB-->>Master: Data valid atau tidak ditemukan
        S->>Generator: nextId(tanggal rencana)
        Generator->>DB: Kunci counter tahun dan naikkan nomor
        DB-->>Generator: TASK-tahun-nomor
        S->>Repo: Simpan Tugas PENDING dan assignees
        Repo->>DB: INSERT tugas dan penugasan_karyawan
        DB-->>Repo: Tugas tersimpan
        Repo-->>S: Tugas
        S-->>C: Tugas
        C-->>API: JSON tugas
        API-->>UI: Berhasil dan muat ulang tabel
    else Ubah tugas PENDING
        Admin->>UI: Pilih Ubah dan simpan perubahan
        UI->>API: PUT /api/tugas/{id}
        API->>Filter: Bearer JWT
        Filter->>C: Otorisasi ADMIN
        C->>S: updateTugas(id, request)
        S->>Repo: findById(id)
        Repo->>DB: SELECT tugas dan assignments
        DB-->>Repo: Tugas
        S->>S: Validasi status PENDING, tanggal, field dan assignee
        S->>Repo: Update data tugas dan assignments
        Repo->>DB: UPDATE tugas dan penugasan_karyawan
        DB-->>Repo: Tugas diperbarui
        S-->>C: Tugas terbaru atau error validasi
        C-->>API: Response
        API-->>UI: Tampilkan hasil dan muat ulang
    else Hapus tugas PENDING
        Admin->>UI: Konfirmasi hapus
        UI->>API: DELETE /api/tugas/{id}
        API->>Filter: Bearer JWT
        Filter->>C: Otorisasi ADMIN
        C->>S: deleteTugas(id)
        S->>Repo: findById(id)
        Repo->>DB: SELECT tugas
        alt Status PENDING
            S->>Repo: delete(tugas)
            Repo->>DB: DELETE tugas, assignment terhapus melalui cascade
            DB-->>Repo: Terhapus
            S-->>C: Berhasil
            C-->>API: 204 No Content
            API-->>UI: Hapus baris dan muat ulang
        else Status bukan PENDING
            S-->>C: 409 penghapusan ditolak
            C-->>API: Error response
            API-->>UI: Tampilkan error
        end
    end
```

**Interpretasi:** Form Admin membuat tugas selalu melalui `POST /api/tugas/bulk`, termasuk jika hanya satu employee dipilih. Form menyaring employee aktif; service create memvalidasi keberadaan dan role employee, tetapi tidak memeriksa status aktif. Edit dan hapus biasa hanya berlaku untuk `PENDING`; penjadwalan ulang tugas `REJECTED` dijelaskan pada [sequence verifikasi](task-verification-sequence.md). Tanggal adalah rencana, bukan deadline atau waktu pembuatan.
