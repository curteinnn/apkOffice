# Attendance Sequence

**Tujuan:** Menelusuri request absensi Employee Web hingga repository dan database, termasuk aturan check-out.

```mermaid
sequenceDiagram
    actor Employee
    participant UI as Dashboard useAttendance
    participant API as api.js
    participant Filter as JWT filter
    participant C as AttendanceController
    participant S as AttendanceService
    participant AR as AttendanceRepository
    participant TR as TugasRepository
    participant DB as MariaDB

    Employee->>UI: Buka dashboard / tekan tombol absensi
    UI->>API: GET /api/attendance/my
    API->>Filter: GET + Bearer token
    Filter->>C: User dan role terautentikasi
    C->>S: getMyAttendance(username)
    S->>AR: findByUserOrderByTanggalDesc(user)
    AR->>DB: Baca attendance milik user
    DB-->>AR: List attendance
    AR-->>S: List attendance
    S-->>C: List attendance
    C-->>API: JSON list
    API-->>UI: JSON list

    alt Check-in
        Employee->>UI: Tekan Check In
        UI->>API: POST /api/attendance/check-in
        API->>Filter: Token
        Filter->>C: Identitas request
        C->>C: Pastikan role EMPLOYEE
        C->>S: checkIn(username)
        S->>AR: findByUserAndTanggal(user, tanggal WIB hari ini)
        AR->>DB: Cari attendance hari ini
        alt Record sudah ada
            DB-->>AR: Record ditemukan
            AR-->>S: Attendance ada
            S-->>C: 400 sudah check-in hari ini
            C-->>API: Error JSON
            API-->>UI: Tampilkan error
        else Record belum ada
            DB-->>AR: Kosong
            AR-->>S: Tidak ada attendance
            S->>AR: save(user, tanggal, jamMasuk, PRESENT)
            AR->>DB: INSERT attendance
            DB-->>AR: Attendance tersimpan
            AR-->>S: Attendance tersimpan
            S-->>C: Attendance tersimpan
            C-->>API: JSON attendance
            API-->>UI: JSON attendance
        end
    else Check-out
        Employee->>UI: Tekan Check Out
        UI->>API: POST /api/attendance/check-out
        API->>Filter: Token
        Filter->>C: Identitas request
        C->>C: Pastikan role EMPLOYEE
        C->>S: checkOut(username)
        S->>AR: findByUserAndTanggal(user, hari ini)
        AR->>DB: Cari attendance hari ini
        alt Tidak check-in atau sudah check-out
            DB-->>AR: Kosong atau jamKeluar terisi
            AR-->>S: Attendance tidak aktif
            S-->>C: 400 check-in diperlukan / sudah checkout
            C-->>API: Error JSON
            API-->>UI: Tampilkan error
        else Attendance aktif
            DB-->>AR: Attendance jamKeluar null
            AR-->>S: Attendance aktif
            S->>TR: findAssignedToOnDate(user, hariIni)
            TR->>DB: SELECT tugas join penugasan_karyawan untuk user dan tanggal hari ini
            DB-->>TR: Tugas user pada tanggal check-in
            TR-->>S: Daftar tugas hari ini
            S->>S: Filter status PENDING atau IN_PROGRESS
            alt Ada tugas PENDING/IN_PROGRESS
                S-->>C: 400 jumlah dan judul tugas yang masih aktif
                C-->>API: Error JSON
                API-->>UI: Teruskan message backend
                UI-->>Employee: Tampilkan judul dan status tugas yang menahan check-out
            else Tidak ada tugas aktif yang memblokir
                S->>AR: Set jamKeluar WIB dan save
                AR->>DB: UPDATE attendance
                DB-->>AR: Attendance check-out berhasil
                AR-->>S: Attendance tersimpan
                S-->>C: Attendance tersimpan
                C-->>API: JSON attendance
                API-->>UI: JSON attendance
                UI-->>Employee: Check-out berhasil
            end
        end
    end
```

**Interpretasi:** Tugas dihitung per karyawan dan hanya jika `tugas.tanggal` sama dengan tanggal check-in hari ini. Status `PENDING` dan `IN_PROGRESS` menahan check-out; `SUBMITTED`, `APPROVED`, dan `REJECTED` tidak. Jika tidak ada tugas pada tanggal itu, check-out tetap boleh. Pesan penolakan memuat jumlah, judul, dan status tugas penghambat. Tanggal tugas adalah tanggal rencana, bukan tanggal pembuatan atau deadline; status tugas bersama berlaku bagi seluruh assignee.
