# Master User dan Master Data Sequence

**Tujuan:** Menjelaskan pembuatan karyawan dari Admin Desktop serta bentuk umum CRUD master jabatan/jenis tugas.

## Buat karyawan

```mermaid
sequenceDiagram
    actor Admin
    participant Form as TambahKaryawan
    participant Api as UserApiService ApiClient
    participant Filter as JwtAuthenticationFilter
    participant Security as SecurityConfig
    participant C as UserController
    participant S as UserService
    participant JC as JabatanController
    participant JS as JabatanService
    participant JR as JabatanRepository
    participant UR as UserRepository
    participant Encoder as BCrypt PasswordEncoder
    participant DB as MariaDB

    Admin->>Form: Buka master user
    Form->>Api: GET /api/users
    Api->>Filter: Request Bearer JWT
    Filter->>UR: findByUsername(subject) untuk validasi token
    UR->>DB: SELECT user dan role terkini
    DB-->>Filter: Akun aktif dan role ADMIN
    Filter->>Security: Set authority ROLE_ADMIN
    Security->>C: Izinkan GET /api/users
    C->>S: getAllUsers()
    S->>UR: findAll()
    UR->>DB: SELECT user
    DB-->>UR: List user
    UR-->>S: List user
    S-->>C: List user
    C-->>Api: JSON user (password dikecualikan)
    Api-->>Form: List user
    Form->>Api: GET /api/jabatan?aktif=true
    Api->>Filter: Request Bearer JWT
    Filter->>Security: Validasi token dan role
    Security->>JC: Izinkan GET /api/jabatan
    JC->>JS: getAll(aktif=true)
    JS->>JR: findAllByStatusOrderByNamaAsc(ACTIVE)
    JR->>DB: SELECT jabatan aktif
    DB-->>JR: Daftar jabatan aktif
    JR-->>JS: Daftar jabatan aktif
    JS-->>JC: Daftar jabatan aktif
    JC-->>Api: JSON jabatan
    Api-->>Form: Daftar jabatan aktif

    Admin->>Form: Isi data dan pilih jabatan
    Form->>Api: POST /api/users (CreateUserRequest)
    Api->>Filter: Bearer JWT
    Filter->>Security: Token valid dan role ADMIN
    Security->>C: Izinkan POST /api/users
    C->>S: createUser(request)
    S->>S: Wajib isi username, password, nama, email, role hanya EMPLOYEE
    S->>JR: Cari jabatanId dan validasi status ACTIVE
    JR->>DB: SELECT jabatan
    DB-->>JR: Jabatan aktif
    JR-->>S: Jabatan aktif
    S->>Encoder: encode(password)
    Encoder-->>S: Hash BCrypt
    S->>UR: save(user)
    UR->>DB: INSERT user
    DB-->>UR: User tersimpan
    UR-->>S: User tersimpan
    S-->>C: User tersimpan
    C-->>Api: JSON user tanpa password
    Api-->>Form: Response berhasil
```

## Pengelolaan master jabatan dan jenis tugas

```mermaid
sequenceDiagram
    actor Admin
    participant UI as MasterJabatan MasterJenisTugas
    participant API as JabatanApiService JenisTugasApiService
    participant Filter as JWT + SecurityConfig
    participant C as Controller master
    participant S as Service master
    participant Repo as Repository terkait
    participant DB as MariaDB

    Admin->>UI: Pilih tampil, tambah, ubah, nonaktifkan, atau hapus
    UI->>API: GET/POST/PUT/DELETE pada endpoint master
    API->>Filter: Request Bearer token
    Filter->>C: Otorisasi ADMIN
    C->>S: Operasi CRUD
    S->>S: Validasi field dan duplikasi nama/ID
    opt Jabatan: hapus atau ubah status
        S->>Repo: Periksa user yang memakai jabatan
    end
    opt Jenis tugas: hapus
        S->>Repo: Periksa tugas yang memakai jenis
    end
    S->>Repo: Simpan perubahan
    Repo->>DB: SELECT / INSERT / UPDATE / DELETE
    DB-->>Repo: Hasil query
    Repo-->>S: Hasil persistence
    S-->>C: Entity/daftar atau error validasi
    C-->>API: JSON atau HTTP error
    API-->>UI: Tampilkan hasil operasi
```

**Interpretasi:** CRUD master jabatan dan jenis tugas hanya tersedia untuk ADMIN. Jabatan menyediakan perubahan status; penghapusan ditolak bila sedang dipakai user. Jenis tugas dapat dihapus hanya bila belum dipakai tugas. Pengelolaan user menolak role selain EMPLOYEE; pembuatan memerlukan jabatan aktif.
