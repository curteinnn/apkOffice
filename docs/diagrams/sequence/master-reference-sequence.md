# Master Jabatan dan Jenis Tugas Sequence

**Tujuan:** Merangkum CRUD master referensi yang dikelola Admin Desktop.

```mermaid
sequenceDiagram
    actor Admin
    participant UI as MasterJabatan atau MasterJenisTugas
    participant API as JabatanApiService atau JenisTugasApiService
    participant Filter as JWT filter + SecurityConfig
    participant C as MasterController terkait
    participant S as MasterService terkait
    participant Repos as Repository master dan referensi
    participant DB as MariaDB

    alt Master Jabatan
        Admin->>UI: Buka master
        UI->>API: GET /api/jabatan
        API->>Filter: Bearer JWT
        Filter->>C: Otorisasi ADMIN
        C->>S: getAll(false)
        S->>Repos: findAllByOrderByNamaAsc
        Repos->>DB: SELECT jabatan
        DB-->>Repos: Daftar jabatan dan status
        Repos-->>S: Daftar jabatan
        S-->>C: Daftar jabatan
        C-->>API: JSON jabatan
        API-->>UI: Tampilkan tabel
        opt Tambah, ubah nama, atau ubah status
            Admin->>UI: Pilih aksi dan isi data
            UI->>API: POST /api/jabatan, PUT /api/jabatan/{id}, atau PUT /api/jabatan/{id}/status
            API->>Filter: Bearer JWT
            Filter->>C: Otorisasi ADMIN
            C->>S: create, update, atau updateStatus
            S->>S: Validasi nama/status dan duplikasi nama
            S->>Repos: save(jabatan)
            Repos->>DB: INSERT atau UPDATE jabatan
            DB-->>Repos: Jabatan tersimpan
            Repos-->>S: Jabatan terbaru
            S-->>C: JSON atau HTTP error
            C-->>API: Response
            API-->>UI: Tampilkan hasil dan muat ulang
        end
        opt Hapus jabatan
            Admin->>UI: Konfirmasi hapus
            UI->>API: DELETE /api/jabatan/{id}
            API->>Filter: Bearer JWT
            Filter->>C: Otorisasi ADMIN
            C->>S: delete(id)
            S->>Repos: existsByJabatanRef_Id(id)
            Repos->>DB: Periksa relasi user
            alt Masih dipakai user
                DB-->>Repos: Relasi ditemukan
                S-->>C: 409, sarankan nonaktifkan jabatan
            else Tidak dipakai
                S->>Repos: delete(jabatan)
                Repos->>DB: DELETE jabatan
                DB-->>Repos: Terhapus
                S-->>C: Berhasil
            end
            C-->>API: Response atau error
            API-->>UI: Tampilkan hasil dan muat ulang
        end
    else Master Jenis Tugas
        Admin->>UI: Buka master
        UI->>API: GET /api/jenis-tugas
        API->>Filter: Bearer JWT
        Filter->>C: Otorisasi ADMIN
        C->>S: getAll()
        S->>Repos: findAllByOrderByIdAsc
        Repos->>DB: SELECT jenis_tugas
        DB-->>Repos: Daftar ID dan nama
        Repos-->>S: Daftar jenis tugas
        S-->>C: Daftar jenis tugas
        C-->>API: JSON jenis tugas
        API-->>UI: Tampilkan tabel
        opt Tambah atau ubah nama
            Admin->>UI: Isi ID dan nama saat tambah, atau nama saat ubah
            UI->>API: POST /api/jenis-tugas atau PUT /api/jenis-tugas/{id}
            API->>Filter: Bearer JWT
            Filter->>C: Otorisasi ADMIN
            C->>S: create atau update
            S->>S: Validasi ID, nama, dan duplikasi
            S->>Repos: save(jenisTugas)
            Repos->>DB: INSERT atau UPDATE jenis_tugas
            DB-->>Repos: Jenis tugas tersimpan
            Repos-->>S: Jenis tugas terbaru
            S-->>C: JSON atau HTTP error
            C-->>API: Response
            API-->>UI: Tampilkan hasil dan muat ulang
        end
        opt Hapus jenis tugas
            Admin->>UI: Konfirmasi hapus
            UI->>API: DELETE /api/jenis-tugas/{id}
            API->>Filter: Bearer JWT
            Filter->>C: Otorisasi ADMIN
            C->>S: delete(id)
            S->>Repos: existsByJenisTugas_Id(id)
            Repos->>DB: Periksa relasi tugas
            alt Masih digunakan tugas
                DB-->>Repos: Relasi ditemukan
                S-->>C: 409, penghapusan ditolak
            else Tidak digunakan
                S->>Repos: delete(jenisTugas)
                Repos->>DB: DELETE jenis_tugas
                DB-->>Repos: Terhapus
                S-->>C: Berhasil
            end
            C-->>API: Response atau error
            API-->>UI: Tampilkan hasil dan muat ulang
        end
    end
```

**Interpretasi:** Jabatan mendukung tambah, ubah nama, ubah status, dan hapus jika belum direferensikan user. Jenis tugas mendukung tambah, ubah nama, dan hapus jika belum direferensikan tugas; tidak ada endpoint status jenis tugas.
