# Master User Sequence

**Tujuan:** Menjelaskan daftar dan CRUD akun karyawan melalui Admin Desktop.

```mermaid
sequenceDiagram
    actor Admin
    participant UI as TambahKaryawan
    participant UserAPI as UserApiService ApiClient
    participant JabatanAPI as JabatanApiService ApiClient
    participant Filter as JWT filter + SecurityConfig
    participant UserBackend as UserController, UserService, UserRepository
    participant JabatanBackend as JabatanController, JabatanService, Repository
    participant Encoder as BCrypt PasswordEncoder
    participant DB as MariaDB

    Admin->>UI: Buka Master User
    UI->>UserAPI: GET /api/users
    UserAPI->>Filter: Bearer JWT
    Filter->>UserBackend: Otorisasi ADMIN
    UserBackend->>DB: SELECT user
    DB-->>UserBackend: Daftar user
    UserBackend-->>UserAPI: JSON user tanpa password
    UserAPI-->>UI: UI menyaring dan menampilkan employee
    UI->>JabatanAPI: GET /api/jabatan?aktif=true
    JabatanAPI->>Filter: Bearer JWT
    Filter->>JabatanBackend: Otorisasi ADMIN
    JabatanBackend->>DB: SELECT jabatan ACTIVE
    DB-->>JabatanBackend: Daftar jabatan aktif
    JabatanBackend-->>JabatanAPI: JSON jabatan
    JabatanAPI-->>UI: Pilihan jabatan

    alt Tambah employee
        Admin->>UI: Isi username, password, nama, email dan jabatan
        UI->>UserAPI: POST /api/users dengan role EMPLOYEE
        UserAPI->>Filter: Bearer JWT
        Filter->>UserBackend: Otorisasi ADMIN
        UserBackend->>UserBackend: Validasi field wajib dan role EMPLOYEE
        UserBackend->>JabatanBackend: Pastikan jabatan aktif
        JabatanBackend->>DB: SELECT jabatanId dan status
        DB-->>JabatanBackend: Jabatan valid atau tidak valid
        UserBackend->>Encoder: Hash password
        Encoder-->>UserBackend: BCrypt hash
        UserBackend->>DB: INSERT user ACTIVE
        DB-->>UserBackend: User tersimpan
        UserBackend-->>UserAPI: JSON tanpa password
        UserAPI-->>UI: Berhasil dan muat ulang daftar
    else Ubah employee
        Admin->>UI: Ubah username, nama, email, jabatan atau status
        UI->>UserAPI: PUT /api/users/{id}, role tetap EMPLOYEE
        UserAPI->>Filter: Bearer JWT
        Filter->>UserBackend: Otorisasi ADMIN
        UserBackend->>DB: SELECT user target
        DB-->>UserBackend: User target
        UserBackend->>UserBackend: Tolak target non-EMPLOYEE dan validasi field/status
        UserBackend->>JabatanBackend: Validasi jabatan untuk user
        UserBackend->>DB: UPDATE user dan updatedAt
        DB-->>UserBackend: User terbaru
        UserBackend-->>UserAPI: JSON tanpa password atau HTTP error
        UserAPI-->>UI: Tampilkan hasil dan muat ulang
    else Hapus employee
        Admin->>UI: Konfirmasi hapus employee
        UI->>UserAPI: DELETE /api/users/{id}
        UserAPI->>Filter: Bearer JWT
        Filter->>UserBackend: Otorisasi ADMIN
        UserBackend->>DB: SELECT user dan validasi role EMPLOYEE
        DB-->>UserBackend: User target
        UserBackend->>DB: DELETE user
        alt Tidak ada relasi yang menghalangi
            DB-->>UserBackend: User terhapus
            UserBackend-->>UserAPI: Berhasil
            UserAPI-->>UI: Muat ulang daftar
        else Masih direferensikan attendance atau tugas
            DB-->>UserBackend: Constraint error
            UserBackend-->>UserAPI: Error response
            UserAPI-->>UI: Tampilkan error dan sarankan nonaktifkan akun
        end
    end
```

**Interpretasi:** Modul hanya mengelola role `EMPLOYEE`; akun `ADMIN` tidak dapat dibuat, diubah, atau dihapus melalui Master User. Penghapusan employee bisa ditolak karena relasi data. Password hanya dikirim saat pembuatan dan di-hash di backend.
