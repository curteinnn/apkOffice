# Admin Account Sequence

**Tujuan:** Merangkum login Admin Desktop, perubahan password, dan logout lokal dalam satu siklus sesi.

```mermaid
sequenceDiagram
    actor Admin
    participant UI as Admin Desktop login/dashboard/dialog
    participant API as AuthApiService dan UserApiService
    participant Security as JWT filter + SecurityConfig
    participant Auth as AuthController + AuthService
    participant Account as UserController + UserService + UserRepository
    participant DB as MariaDB
    participant Encoder as BCrypt PasswordEncoder
    participant JWT as JwtService
    participant Session as AuthSession

    Admin->>UI: Masukkan username dan password
    UI->>API: POST /api/auth/login
    API->>Auth: LoginRequest
    Auth->>DB: Cari user berdasarkan username
    DB-->>Auth: User atau kosong
    alt User tidak ditemukan atau nonaktif
        Auth-->>API: 401 autentikasi gagal
        API-->>UI: Tampilkan error login
    else Akun aktif
        Auth->>Encoder: Cocokkan password input dengan hash
        alt Password salah
            Encoder-->>Auth: Tidak cocok
            Auth-->>API: 401 autentikasi gagal
            API-->>UI: Tampilkan error login
        else Password cocok
            Encoder-->>Auth: Cocok
            Auth->>JWT: Generate token
            JWT-->>Auth: JWT
            Auth-->>API: LoginResponse
            API-->>UI: Token dan profil
            UI->>UI: Pastikan role ADMIN
            alt Role bukan ADMIN
                UI-->>Admin: Tolak akses dan jangan simpan sesi
            else Role ADMIN
                UI->>Session: set(AuthResult)
                UI-->>Admin: Tampilkan dashboard
                opt Admin memilih Ubah Password
                    Admin->>UI: Isi password lama, baru, dan konfirmasi
                    UI->>UI: Validasi field dan kecocokan konfirmasi
                    UI->>API: PUT /api/users/change-password + Bearer JWT
                    API->>Security: Validasi JWT dan akses authenticated
                    Security->>Account: Teruskan username dari request terautentikasi
                    Account->>DB: Cari akun pemilik username
                    DB-->>Account: Data user dan hash password
                    Account->>Encoder: Cocokkan password lama
                    alt Password lama salah atau password baru kurang dari 6 karakter
                        Account-->>API: 400 validasi gagal
                        API-->>UI: Tampilkan error
                    else Password valid
                        Account->>Encoder: Hash password baru
                        Encoder-->>Account: BCrypt hash
                        Account->>DB: UPDATE password dan updatedAt
                        DB-->>Account: User tersimpan
                        Account-->>API: Response sukses
                        API-->>UI: Tampilkan konfirmasi berhasil
                    end
                end
                Admin->>UI: Pilih Logout
                UI->>Session: clear() secara lokal
                UI-->>Admin: Kembali ke form login
            end
        end
    end
```

**Interpretasi:** Login memeriksa kredensial dan status akun di backend; sesi Admin Desktop hanya dibuat untuk role `ADMIN`. Perubahan password memakai endpoint terlindungi, memeriksa password lama, dan menyimpan hash baru. Logout hanya membersihkan `AuthSession` di desktop dan tidak menghubungi backend.
