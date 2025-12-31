# MerbsConnect Authentication API - cURL Examples

This document provides cURL command examples for testing each endpoint of the MerbsConnect Authentication API.

---

## 1. User Registration (Sign Up)

**Endpoint**: `POST /api/v1/auth/signup`

```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "SecurePass123!"
  }'
```

**Expected Response (200 OK)**:
```json
{
  "message": "User registered successfully. Please check your email for verification link."
}
```

---

## 2. User Login

**Endpoint**: `POST /api/v1/auth/login`

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePass123!"
  }'
```

**Expected Response (200 OK)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "username": "john.doe@example.com",
  "roles": ["ROLE_USER"]
}
```

**Save the token for use in subsequent requests**:
```bash
TOKEN="your_token_here"
```

---

## 3. Verify Email

**Endpoint**: `GET /api/v1/auth/verify-email`

```bash
curl -X GET "http://localhost:8080/api/v1/auth/verify-email?token=YOUR_VERIFICATION_TOKEN"
```

**Expected Response (200 OK)**:
```json
{
  "message": "Email verified successfully. User can now log in."
}
```

---

## 4. Request Password Reset

**Endpoint**: `POST /api/v1/auth/forgot-password`

```bash
curl -X POST "http://localhost:8080/api/v1/auth/forgot-password?email=john.doe@example.com"
```

**Expected Response (200 OK)**:
```json
{
  "message": "Password reset email sent successfully. Please check your email for the reset link."
}
```

---

## 5. Reset Password

**Endpoint**: `POST /api/v1/auth/reset-password`

```bash
curl -X POST http://localhost:8080/api/v1/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_PASSWORD_RESET_TOKEN",
    "newPassword": "NewPass123!",
    "confirmPassword": "NewPass123!"
  }'
```

**Expected Response (200 OK)**:
```json
{
  "message": "Password reset successfully. You can now log in with your new password."
}
```

---

## 6. Refresh Token

**Endpoint**: `POST /api/v1/auth/refresh-token`

```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

**Expected Response (200 OK)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "username": "john.doe@example.com",
  "roles": ["ROLE_USER"]
}
```

---

## 7. Resend Token

**Endpoint**: `POST /api/v1/auth/resend-token`

### Resend Verification Email:
```bash
curl -X POST "http://localhost:8080/api/v1/auth/resend-token?email=john.doe@example.com&tokenType=VERIFICATION"
```

### Resend Password Reset Email:
```bash
curl -X POST "http://localhost:8080/api/v1/auth/resend-token?email=john.doe@example.com&tokenType=PASSWORD_RESET"
```

**Expected Response (200 OK)**:
```json
{
  "message": "Token resent successfully. Please check your email."
}
```

---

## Complete User Flow Example

### Step 1: Register a New User
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com",
    "password": "SecurePass123!"
  }'
```

### Step 2: Check Email for Verification Link (manually or check logs)
- Verification token will be sent to jane.smith@example.com

### Step 3: Verify Email
```bash
curl -X GET "http://localhost:8080/api/v1/auth/verify-email?token=VERIFICATION_TOKEN_FROM_EMAIL"
```

### Step 4: Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane.smith@example.com",
    "password": "SecurePass123!"
  }'
```

### Step 5: Save the Access Token
```bash
TOKEN="access_token_from_login_response"
```

### Step 6: Use the Token for Protected Endpoints
```bash
curl -X GET "http://localhost:8080/api/v1/users/profile" \
  -H "Authorization: Bearer $TOKEN"
```

### Step 7: Refresh Token When Expired
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "refresh_token_from_login_response"
  }'
```

---

## Error Examples

### Invalid Email Format
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "invalid-email",
    "password": "SecurePass123!"
  }'
```

**Response (400 Bad Request)**:
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email should be valid",
  "path": "/api/v1/auth/signup"
}
```

### Weak Password
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "weak"
  }'
```

**Response (400 Bad Request)**:
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Password must be at least 8 characters long",
  "path": "/api/v1/auth/signup"
}
```

### Email Not Verified (trying to login)
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "unverified@example.com",
    "password": "SecurePass123!"
  }'
```

**Response (403 Forbidden)**:
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Email not verified. Please verify your email before logging in.",
  "path": "/api/v1/auth/login"
}
```

### Invalid Credentials
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nonexistent@example.com",
    "password": "WrongPassword123!"
  }'
```

**Response (401 Unauthorized)**:
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "path": "/api/v1/auth/login"
}
```

---

## Using Variables in cURL Scripts

### Save Token to File
```bash
# Login and save token
RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePass123!"
  }')

# Extract token using jq
TOKEN=$(echo $RESPONSE | jq -r '.token')
REFRESH_TOKEN=$(echo $RESPONSE | jq -r '.refreshToken')

# Use the token in subsequent requests
echo "Access Token: $TOKEN"
echo "Refresh Token: $REFRESH_TOKEN"
```

### Use Token in Subsequent Requests
```bash
curl -X GET "http://localhost:8080/api/v1/users/profile" \
  -H "Authorization: Bearer $TOKEN"
```

---

## Testing in Postman

### Import Collection
1. Open Postman
2. Click "Import"
3. Enter URL: `http://localhost:8080/v3/api-docs`
4. Click "Import"
5. Postman will create a collection with all endpoints

### Manual Setup
1. Create a new request
2. Set method and URL
3. Add headers: `Content-Type: application/json`
4. Add body (JSON format)
5. Click "Send"

---

## Tips for Testing

1. **Keep tokens safe**: Don't share your tokens in logs or version control
2. **Use environment variables**: Store sensitive data in Postman environments
3. **Test error cases**: Verify error handling works correctly
4. **Check email inbox**: Look for verification and password reset tokens
5. **Use HTTPS in production**: Never send credentials over HTTP
6. **Add delays**: Wait for emails before verifying or testing
7. **Clean up test data**: Remove test accounts after testing

---

## Rate Limiting

The API implements rate limiting on token resend operations:
- **Maximum Attempts**: 3 per hour
- **Cooldown**: 1 minute between attempts
- **Window**: 1 hour

If you exceed the rate limit, you'll receive a 429 (Too Many Requests) response.

---

## Common Curl Options

```bash
-X POST              # HTTP method
-H "Header: value"   # Add header
-d '{"data": "value"}' # Request body
-s                   # Silent mode (no progress)
-i                   # Include response headers
-w '\n'              # Add newline to output
-o filename          # Save output to file
-O                   # Save with original filename
```

---

## Testing Checklist

- [ ] Register a new user
- [ ] Check verification email was sent
- [ ] Verify email with token
- [ ] Login with verified account
- [ ] Refresh access token
- [ ] Request password reset
- [ ] Check password reset email
- [ ] Reset password with token
- [ ] Login with new password
- [ ] Test invalid credentials
- [ ] Test email not verified scenario
- [ ] Test expired token scenarios
- [ ] Test rate limiting on resend

---

Last Updated: December 31, 2025
Version: 1.0.0

