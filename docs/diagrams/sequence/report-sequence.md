# Admin Reports Sequence

**Tujuan:** Merangkum empat report Admin Desktop dan pemrosesan data di sisi client.

```mermaid
sequenceDiagram
    actor Admin
    participant UI as Frame report Admin
    participant TaskAPI as TaskApiService ApiClient
    participant AttendanceAPI as AttendanceApiService ApiClient
    participant Filter as JWT filter + SecurityConfig
    participant TaskC as TugasController
    participant AttendanceC as AttendanceController
    participant Services as Backend services
    participant Repos as JPA repositories
    participant DB as MariaDB

    Admin->>UI: Buka salah satu menu report
    alt Daftar Tugas
        UI->>TaskAPI: getAll()
        TaskAPI->>Filter: GET /api/tugas + Bearer JWT
        Filter->>TaskC: Otorisasi ADMIN
        TaskC->>Services: getAllTugas(username, ADMIN)
        Services->>Repos: findAll tasks
        Repos->>DB: SELECT tugas dan assignees
        DB-->>Repos: Task rows
        Repos-->>Services: Daftar tugas
        Services-->>TaskC: Daftar tugas
        TaskC-->>TaskAPI: JSON tasks
        TaskAPI-->>UI: Tugas
        UI-->>Admin: Tampilkan daftar seluruh tugas tanpa kolom nominal
    else Rekap Tugas per Karyawan
        UI->>TaskAPI: getAll()
        TaskAPI->>Filter: GET /api/tugas + Bearer JWT
        Filter->>TaskC: Otorisasi ADMIN
        TaskC->>Services: getAllTugas(username, ADMIN)
        Services->>Repos: findAll tasks
        Repos->>DB: SELECT tugas dan assignees
        DB-->>Repos: Task rows
        Repos-->>Services: Daftar tugas
        Services-->>TaskC: Daftar tugas
        TaskC-->>TaskAPI: JSON tasks
        TaskAPI-->>UI: Tugas dan assignees
        UI->>UI: Filter rentang tanggal dan employee
        UI->>UI: Agregasi total assignment per status dan ID tugas
        UI-->>Admin: Tampilkan rekap produktivitas
    else Rekap Nominal Tugas per Karyawan
        UI->>TaskAPI: getAll()
        TaskAPI->>Filter: GET /api/tugas + Bearer JWT
        Filter->>TaskC: Otorisasi ADMIN
        TaskC->>Services: getAllTugas(username, ADMIN)
        Services->>Repos: findAll tasks
        Repos->>DB: SELECT tugas dan nominal assignment
        DB-->>Repos: Task rows dan nominal per assignee
        Repos-->>Services: Daftar tugas
        Services-->>TaskC: Daftar tugas
        TaskC-->>TaskAPI: JSON tasks
        TaskAPI-->>UI: Tugas dan nominalByAssignee
        UI->>UI: Filter tanggal dan employee, pilih status APPROVED
        UI->>UI: Jumlahkan nominal per assignee dan hitung nominal kosong
        UI-->>Admin: Tampilkan total nominal, jumlah tugas, dan ID tugas
    else Rekap Absensi
        UI->>AttendanceAPI: getAll()
        AttendanceAPI->>Filter: GET /api/attendance + Bearer JWT
        Filter->>AttendanceC: Otorisasi ADMIN
        AttendanceC->>Services: getAllAttendance()
        Services->>Repos: findAll attendance
        Repos->>DB: SELECT attendance dan user
        DB-->>Repos: Attendance rows
        Repos-->>Services: Daftar attendance
        Services-->>AttendanceC: Daftar attendance
        AttendanceC-->>AttendanceAPI: JSON attendance
        AttendanceAPI-->>UI: Attendance
        UI->>UI: Filter lokal rentang tanggal
        UI->>UI: Hitung total, PRESENT, belum check-out, dan tanggal terbaru
        UI-->>Admin: Tampilkan ringkasan dan tabel rekap absensi
    end

    opt Request gagal atau rentang/filter tidak valid
        UI-->>Admin: Tampilkan pesan error atau validasi rentang tanggal
    end
```

**Interpretasi:** Keempat menu report menggunakan endpoint data umum, bukan endpoint report khusus. Penyaringan dan agregasi dilakukan di Admin Desktop. Rekap produktivitas menghitung status dan jumlah assignment per karyawan; nominal hanya menjumlahkan tugas `APPROVED` dan menampilkan nominal yang belum tersedia. Tidak ada proses export pada alur report yang ditelusuri.
