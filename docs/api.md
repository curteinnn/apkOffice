# API Documentation

Dokumentasi REST API untuk aplikasi apkOffice.

## Base URL

```text
http://localhost:8080
```

## Authentication

API menggunakan JWT (JSON Web Token).

Login terlebih dahulu melalui:

```http
POST /api/auth/login
```

Setelah berhasil login, gunakan token pada endpoint yang membutuhkan autentikasi:

```http
Authorization: Bearer <token>
```

---

# Authentication

## Login

Login menggunakan username dan password.

**Method**

```http
POST /api/auth/login
```

**Request Body**

```json
{
  "username": "aldry",
  "password": "aldryyy"
}
```

**Response**

```json
{
  "id": 12345679,
  "username": "aldry",
  "namaLengkap": "Aldry",
  "email": "aldry@gmail.com",
  "jabatan": "Employee",
  "role": "EMPLOYEE",
  "foto": null,
  "status": "ACTIVE",
  "token": "JWT_TOKEN"
}
```

---

# Users

Base endpoint:

```text
/api/users
```

## Get All Users

Mengambil seluruh data user.

**Method**

```http
GET /api/users
```

**Authentication**

Bearer Token

**Response**

```json
[
  {
    "id": 12345679,
    "username": "aldry",
    "namaLengkap": "Aldry",
    "email": "aldry@gmail.com",
    "jabatan": "Employee",
    "role": "EMPLOYEE",
    "foto": null,
    "status": "ACTIVE"
  }
]
```

## Create User

Membuat user baru.

**Method**

```http
POST /api/users
```

**Authentication**

Bearer Token

**Request Body**

```json
{
  "username": "namauser",
  "password": "123",
  "namaLengkap": "Nama User",
  "email": "user@gmail.com",
  "jabatan": "Employee",
  "role": "EMPLOYEE"
}
```

## Update User

Mengubah data user.

**Method**

```http
PUT /api/users/{id}
```

**Authentication**

Bearer Token

**Request Body**

```json
{
  "username": "namauser",
  "namaLengkap": "Nama User",
  "email": "user@gmail.com",
  "jabatan": "Employee",
  "role": "EMPLOYEE",
  "status": "ACTIVE"
}
```

## Delete User

Menghapus user.

**Method**

```http
DELETE /api/users/{id}
```

**Authentication**

Bearer Token

---

# Jenis Tugas

## Get Jenis Tugas

Mengambil kategori tugas yang tersedia. Endpoint memerlukan Bearer Token.

```http
GET /api/jenis-tugas
```

Data awal: `INT-001` (Instalasi), `MNT-001` (Maintenance), dan `TRB-001`
(Perbaikan/Troubleshooting). Nilai `id` jenis tugas adalah string tetap dan
menjadi primary key yang direferensikan `tugas.jenis_tugas_id`.
Migrasi skema dijalankan manual melalui Navicat dari file SQL pada
`backend/src/main/resources/db/migration`. Untuk perubahan ID jenis tugas,
jalankan `V003__jenis_tugas_string_id.sql` setelah V001. Backend tidak
menjalankan migrasi otomatis; Hibernate tetap menggunakan `ddl-auto=none`.

Saat membuat tugas melalui `POST /api/tugas`, sertakan `jenisTugasId` dari
respons endpoint ini. Respons tugas menyertakan objek `jenisTugas`.

Admin dapat membuat satu tugas untuk beberapa karyawan melalui
`POST /api/tugas/bulk` dengan `userIds` berupa daftar ID karyawan. Semua assignee
mengacu pada ID tugas yang sama dan berbagi status serta hasil verifikasinya.
Daftar assignee disimpan pada tabel relasi `penugasan_karyawan`; terapkan manual
`V005__shared_tugas_assignees.sql` sebelum `V006__move_nominal_to_assignment.sql`.
Nominal disimpan per karyawan pada tabel relasi tersebut.

Primary key tugas berbentuk string `TASK-<tahun tanggal>-<nomor 4 digit>` dan
dibuat backend saat tugas disimpan. Nomornya berurutan per tahun tanggal tugas;
ID tersebut juga dipakai oleh `penugasan_karyawan.tugas_id` dan endpoint aksi
tugas. Terapkan manual `V008__string_task_ids.sql` setelah V007 agar tipe PK dan
FK sesuai serta tabel sequence tersedia.

---

# Data pelanggan pada tugas

Tidak ada master pelanggan atau endpoint `/api/pelanggan`. Admin mengisi
`namaPelanggan`, `lokasi`, dan `linkGmaps` langsung saat membuat tugas. Nilai
tersebut disimpan pada tugas dan ditampilkan di Employee Web. Migrasi manual
`V007__task_customer_inline.sql` menyalin nama serta lokasi lama dari tabel
pelanggan ke tugas, lalu menghapus relasi dan tabel lama. Jalankan setelah V006
dan setelah migrasi lama untuk tabel pelanggan (termasuk V004 jika digunakan).

---

# Attendance

Base endpoint:

```text
/api/attendance
```

## Get All Attendance

Mengambil data absensi.

**Method**

```http
GET /api/attendance
```

**Authentication**

Bearer Token

## Check In

Melakukan absensi masuk.

**Method**

```http
POST /api/attendance/check-in
```

**Authentication**

Bearer Token

Tidak membutuhkan request body.

User diambil berdasarkan username dari JWT token.

## Check Out

Melakukan absensi keluar.

**Method**

```http
POST /api/attendance/check-out
```

**Authentication**

Bearer Token

Tidak membutuhkan request body.

User diambil berdasarkan username dari JWT token.

---

# Tugas

Base endpoint:

```text
/api/tugas
```

Status tugas:

```text
PENDING
IN_PROGRESS
SUBMITTED
APPROVED
REJECTED
```

## Get Tugas

Employee hanya mendapatkan tugas miliknya sendiri.

Admin mendapatkan seluruh tugas.

**Method**

```http
GET /api/tugas
```

**Authentication**

Bearer Token

### Employee

Response hanya berisi tugas milik employee yang sedang login.

### Admin

Response berisi seluruh tugas.

## Create Tugas

Admin dapat membuat tugas untuk satu karyawan melalui endpoint ini. Untuk tugas
yang diberikan ke beberapa karyawan sekaligus, gunakan `POST /api/tugas/bulk`.

**Method**

```http
POST /api/tugas
```

**Role**

```text
ADMIN
```

**Authentication**

Bearer Token

**Request Body**

```json
{
  "userId": 12345681,
  "jenisTugasId": "INT-001",
  "namaPelanggan": "Pelanggan Rumah 1",
  "judul": "Membuat laporan",
  "lokasi": "Alamat lokasi pekerjaan",
  "linkGmaps": "https://maps.google.com/...",
  "deskripsi": "Membuat laporan kegiatan hari ini",
  "tanggal": "2026-11-16"
}
```

`namaPelanggan`, alamat `lokasi`, dan `linkGmaps` tersimpan langsung pada record
tugas. Untuk penugasan bersama, request bulk memakai `userIds` dan field tugas
yang sama.

Tugas baru otomatis memiliki:

```text
status = PENDING
```

## Verify Tugas

Admin melakukan verifikasi tugas employee.

**Method**

```http
PUT /api/tugas/{id}/verify
```

**Role**

```text
ADMIN
```

**Authentication**

Bearer Token

Untuk menyetujui tugas, kirim nominal untuk setiap ID karyawan yang ditugaskan:

```json
{
  "status": "APPROVED",
  "nominalByAssignee": {
    "12345681": 50000,
    "12345682": 45000
  }
}
```

Untuk menolak tugas:

```json
{
  "status": "REJECTED",
  "nominalByAssignee": null
}
```

Jika status `APPROVED`, nominal wajib diisi untuk seluruh assignee dan nilainya
tidak boleh negatif.

Jika status `REJECTED`, nominal seluruh assignee akan dikosongkan.

**Contoh Response**

```json
{
  "id": 3,
  "judul": "Membuat laporan",
  "deskripsi": "Membuat laporan kegiatan hari ini",
  "fotoBukti": "foto/laporan.jpg",
  "status": "APPROVED",
  "nominal": 50000,
  "tanggal": "2026-09-14"
}
```

---

# Roles

## ADMIN

Admin dapat melakukan proses yang membutuhkan role administrator, seperti:

- Verifikasi tugas
- Mengelola data user
- Melihat data tugas

## EMPLOYEE

Employee dapat melakukan:

- Login
- Check-in
- Check-out
- Membuat tugas
- Melihat tugas miliknya

---

# Error Response

Format error API:

```json
{
  "status": 400,
  "message": "Pesan error"
}
```

Status HTTP yang digunakan:

| Status | Keterangan |
|---|---|
| 400 | Request tidak valid |
| 401 | Token tidak valid atau belum login |
| 403 | Tidak memiliki akses |
| 404 | Data tidak ditemukan |

Contoh duplicate check-in:

```json
{
  "status": 400,
  "message": "Anda sudah melakukan check-in hari ini"
}
```

---

# Development

Backend dijalankan menggunakan Spring Boot.

**Port**

```text
8080
```

**Run**

```cmd
mvnw.cmd spring-boot:run
```

Database menggunakan MariaDB/MySQL.

Nama database:

```text
myin
```

---

# Notes

- Client seperti Admin Swing dan Employee Web berkomunikasi dengan database melalui REST API.
- Client tidak melakukan koneksi langsung ke database.
- Endpoint yang membutuhkan autentikasi menggunakan JWT.
- Role digunakan untuk membatasi akses endpoint tertentu.
- Payroll masih dalam tahap perancangan dan belum termasuk dalam dokumentasi API.
