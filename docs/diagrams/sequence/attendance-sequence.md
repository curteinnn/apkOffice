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
            S->>TR: findAssignedTo(user)
            TR->>DB: Baca semua tugas assigned user
            DB-->>TR: List tugas
            TR-->>S: List tugas (tanpa filter tanggal)
            alt Tidak ada status SUBMITTED/APPROVED
                S-->>C: 400 tugas harus dikirim atau disetujui
                C-->>API: Error JSON
                API-->>UI: Tampilkan error
            else Ada tugas SUBMITTED/APPROVED
                S->>AR: Set jamKeluar WIB dan save
                AR->>DB: UPDATE attendance
                DB-->>AR: Attendance check-out berhasil
                AR-->>S: Attendance tersimpan
                S-->>C: Attendance tersimpan
                C-->>API: JSON attendance
                API-->>UI: JSON attendance
            end
        end
    end
```

**Interpretasi:** UI membaca riwayat untuk menemukan record tanggal hari ini. Guard frontend memudahkan UX, sementara role dan aturan utama ditegakkan controller/service. Backend tidak membuat checkout otomatis lintas hari pada alur yang ditelusuri ini.
