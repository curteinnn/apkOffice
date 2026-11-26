# Admin Task Verification Sequence

**Tujuan:** Menjelaskan persetujuan/penolakan tugas dan penjadwalan ulang manual untuk tugas yang ditolak.

```mermaid
sequenceDiagram
    actor Admin
    participant UI as ListTugas VERIFICATION
    participant Form as PengajuanTugas
    participant API as TaskApiService ApiClient
    participant Filter as JWT filter + SecurityConfig
    participant C as TugasController
    participant S as TugasService
    participant Users as UserRepository
    participant Repo as TugasRepository
    participant DB as MariaDB

    Admin->>UI: Buka Verifikasi Tugas
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
    API-->>UI: Tampilkan tugas SUBMITTED
    Admin->>UI: Pilih verifikasi
    UI->>UI: Pastikan status SUBMITTED

    alt Setujui
        loop Setiap assignee
            Admin->>UI: Masukkan nominal non-negatif
        end
        UI->>API: PUT /api/tugas/{id}/verify status APPROVED dan nominalByAssignee
        API->>Filter: Bearer JWT
        Filter->>C: Otorisasi ADMIN
        C->>S: verifyTugas(id, request)
        S->>Repo: findById(id)
        Repo->>DB: SELECT tugas dan assignments
        DB-->>Repo: Tugas SUBMITTED dan daftar assignee
        S->>S: Validasi ID nominal sama dengan semua assignee
        S->>S: Validasi nominal terisi dan tidak negatif
        S->>Repo: Simpan nominal dan status APPROVED
        S->>S: Hapus reject_reason sebelumnya
        Repo->>DB: UPDATE tugas dan penugasan_karyawan
        DB-->>Repo: Tugas APPROVED
        Repo-->>S: Tugas terbaru
        S-->>C: Tugas terbaru
        C-->>API: JSON tugas
        API-->>UI: Muat ulang daftar
    else Tolak
        Admin->>UI: Masukkan alasan wajib, maksimal 2000 karakter
        UI->>API: PUT /api/tugas/{id}/verify status REJECTED dan alasanPenolakan
        API->>Filter: Bearer JWT
        Filter->>C: Otorisasi ADMIN
        C->>S: verifyTugas(id, request)
        S->>Repo: findById(id)
        Repo->>DB: SELECT tugas dan assignments
        DB-->>Repo: Tugas SUBMITTED
        S->>S: Validasi alasanPenolakan
        S->>Repo: Simpan reject_reason, kosongkan nominal, set REJECTED
        Repo->>DB: UPDATE tugas dan penugasan_karyawan
        DB-->>Repo: Tugas REJECTED
        Repo-->>S: Tugas terbaru
        S-->>C: Tugas terbaru
        C-->>API: JSON tugas
        API-->>UI: Muat ulang daftar
        opt Admin menjadwalkan ulang tugas yang ditolak
            Admin->>UI: Pilih Ubah dan konfirmasi jadwal ulang
            UI->>Form: Buka form dengan data tugas
            Admin->>Form: Tentukan tanggal rencana dan assignee
            Form->>API: PUT /api/tugas/{id}
            API->>Filter: Bearer JWT
            Filter->>C: Otorisasi ADMIN
            C->>S: updateTugas(id, request)
            S->>Repo: findById(id)
            Repo->>DB: SELECT tugas REJECTED
            DB-->>Repo: Tugas dan assignments
            S->>Users: Validasi assignee employee aktif
            S->>S: Simpan perubahan dan set status PENDING
            S->>Repo: save(tugas)
            Repo->>DB: UPDATE tanggal rencana, assignment, status
            Note over S,DB: reject_reason tetap tersimpan sebagai catatan penolakan sebelumnya
            DB-->>Repo: Tugas PENDING
            Repo-->>S: Tugas terbaru
            S-->>C: Tugas terbaru
            C-->>API: JSON tugas
            API-->>Form: Berhasil dan muat ulang daftar
        end
    end
```

**Interpretasi:** Hanya tugas `SUBMITTED` yang dapat diverifikasi. Persetujuan menyimpan nominal per assignee dan menghapus alasan penolakan lama. Penolakan memerlukan alasan dan mengosongkan nominal. Penjadwalan ulang tidak otomatis: admin mengedit tanggal rencana dan assignee; setelah penyimpanan backend mengubah status `REJECTED` menjadi `PENDING`. Tanggal rencana tidak membatasi pengerjaan lebih awal.
