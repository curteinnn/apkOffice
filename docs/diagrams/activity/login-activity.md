# Login Activity Diagram

**Tujuan:** Menjelaskan pemeriksaan kredensial di backend dan pemeriksaan role yang dilakukan masing-masing client setelah menerima response.

```mermaid
flowchart TD
    Start([Mulai]) --> Input[Pengguna mengisi username dan password]
    Input --> Client{Client yang digunakan?}

    Client -->|Admin Desktop| AdminInput{Field kosong?}
    AdminInput -->|Ya| AdminWarn[Tampilkan validasi lokal]
    AdminWarn --> End([Selesai])
    AdminInput -->|Tidak| AdminPost[POST /api/auth/login]

    Client -->|Employee Web| WebInput{Form valid?}
    WebInput -->|Tidak| WebWarn[Validasi required di browser]
    WebWarn --> End
    WebInput -->|Ya| WebPost[POST /api/auth/login]

    AdminPost --> Backend[AuthController ke AuthService]
    WebPost --> Backend
    Backend --> FindUser[Cari user berdasarkan username]
    FindUser --> Found{User ditemukan dan ACTIVE?}
    Found -->|Tidak| Fail401[Response 401]
    Found -->|Ya| Password{BCrypt cocok?}
    Password -->|Tidak| Fail401
    Password -->|Ya| Jwt[Buat JWT berisi subject username, role, issuedAt, expiry]
    Jwt --> Response[LoginResponse: identitas, role, status, token]
    Response --> RoleGate{Role cocok dengan client?}
    RoleGate -->|Admin Desktop dan ADMIN| AdminStore[Simpan AuthResult pada AuthSession]
    RoleGate -->|Employee Web dan EMPLOYEE| WebStore[Simpan user dan token di sessionStorage]
    RoleGate -->|Tidak cocok| RoleReject[Tampilkan penolakan role; token tidak disimpan]
    Fail401 --> ClientError[Tampilkan pesan login gagal]
    AdminStore --> AdminDashboard[Buka Dashboard Admin]
    WebStore --> EmployeeDashboard[Navigasi ke /dashboard]
    AdminDashboard --> End
    EmployeeDashboard --> End
    RoleReject --> End
    ClientError --> End
```

**Interpretasi:** Password diverifikasi backend dengan `PasswordEncoder` BCrypt dan akun nonaktif ditolak sebelum token diterbitkan. Pemeriksaan role Admin/Employee juga dilakukan di UI masing-masing setelah response login; endpoint terlindungi tetap memiliki kontrol backend sendiri. Login Employee menyimpan token dalam `sessionStorage`, sedangkan Admin Desktop menyimpan token pada session singleton di memori.
