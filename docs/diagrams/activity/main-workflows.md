# Main Workflows Activity Diagrams

## Absensi karyawan

**Tujuan:** Memperlihatkan check-in, check-out, dan prasyarat check-out yang diterapkan service.

```mermaid
flowchart TD
    S([Mulai]) --> User[Employee memilih check-in atau check-out]
    User --> Auth[JWT filter mengautentikasi token]
    Auth --> Role{Role EMPLOYEE?}
    Role -->|Tidak| Deny[403 Forbidden]
    Role -->|Ya| Action{Aksi?}
    Action -->|Check-in| Existing[Cari attendance user untuk tanggal Asia/Jakarta hari ini]
    Existing --> HasToday{Sudah ada record?}
    HasToday -->|Ya| Duplicate[Tolak: sudah check-in hari ini]
    HasToday -->|Tidak| SaveIn[Simpan tanggal, jamMasuk, status PRESENT]
    Action -->|Check-out| GetToday[Cari attendance user hari ini]
    GetToday --> HasRecord{Record ada dan belum check-out?}
    HasRecord -->|Tidak ada| NoCheckin[Tolak: check-in terlebih dahulu]
    HasRecord -->|Sudah checkout| DuplicateOut[Tolak: sudah check-out]
    HasRecord -->|Ya| Assigned[Ambil semua tugas assigned user]
    Assigned --> AnyDone{Ada status SUBMITTED atau APPROVED?}
    AnyDone -->|Tidak| NeedTask[Tolak check-out]
    AnyDone -->|Ya| SaveOut[Simpan jamKeluar Asia/Jakarta]
    SaveIn --> Reply[Response attendance]
    SaveOut --> Reply
    Deny --> E([Selesai])
    Duplicate --> E
    NoCheckin --> E
    DuplicateOut --> E
    NeedTask --> E
    Reply --> E
```

**Interpretasi:** `jamKeluar` tidak diisi saat check-in. Pemeriksaan tugas saat check-out memakai semua tugas hasil `findAssignedTo(user)` tanpa batas tanggal; status accepted adalah `SUBMITTED` atau `APPROVED`.

## Siklus tugas tim

**Tujuan:** Menunjukkan pembuatan/penugasan, aksi Employee, dan verifikasi Admin.

```mermaid
flowchart TD
    S([Admin mulai]) --> Select[Admin memilih satu atau beberapa employee]
    Select --> Create[POST /api/tugas atau /api/tugas/bulk]
    Create --> Validate[Validasi ID, data wajib, jenis tugas, user ada dan role EMPLOYEE]
    Validate --> Valid{Valid?}
    Valid -->|Tidak| CreateError[Response error]
    Valid -->|Ya| Generate[Generate ID TASK-tahun-nomor]
    Generate --> Persist[Simpan Tugas PENDING dan assignment tim]
    Persist --> Work[Employee memiliki check-in hari ini dan membuka daftar]
    Work --> Start[Employee mulai: PENDING menjadi IN_PROGRESS]
    Start --> Submit[Employee kirim: IN_PROGRESS atau REJECTED menjadi SUBMITTED]
    Submit --> AdminReview[Admin memverifikasi tugas SUBMITTED]
    AdminReview --> Approved{Keputusan Admin?}
    Approved -->|Setuju| Amount[Nominal wajib untuk setiap assignee; nilai tidak negatif]
    Amount --> Approve[Status Tugas menjadi APPROVED]
    Approved -->|Tolak| Reject[Status Tugas menjadi REJECTED; nominal di-null-kan]
    Reject --> EmployeeEdit[Employee dapat mengirim ulang; Admin dapat mengubah data]
    EmployeeEdit --> Submit
    Approve --> E([Selesai])
    CreateError --> E
```

**Interpretasi:** Satu `Tugas` mempunyai satu status bersama untuk semua anggota; nominal disimpan satu nilai per `PenugasanKaryawan`. Perubahan status oleh satu anggota memengaruhi object tugas yang sama untuk anggota tim lain.

## Data master dan laporan Admin

**Tujuan:** Merangkum layar Admin yang menggunakan endpoint CRUD dan laporan aktual.

```mermaid
flowchart LR
    A["Admin Desktop"] --> Auth["JWT dan role ADMIN"]
    Auth --> CRUD{"Fitur master?"}
    CRUD -->|Karyawan| Users["CRUD /api/users"]
    CRUD -->|Jabatan| Jabatan["CRUD /api/jabatan"]
    CRUD -->|Jenis tugas| Jenis["CRUD /api/jenis-tugas"]
    Auth --> Transaction{"Fitur transaksi?"}
    Transaction -->|Kelola tugas| Tugas["GET, POST bulk, PUT, DELETE /api/tugas"]
    Transaction -->|Verifikasi| Verify["PUT /api/tugas/{id}/verify"]
    Transaction -->|Absensi| Attendance["GET /api/attendance"]
    Auth --> Report{"Laporan?"}
    Report -->|Daftar tugas| Tugas
    Report -->|Rekap tugas dan nominal| Tugas
    Report -->|Rekap absensi| Attendance
    Users --> API["Backend controller, service, repository"]
    Jabatan --> API
    Jenis --> API
    Tugas --> API
    Verify --> API
    Attendance --> API
    API --> DB[("MariaDB")]
    API -->|Response JSON| A
```

**Interpretasi:** Empat report Admin tidak memakai endpoint report terpisah. Report tugas dan rekap produktivitas/nominal mengambil tugas dari API; rekap absensi mengambil data absensi. Ringkasan dihitung dan dirender di desktop.
