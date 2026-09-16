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
  "password": "123"
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

Membuat tugas baru.

**Method**

```http
POST /api/tugas
```

**Role**

```text
EMPLOYEE
```

**Authentication**

Bearer Token

**Request Body**

```json
{
  "judul": "Membuat laporan",
  "deskripsi": "Membuat laporan kegiatan hari ini",
  "fotoBukti": "foto/laporan.jpg"
}
```

Tugas baru otomatis memiliki:

```text
status  = PENDING
nominal = null
tanggal = tanggal saat tugas dibuat
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

Untuk menyetujui tugas:

```json
{
  "status": "APPROVED",
  "nominal": 50000
}
```

Untuk menolak tugas:

```json
{
  "status": "REJECTED",
  "nominal": null
}
```

Jika status `APPROVED`, nominal wajib diisi.

Jika status `REJECTED`, nominal akan menjadi `null`.

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
