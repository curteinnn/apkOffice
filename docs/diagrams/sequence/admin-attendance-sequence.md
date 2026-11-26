# Admin Attendance Today Sequence

**Tujuan:** Menjelaskan pemuatan daftar absensi hari ini pada Admin Desktop.

```mermaid
sequenceDiagram
    actor Admin
    participant UI as AbsensiHariIn
    participant API as AttendanceApiService ApiClient
    participant Filter as JWT filter + SecurityConfig
    participant C as AttendanceController
    participant S as AttendanceService
    participant Repo as AttendanceRepository
    participant DB as MariaDB

    Admin->>UI: Buka menu Absensi Hari Ini
    UI->>UI: Tentukan tanggal hari ini di Asia/Jakarta
    UI->>API: GET /api/attendance
    API->>Filter: Bearer JWT
    Filter->>C: Lanjutkan request terautentikasi
    C->>C: Pastikan role ADMIN
    alt Role ADMIN dan request berhasil
        C->>S: getAllAttendance()
        S->>Repo: findAll()
        Repo->>DB: SELECT semua attendance beserta user
        DB-->>Repo: Record attendance
        Repo-->>S: List attendance
        S-->>C: List attendance
        C-->>API: JSON attendance
        API-->>UI: Semua record attendance
        UI->>UI: Filter lokal berdasarkan tanggal hari ini
        UI-->>Admin: Tampilkan nama, jabatan, tanggal, status, jam masuk dan jam keluar
    else Request gagal
        C-->>API: 403 atau error response
        API-->>UI: Teruskan pesan error
        UI-->>Admin: Tampilkan pesan gagal memuat absensi
    end
```

**Interpretasi:** Endpoint mengembalikan semua data attendance untuk Admin. `AbsensiHariIn` menyaring record tanggal saat ini di desktop; halaman ini hanya membaca dan menampilkan absensi, tidak mengubah record.
