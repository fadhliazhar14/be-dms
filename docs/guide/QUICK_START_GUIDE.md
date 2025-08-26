# 🚀 Quick Start Guide - Bank DKI DMS API

## Prerequisites
- Java 21
- MySQL 8.0+
- Maven 3.6+

## 1. Start Application

```bash
# Clone dan masuk ke directory
cd be-dms-development

# Jalankan aplikasi
./mvnw spring-boot:run
```

Aplikasi akan berjalan di: **http://localhost:8080**

## 2. Initial Setup

### Step 1: Register First User
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "admin",
    "userEmail": "admin@bankdki.com",
    "userHashPassword": "admin123",
    "roleId": 2,
    "userJabatan": "Administrator",
    "userTglLahir": "1985-01-01",
    "userTempatLahir": "Jakarta"
  }'
```

### Step 2: Make User Admin
```bash
curl -X POST "http://localhost:8080/api/setup/make-admin?email=admin@bankdki.com"
```

### Step 3: Login & Get Token
```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "userEmail": "admin@bankdki.com",
    "userHashPassword": "admin123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "email": "admin@bankdki.com",
  "role": "ADMIN"
}
```

**Copy token** untuk digunakan di request selanjutnya.

## 3. Test API Endpoints

### Get All Users (Admin only)
```bash
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Create Customer
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "custCifNumber": "CIF001",
    "custStatus": "ACTIVE",
    "custCabang": "Jakarta Pusat",
    "prsnNama": "Budi Santoso",
    "prsnJenisKelamin": "L",
    "cardNik": "3171234567890123",
    "adrsNomorHp": "081234567890",
    "adrsSurel": "budi@email.com",
    "adrsProvinsi": "DKI Jakarta",
    "adrsKota": "Jakarta Pusat",
    "adrsAlamat1": "Jl. Thamrin No. 1"
  }'
```

### Get All Customers
```bash
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 4. Using Postman

1. **Import Collection**: Import file `POSTMAN_COLLECTION.json`
2. **Set Variables**:
   - `base_url`: `http://localhost:8080`
   - `jwt_token`: (akan otomatis ter-set setelah login)
3. **Login First**: Jalankan request "Login" di folder "Authentication"
4. **Test Endpoints**: Token akan otomatis ter-set untuk request lainnya

## 5. Common Use Cases

### 🔐 Authentication Flow
1. Register → Login → Get Token → Use Token

### 👥 User Management
1. Create User → Assign Role → Activate/Deactivate

### 👤 Customer Management
1. Create Customer → Search Customer → Update Customer

### 🏷️ Role Management
1. Create Role → Assign to User

## 6. Default Roles

System akan otomatis membuat 2 role:
- **ADMIN** (ID: 1) - Full access
- **OPERATOR** (ID: 2) - Limited access

## 7. Database Schema

Tabel yang dibuat otomatis:
- `user` - Data pengguna
- `role` - Role sistem
- `cust` - Data customer
- `menu`, `rolemenu` - Menu management
- `task`, `schedule`, `log` - Task management
- `session` - User sessions
- Dan lainnya sesuai DDL

## 8. Troubleshooting

### Port Already in Use
```bash
# Ganti port di application.properties
server.port=8082
```

### Database Connection Error
Periksa konfigurasi database di `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bank_dki
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Token Expired
Token berlaku 24 jam. Login ulang jika expired.

## 9. Next Steps

1. **Customize**: Sesuaikan business logic sesuai kebutuhan
2. **Add Validation**: Tambah validasi data sesuai requirement
3. **Security**: Implement additional security measures
4. **Testing**: Buat unit test dan integration test
5. **Documentation**: Update API documentation sesuai perubahan

## 🎯 Ready to Use!

API sudah siap digunakan untuk development aplikasi Bank DKI DMS.

Untuk dokumentasi lengkap, lihat: `API_DOCUMENTATION.md`