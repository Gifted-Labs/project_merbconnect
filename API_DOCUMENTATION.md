# MerbsConnect Authentication API Documentation

## Overview
The MerbsConnect Authentication API provides comprehensive endpoints for user registration, login, email verification, password reset, and token management. All endpoints are secured with JWT (JSON Web Tokens) where required.

**Base URL:** `http://localhost:8080/api/v1/auth`

**API Version:** v1.0.0

## Security
This API uses JWT (Bearer) authentication for protected endpoints. Include the JWT token in the `Authorization` header as follows:
```
Authorization: Bearer <your_jwt_token>
```

## Response Status Codes
- `200 OK` - Request succeeded
- `400 Bad Request` - Invalid input or validation failed
- `401 Unauthorized` - Authentication failed or token expired
- `403 Forbidden` - User email not verified or insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## Authentication Endpoints

### 1. User Registration (Sign Up)
**Endpoint:** `POST /api/v1/auth/signup`

**Summary:** Register a new user account

**Description:** Creates a new user account with the provided registration details. The user email must be unique and valid. A verification email will be sent to the provided email address.

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "SecurePass123!"
}
```

**Field Validation:**
- `firstName`: Required, 2-50 characters
- `lastName`: Required, 2-50 characters
- `email`: Required, valid email format, must be unique
- `password`: Required, minimum 8 characters, must contain uppercase, lowercase, digit, and special character (@#$%^&+=)

**Response (200 OK):**
```json
{
  "message": "User registered successfully. Please check your email for verification link."
}
```

**Error Responses:**
- **400 Bad Request:**
  - First name or last name is blank or exceeds 50 characters
  - Email format is invalid
  - Email already exists
  - Password does not meet security requirements

- **500 Internal Server Error:** Internal server error while processing registration

---

### 2. User Login
**Endpoint:** `POST /api/v1/auth/login`

**Summary:** Authenticate user and generate JWT tokens

**Description:** Authenticates a user with their email and password credentials. On successful authentication, returns a JWT access token, refresh token, and user details. The access token should be used in the Authorization header for subsequent API requests.

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 123,
  "username": "john.doe@example.com",
  "roles": ["ROLE_USER"]
}
```

**Error Responses:**
- **400 Bad Request:** Email or password is blank
- **401 Unauthorized:** Invalid credentials (email does not exist or password is incorrect)
- **403 Forbidden:** Email not verified. Please verify your email before logging in.
- **500 Internal Server Error:** Internal server error during authentication

---

## Email Verification Endpoints

### 3. Verify Email Address
**Endpoint:** `GET /api/v1/auth/verify-email`

**Summary:** Verify user email address

**Description:** Verifies the user's email address using the verification token sent to their email. The token is typically provided as a link in the verification email. Email verification must be completed before the user can log in.

**Query Parameters:**
- `token` (required): Email verification token sent to user's email address
  - Example: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`

**Example Request:**
```
GET /api/v1/auth/verify-email?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
{
  "message": "Email verified successfully. User can now log in."
}
```

**Error Responses:**
- **400 Bad Request:** Invalid or expired verification token
- **404 Not Found:** User associated with the token not found
- **500 Internal Server Error:** Internal server error during email verification

---

## Password Reset Endpoints

### 4. Request Password Reset
**Endpoint:** `POST /api/v1/auth/forgot-password`

**Summary:** Request a password reset

**Description:** Initiates a password reset process for a user account. A password reset link will be sent to the provided email address. This endpoint does not require authentication and can be accessed without a valid JWT token.

**Query Parameters:**
- `email` (required): Email address of the user requesting password reset
  - Example: `user@example.com`

**Example Request:**
```
POST /api/v1/auth/forgot-password?email=john.doe@example.com
```

**Response (200 OK):**
```json
{
  "message": "Password reset email sent successfully. Please check your email for the reset link."
}
```

**Error Responses:**
- **400 Bad Request:** Invalid email format or email parameter is missing
- **404 Not Found:** User with the provided email address not found
- **500 Internal Server Error:** Internal server error while sending password reset email

---

### 5. Reset Password
**Endpoint:** `POST /api/v1/auth/reset-password`

**Summary:** Reset user password with verification token

**Description:** Resets a user's password using a valid password reset token. The new password must meet the security requirements (minimum 8 characters, must contain uppercase, lowercase, digit, and special character). This endpoint does not require JWT authentication.

**Request Body:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "newPassword": "NewPass123!",
  "confirmPassword": "NewPass123!"
}
```

**Field Validation:**
- `token`: Required, must be valid and not expired
- `newPassword`: Required, minimum 8 characters, must contain uppercase, lowercase, digit, special character, and no whitespace
- `confirmPassword`: Required, must match newPassword

**Response (200 OK):**
```json
{
  "message": "Password reset successfully. You can now log in with your new password."
}
```

**Error Responses:**
- **400 Bad Request:**
  - Token is blank or invalid
  - New password is blank
  - Password does not meet security requirements
  - Password confirmation does not match new password
  - Token has expired

- **404 Not Found:** User associated with the password reset token not found
- **500 Internal Server Error:** Internal server error while resetting password

---

## Token Management Endpoints

### 6. Refresh Access Token
**Endpoint:** `POST /api/v1/auth/refresh-token`

**Summary:** Refresh JWT access token

**Description:** Generates a new JWT access token using a valid refresh token. Use this endpoint when your access token has expired but your refresh token is still valid. The new access token can be used for subsequent API requests.

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 123,
  "username": "john.doe@example.com",
  "roles": ["ROLE_USER"]
}
```

**Error Responses:**
- **400 Bad Request:** Invalid or missing refresh token
- **401 Unauthorized:** Refresh token has expired or is invalid
- **500 Internal Server Error:** Internal server error while refreshing token

---

### 7. Resend Token
**Endpoint:** `POST /api/v1/auth/resend-token`

**Summary:** Resend verification or password reset token

**Description:** Resends a verification email or password reset email to the user. Use this endpoint if the user did not receive the initial token email or if the token has expired. Specify the token type (VERIFICATION or PASSWORD_RESET) to indicate which token to resend.

**Query Parameters:**
- `email` (required): Email address of the user requesting token resend
  - Example: `user@example.com`
- `tokenType` (required): Type of token to resend
  - Allowed values: `VERIFICATION`, `PASSWORD_RESET`
  - Example: `VERIFICATION`

**Example Request:**
```
POST /api/v1/auth/resend-token?email=john.doe@example.com&tokenType=VERIFICATION
```

**Response (200 OK):**
```json
{
  "message": "Token resent successfully. Please check your email."
}
```

**Error Responses:**
- **400 Bad Request:** Invalid input - email format is invalid or token type is missing
- **404 Not Found:** User with the provided email address not found
- **500 Internal Server Error:** Internal server error while resending token

---

## Data Models

### RegistrationRequest
```json
{
  "firstName": "string (2-50 chars, required)",
  "lastName": "string (2-50 chars, required)",
  "email": "string (valid email, unique, required)",
  "password": "string (min 8 chars, with uppercase, lowercase, digit, special char, required)"
}
```

### LoginRequest
```json
{
  "email": "string (required)",
  "password": "string (required)"
}
```

### PasswordResetRequest
```json
{
  "token": "string (required)",
  "newPassword": "string (min 8 chars, with uppercase, lowercase, digit, special char, required)",
  "confirmPassword": "string (must match newPassword, required)"
}
```

### TokenRefreshRequest
```json
{
  "refreshToken": "string (required)"
}
```

### JwtResponse
```json
{
  "token": "string (JWT access token)",
  "type": "Bearer",
  "refreshToken": "string (JWT refresh token)",
  "id": "number (user ID)",
  "username": "string (user email)",
  "roles": ["string (user roles)"]
}
```

### MessageResponse
```json
{
  "message": "string (response message)"
}
```

---

## Token Specifications

### Access Token
- **Expiration:** Typically 1 hour (3600000 milliseconds)
- **Purpose:** Used to authenticate API requests
- **Header Format:** `Authorization: Bearer <access_token>`

### Refresh Token
- **Expiration:** Typically 7 days (8460000 milliseconds)
- **Purpose:** Used to obtain a new access token when the current one expires
- **Endpoint:** POST `/api/v1/auth/refresh-token`

### Verification Token
- **Expiration:** 24 hours
- **Purpose:** Used to verify user email addresses
- **Delivery:** Sent via email during registration

### Password Reset Token
- **Expiration:** 1 hour
- **Purpose:** Used to reset forgotten passwords
- **Delivery:** Sent via email when password reset is requested

---

## Common Error Handling

### Validation Error Example
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Password must contain at least one digit, one lowercase, one uppercase letter, and one special character",
  "path": "/api/v1/auth/signup"
}
```

### Authentication Error Example
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

## Rate Limiting
The API implements rate limiting on token resend operations:
- **Maximum Attempts:** 3 attempts
- **Cooldown Period:** 1 minute
- **Attempt Window:** 1 hour

---

## CORS Configuration
The API supports Cross-Origin Resource Sharing (CORS) from the following origins:
- `http://localhost:5173`
- `http://localhost:5174`
- `http://localhost:8080`

---

## API Documentation Access
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`
- **OpenAPI YAML:** `http://localhost:8080/v3/api-docs.yaml`

---

## Best Practices

### 1. Token Security
- Store JWT tokens securely (use httpOnly cookies for web applications)
- Never expose tokens in logs or error messages
- Always use HTTPS in production

### 2. Password Security
- Enforce strong password requirements
- Never transmit passwords in plain text
- Always use HTTPS when transmitting credentials

### 3. Error Handling
- Never leak sensitive information in error messages
- Log security-related errors for monitoring
- Implement proper exception handling

### 4. Rate Limiting
- Respect rate limits on token resend operations
- Implement exponential backoff for retries
- Monitor for suspicious activity

### 5. Token Refresh
- Implement automatic token refresh before expiration
- Handle refresh token expiration gracefully
- Force re-authentication when refresh token expires

---

## Support
For API support and questions, please contact: support@merbsconnect.com

Last Updated: December 31, 2024
Version: 1.0.0

