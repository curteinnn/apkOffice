# Overall Class Diagram

**Tujuan:** Menunjukkan lapisan backend dan domain persistence utama. DTO generik dan form Swing tidak ditampilkan agar hubungan arsitektur tetap terbaca.

```mermaid
classDiagram
    direction LR

    class AuthController
    class UserController
    class AttendanceController
    class TugasController
    class JabatanController
    class JenisTugasController

    class AuthService
    class UserService
    class AttendanceService
    class TugasService
    class JabatanService
    class JenisTugasService
    class JwtService
    class TugasIdGenerator
    class JwtAuthenticationFilter

    class UserRepository
    class AttendanceRepository
    class TugasRepository
    class JabatanRepository
    class JenisTugasRepository

    class User {
        -Long id
        -String username
        -String password
        -String namaLengkap
        -String email
        -Role role
        -Status status
        +getJabatan()
        +getJabatanId()
    }
    class Attendance {
        -Long id
        -LocalDate tanggal
        -LocalDateTime jamMasuk
        -LocalDateTime jamKeluar
        -String status
    }
    class Tugas {
        -String id
        -String judul
        -String namaPelanggan
        -String lokasi
        -String linkGmaps
        -String deskripsi
        -String status
        -LocalDate tanggal
        +getAssignees()
        +getNominalByAssignee()
    }
    class PenugasanKaryawan {
        -PenugasanKaryawanId id
        -BigDecimal nominal
    }
    class PenugasanKaryawanId {
        -String tugasId
        -Long karyawanId
    }
    class JenisTugas {
        -String id
        -String nama
    }
    class Jabatan {
        -Long id
        -String nama
        -String namaNormalized
        -Status status
    }

    AuthController --> AuthService
    UserController --> UserService
    AttendanceController --> AttendanceService
    TugasController --> TugasService
    JabatanController --> JabatanService
    JenisTugasController --> JenisTugasService

    AuthService --> UserRepository
    AuthService --> JwtService
    UserService --> UserRepository
    UserService --> JabatanRepository
    AttendanceService --> AttendanceRepository
    AttendanceService --> UserRepository
    AttendanceService --> TugasRepository
    TugasService --> TugasRepository
    TugasService --> UserRepository
    TugasService --> JenisTugasRepository
    TugasService --> TugasIdGenerator
    JabatanService --> JabatanRepository
    JabatanService --> UserRepository
    JenisTugasService --> JenisTugasRepository
    JenisTugasService --> TugasRepository
    JwtAuthenticationFilter --> UserRepository

    User "0..*" --> "0..1" Jabatan : jabatanRef
    User "1" --> "0..*" Attendance : user
    JenisTugas "0..1" <-- "0..*" Tugas : jenisTugas
    Tugas "1" *-- "0..*" PenugasanKaryawan : cascade assignments
    User "1" <-- "0..*" PenugasanKaryawan : karyawan
    PenugasanKaryawanId ..> Tugas : composite key uses tugasId
    PenugasanKaryawanId ..> User : composite key uses karyawanId
```

**Interpretasi relasi:** `Tugas` mengelola collection `PenugasanKaryawan` dengan cascade dan orphan removal. Ini adalah relasi tim many-to-many yang direalisasikan lewat entity penghubung karena tabel tersebut juga menyimpan `nominal` per karyawan. `User` memiliki opsional `Jabatan`, dan satu user dapat memiliki banyak record attendance.

**Sumber:** `backend/src/main/java/com/apoffice/backend/{controller,service,repository,entity}`.
