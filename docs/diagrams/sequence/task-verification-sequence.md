# Task Verification Sequence

**Tujuan:** Menjelaskan verifikasi Admin dan validasi nominal per assignee.

```mermaid
sequenceDiagram
    actor Admin
    participant UI as ListTugas mode VERIFICATION
    participant API as TaskApiService ApiClient
    participant Filter as JWT filter + SecurityConfig
    participant C as TugasController
    participant S as TugasService
    participant Repo as TugasRepository
    participant DB as MariaDB

    Admin->>UI: Buka verifikasi tugas
    UI->>API: GET /api/tugas
    API->>Filter: Bearer JWT
    Filter->>C: Role ADMIN
    C->>S: getAllTugas(username, ADMIN)
    S->>Repo: findAll()
    Repo->>DB: SELECT tugas dan assignees
    DB-->>Repo: Tugas dan assignments
    Repo-->>S: Daftar tugas
    S-->>C: Daftar tugas
    C-->>API: JSON tasks
    API-->>UI: Daftar tugas
    UI-->>Admin: Tampilkan task berstatus SUBMITTED
    Admin->>UI: Pilih setujui atau tolak
    alt Setujui
        Admin->>UI: Isi nominal untuk tiap assignee
        UI->>API: PUT verify status APPROVED dan nominalByAssignee
    else Tolak
        Admin->>UI: Isi alasan penolakan wajib, maksimal 2000 karakter
        UI->>API: PUT verify status REJECTED dan alasanPenolakan
    end
    API->>Filter: Bearer JWT
    Filter->>C: Otorisasi ADMIN
    C->>S: verifyTugas(id, request)
    S->>Repo: findById(id)
    Repo->>DB: SELECT task + assignments
    DB-->>Repo: Tugas dan assignments
    Repo-->>S: Tugas
    S->>S: Pastikan status saat ini SUBMITTED dan status tujuan valid
    alt Status APPROVED
        S->>S: Pastikan key nominal tepat sama dengan ID semua assignee
        S->>S: Tolak nominal null atau negatif
        S->>S: Set nominal pada tiap PenugasanKaryawan
        S->>S: Hapus reject_reason sebelumnya
    else Status REJECTED
        S->>S: Validasi alasanPenolakan wajib dan panjang maksimal 2000
        S->>S: Simpan alasanPenolakan ke kolom reject_reason
        S->>S: Set nominal seluruh assignment menjadi null
    end
    S->>Repo: Set status bersama dan save
    Repo->>DB: UPDATE tugas dan assignments
    DB-->>Repo: Tugas terverifikasi
    Repo-->>S: Tugas terbaru
    S-->>C: Tugas terbaru
    C-->>API: JSON task
    API-->>UI: Entity task terbaru
    UI-->>Admin: Muat ulang tabel verifikasi
```

**Interpretasi:** Verifikasi hanya menerima tugas `SUBMITTED`. Nominal harus tersedia untuk setiap anggota dan tidak negatif saat approve; saat reject alasan wajib diisi dan nominal semua assignment di-null-kan. Alasan disimpan ke kolom database `reject_reason` dan dikirim pada JSON sebagai `alasanPenolakan`. Status tugas satu nilai bersama untuk seluruh assignee.

## Penjadwalan ulang tugas yang ditolak

```mermaid
sequenceDiagram
    actor Admin
    participant UI as ListTugas dan PengajuanTugas
    participant API as TaskApiService ApiClient
    participant Filter as JWT + SecurityConfig
    participant C as TugasController
    participant S as TugasService
    participant Repo as TugasRepository
    participant DB as MariaDB

    Admin->>UI: Pilih Ubah pada tugas REJECTED
    UI->>Admin: Tampilkan konfirmasi penjadwalan ulang
    Admin->>UI: Tentukan tanggal rencana dan data penerima
    UI->>API: PUT /api/tugas/{id}
    API->>Filter: Bearer JWT
    Filter->>C: Validasi token dan role ADMIN
    C->>S: updateTugas(id, request)
    S->>Repo: findById(id)
    Repo->>DB: SELECT tugas dan assignees
    DB-->>Repo: Tugas REJECTED
    Repo-->>S: Tugas
    S->>S: Validasi data, tanggal rencana, dan assignee employee aktif
    S->>S: Perbarui data tugas dan set status PENDING
    S->>Repo: save(tugas)
    Repo->>DB: UPDATE tugas dan penugasan_karyawan
    Note over S,DB: reject_reason dipertahankan sebagai catatan penolakan sebelumnya, tidak ada pemindahan tanggal otomatis
    DB-->>Repo: Tugas terjadwal ulang
    Repo-->>S: Tugas PENDING
    S-->>C: Tugas terbaru
    C-->>API: JSON task
    API-->>UI: Response berhasil
    UI-->>Admin: Muat ulang daftar tugas
```

**Interpretasi:** Tugas `REJECTED` hanya kembali ke `PENDING` setelah admin mengedit dan menyimpan data serta tanggal rencana. Backend tidak menjadwalkan ulang secara otomatis. Karyawan dapat melihat alasan penolakan sebelumnya pada detail tugas.
