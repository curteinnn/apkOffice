# Task Assignment Sequence

**Tujuan:** Menunjukkan pengambilan pilihan form dan pembuatan task untuk satu atau banyak employee.

```mermaid
sequenceDiagram
    actor Admin
    participant Form as PengajuanTugas
    participant Client as TaskApiService ApiClient
    participant Filter as JwtAuthenticationFilter
    participant Security as SecurityConfig
    participant UserC as UserController
    participant UserS as UserService
    participant TypeC as JenisTugasController
    participant TypeS as JenisTugasService
    participant C as TugasController
    participant S as TugasService
    participant Users as UserRepository
    participant Types as JenisTugasRepository
    participant Generator as TugasIdGenerator
    participant Tasks as TugasRepository
    participant DB as MariaDB

    Admin->>Form: Buka form tugas
    Form->>Client: GET /api/users
    Client->>Filter: Bearer JWT
    Filter->>Users: findByUsername(subject), validasi role token
    Users->>DB: SELECT user yang login
    DB-->>Users: Akun aktif dan role ADMIN
    Filter->>Security: Set authority ROLE_ADMIN
    Security->>UserC: Izinkan GET /api/users
    UserC->>UserS: getAllUsers()
    UserS->>Users: findAll()
    Users->>DB: SELECT user list
    DB-->>Users: User rows
    Users-->>UserS: User list
    UserS-->>UserC: User list
    UserC-->>Client: JSON users tanpa password
    Client-->>Form: User, UI menyaring EMPLOYEE aktif
    Form->>Client: GET /api/jenis-tugas
    Client->>Filter: Bearer JWT
    Filter->>Security: Validasi token dan role
    Security->>TypeC: Izinkan GET /api/jenis-tugas
    TypeC->>TypeS: getAll()
    TypeS->>Types: findAllByOrderByIdAsc()
    Types->>DB: SELECT jenis tugas
    DB-->>Types: Jenis tugas
    Types-->>TypeS: Daftar jenis
    TypeS-->>TypeC: Daftar jenis
    TypeC-->>Client: JSON jenis tugas
    Client-->>Form: Pilihan jenis tugas
    Admin->>Form: Isi data, tanggal, pilih assignee(s)
    alt Satu employee
        Form->>Client: POST /api/tugas (userId, detail task)
    else Beberapa employee
        Form->>Client: POST /api/tugas/bulk (userIds, detail task)
    end
    Client->>Filter: Request Bearer JWT
    Filter->>Security: Validasi token dan role ADMIN
    Security->>C: Izinkan POST endpoint tugas tunggal atau bulk
    C->>S: createTugas(...) atau createTugasForEmployees(...)
    opt Bulk
        S->>S: Tolak daftar kosong/null/ID duplikat
        S->>Users: findAllById(userIds)
        Users->>DB: SELECT user sesuai ID
        DB-->>S: User list
        S->>S: Pastikan semua ditemukan dan role EMPLOYEE
    end
    S->>S: Validasi nama pelanggan, judul, lokasi, tanggal, jenis tugas
    S->>Users: findById(userId pertama / user tunggal)
    Users->>DB: SELECT user target
    DB-->>Users: User atau kosong
    Users-->>S: User
    S->>Types: findById(jenisTugasId)
    Types->>DB: SELECT jenis tugas
    DB-->>S: Jenis tugas atau kosong
    S->>Generator: nextId(tanggal)
    Generator->>DB: INSERT sequence tahun jika belum ada
    Generator->>DB: SELECT nomor_terakhir FOR UPDATE
    DB-->>Generator: Nomor terakhir
    Generator->>DB: UPDATE nomor_terakhir
    Generator-->>S: TASK-tahun-nomor
    alt Satu employee
        S->>Tasks: save(Tugas PENDING + satu assignment)
        Tasks->>DB: INSERT tugas dan penugasan_karyawan
        DB-->>Tasks: Tugas dan assignment tersimpan
    else Bulk
        S->>Tasks: save(Tugas PENDING + assignee pertama)
        Tasks->>DB: INSERT tugas dan assignment awal
        DB-->>Tasks: Tugas tersimpan
        S->>S: Ganti collection dengan seluruh userIds yang tervalidasi
        S->>Tasks: save(Tugas + seluruh assignment)
        Tasks->>DB: Simpan assignment anggota lainnya
        DB-->>Tasks: Tugas dan assignment lengkap
    end
    Tasks-->>S: Tugas tersimpan
    S-->>C: Entity Tugas
    C-->>Client: 200 JSON Tugas + assignees
    Client-->>Form: Response berhasil
```

**Interpretasi:** Form memilih karyawan melalui multi-selection. Backend memvalidasi duplikasi dan keberadaan ID serta role employee sebelum membuat row task dan assignment dalam transaksi. `TugasIdGenerator` mengunci counter per tahun lewat query native. Source service create memastikan role employee tetapi tidak memvalidasi status ACTIVE; UI form menyaring karyawan aktif.
