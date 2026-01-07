# Postman Quick Start - MerbsConnect API

## 🚀 Quick Setup (5 Minutes)

### Step 1: Login to Get Token

**Request:**
```
POST http://localhost:9000/api/v1/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "yourPassword"
}
```

**Copy this from response:**
```json
{
  "token": "eyJhbGc... ← COPY THIS"
}
```

### Step 2: Create Event

**Request:**
```
POST http://localhost:9000/api/v1/events
Authorization: Bearer eyJhbGc...  ← PASTE TOKEN HERE
Content-Type: application/json

{
  "title": "My Test Event",
  "description": "Testing from Postman",
  "location": "New York",
  "date": "2024-12-31",
  "time": "10:00:00",
  "speakers": [],
  "contacts": [],
  "sponsors": []
}
```

## 📋 Common Requests

### View All Events (No Token Needed)
```
GET http://localhost:9000/api/v1/events
```

### View Specific Event (No Token Needed)
```
GET http://localhost:9000/api/v1/events/1
```

### Update Event (Token Required)
```
PUT http://localhost:9000/api/v1/events/1
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "title": "Updated Event Title",
  ...
}
```

### Delete Event (Token Required)
```
DELETE http://localhost:9000/api/v1/events/1
Authorization: Bearer YOUR_TOKEN
```

## ⚠️ Common Mistakes

❌ **WRONG:** `Authorization: eyJhbGc...`  
✅ **CORRECT:** `Authorization: Bearer eyJhbGc...`

❌ **WRONG:** Date: `"2024-1-1"`  
✅ **CORRECT:** Date: `"2024-01-01"`

❌ **WRONG:** Time: `"10:00"`  
✅ **CORRECT:** Time: `"10:00:00"`

## 🔧 Troubleshooting

| Error | Solution |
|-------|----------|
| 401 Unauthorized | Login first to get token |
| 403 Forbidden | Add `Bearer ` before token |
| 500 Server Error | Check date is in future, not duplicate |

## 💾 Save Time: Use Environment

1. Create environment: "MerbsConnect"
2. Add variable: `base_url` = `http://localhost:9000`
3. Add variable: `jwt_token` = (empty)
4. In login request Tests tab:
   ```javascript
   pm.environment.set("jwt_token", pm.response.json().token);
   ```
5. Use `{{jwt_token}}` in Authorization headers

Done! 🎉
