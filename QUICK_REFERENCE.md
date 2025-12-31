# MerbsConnect Authentication API - Quick Reference Guide

## 🚀 Quick Start

### Access Swagger UI
**URL**: `http://localhost:8080/swagger-ui.html`

### Build & Run Project
```bash
cd C:\Users\aaa\Documents\merbsconnect
mvn clean install
mvn spring-boot:run
```

---

## 📋 Endpoint Reference

### Authentication Endpoints

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/v1/auth/signup` | POST | ❌ | Register new user |
| `/api/v1/auth/login` | POST | ❌ | Login & get tokens |
| `/api/v1/auth/verify-email` | GET | ❌ | Verify email address |
| `/api/v1/auth/forgot-password` | POST | ❌ | Request password reset |
| `/api/v1/auth/reset-password` | POST | ❌ | Reset password |
| `/api/v1/auth/refresh-token` | POST | ❌ | Get new access token |
| `/api/v1/auth/resend-token` | POST | ❌ | Resend verification/reset email |

---

## 🔐 Authentication

### Getting Started
1. **Register**: `POST /signup` → Receive verification email
2. **Verify**: `GET /verify-email?token=TOKEN` → Activate account
3. **Login**: `POST /login` → Receive JWT tokens
4. **Use**: Include token in header: `Authorization: Bearer TOKEN`

### Token Management
- **Access Token**: Valid for 1 hour
- **Refresh Token**: Valid for 7 days
- **Refresh**: `POST /refresh-token` when access token expires

### Using Tokens
```bash
curl -X GET "http://localhost:8080/api/v1/users/profile" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

## ✅ Password Requirements

Passwords must contain:
- ✓ Minimum 8 characters
- ✓ At least one UPPERCASE letter
- ✓ At least one lowercase letter
- ✓ At least one digit (0-9)
- ✓ At least one special character (@#$%^&+=)

**Example Valid Password**: `SecurePass123!`

---

## 📧 Email Requirements

- Must be a valid email format
- Must be unique across the system
- Verification required before login
- Used as username for login

---

## 🔄 Complete User Journey

```
1. POST /signup
   ↓
2. Receive verification email
   ↓
3. GET /verify-email?token=TOKEN
   ↓
4. POST /login
   ↓
5. Receive access token & refresh token
   ↓
6. Use token for API requests
   ↓
7. POST /refresh-token (when access token expires)
   ↓
8. Continue using new token
```

---

## 📊 HTTP Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| 200 | Success | Login successful, token generated |
| 400 | Bad Request | Invalid input, validation failed |
| 401 | Unauthorized | Invalid credentials, expired token |
| 403 | Forbidden | Email not verified |
| 404 | Not Found | User not found |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Server Error | Internal server error |

---

## 🧪 Testing Endpoints

### Using Swagger UI
1. Navigate to `http://localhost:8080/swagger-ui.html`
2. Expand endpoint
3. Click "Try it out"
4. Fill in parameters
5. Click "Execute"

### Using cURL
See `CURL_EXAMPLES.md` for detailed cURL commands

### Using Postman
1. Import OpenAPI spec: `http://localhost:8080/v3/api-docs`
2. Create request from collection
3. Fill parameters
4. Send request

---

## 🛠️ Request/Response Examples

### Registration Request
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

### Login Response
```json
{
  "token": "eyJhbGc...",
  "type": "Bearer",
  "refreshToken": "eyJhbGc...",
  "id": 1,
  "username": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

---

## ⚠️ Common Errors

| Issue | Solution |
|-------|----------|
| Email already exists | Use different email |
| Weak password | Add uppercase, digit, special char |
| Invalid email format | Use valid email (user@domain.com) |
| Email not verified | Click verification link |
| Invalid token | Request new token |
| Expired access token | Use refresh token to get new one |
| Rate limit exceeded | Wait 1 minute before retry |

---

## 📚 Documentation Files

| File | Purpose |
|------|---------|
| `API_DOCUMENTATION.md` | Complete API reference |
| `CURL_EXAMPLES.md` | cURL command examples |
| `SWAGGER_SETUP_SUMMARY.md` | Setup details and features |
| `QUICK_REFERENCE.md` | This file (quick lookup) |

---

## 🔗 Useful Links

| Resource | URL |
|----------|-----|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| OpenAPI YAML | `http://localhost:8080/v3/api-docs.yaml` |

---

## 📱 API Features

✅ JWT Authentication  
✅ Email Verification  
✅ Password Reset  
✅ Token Refresh  
✅ Rate Limiting  
✅ Input Validation  
✅ Error Handling  
✅ CORS Support  
✅ Request/Response Logging  
✅ Comprehensive API Docs  

---

## 🔒 Security Notes

- Never expose tokens in logs
- Always use HTTPS in production
- Store tokens securely (httpOnly cookies)
- Implement token refresh before expiration
- Log out by discarding token
- Monitor suspicious login attempts
- Implement 2FA for additional security

---

## 🐛 Troubleshooting

### Port 8080 Already in Use
```bash
# Find process using port
netstat -ano | findstr :8080

# Kill process (replace PID)
taskkill /PID PID_NUMBER /F

# Or use different port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Email Not Received
- Check spam/junk folder
- Verify email address is correct
- Check application logs
- Try resend token endpoint

### JWT Token Issues
- Verify token format: `Bearer TOKEN`
- Check token expiration: 1 hour
- Use refresh token to get new one
- Ensure Authorization header is present

### Database Connection Issues
- Verify PostgreSQL is running
- Check database URL in `application.yaml`
- Verify username/password
- Check database exists

---

## 📞 Support

**Issues**: Check logs in application console  
**Questions**: See `API_DOCUMENTATION.md`  
**Examples**: See `CURL_EXAMPLES.md`  
**Contact**: support@merbsconnect.com  

---

## 📝 Notes

- All timestamps in UTC
- Tokens are JWT format
- Rate limit window: 1 hour
- Token resend cooldown: 1 minute
- Maximum resend attempts: 3

---

**Last Updated**: December 31, 2025  
**Version**: 1.0.0  
**Status**: ✅ Production Ready

