# Employee Task Sequence

**Tujuan:** Menelusuri pemuatan daftar tugas serta aksi mulai/kirim dari Employee Web.

```mermaid
sequenceDiagram
    actor Employee
    participant UI as ListTugas useListTugas
    participant API as api.js
    participant Filter as JWT filter + SecurityConfig
    participant AttendanceC as AttendanceController
    participant AttendanceS as AttendanceService
    participant TaskC as TugasController
    participant TaskS as TugasService
    participant Attendance as AttendanceRepository
    participant Tasks as TugasRepository
    participant DB as MariaDB

    Employee->>UI: Buka Daftar Tugas
    UI->>API: GET /api/attendance/my
    API->>Filter: Bearer JWT
    Filter->>AttendanceC: Autentikasi user
    AttendanceC->>AttendanceS: getMyAttendance(username)
    AttendanceS->>Attendance: findByUserOrderByTanggalDesc(user)
    Attendance->>DB: SELECT attendance user
    DB-->>Attendance: List attendance
    Attendance-->>AttendanceS: List attendance
    AttendanceS-->>AttendanceC: List attendance
    AttendanceC-->>API: JSON riwayat
    API-->>UI: JSON riwayat
    UI->>UI: Cari tanggal Asia/Jakarta hari ini
    alt Belum ada jam masuk hari ini
        UI-->>Employee: Tampilkan access denied
    else Sudah check-in hari ini
        UI->>API: GET /api/tugas
        API->>Filter: Bearer JWT
        Filter->>TaskC: username dan role dari token
        TaskC->>Attendance: Cari attendance hari ini
        Attendance->>DB: SELECT user + tanggal hari ini
        DB-->>TaskC: Attendance ditemukan
        TaskC->>TaskS: getAllTugas(username, EMPLOYEE)
        TaskS->>Tasks: findAssignedTo(user)
        Tasks->>DB: SELECT tugas join penugasan_karyawan
        DB-->>Tasks: Tugas dan assignees
        Tasks-->>TaskS: List tugas
        TaskS-->>TaskC: List tugas
        TaskC-->>UI: JSON tugas
        UI-->>Employee: Tampilkan kartu tugas dan anggota tim
        alt Mulai ketika PENDING
            Employee->>UI: Mulai tugas
            UI->>API: PUT /api/tugas/{id}/start
        else Kirim ketika IN_PROGRESS atau REJECTED
            Employee->>UI: Selesaikan tugas
            UI->>API: PUT /api/tugas/{id}/submit
        end
        API->>Filter: Token dan role EMPLOYEE
        Filter->>TaskC: Otorisasi request
        TaskC->>Attendance: Cari attendance hari ini, wajib jamMasuk dan jamKeluar null
        Attendance->>DB: SELECT attendance user hari ini
        DB-->>Attendance: Record / kosong
        Attendance-->>TaskC: Attendance hari ini
        alt Tidak ada check-in atau sudah check-out
            TaskC-->>API: 403 Forbidden
            API-->>UI: Tampilkan error
            UI-->>Employee: Aksi tidak diizinkan
        else Attendance aktif
            TaskC->>TaskS: startTugas/submitTugas(id, username)
            TaskS->>Tasks: findById(id)
            Tasks->>DB: SELECT tugas dan assignees
            DB-->>Tasks: Tugas
            Tasks-->>TaskS: Tugas
            TaskS->>TaskS: Pastikan username assignee dan transisi status sah
            alt Bukan assignee atau status tidak sah
                TaskS-->>TaskC: 403/400
                TaskC-->>API: Error response
                API-->>UI: Tampilkan error
                UI-->>Employee: Aksi gagal
            else Diizinkan
                TaskS->>Tasks: Ubah status task bersama dan save
                Tasks->>DB: UPDATE tugas
                DB-->>Tasks: Tugas terbaru
                Tasks-->>TaskS: Tugas terbaru
                TaskS-->>TaskC: Tugas terbaru
                TaskC-->>API: JSON tugas terbaru
                API-->>UI: Tugas terbaru
                UI-->>Employee: Perbarui status
            end
        end
    end
```

**Interpretasi:** Sebelum memuat tugas, UI membaca attendance pribadi lalu memastikan ada check-in hari ini. Controller mengulang pemeriksaan attendance untuk daftar tugas, dan memeriksa attendance aktif (belum checkout) untuk aksi start/submit. Perubahan status pada entity `Tugas` berlaku pada semua assignee.
