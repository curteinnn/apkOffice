# Authentication Sequence

**Tujuan:** Memerinci login dari dua client dan autentikasi token pada request protected.

## Login

```mermaid
sequenceDiagram
    actor User as Admin atau Karyawan
    participant Client as Login UI
    participant AuthC as AuthController
    participant AuthS as AuthService
    participant Users as UserRepository
    participant DB as MariaDB
    participant Encoder as PasswordEncoder BCrypt
    participant Jwt as JwtService

    User->>Client: Masukkan username dan password
    Client->>AuthC: POST /api/auth/login
    AuthC->>AuthS: login(LoginRequest)
    AuthS->>Users: findByUsername(username)
    Users->>DB: SELECT user berdasarkan username
    DB-->>Users: user atau kosong
    Users-->>AuthS: Optional<User>
    alt User tidak ditemukan
        AuthS-->>Client: 401 username/password salah
    else User ditemukan tetapi INACTIVE
        AuthS-->>Client: 401 akun tidak aktif
    else User aktif
        AuthS->>Encoder: matches(password input, hash)
        alt Password tidak cocok
            Encoder-->>AuthS: false
            AuthS-->>Client: 401 username/password salah
        else Password cocok
            Encoder-->>AuthS: true
            AuthS->>Jwt: generateToken(user)
            Jwt-->>AuthS: JWT subject username, claim role, issued/expiry
            AuthS-->>AuthC: LoginResponse dan token
            AuthC-->>Client: 200 JSON
            Client->>Client: Periksa role cocok dengan client
            alt Role sesuai
                Client->>Client: Simpan token dan buka halaman utama
            else Role tidak sesuai
                Client->>User: Tampilkan penolakan role, jangan simpan sesi
            end
        end
    end
```

## Request terautentikasi

```mermaid
sequenceDiagram
    participant Client as Admin Desktop atau Employee Web
    participant Filter as JwtAuthenticationFilter
    participant JWT as JJWT parser
    participant Users as UserRepository
    participant DB as MariaDB
    participant Security as SecurityFilterChain
    participant Controller

    Client->>Filter: Request Authorization: Bearer JWT
    Filter->>JWT: Verifikasi signature dan expiry, baca subject/role
    alt Token malformed, invalid, atau expired
        JWT-->>Filter: JwtException
        Filter-->>Client: 401 token invalid/expired
    else Claims valid
        JWT-->>Filter: username dan role
        Filter->>Users: findByUsername(username)
        Users->>DB: SELECT akun terkini
        DB-->>Users: User
        Users-->>Filter: User atau kosong
        alt User tidak ada, tidak aktif, atau role berubah
            Filter-->>Client: 401 token tidak valid / akses akun berubah
        else Akun aktif dan role sama dengan claim
            Filter->>Security: Set username dan ROLE_ADMIN/ROLE_EMPLOYEE
            Security->>Security: Evaluasi matcher endpoint
            alt Tidak terautentikasi atau role tidak cukup
                Security-->>Client: 401/403
            else Diizinkan
                Security->>Controller: Lanjutkan request
                Controller-->>Client: Response endpoint
            end
        end
    end
```

**Interpretasi:** Session backend stateless. Filter membaca ulang akun pada database tiap request protected, sehingga akun nonaktif atau perubahan role membatalkan token lama. Login berhasil sendiri tidak menjamin role cocok untuk aplikasi; Admin Desktop menolak role selain ADMIN dan Employee Web menolak role selain EMPLOYEE.
