# Spring Security 6.x Exception Handling Guide

## 📋 **Problem Discovered**

### **New Exception Type in Spring Security 6.x**
After fixing the initial conflicts, we discovered that Spring Security 6.x introduces a **new exception type** for method-level authorization failures:

**Error Log:**
```
org.springframework.security.authorization.AuthorizationDeniedException: Access Denied
at org.springframework.security.authorization.method.ThrowingMethodAuthorizationDeniedHandler.handleDeniedInvocation
```

This is **different** from the traditional `AccessDeniedException` we handled before.

---

## 🔍 **Exception Type Comparison**

### **Spring Security 5.x (Legacy)**
```java
// HTTP-level access denied
org.springframework.security.access.AccessDeniedException

// Authentication failures  
org.springframework.security.core.AuthenticationException
org.springframework.security.authentication.BadCredentialsException
```

### **Spring Security 6.x (Current)**
```java
// HTTP-level access denied (still exists)
org.springframework.security.access.AccessDeniedException

// NEW: Method-level authorization denied
org.springframework.security.authorization.AuthorizationDeniedException

// Authentication failures (unchanged)
org.springframework.security.core.AuthenticationException
org.springframework.security.authentication.BadCredentialsException
```

---

## 🎯 **When Each Exception Occurs**

### **1. HTTP-Level Access Denied**
**Exception**: `org.springframework.security.access.AccessDeniedException`
**Trigger**: URL-based security restrictions in SecurityConfig
**Handler**: `CustomAccessDeniedHandler`

```java
// SecurityConfig.java
.requestMatchers("/api/admin/**").hasRole("ADMIN")
// When non-ADMIN tries to access /api/admin/* → AccessDeniedException
```

### **2. Method-Level Authorization Denied** 
**Exception**: `org.springframework.security.authorization.AuthorizationDeniedException`
**Trigger**: `@PreAuthorize` annotations on controller methods
**Handler**: `GlobalExceptionHandler.handleMethodAuthorizationDenied()`

```java
// Controller method
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<UserDTO>> getAllUsers() {
    // When non-ADMIN calls this method → AuthorizationDeniedException
}
```

### **3. Authentication Failures**
**Exception**: `AuthenticationException` / `BadCredentialsException`
**Trigger**: JWT validation failures, invalid credentials
**Handler**: `CustomAuthenticationEntryPoint`

---

## 🛠 **Complete Exception Handler Mapping**

| Exception Type | Level | Trigger | Handler | Status | Message |
|---|---|---|---|---|---|
| `AuthenticationException` | HTTP | JWT/Auth failure | `CustomAuthenticationEntryPoint` | 401 | Authentication required |
| `AccessDeniedException` | HTTP | URL restrictions | `CustomAccessDeniedHandler` | 403 | Access denied (HTTP) |
| `AuthorizationDeniedException` | Method | `@PreAuthorize` | `GlobalExceptionHandler` | 403 | Access denied (Method) |

---

## 📊 **Exception Flow Diagram**

```
Request to Protected Endpoint
         ↓
┌─────────────────────────┐
│   JWT Authentication   │ → AuthenticationException → CustomAuthenticationEntryPoint (401)
│      Filter Chain      │
└─────────────────────────┘
         ↓ (Authenticated)
┌─────────────────────────┐
│   HTTP-Level Security   │ → AccessDeniedException → CustomAccessDeniedHandler (403)
│    (URL patterns)       │
└─────────────────────────┘
         ↓ (Authorized at HTTP level)
┌─────────────────────────┐
│   Controller Method     │
│    Invocation           │
└─────────────────────────┘
         ↓
┌─────────────────────────┐
│   Method-Level Security │ → AuthorizationDeniedException → GlobalExceptionHandler (403)
│    (@PreAuthorize)      │
└─────────────────────────┘
         ↓ (Authorized at method level)
┌─────────────────────────┐
│   Business Logic        │ → BusinessException → GlobalExceptionHandler (400/409/500)
│    Execution            │
└─────────────────────────┘
```

---

## ✅ **Solution Implemented**

### **Added Method-Level Authorization Handler**
```java
@ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
public ResponseEntity<ApiResponse<?>> handleMethodAuthorizationDenied(
        org.springframework.security.authorization.AuthorizationDeniedException ex, 
        HttpServletRequest request) {

    log.warn("Method-level authorization denied on path: {} - Error: {}",
            request.getRequestURI(), ex.getMessage());

    return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), 
                  "Access denied. You do not have sufficient permissions to access this resource.", 
                  null));
}
```

### **Why This Approach?**
- **Separation of Concerns**: HTTP-level vs Method-level security
- **Consistent Responses**: Same ApiResponse format across all handlers
- **Proper Logging**: Different log messages for different security layers
- **Spring Security 6.x Compatibility**: Handles new exception types properly

---

## 🧪 **Testing Different Scenarios**

### **1. Test Authentication Failure (401)**
```bash
# No JWT token
curl -X GET http://localhost:9000/api/users
# → CustomAuthenticationEntryPoint → 401 Unauthorized

# Invalid JWT token  
curl -X GET http://localhost:9000/api/users -H "Authorization: Bearer invalid_token"
# → CustomAuthenticationEntryPoint → 401 Unauthorized
```

### **2. Test HTTP-Level Access Denied (403)**
```bash
# Valid user token but accessing admin-only URL
curl -X GET http://localhost:9000/api/admin/some-endpoint -H "Authorization: Bearer user_token"
# → CustomAccessDeniedHandler → 403 Forbidden
```

### **3. Test Method-Level Access Denied (403)**
```bash
# Valid user token but calling admin-only method
curl -X GET http://localhost:9000/api/users -H "Authorization: Bearer user_token"
# → GlobalExceptionHandler.handleMethodAuthorizationDenied → 403 Forbidden
```

---

## 📝 **Response Format Consistency**

All handlers now return consistent `ApiResponse` format:

### **Authentication Failure (401)**
```json
{
  "status": 401,
  "message": "Authentication required. Please provide a valid JWT token.",
  "data": null,
  "errors": null,
  "timestamp": "2025-08-26T15:30:58"
}
```

### **HTTP-Level Access Denied (403)**
```json
{
  "status": 403,
  "message": "Access denied. You do not have permission to access this resource.",
  "data": null,
  "errors": null,
  "timestamp": "2025-08-26T15:30:58"
}
```

### **Method-Level Access Denied (403)**
```json
{
  "status": 403,
  "message": "Access denied. You do not have sufficient permissions to access this resource.",
  "data": null,
  "errors": null,
  "timestamp": "2025-08-26T15:30:58"
}
```

---

## 🚀 **Best Practices Applied**

### **1. Layer-Specific Handling**
✅ **HTTP Security** → Spring Security handlers
✅ **Method Security** → GlobalExceptionHandler 
✅ **Business Logic** → GlobalExceptionHandler

### **2. Clear Exception Messages**
✅ **Authentication**: "Authentication required"
✅ **HTTP Authorization**: "You do not have permission" 
✅ **Method Authorization**: "You do not have sufficient permissions"

### **3. Proper Logging**
✅ **Different log levels**: WARN for security, ERROR for unexpected
✅ **Contextual information**: path, exception type, message
✅ **Security-conscious**: No sensitive data in logs

### **4. Consistent API Contract**
✅ **Same ApiResponse structure** across all exception types
✅ **Proper HTTP status codes** (401, 403, 400, 409, 500)
✅ **Timestamp and error tracking** for debugging

---

## 📚 **Key Takeaways**

1. **Spring Security 6.x** introduces new exception types for method-level security
2. **Different security layers** require different exception handling approaches
3. **Consistent API responses** are crucial for frontend integration
4. **Proper separation** between HTTP-level and method-level security exceptions
5. **Comprehensive logging** helps with debugging and monitoring

This implementation ensures that your application properly handles all types of security exceptions while maintaining consistency and following Spring Security 6.x best practices.
