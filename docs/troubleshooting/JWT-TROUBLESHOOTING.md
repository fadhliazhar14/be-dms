# JWT Token Troubleshooting Guide

## 📋 **Common JWT Error Analysis**

### **Error**: `JWT strings must contain exactly 2 period characters. Found: 0`

**Root Cause**: This error indicates that the JWT token sent by the client is malformed or invalid.

**Possible Causes**:
1. **Empty Token**: Client sends `Bearer ` without any token
2. **Invalid Token Format**: Token doesn't have the required `header.payload.signature` structure
3. **Corrupted Token**: Token got corrupted during transmission
4. **Frontend Issue**: Frontend application not properly storing or sending the JWT token

---

## 🛠 **Debugging Tools**

### **1. JWT Debug Endpoint**
We've implemented a debug endpoint to help troubleshoot JWT issues:

#### **Endpoint**: `POST /api/debug/jwt-validate`
- **Access**: Public (no authentication required)
- **Purpose**: Validate JWT token structure and extract information

#### **Usage**:
```bash
# Test with a JWT token
curl -X POST http://localhost:9000/api/debug/jwt-validate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -H "Content-Type: application/json"
```

#### **Sample Response**:
```json
{
  "status": 200,
  "message": "JWT Debug Information",
  "data": {
    "authHeader": "Present",
    "authHeaderLength": 157,
    "tokenLength": 150,
    "tokenEmpty": false,
    "dotCount": 2,
    "isProperJwtStructure": true,
    "username": "admin@example.com",
    "expirationDate": "2025-08-26T15:00:00",
    "isExpired": false,
    "tokenValid": "Token structure is valid"
  },
  "errors": null,
  "timestamp": "2025-08-26T14:46:20.324"
}
```

#### **Error Cases**:
```json
{
  "status": 200,
  "message": "JWT Debug Information", 
  "data": {
    "authHeader": "Present",
    "authHeaderLength": 7,
    "tokenLength": 0,
    "tokenEmpty": true,
    "dotCount": 0,
    "isProperJwtStructure": false,
    "tokenError": "Invalid JWT structure - expected format: header.payload.signature"
  }
}
```

### **2. Health Check Endpoint**
#### **Endpoint**: `GET /api/debug/ping`
- **Purpose**: Verify if the debug endpoints are accessible

```bash
curl http://localhost:9000/api/debug/ping
```

---

## 🔧 **Enhanced JWT Filter Logging**

The `JwtAuthenticationFilter` now provides detailed logging for different JWT error scenarios:

### **Log Messages**:

1. **Empty Token**:
   ```
   WARN: JWT token is empty or null after removing Bearer prefix
   ```

2. **Malformed Token**:
   ```
   ERROR: JWT token is malformed (invalid format). Token length: 0, Expected format: header.payload.signature
   ```

3. **Expired Token**:
   ```
   WARN: JWT token has expired for user: admin@example.com
   ```

4. **Invalid Signature**:
   ```
   ERROR: JWT token signature validation failed: JWT signature does not match locally computed signature
   ```

5. **Invalid Authorization Header**:
   ```
   WARN: Authorization header present but does not start with 'Bearer '. Header: Basic dGVzdA...
   ```

---

## 🔍 **Step-by-Step Troubleshooting**

### **Step 1: Check Authorization Header**
```bash
# Verify client is sending proper Authorization header
curl -X GET http://localhost:9000/api/users \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -v  # Verbose output to see headers
```

### **Step 2: Validate Token Structure**
Use the debug endpoint to check token structure:
```bash
curl -X POST http://localhost:9000/api/debug/jwt-validate \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### **Step 3: Check Token Generation**
Verify the login endpoint returns valid tokens:
```bash
curl -X POST http://localhost:9000/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@example.com", "password": "password"}'
```

### **Step 4: Verify JWT Secret Configuration**
Ensure `application.properties` has correct JWT secret:
```properties
jwt.secret=YOUR_BASE64_ENCODED_256BIT_SECRET
jwt.expiration=86400000
```

---

## ⚙️ **Security Improvements Made**

### **1. Public Endpoint Filtering**
- JWT processing is now skipped for public endpoints
- Reduces unnecessary processing and logging noise
- Public endpoints: `/api/auth/**`, `/api/setup/**`, `/api/debug/**`, `/api/test/all`, `/api/roles/active`

### **2. Enhanced Error Handling**
- Specific exception types are caught and logged appropriately
- Different log levels for different severity issues
- Security-conscious logging (token content not logged)

### **3. Improved Security Configuration**
- Fixed `permitAll()` configuration for protected endpoints
- Only truly public endpoints are accessible without authentication
- Proper role-based access control maintained

---

## 📊 **Common Client-Side Issues**

### **Frontend JavaScript Example**:
```javascript
// WRONG: Empty token
fetch('/api/users', {
  headers: {
    'Authorization': 'Bearer '  // Empty token after Bearer
  }
});

// WRONG: No Bearer prefix
fetch('/api/users', {
  headers: {
    'Authorization': 'abc123def456'  // Missing Bearer prefix
  }
});

// CORRECT: Proper JWT token
fetch('/api/users', {
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
  }
});
```

### **Token Storage Best Practices**:
```javascript
// Login and store token
const response = await fetch('/api/auth/signin', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'user@example.com', password: 'password' })
});

const data = await response.json();
if (data.accessToken) {
  localStorage.setItem('jwt_token', data.accessToken);
  localStorage.setItem('refresh_token', data.refreshToken);
}

// Use token in subsequent requests
const token = localStorage.getItem('jwt_token');
if (token) {
  fetch('/api/users', {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
}
```

---

## 🎯 **Next Steps for Complete Resolution**

1. **Test the debug endpoint** with various token scenarios
2. **Check frontend application** JWT token handling
3. **Monitor logs** for specific error patterns
4. **Implement token refresh** mechanism if tokens are expiring
5. **Add client-side token validation** before sending requests

---

## 📞 **Quick Diagnostic Commands**

```bash
# Check if service is running
curl http://localhost:9000/api/debug/ping

# Test with malformed token (should show proper error)
curl -X POST http://localhost:9000/api/debug/jwt-validate \
  -H "Authorization: Bearer invalid_token"

# Test with empty token (should show proper error) 
curl -X POST http://localhost:9000/api/debug/jwt-validate \
  -H "Authorization: Bearer "

# Test without Authorization header
curl -X POST http://localhost:9000/api/debug/jwt-validate
```

Use these tools and guidelines to systematically identify and resolve JWT token issues.
