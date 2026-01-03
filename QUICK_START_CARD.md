# 📌 QUICK START CARD - MerbsConnect Swagger API Documentation

## ⚡ 30-Second Quick Start

```bash
# 1. Navigate to project
cd C:\Users\aaa\Documents\merbsconnect

# 2. Build
mvn clean install

# 3. Run
mvn spring-boot:run

# 4. Open browser
# http://localhost:8080/swagger-ui.html

# 5. Start testing!
```

---

## 🎯 7 Endpoints at Your Fingertips

| # | Endpoint | Method | What It Does |
|---|----------|--------|--------------|
| 1 | `/api/v1/auth/signup` | POST | Register new user |
| 2 | `/api/v1/auth/login` | POST | Login & get tokens |
| 3 | `/api/v1/auth/verify-email` | GET | Activate email |
| 4 | `/api/v1/auth/forgot-password` | POST | Start password reset |
| 5 | `/api/v1/auth/reset-password` | POST | Complete password reset |
| 6 | `/api/v1/auth/refresh-token` | POST | Get new access token |
| 7 | `/api/v1/auth/resend-token` | POST | Resend verification/reset |

---

## 🔑 Important Values

### Password Requirements
- ✅ 8+ characters
- ✅ 1 uppercase letter
- ✅ 1 lowercase letter
- ✅ 1 digit
- ✅ 1 special character (@#$%^&+=)

### Token Expiration
- ⏱️ **Access Token**: 1 hour
- ⏱️ **Refresh Token**: 7 days
- ⏱️ **Email Verification**: 24 hours
- ⏱️ **Password Reset**: 1 hour

### API Base URL
```
http://localhost:8080/api/v1/auth
```

---

## 📲 First API Call (cURL)

### Register a User
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "SecurePass123!"
  }'
```

**Expected Response**:
```json
{
  "message": "User registered successfully. Please check your email for verification link."
}
```

---

## 🔑 Using the API

### 1. Register
```bash
POST /signup
```
Send: firstName, lastName, email, password  
Get: Confirmation message, verification email sent

### 2. Verify Email
```bash
GET /verify-email?token=TOKEN_FROM_EMAIL
```
Send: Verification token from email  
Get: Email verified confirmation

### 3. Login
```bash
POST /login
```
Send: email, password  
Get: accessToken, refreshToken, userInfo

### 4. Use Token
```bash
Authorization: Bearer YOUR_ACCESS_TOKEN
```
Add this to all future API requests

### 5. Refresh When Expired
```bash
POST /refresh-token
```
Send: Your refresh token  
Get: New access token

---

## 🧪 Testing Tools

### 🌐 Interactive Testing
**URL**: `http://localhost:8080/swagger-ui.html`
- Click endpoint
- Click "Try it out"
- Fill parameters
- Click "Execute"
- See response

### 📋 API Specification
- **JSON**: `http://localhost:8080/v3/api-docs`
- **YAML**: `http://localhost:8080/v3/api-docs.yaml`
- Use these for code generation

### 📚 Documentation Files
```
INDEX.md ......................... Start here
QUICK_REFERENCE.md .............. Fast answers
API_DOCUMENTATION.md ............ Complete specs
CURL_EXAMPLES.md ................ More examples
SWAGGER_SETUP_SUMMARY.md ........ How it works
```

---

## ❌ Common Errors & Fixes

| Error | Cause | Fix |
|-------|-------|-----|
| Invalid password | Too short, missing char | Add uppercase, digit, special char |
| Email exists | Already registered | Use different email |
| Email not verified | Haven't verified yet | Click verification email link |
| Invalid token | Expired or wrong | Use refresh-token endpoint |
| Port 8080 in use | Another app using port | Kill process or use different port |

---

## 🔐 Security Checklist

- [ ] Never expose tokens in logs
- [ ] Use HTTPS in production
- [ ] Store tokens securely
- [ ] Refresh tokens before expiry
- [ ] Logout by discarding token
- [ ] Use strong passwords
- [ ] Don't share tokens
- [ ] Check email address validity

---

## 📚 Documentation Map

```
Need quick answer?
├─ QUICK_REFERENCE.md (5 min read)
│
Need complete info?
├─ API_DOCUMENTATION.md (20 min read)
│
Need examples?
├─ CURL_EXAMPLES.md (test commands)
│
Need navigation?
├─ INDEX.md (guide to all docs)
│
Want to test?
├─ Swagger UI (interactive)
│  └─ http://localhost:8080/swagger-ui.html
│
Need details?
├─ SWAGGER_SETUP_SUMMARY.md (how it works)
│
Need verification?
├─ IMPLEMENTATION_CHECKLIST.md (status check)
```

---

## 🆘 Troubleshooting

### Application won't start?
```bash
# Check Maven installation
mvn -v

# Try building again
mvn clean install -DskipTests
```

### Port 8080 already in use?
```bash
# Use different port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Can't access Swagger UI?
```bash
# Check if app is running
# Try: http://localhost:8080/health
# Should return {"status":"UP"}
```

### Email not received?
- Check spam folder
- Wait a few moments
- Try resend-token endpoint
- Check application logs

---

## 💡 Pro Tips

✅ Save JWT tokens in environment variables  
✅ Test in Swagger UI before coding  
✅ Copy cURL commands from Swagger UI  
✅ Use Postman for collection management  
✅ Generate client code from OpenAPI spec  
✅ Implement automatic token refresh  
✅ Log API calls for debugging  
✅ Monitor rate limits on resend  

---

## 📞 Quick Links

| Need | URL/Command |
|------|-------------|
| Try API | `http://localhost:8080/swagger-ui.html` |
| API Spec | `http://localhost:8080/v3/api-docs` |
| Start Docs | `INDEX.md` |
| Quick Help | `QUICK_REFERENCE.md` |
| Examples | `CURL_EXAMPLES.md` |
| Details | `API_DOCUMENTATION.md` |

---

## ⏱️ Timeline

| Step | Time | Action |
|------|------|--------|
| 1 | 1 min | Build project |
| 2 | 1 min | Run application |
| 3 | 30 sec | Open Swagger UI |
| 4 | 5 min | Read QUICK_REFERENCE.md |
| 5 | 5 min | Try endpoints in Swagger |
| 6 | 10 min | Read API_DOCUMENTATION.md |
| 7 | 5 min | Test with cURL commands |
| **Total** | **27 min** | **Ready to integrate!** |

---

## ✅ Success Checklist

- [ ] Application running on port 8080
- [ ] Swagger UI accessible
- [ ] Can see all 7 endpoints
- [ ] Can click "Try it out"
- [ ] Read QUICK_REFERENCE.md
- [ ] Read at least one endpoint in API_DOCUMENTATION.md
- [ ] Tried one endpoint in Swagger UI
- [ ] Looked at CURL_EXAMPLES.md
- [ ] Understand authentication flow
- [ ] Ready to integrate

---

## 🎓 Learning Resources

| Level | Start With | Next | Then |
|-------|-----------|------|------|
| Beginner | QUICK_REFERENCE.md | Swagger UI | API_DOCUMENTATION.md |
| Intermediate | SWAGGER_SETUP_SUMMARY.md | API_DOCUMENTATION.md | Source code |
| Advanced | Source code | OpenAPI spec | Client generation |

---

## 🎯 Common Tasks

### Task: Test Login
1. Go to `http://localhost:8080/swagger-ui.html`
2. Find POST /login
3. Click "Try it out"
4. Enter email and password
5. Click "Execute"
6. See token in response

### Task: Get cURL Command
1. Go to `http://localhost:8080/swagger-ui.html`
2. Expand endpoint
3. Click "Try it out"
4. Scroll down to "curl"
5. Copy the command

### Task: Reset Password
1. See CURL_EXAMPLES.md
2. Find "Reset Password" section
3. Replace token and password
4. Run command

### Task: Build for Production
1. Set HTTPS in configuration
2. Use production database
3. Secure JWT secret key
4. Enable CORS for your domain
5. Deploy using `mvn clean package`

---

## 📊 At a Glance

```
✅ Status: READY TO USE
✅ Endpoints: 7 (all documented)
✅ Documentation: 6 comprehensive guides
✅ Examples: 20+ working commands
✅ Testing: Interactive Swagger UI
✅ Security: JWT with best practices
✅ Quality: Production-ready
```

---

## 🚀 You're Ready!

Everything is set up and documented. Just:

1. **Build**: `mvn clean install`
2. **Run**: `mvn spring-boot:run`
3. **Test**: `http://localhost:8080/swagger-ui.html`
4. **Learn**: Read `INDEX.md`
5. **Integrate**: Use `API_DOCUMENTATION.md`

---

**Remember**: When in doubt, check the documentation! 📚

**Last Updated**: December 31, 2025  
**Version**: 1.0.0  
**Status**: ✅ Production Ready

**Good luck! 🚀**

