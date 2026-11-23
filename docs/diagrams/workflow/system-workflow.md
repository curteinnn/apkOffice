# System Workflow Diagram

**Tujuan:** Merangkum alur Admin dan Employee dari client menuju backend dan database.

**Komponen:** Admin Desktop, Employee Web, JWT/security filter, controller, service, repository, MariaDB. Keduanya memakai endpoint backend yang sama; Admin Desktop mengirim HTTP JSON dengan token dari `AuthSession`, Employee Web menambahkan token `sessionStorage` pada `apiRequest`.

```mermaid
flowchart LR
    subgraph Actors[Pengguna]
        A[Admin]
        E[Karyawan]
    end

    subgraph Clients[Client]
        AD["Admin Desktop<br/>Java Swing"]
        EW["Employee Web<br/>React"]
    end

    subgraph Backend[Backend Spring Boot]
        Login[POST /api/auth/login]
        Filter[JWT filter]
        Gate["Security matcher<br/>role dan authentication"]
        C[Controller]
        S["Service<br/>validasi dan business logic"]
        R[Spring Data repositories]
    end

    DB[(MariaDB)]
    A --> AD
    E --> EW
    AD -->|login dan REST request| Login
    EW -->|login dan REST request| Login
    Login --> C
    C --> S
    S --> R
    R --> DB
    DB --> R
    R --> S
    S --> C
    C -->|token pada login| AD
    C -->|token pada login| EW
    AD -->|Authorization: Bearer token| Filter
    EW -->|Authorization: Bearer token| Filter
    Filter -->|signature, expiry, user aktif dan role| Gate
    Gate -->|tolak: 401/403| AD
    Gate -->|tolak: 401/403| EW
    Gate -->|terima| C
    C --> S
    S --> R
    C -->|JSON response atau error| AD
    C -->|JSON response atau error| EW
```

### Alur berdasar role

```mermaid
flowchart TD
    Admin[Admin] --> AD[Admin Desktop]
    AD -->|login| Auth[POST /api/auth/login]
    AD -->|JWT| AdminAPI[Endpoint Admin]
    AdminAPI --> Master[CRUD karyawan, jabatan, jenis tugas]
    AdminAPI --> TaskAdmin[Buat, ubah, hapus terbatas, verifikasi tugas]
    AdminAPI --> AdminRead[Baca absensi dan daftar tugas]
    AdminRead --> Reports[Laporan dihitung pada client dari data API]

    Employee[Karyawan] --> EW[Employee Web]
    EW -->|login role EMPLOYEE| Auth
    EW -->|JWT| Attendance[Attendance API]
    EW -->|JWT + record absensi hari ini| TaskRead[GET tugas yang ditugaskan]
    Attendance --> CheckIn[Check-in]
    Attendance --> CheckOut[Check-out jika memenuhi aturan service]
    TaskRead --> TaskActions[Mulai / kirim tugas]

    Auth --> Backend[Backend memverifikasi password dan membuat JWT]
    AdminAPI --> Backend
    Attendance --> Backend
    TaskRead --> Backend
    TaskActions --> Backend
    Backend --> DB[(MariaDB via JPA repository)]
```

**Interpretasi:** Login publik menghasilkan token JWT bila kredensial benar dan akun aktif. Pada request terlindungi filter memvalidasi token, lalu role request dibatasi oleh `SecurityConfig` dan beberapa controller/service. Backend menjadi satu-satunya jalur aplikasi menuju data. Dashboard dan report Admin mengambil data API lalu menyusun ringkasan di client.

**Catatan:** `GET /api/tugas` untuk Employee memerlukan record absensi tanggal berjalan, tetapi tetap mengembalikan daftar setelah check-out; endpoint mutasi tugas mensyaratkan absensi aktif dan belum check-out.
