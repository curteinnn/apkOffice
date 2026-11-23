# Class Diagram Modul Tugas, Absensi, dan Autentikasi

**Tujuan:** Memperinci dependency dan domain relasi yang menjalankan alur utama. Class diagram tidak menyamakan `status` tugas dengan status per anggota; status hanya dimiliki `Tugas`.

```mermaid
classDiagram
    direction TB

    class SecurityConfig {
        +securityFilterChain(HttpSecurity)
    }
    class JwtAuthenticationFilter {
        -String secretKey
        +doFilterInternal(request, response, chain)
    }
    class AuthService {
        +login(LoginRequest) LoginResponse
    }
    class JwtService {
        +generateToken(User) String
    }
    class AttendanceController {
        +getAllAttendance(request)
        +getMyAttendance(request)
        +checkIn(request)
        +checkOut(request)
    }
    class AttendanceService {
        +getAllAttendance()
        +getMyAttendance(username)
        +checkIn(username)
        +checkOut(username)
    }
    class TugasController {
        +getAllTugas(request)
        +createTugas(request, httpRequest)
        +createTugasForEmployees(request, httpRequest)
        +startTugas(id, httpRequest)
        +submitTugas(id, httpRequest)
        +verifyTugas(id, request, httpRequest)
    }
    class TugasService {
        +getAllTugas(username, role)
        +createTugas(request)
        +createTugasForEmployees(request)
        +startTugas(id, username)
        +submitTugas(id, username)
        +verifyTugas(id, request)
    }
    class TugasIdGenerator {
        +nextId(tanggalTugas) String
    }
    class TugasRepository {
        +findAssignedTo(user) List~Tugas~
    }
    class AttendanceRepository {
        +findByUserAndTanggal(user, tanggal)
        +findByUserOrderByTanggalDesc(user)
    }

    class User {
        -Long id
        -Role role
        -Status status
    }
    class Attendance {
        -LocalDate tanggal
        -LocalDateTime jamMasuk
        -LocalDateTime jamKeluar
        -String status
    }
    class Tugas {
        -String id
        -String status
        -LocalDate tanggal
        +getAssignees()
        +getNominalByAssignee()
    }
    class PenugasanKaryawan {
        -BigDecimal nominal
    }

    SecurityConfig --> JwtAuthenticationFilter
    JwtAuthenticationFilter --> User
    AuthService --> JwtService
    AttendanceController --> AttendanceService
    AttendanceService --> AttendanceRepository
    AttendanceService --> TugasRepository
    TugasController --> AttendanceRepository : verifies today's check-in
    TugasController --> TugasService
    TugasService --> TugasRepository
    TugasService --> TugasIdGenerator

    User "1" --> "0..*" Attendance
    Tugas "1" *-- "0..*" PenugasanKaryawan
    User "1" <-- "0..*" PenugasanKaryawan
```

### Fakta model yang relevan

- `Attendance.jamKeluar` dapat null sebelum check-out; hanya satu record tanggal/user yang dicari oleh service untuk check-in dan check-out.
- Anggota task disimpan melalui composite key `(tugas_id, karyawan_id)` pada `PenugasanKaryawan`; nominal null atau bernilai per assignment.
- `Tugas.status` mencakup nilai string yang digunakan service: `PENDING`, `IN_PROGRESS`, `SUBMITTED`, `APPROVED`, `REJECTED`.
- `TugasIdGenerator` menggunakan query native ke `tugas_id_sequence`, mengunci sequence per tahun, lalu membentuk `TASK-<tahun>-<nomor 4 digit minimum>`.
- JWT filter membuat authority `ROLE_ADMIN`/`ROLE_EMPLOYEE` setelah memeriksa signature/expiry, role claim, dan status/role akun melalui `UserRepository`.

**Interpretasi:** Relationship pada class diagram berasal dari `@ManyToOne`, `@OneToMany`, dan composite `@EmbeddedId`. Repository untuk `PenugasanKaryawan` tidak dideklarasikan tersendiri; persist dilakukan melalui collection pada `Tugas`.
