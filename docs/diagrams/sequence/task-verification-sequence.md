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
    Admin->>UI: Pilih setujui/tolak; bila setuju isi nominal tiap assignee
    UI->>API: PUT /api/tugas/{id}/verify {status, nominalByAssignee}
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
    else Status REJECTED
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

**Interpretasi:** Verifikasi hanya menerima tugas `SUBMITTED`. Nominal harus tersedia untuk setiap anggota dan tidak negatif saat approve; saat reject semua nominal di-null-kan. Status task satu nilai bersama untuk seluruh anggota.
