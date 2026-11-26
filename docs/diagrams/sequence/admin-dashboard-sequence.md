# Admin Dashboard Sequence

**Tujuan:** Menjelaskan pengambilan dan agregasi data ringkasan dashboard Admin Desktop.

```mermaid
sequenceDiagram
    actor Admin
    participant UI as Dashboard
    participant Worker as SwingWorker
    participant API as User/Attendance/Task API services
    participant Filter as JWT filter + SecurityConfig
    participant Backend as Controllers, services, repositories
    participant DB as MariaDB

    Admin->>UI: Login berhasil dan dashboard dibuka
    UI->>Worker: Mulai loadDashboardData()
    alt Semua request berhasil
        Worker->>API: GET /api/users + Bearer JWT
        API->>Filter: Validasi token
        Filter->>Backend: Otorisasi ADMIN dan getAllUsers()
        Backend->>DB: SELECT user
        DB-->>Backend: User rows
        Backend-->>API: JSON users
        API-->>Worker: User data
        Worker->>API: GET /api/attendance + Bearer JWT
        API->>Filter: Validasi token
        Filter->>Backend: Otorisasi ADMIN dan getAllAttendance()
        Backend->>DB: SELECT attendance
        DB-->>Backend: Attendance rows
        Backend-->>API: JSON attendance
        API-->>Worker: Attendance data
        Worker->>API: GET /api/tugas + Bearer JWT
        API->>Filter: Validasi token
        Filter->>Backend: Otorisasi ADMIN dan getAllTugas()
        Backend->>DB: SELECT tugas dan assignees
        DB-->>Backend: Task rows
        Backend-->>API: JSON tasks
        API-->>Worker: Task data
        Worker->>UI: Kembalikan hasil tiga request
        UI->>UI: Hitung employee, hadir hari ini, status aktif dan approved
        UI->>UI: Urutkan createdAt terbaru dan ambil maksimal 8 tugas
        UI-->>Admin: Tampilkan ringkasan dan tugas terbaru
    else Salah satu request gagal
        Worker-->>UI: Exception dari get()
        UI->>UI: Set metrik ke tanda minus
        UI-->>Admin: Tampilkan dialog gagal memuat dashboard
    end
```

**Interpretasi:** Dashboard memanggil endpoint user, attendance, lalu tugas secara berurutan di `SwingWorker`. Hitungan employee memakai role `EMPLOYEE`; hadir hari ini memakai tanggal WIB dan status `PRESENT`; tugas aktif mencakup `PENDING`, `IN_PROGRESS`, `SUBMITTED`, `REJECTED`, sedangkan `APPROVED` dihitung terpisah. Jika satu request gagal, rangkaian fetch berhenti dan dashboard menampilkan dialog error.
