# Object Diagram — Contoh Tim Tugas

**Tujuan:** Memberikan snapshot object dummy untuk relasi user, jabatan, attendance, tugas, dan nominal assignment. Nama/email serta ID berikut hanya contoh dan bukan data database.

```mermaid
flowchart LR
    J1["jabatanTeknisi : Jabatan<br/>id = 7<br/>nama = Teknisi<br/>status = ACTIVE"]
    U1["employeeA : User<br/>id = 700001<br/>username = teknisi.a<br/>role = EMPLOYEE<br/>status = ACTIVE"]
    U2["employeeB : User<br/>id = 700002<br/>username = teknisi.b<br/>role = EMPLOYEE<br/>status = ACTIVE"]
    A1["attendanceA : Attendance<br/>id = 21<br/>tanggal = 2026-11-23<br/>jamMasuk = 2026-11-23T08:00<br/>jamKeluar = null<br/>status = PRESENT"]
    A2["attendanceB : Attendance<br/>id = 22<br/>tanggal = 2026-11-23<br/>jamMasuk = 2026-11-23T08:10<br/>jamKeluar = 2026-11-23T17:00<br/>status = PRESENT"]
    JT1["jenis : JenisTugas<br/>id = INT-001<br/>nama = Instalasi"]
    T1["task : Tugas<br/>id = TASK-2026-0001<br/>judul = Pemasangan unit AC<br/>status = APPROVED<br/>tanggal = 2026-11-23"]
    P1["assignmentA : PenugasanKaryawan<br/>key = TASK-2026-0001 + 700001<br/>nominal = 250000.00"]
    P2["assignmentB : PenugasanKaryawan<br/>key = TASK-2026-0001 + 700002<br/>nominal = 150000.00"]

    U1 -->|jabatanRef| J1
    U2 -->|jabatanRef| J1
    U1 -->|user| A1
    U2 -->|user| A2
    T1 -->|jenisTugas| JT1
    T1 -->|assignment collection| P1
    T1 -->|assignment collection| P2
    P1 -->|karyawan| U1
    P2 -->|karyawan| U2
```

**Interpretasi:** Satu task tim direpresentasikan oleh satu `Tugas` dan dua row `PenugasanKaryawan`. `nominal` berbeda per anggota; status `APPROVED` hanya berada pada task. Attendance adalah object masing-masing user dan field `jamKeluar` dapat null sebelum checkout.
