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
        TaskC-->>UI: JSON tugas termasuk alasanPenolakan
        UI-->>Employee: Tampilkan kartu tugas, anggota tim, status, dan tanggal rencana
        alt Status PENDING
            Employee->>UI: Tekan Mulai Tugas
            UI->>API: PUT /api/tugas/{id}/start
            API->>Filter: Bearer JWT
            Filter->>TaskC: Otorisasi EMPLOYEE
            TaskC->>Attendance: Pastikan check-in aktif hari ini
            Attendance->>DB: SELECT attendance user hari ini
            DB-->>Attendance: jamMasuk terisi dan jamKeluar null
            TaskC->>TaskS: startTugas(id, username)
            TaskS->>Tasks: findById(id)
            Tasks->>DB: SELECT tugas dan assignees
            DB-->>Tasks: Tugas PENDING
            TaskS->>TaskS: Validasi assignee dan status PENDING
            TaskS->>Tasks: Ubah status bersama menjadi IN_PROGRESS
            Tasks->>DB: UPDATE tugas
            DB-->>Tasks: Tugas IN_PROGRESS
            Tasks-->>TaskS: Tugas terbaru
            TaskS-->>TaskC: Tugas terbaru
            TaskC-->>API: JSON tugas
            API-->>UI: Tugas terbaru
            UI-->>Employee: Perbarui status
        else Status IN_PROGRESS
            Employee->>UI: Tekan Selesaikan Tugas
            UI->>API: PUT /api/tugas/{id}/submit
            API->>Filter: Bearer JWT
            Filter->>TaskC: Otorisasi EMPLOYEE
            TaskC->>Attendance: Pastikan check-in aktif hari ini
            Attendance->>DB: SELECT attendance user hari ini
            DB-->>Attendance: jamMasuk terisi dan jamKeluar null
            TaskC->>TaskS: submitTugas(id, username)
            TaskS->>Tasks: findById(id)
            Tasks->>DB: SELECT tugas dan assignees
            DB-->>Tasks: Tugas IN_PROGRESS
            TaskS->>TaskS: Validasi assignee dan status IN_PROGRESS
            TaskS->>Tasks: Ubah status bersama menjadi SUBMITTED
            Tasks->>DB: UPDATE tugas
            DB-->>Tasks: Tugas SUBMITTED
            Tasks-->>TaskS: Tugas terbaru
            TaskS-->>TaskC: Tugas terbaru
            TaskC-->>API: JSON tugas
            API-->>UI: Tugas terbaru
            UI-->>Employee: Tampilkan menunggu verifikasi admin
        else Status REJECTED
            UI-->>Employee: Tampilkan alasan penolakan tanpa tombol kirim ulang
            Note over Employee,UI: Admin perlu mengedit dan menyimpan jadwal ulang agar status kembali ke PENDING
        else Status SUBMITTED atau APPROVED
            UI-->>Employee: Tampilkan status dan tanpa aksi tugas
        end
    end
    Note over UI,TaskS: Tanggal adalah tanggal rencana. Start dan submit tidak dibatasi ke tanggal itu sehingga tugas dapat dikerjakan lebih awal.
```

**Interpretasi:** Sebelum memuat tugas, UI membaca attendance pribadi lalu memastikan ada check-in hari ini. Controller juga mensyaratkan record check-in hari ini saat `GET /api/tugas`, tetapi tetap mengizinkan karyawan yang sudah check-out melihat daftar. Aksi start/submit memerlukan attendance aktif dan karyawan harus menjadi assignee. UI menampilkan tombol mulai pada `PENDING` dan tombol kirim pada `IN_PROGRESS`; pada `REJECTED`, UI menampilkan alasan penolakan tanpa tombol kirim ulang. Admin mengubah tugas yang ditolak melalui alur penjadwalan ulang. Tanggal rencana tidak membatasi karyawan untuk memulai lebih awal. Status pada entity `Tugas` berlaku bagi semua assignee. Implementasi service masih menerima `REJECTED` pada endpoint submit jika dipanggil langsung, meskipun Employee Web tidak menampilkan tombol untuk status itu.
