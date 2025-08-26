# 🏦 Bank DKI Document Management System (DMS) API

REST API untuk sistem manajemen dokumen Bank DKI yang menyediakan fitur authentication, user management, customer management, dan sistem administrasi lengkap berdasarkan struktur database DDL yang telah disesuaikan.

## 📋 Table of Contents
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [Architecture](#architecture)
- [API Documentation](#api-documentation)
- [Authentication](#authentication)
- [Role Management](#role-management)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)

## 🔧 Prerequisites

- Java 21 or later
- MySQL 8.0 or later
- Maven 3.6 or later (atau gunakan Maven wrapper yang disediakan)

## 🚀 Installation

1. Clone repository ini
2. Pastikan MySQL server berjalan
3. Buat database `dms` (opsional, akan otomatis dibuat):
   ```sql
   CREATE DATABASE dms;
   ```
4. Compile dan jalankan aplikasi:
   ```bash
   ./mvnw spring-boot:run
   ```

Aplikasi akan berjalan di `http://localhost:8080`

## ⚙️ Configuration

### Database Configuration
File: `src/main/resources/application.properties`

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/dms?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:3600000}
jwt.refresh-expiration=${JWT_REFRESH_EXPIRATION:604800000}
```

### Security Configuration
- **JWT Secret key**: Harus berupa Base64-encoded 32-byte key (256-bit) untuk HS256
- **Token expiration**: Default 1 jam (3600000 ms)
- **Refresh token expiration**: Default 7 hari (604800000 ms)
- **CORS**: Dikonfigurasi melalui property `app.cors.allowed-origins`

#### JWT Secret Key Requirements
⚠️ **IMPORTANT**: JWT secret harus berupa Base64-encoded string dengan panjang minimum 32 byte (256-bit).

**Generate secure Base64 key:**
```bash
# Generate 32 random bytes and encode to Base64
openssl rand -base64 32
```

**Example:**
```bash
# Output example: dGhpcyBpcyBhIHNlY3VyZSBqd3Qgc2VjcmV0IGZvciAyNTYgYml0
export JWT_SECRET="dGhpcyBpcyBhIHNlY3VyZSBqd3Qgc2VjcmV0IGZvciAyNTYgYml0"
```

## 📊 Database Schema

### Core Tables (Updated Schema)

#### User Table
```sql
CREATE TABLE user (
    UserId SMALLINT AUTO_INCREMENT PRIMARY KEY,
    UserName VARCHAR(100),
    UserEmail VARCHAR(100),
    UserHashPassword VARCHAR(100),
    UserIsActive BOOLEAN,
    RoleId SMALLINT NOT NULL,
    UserCreateAt DATETIME,
    UserUpdateAt DATETIME,
    UserCreateBy VARCHAR(50),
    UserUpdateBy VARCHAR(50),
    UserTglLahir VARCHAR(100),
    UserJabatan VARCHAR(40),
    UserTempatLahir VARCHAR(100),
    FOREIGN KEY (RoleId) REFERENCES role(RoleId)
);
```

#### Role Table
```sql
CREATE TABLE role (
    RoleId SMALLINT AUTO_INCREMENT PRIMARY KEY,
    RoleName VARCHAR(50),
    RoleIsActive BOOLEAN,
    RoleCreateDate DATETIME,
    RoleUpdateDate DATETIME,
    RoleCreateBy VARCHAR(50),
    RoleUpdateBy VARCHAR(50)
);
```

**Important**: Setiap user hanya memiliki **1 role** (many-to-one relationship), bukan many-to-many.

## 🏗️ Architecture

### Package Structure
```
src/main/java/com/bank_dki/be_dms/
├── config/
│   ├── DataInitializer.java          # Database seeding
│   └── SecurityConfig.java           # Spring Security configuration
├── controller/
│   ├── AuthController.java           # Authentication endpoints
│   ├── RoleController.java           # Role management endpoints
│   └── TestController.java           # Test endpoints
├── dto/
│   ├── JwtResponse.java              # JWT response model
│   ├── LoginRequest.java             # Login request model
│   ├── MessageResponse.java          # Generic response model
│   └── SignupRequest.java            # Signup request model
├── entity/
│   ├── Role.java                     # Role entity
│   └── User.java                     # User entity
├── repository/
│   ├── RoleRepository.java           # Role data access
│   └── UserRepository.java           # User data access
├── security/
│   └── JwtAuthenticationFilter.java  # JWT filter
├── service/
│   ├── AuthService.java              # Authentication business logic
│   ├── RoleService.java              # Role management business logic
│   └── UserDetailsServiceImpl.java   # Spring Security UserDetails
├── util/
│   └── JwtUtil.java                  # JWT utilities
├── BeDmsApplication.java             # Main application class
└── ServletInitializer.java           # WAR deployment config
```

### Key Components

#### Authentication Flow
1. **Login**: User mengirim username/email + password
2. **Validation**: Spring Security memvalidasi credentials
3. **JWT Generation**: Server generate JWT token jika valid
4. **Response**: Return JWT token dengan user info
5. **Request Authorization**: Client kirim JWT di header `Authorization: Bearer <token>`

#### Security Layers
- **JWT Authentication Filter**: Memvalidasi JWT token pada setiap request
- **Method-level Security**: Menggunakan `@PreAuthorize` untuk role-based access
- **Password Encryption**: BCrypt hashing untuk password

## 📚 Documentation

### 🚀 **[Quick Navigation](DOCS-NAVIGATION.md)** | 📖 **[Documentation Hub (HTML)](docs/index.html)** | 📋 **[Documentation Hub (Markdown)](docs/README.md)**

All project documentation is now organized in the `docs/` folder for easy access:

- **[🏠 Documentation Hub](docs/README.md)** - Main documentation portal with organized categories
- **[🚀 Quick Start Guide](docs/guide/QUICK_START_GUIDE.md)** - Get up and running in minutes  
- **[📡 API Documentation](docs/api/API_DOCUMENTATION.md)** - Complete API endpoint documentation
- **[🔍 Troubleshooting](docs/troubleshooting/JWT-TROUBLESHOOTING.md)** - Common issues and solutions
- **[🔧 Technical Docs](docs/technical/)** - In-depth implementation details

### Base URL for API
```
http://localhost:9000/api
```

### Authentication Endpoints

#### 1. Login
**POST** `/auth/signin`

**Request Body:**
```json
{
    "usernameOrEmail": "admin",
    "password": "password123"
}
```

**Success Response (200):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "username": "admin",
    "email": "admin@example.com",
    "role": "ADMIN"
}
```

**Error Response (400):**
```json
{
    "message": "Invalid credentials"
}
```

#### 2. Register
**POST** `/auth/signup`

**Request Body:**
```json
{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "password123",
    "roles": ["OPERATOR"]
}
```

**Success Response (200):**
```json
{
    "message": "User registered successfully!"
}
```

**Error Response (400):**
```json
{
    "message": "Error: Username is already taken!"
}
```

### Role Management Endpoints (Admin Only)

#### 1. Get All Roles
**GET** `/admin/roles`

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**
```json
[
    {
        "id": 1,
        "name": "ADMIN",
        "description": "Administrator role with full access"
    },
    {
        "id": 2,
        "name": "OPERATOR",
        "description": "Operator role with limited access"
    }
]
```

#### 2. Create Role
**POST** `/admin/roles?name=MANAGER&description=Manager role`

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**
```json
{
    "message": "Role created successfully!"
}
```

#### 3. Assign Role to User
**POST** `/admin/roles/assign?userId=1&roleId=2`

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**
```json
{
    "message": "Role assigned successfully!"
}
```

#### 4. Remove Role from User
**POST** `/admin/roles/remove?userId=1&roleId=2`

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**
```json
{
    "message": "Role removed successfully!"
}
```

#### 5. Get User Roles
**GET** `/admin/roles/user/{userId}`

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**
```json
[
    {
        "id": 1,
        "name": "ADMIN",
        "description": "Administrator role with full access"
    }
]
```

### Test Endpoints

#### 1. Public Access
**GET** `/test/all`

**Success Response (200):**
```
Public Content
```

#### 2. User Access (Any authenticated user)
**GET** `/test/user`

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**
```
User Content
```

#### 3. Operator Access (Operator or Admin)
**GET** `/test/operator`

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**
```
Operator Board
```

#### 4. Admin Access (Admin only)
**GET** `/test/admin`

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**
```
Admin Board
```

## 🔐 Authentication

### JWT Token Structure
```
Header: {
    "alg": "HS256",
    "typ": "JWT"
}

Payload: {
    "sub": "username",
    "iat": 1640995200,
    "exp": 1641081600
}
```

### How to Use JWT Token

1. **Login** untuk mendapatkan token
2. **Simpan token** di client (localStorage, sessionStorage, dll)
3. **Kirim token** pada setiap request yang memerlukan authentication:
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

### Token Expiration
- Default: 24 jam
- Setelah expired, user harus login ulang
- Tidak ada refresh token (implement jika diperlukan)

## 👥 Role Management

### Default Roles
Saat aplikasi pertama kali dijalankan, system akan otomatis membuat:

1. **ADMIN**
   - Akses penuh ke semua fitur
   - Dapat manage roles dan users
   - Dapat mengakses semua endpoints

2. **OPERATOR**
   - Akses terbatas
   - Tidak dapat manage roles
   - Default role untuk user baru

### Role-Based Access Control

#### Annotation-based Security
```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> adminOnlyEndpoint() {
    // Admin only code
}

@PreAuthorize("hasRole('OPERATOR') or hasRole('ADMIN')")
public ResponseEntity<?> operatorOrAdminEndpoint() {
    // Operator or Admin code
}
```

#### URL-based Security
```java
.requestMatchers("/api/admin/**").hasRole("ADMIN")
.requestMatchers("/api/operator/**").hasAnyRole("ADMIN", "OPERATOR")
```

## 🧪 Testing

### Manual Testing dengan cURL

#### 1. Register User Baru
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "roles": ["OPERATOR"]
  }'
```

#### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "password123"
  }'
```

#### 3. Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/test/operator \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

### Testing dengan Postman

1. **Import Collection**: Buat collection dengan semua endpoints
2. **Environment Variables**: Set `baseUrl` dan `token`
3. **Pre-request Scripts**: Auto-set Authorization header
4. **Test Scripts**: Validate responses

### Unit Testing
Jalankan unit tests:
```bash
./mvnw test
```

## 🔍 Troubleshooting

### Common Issues

#### 1. Database Connection Error
```
Error: Communications link failure
```
**Solution:**
- Pastikan MySQL server berjalan
- Check koneksi database di `application.properties`
- Verify username/password database

#### 2. JWT Token Invalid
```
Error: JWT signature does not match locally computed signature
```
**Solution:**
- Check JWT secret key di `application.properties`
- Pastikan token belum expired
- Verify token format: `Bearer <token>`

#### 3. Access Denied
```
Error: Access Denied
```
**Solution:**
- Check user roles
- Verify endpoint permissions
- Ensure JWT token valid

#### 4. Port Already in Use
```
Error: Port 8080 was already in use
```
**Solution:**
- Change port di `application.properties`: `server.port=8081`
- Kill process using port: `lsof -ti:8080 | xargs kill -9`

### Logging
Enable debug logging untuk troubleshooting:
```properties
logging.level.org.springframework.security=DEBUG
logging.level.com.bank_dki.be_dms=DEBUG
```

### Database Issues
Check database connection:
```sql
SHOW DATABASES;
USE dms;
SHOW TABLES;
SELECT * FROM users;
SELECT * FROM roles;
```

## 📝 Development Notes

### Extending the System

#### Adding New Roles
1. Modify `DataInitializer.java`
2. Add role in database
3. Update security configuration

#### Adding New Endpoints
1. Create controller method
2. Add security annotation
3. Test with appropriate roles

#### Custom JWT Claims
Modify `JwtUtil.java` untuk menambah custom claims:
```java
claims.put("roles", user.getRoles());
claims.put("department", user.getDepartment());
```

### Best Practices
- Always validate input
- Use DTOs for API requests/responses
- Implement proper error handling
- Log security events
- Regular security updates

### Security Considerations
- Change default JWT secret
- Implement rate limiting
- Add input validation
- Use HTTPS in production
- Regular security audits

---

## 📞 Support

Jika ada pertanyaan atau issues, silakan buat issue di repository ini atau contact developer.

**Happy Coding! 🚀**