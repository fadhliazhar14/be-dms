# Bank DKI DMS API Documentation

## Overview
Bank DKI Document Management System (DMS) REST API untuk mengelola user, role, customer, dan sistem administrasi.

**Base URL**: `http://localhost:8080`  
**Authentication**: JWT Token (Bearer)  
**Content-Type**: `application/json`

---

## 🔐 Authentication Endpoints

### 1. User Login
Melakukan login dan mendapatkan JWT token.

**Endpoint**: `POST /api/auth/signin`

**Request Body**:
```json
{
  "userEmail": "admin@bankdki.com",
  "userHashPassword": "password123"
}
```

**Response Success (200)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "email": "admin@bankdki.com",
  "role": "ADMIN"
}
```

**Response Error (400)**:
```json
{
  "message": "Error: Invalid credentials"
}
```

### 2. User Registration
Mendaftarkan user baru.

**Endpoint**: `POST /api/auth/signup`

**Request Body**:
```json
{
  "userName": "john_doe",
  "userEmail": "john@bankdki.com",
  "userHashPassword": "password123",
  "roleId": 2,
  "userTglLahir": "1990-01-15",
  "userJabatan": "Customer Service",
  "userTempatLahir": "Jakarta"
}
```

**Response Success (200)**:
```json
{
  "message": "User registered successfully!"
}
```

---

## 👥 User Management Endpoints

### 3. Get All Users
Mengambil semua data user (Admin only).

**Endpoint**: `GET /api/admin/users`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN

**Response Success (200)**:
```json
[
  {
    "userId": 1,
    "userName": "admin",
    "userEmail": "admin@bankdki.com",
    "userCreateAt": "2024-01-15T10:00:00",
    "userUpdateAt": "2024-01-15T10:00:00",
    "userIsActive": true,
    "roleId": 1,
    "roleName": "ADMIN",
    "userJabatan": "Administrator",
    "userTglLahir": "1985-01-01",
    "userTempatLahir": "Jakarta"
  }
]
```

### 4. Get Active Users
Mengambil user yang aktif saja.

**Endpoint**: `GET /api/admin/users/active`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN or OPERATOR

### 5. Get User by ID
Mengambil detail user berdasarkan ID.

**Endpoint**: `GET /api/admin/users/{id}`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN

**Response Success (200)**:
```json
{
  "userId": 1,
  "userName": "admin",
  "userEmail": "admin@bankdki.com",
  "userIsActive": true,
  "roleId": 1,
  "roleName": "ADMIN"
}
```

### 6. Create User
Membuat user baru (Admin only).

**Endpoint**: `POST /api/admin/users`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN

**Request Body**:
```json
{
  "userName": "jane_doe",
  "userEmail": "jane@bankdki.com",
  "userHashPassword": "password123",
  "roleId": 2,
  "userTglLahir": "1992-05-20",
  "userJabatan": "Teller",
  "userTempatLahir": "Bandung",
  "userIsActive": true
}
```

### 7. Update User
Mengupdate data user (Admin only).

**Endpoint**: `PUT /api/admin/users/{id}`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN

**Request Body**:
```json
{
  "userName": "jane_smith",
  "userEmail": "jane.smith@bankdki.com",
  "roleId": 1,
  "userTglLahir": "1992-05-20",
  "userJabatan": "Supervisor",
  "userTempatLahir": "Bandung",
  "userIsActive": true
}
```

### 8. Delete User
Menghapus user (Admin only).

**Endpoint**: `DELETE /api/admin/users/{id}`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN

### 9. Activate/Deactivate User
Mengaktifkan atau menonaktifkan user.

**Endpoints**:
- `PATCH /api/admin/users/{id}/activate` - Aktivasi user
- `PATCH /api/admin/users/{id}/deactivate` - Nonaktifkan user

**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN

---

## 🏷️ Role Management Endpoints

### 10. Get All Roles
Mengambil semua role (Admin only).

**Endpoint**: `GET /api/admin/roles`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN

**Response Success (200)**:
```json
[
  {
    "roleId": 1,
    "roleName": "ADMIN",
    "roleIsActive": true,
    "roleCreateDate": "2024-01-15T10:00:00",
    "roleCreateBy": "SYSTEM"
  },
  {
    "roleId": 2,
    "roleName": "OPERATOR",
    "roleIsActive": true,
    "roleCreateDate": "2024-01-15T10:00:00",
    "roleCreateBy": "SYSTEM"
  }
]
```

### 11. Get Active Roles
Mengambil role yang aktif.

**Endpoint**: `GET /api/admin/roles/active`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN or OPERATOR

### 12. Create Role
Membuat role baru (Admin only).

**Endpoint**: `POST /api/admin/roles`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN

**Request Body**:
```json
{
  "roleName": "MANAGER",
  "roleIsActive": true,
  "roleCreateBy": "admin"
}
```

### 13. Update Role
Mengupdate role (Admin only).

**Endpoint**: `PUT /api/admin/roles/{id}`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN

### 14. Assign Role to User
Menugaskan role ke user.

**Endpoint**: `POST /api/admin/roles/assign`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN

**Request Parameters**:
- `userId` (Short): ID user
- `roleId` (Short): ID role

---

## 👤 Customer Management Endpoints

### 15. Get All Customers
Mengambil semua data customer yang aktif.

**Endpoint**: `GET /api/customers`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN or OPERATOR

**Response Success (200)**:
```json
[
  {
    "custId": 1,
    "custCifNumber": "CIF001",
    "custStatus": "ACTIVE",
    "custCabang": "Jakarta Pusat",
    "prsnNama": "Budi Santoso",
    "prsnJenisKelamin": "L",
    "cardNik": "3171234567890123",
    "adrsNomorHp": "081234567890",
    "adrsSurel": "budi@email.com",
    "custCreateDate": "2024-01-15T10:00:00"
  }
]
```

### 16. Get Customer by ID
Mengambil detail customer berdasarkan ID.

**Endpoint**: `GET /api/customers/{id}`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN or OPERATOR

### 17. Get Customer by CIF Number
Mengambil customer berdasarkan nomor CIF.

**Endpoint**: `GET /api/customers/cif/{cifNumber}`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN or OPERATOR

### 18. Get Customer by NIK
Mengambil customer berdasarkan NIK.

**Endpoint**: `GET /api/customers/nik/{nik}`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN or OPERATOR

### 19. Create Customer
Membuat data customer baru.

**Endpoint**: `POST /api/customers`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN or OPERATOR

**Request Body**:
```json
{
  "custCifNumber": "CIF002",
  "custStatus": "ACTIVE",
  "custCabang": "Jakarta Selatan",
  "custGolNasabah": "RETAIL",
  "prsnNama": "Siti Nurhaliza",
  "prsnJenisKelamin": "P",
  "prsnTanggalLahir": "1985-03-15",
  "prsnTempatLahir": "Jakarta",
  "cardNik": "3172345678901234",
  "cardNpwp": "123456789012345",
  "adrsNomorHp": "081987654321",
  "adrsSurel": "siti@email.com",
  "adrsProvinsi": "DKI Jakarta",
  "adrsKota": "Jakarta Selatan",
  "adrsAlamat1": "Jl. Sudirman No. 123",
  "workInstansi": "PT ABC",
  "workStatusPekerjaan": "Karyawan Swasta"
}
```

### 20. Update Customer
Mengupdate data customer.

**Endpoint**: `PUT /api/customers/{id}`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN or OPERATOR

### 21. Delete Customer
Menghapus customer (soft delete).

**Endpoint**: `DELETE /api/customers/{id}`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN

### 22. Search Customers
Mencari customer berdasarkan kriteria.

**Endpoints**:
- `GET /api/customers/status/{status}` - Cari berdasarkan status
- `GET /api/customers/cabang/{cabang}` - Cari berdasarkan cabang
- `GET /api/customers/search?nama={nama}` - Cari berdasarkan nama

---

## ⚙️ Setup & Admin Endpoints

### 23. Make User Admin
Menugaskan role admin ke user (untuk setup awal).

**Endpoint**: `POST /api/setup/make-admin`

**Request Parameters**:
- `email` (String): Email user yang akan dijadikan admin

**Response Success (200)**:
```json
{
  "message": "ADMIN role assigned to user: admin@bankdki.com"
}
```

### 24. Assign Admin Role
Menugaskan role admin ke user (endpoint admin).

**Endpoint**: `POST /api/admin/assign-admin-role`  
**Authorization**: `Bearer {jwt_token}`

**Request Parameters**:
- `email` (String): Email user

---

## 📋 HTTP Status Codes

| Code | Description |
|------|-------------|
| 200  | Success |
| 201  | Created |
| 400  | Bad Request |
| 401  | Unauthorized |
| 403  | Forbidden |
| 404  | Not Found |
| 500  | Internal Server Error |

---

## 🔒 Authentication Flow

1. **Register/Login**: Dapatkan JWT token melalui `/api/auth/signin`
2. **Include Token**: Sertakan token di header setiap request:
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
   ```
3. **Token Expiry**: Token berlaku selama 24 jam (86400000 ms)

## 🏗️ Data Model

### User-Role Relationship
- **One-to-One**: Setiap user memiliki **tepat 1 role**
- **Role Assignment**: Role ditentukan saat user dibuat dan dapat diubah oleh admin
- **Role Validation**: Sistem memvalidasi roleId saat create/update user

---

## 📝 Example Usage

### Login dan Akses Protected Endpoint

```bash
# 1. Login
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "userEmail": "admin@bankdki.com",
    "userHashPassword": "password123"
  }'

# Response akan memberikan token
# {
#   "token": "eyJhbGciOiJIUzI1NiJ9...",
#   "username": "admin",
#   "email": "admin@bankdki.com",
#   "role": "ADMIN"
# }

# 2. Gunakan token untuk akses endpoint protected
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### Create Customer

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "custCifNumber": "CIF003",
    "custStatus": "ACTIVE",
    "prsnNama": "John Doe",
    "prsnJenisKelamin": "L",
    "cardNik": "3173456789012345",
    "adrsNomorHp": "081234567890",
    "adrsSurel": "john@email.com"
  }'
```

---

## 📅 Schedule Management Endpoints

### 25. Get Operators by Date Grouped by Tasks
Mendapatkan daftar operator dari schedule pada tanggal tertentu dikelompokkan berdasarkan tasknya.

**Endpoint**: `GET /api/schedule/operators-by-date`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN or OPERATOR

**Request Parameters**:
- `date` (LocalDate, optional): Tanggal schedule (format: YYYY-MM-DD). Default: hari ini

**Response Success (200)**:
```json
[
  {
    "taskId": 1,
    "taskName": "Data Entry",
    "operators": [
      {
        "userId": 2,
        "userName": "john_doe",
        "userEmail": "john@bankdki.com",
        "userJabatan": "Operator"
      },
      {
        "userId": 3,
        "userName": "jane_smith",
        "userEmail": "jane@bankdki.com",
        "userJabatan": "Senior Operator"
      }
    ]
  }
]
```

### 26. Add Operators to Schedule
Menambahkan daftar operator ke dalam schedule berdasarkan tanggal dan task type yang dipilih admin.

**Endpoint**: `POST /api/schedule/add-operators`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN

**Request Body**:
```json
{
  "userIds": [2, 3, 4],
  "taskId": 1,
  "scheduleDate": "2025-08-16",
  "createdBy": "admin"
}
```

**Response Success (200)**:
```json
{
  "message": "Operators added to schedule successfully!"
}
```

### 27. Remove Operator from Schedule
Hapus operator yang dipilih admin dari schedule berdasarkan tanggal dan task type.

**Endpoint**: `DELETE /api/schedule/remove-operator`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN

**Request Body**:
```json
{
  "userId": 2,
  "taskId": 1,
  "scheduleDate": "2025-08-16"
}
```

**Response Success (200)**:
```json
{
  "message": "Operator removed from schedule successfully!"
}
```

---

## 📊 Customer Statistics Endpoints

### 28. Get Customer Status Count
Mendapatkan jumlah data customer dikelompokkan berdasarkan CustStatus.

**Endpoint**: `GET /api/customer-stats/status-count`  
**Authorization**: `Bearer {jwt_token}`  
**Required Role**: ADMIN or OPERATOR

**Response Success (200)**:
```json
[
  {
    "custStatus": "ACTIVE",
    "count": 150
  },
  {
    "custStatus": "INACTIVE",
    "count": 25
  },
  {
    "custStatus": "PENDING",
    "count": 10
  }
]
```

---

## 🚀 Getting Started

1. **Start Application**:
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Create Admin User**:
   ```bash
   # Setelah registrasi user pertama, jadikan admin
   curl -X POST http://localhost:8081/api/setup/make-admin?email=your_email@bankdki.com
   ```

3. **Ready to Use**: API siap digunakan dengan authentication JWT

---

## 📊 Database Schema

### Core Tables
- `user` - Data pengguna sistem
- `role` - Role/permission pengguna
- `cust` - Data customer/nasabah
- `menu` - Menu sistem
- `rolemenu` - Relasi role-menu
- `task` - Data tugas
- `schedule` - Jadwal tugas
- `log` - Audit log
- `session` - User session
- `status` - Master status
- `systemconfigurator` - Konfigurasi sistem
- `temporaryuploader` - Upload sementara
- `nomor` - Auto numbering

### Key Relationships
- User `many-to-one` Role (1 user = 1 role)
- RoleMenu `many-to-one` Role, Menu
- Log, Schedule `many-to-one` User, Task
- Session `many-to-one` User

### Important Notes
- **Single Role per User**: Setiap user hanya bisa memiliki 1 role saja
- **Role Assignment**: Role diassign melalui `roleId` field di User entity
- **Role Validation**: System memvalidasi roleId yang valid saat create/update user

---

## 🛠️ Development Notes

- **Framework**: Spring Boot 3.5.4
- **Database**: MySQL 8.0
- **Security**: Spring Security + JWT
- **Validation**: Bean Validation
- **Documentation**: Auto-generated schema
- **Port**: 8081 (configurable)

---

**Version**: 1.0  
**Last Updated**: {{ current_date }}  
**Contact**: Development Team Bank DKI