# Report Sequence

**Tujuan:** Menunjukkan pengambilan data laporan Admin. Report dihitung di Admin Desktop; tidak ada endpoint `/api/reports` pada source.

```mermaid
sequenceDiagram
    actor Admin
    participant UI as Dashboard Report Frame
    participant Client as User Attendance Task API Services
    participant Filter as JWT filter
    participant Security as SecurityConfig
    participant Controllers as User Attendance Tugas controllers
    participant Services as Backend services
    participant Repos as JPA repositories
    participant DB as MariaDB

    Admin->>UI: Buka dashboard
    UI->>Client: getUsers()
    Client->>Filter: GET /api/users + Bearer JWT
    Filter->>Security: Validasi signature, akun dan role
    Security->>Controllers: ADMIN diizinkan
    Controllers->>Services: getAllUsers()
    Services->>Repos: findAll users
    Repos->>DB: SELECT user
    DB-->>Repos: User rows
    Repos-->>Services: List users
    Services-->>Controllers: List users
    Controllers-->>Client: JSON users
    Client-->>UI: User data
    UI->>Client: getAllAttendance()
    Client->>Filter: GET /api/attendance + Bearer JWT
    Filter->>Security: Validasi ADMIN
    Security->>Controllers: Otorisasi
    Controllers->>Services: getAllAttendance()
    Services->>Repos: findAll attendance
    Repos->>DB: SELECT attendance
    DB-->>Repos: Attendance rows
    Repos-->>Services: List attendance
    Services-->>Controllers: List attendance
    Controllers-->>Client: JSON attendance
    Client-->>UI: Attendance data
    UI->>Client: getAll()
    Client->>Filter: GET /api/tugas + Bearer JWT
    Filter->>Security: Validasi ADMIN
    Security->>Controllers: Otorisasi
    Controllers->>Services: getAllTugas(username, ADMIN)
    Services->>Repos: findAll tasks
    Repos->>DB: SELECT task dan assignees
    DB-->>Repos: Tugas dan assignments
    Repos-->>Services: List tugas
    Services-->>Controllers: List tugas
    Controllers-->>Client: JSON tasks
    Client-->>UI: Task data
    UI->>UI: Hitung jumlah employee, hadir hari ini, status task dan daftar terbaru
    UI-->>Admin: Render dashboard

    opt Report daftar/produktivitas/nominal tugas
        Admin->>UI: Buka report tugas
        UI->>Client: getAll()
        Client->>Filter: GET /api/tugas + Bearer JWT
        Filter->>Security: Validasi ADMIN
        Security->>Controllers: Otorisasi
        Controllers->>Services: getAllTugas(username, ADMIN)
        Services->>Repos: findAll
        Repos->>DB: Baca Tugas dan assignment nominal
        DB-->>Repos: Tugas dan assignments
        Repos-->>Services: List tugas
        Services-->>Controllers: List tugas
        Controllers-->>Client: JSON tasks
        Client-->>UI: Daftar task
        UI->>UI: Filter tanggal/employee dan agregasi status atau nominal
        UI-->>Admin: Tabel report
    end
    opt Report rekap absensi
        Admin->>UI: Buka rekap absensi
        UI->>Client: getAllAttendance()
        Client->>Filter: GET /api/attendance + Bearer JWT
        Filter->>Security: Validasi ADMIN
        Security->>Controllers: Otorisasi
        Controllers->>Services: getAllAttendance()
        Services->>Repos: findAll
        Repos->>DB: Baca attendance
        DB-->>Repos: Attendance rows
        Repos-->>Services: List attendance
        Services-->>Controllers: List attendance
        Controllers-->>Client: JSON attendance
        Client-->>UI: List attendance
        UI->>UI: Agregasi rekap di client
        UI-->>Admin: Tabel rekap
    end
```

**Interpretasi:** Pada dashboard, pemanggilan user, attendance, dan tugas dijalankan berurutan di `SwingWorker`. Report produktivitas/nominal memfilter data task yang diambil dari endpoint umum dan menghitung total di desktop. Rekap absensi menggunakan endpoint daftar seluruh attendance yang hanya mengizinkan Admin.
