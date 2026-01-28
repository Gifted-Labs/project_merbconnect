# MerbsConnect API Documentation

**Version:** 1.0.0  
**Base URL:** `http://localhost:9000/api`  
**Swagger UI:** `/swagger-ui.html`

---

## Table of Contents

1. [Authentication](#1-authentication)
2. [Events](#2-events)
3. [Event Check-in](#3-event-check-in)
4. [Event Reviews](#4-event-reviews)
5. [Event Gallery](#5-event-gallery)
6. [Event Articles](#6-event-articles)
7. [Admin - User Management](#7-admin---user-management)
8. [Admin - System Monitoring](#8-admin---system-monitoring)
9. [Admin - Configuration](#9-admin---configuration)
10. [Admin - Security & Audit](#10-admin---security--audit)
11. [Academics](#11-academics)
12. [Event Speakers V2 (Enhanced)](#12-event-speakers-v2-enhanced)
13. [Event Itinerary (Program Lineup)](#13-event-itinerary-program-lineup)
14. [Enhanced Event Registration](#14-enhanced-event-registration)
15. [Event with Theme](#15-event-with-theme)


---

## Authentication & Authorization

Most endpoints require authentication via JWT Bearer tokens. Include the token in the `Authorization` header:
```
Authorization: Bearer <your_jwt_token>
```

### Role Hierarchy
| Role | Description |
|------|-------------|
| `SUPER_ADMIN` | Full system access |
| `ADMIN` | Administrative operations |
| `SUPPORT_ADMIN` | Support and event staff operations |
| `USER` | Standard authenticated user |

---

## 1. Authentication

**Base Path:** `/api/v1/auth`

### 1.1 Register User
```http
POST /api/v1/auth/register
```
**Access:** Public

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "SecurePass123!",
  "phone": "+233244000000"
}
```

**Response:** `201 Created`
```json
{
  "message": "Registration successful. Please check your email to verify your account."
}
```

---

### 1.2 Login
```http
POST /api/v1/auth/login
```
**Access:** Public

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass123!"
}
```

**Response:** `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

### 1.3 Verify Email
```http
GET /api/v1/auth/verify-email?token={token}
```
**Access:** Public

**Response:** `200 OK`
```json
{
  "message": "Email verified successfully."
}
```

---

### 1.4 Forgot Password
```http
POST /api/v1/auth/forgot-password?email={email}
```
**Access:** Public

**Response:** `200 OK`
```json
{
  "message": "Password reset instructions sent to your email."
}
```

---

### 1.5 Reset Password
```http
POST /api/v1/auth/reset-password
```
**Access:** Public

**Request Body:**
```json
{
  "token": "reset-token-from-email",
  "newPassword": "NewSecurePass123!"
}
```

---

### 1.6 Refresh Token
```http
POST /api/v1/auth/refresh-token
```
**Access:** Public

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI..."
}
```

---

### 1.7 Resend Token
```http
POST /api/v1/auth/resend-token?email={email}&tokenType={VERIFICATION|PASSWORD_RESET}
```
**Access:** Public

---

## 2. Events

**Base Path:** `/api/events`

### 2.1 Create Event
```http
POST /api/events
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

**Request Body:**
```json
{
  "title": "Annual Tech Conference 2026",
  "description": "A comprehensive technology conference",
  "startDate": "2026-03-15T09:00:00",
  "endDate": "2026-03-15T17:00:00",
  "location": "Accra International Conference Centre",
  "maxAttendees": 500,
  "registrationDeadline": "2026-03-10T23:59:59",
  "category": "CONFERENCE",
  "speakers": [
    {
      "name": "Dr. Jane Smith",
      "bio": "AI Research Lead",
      "photoUrl": "https://example.com/photo.jpg"
    }
  ]
}
```

**Response:** `201 Created`

---

### 2.2 Get All Events
```http
GET /api/events?page=0&size=10&sort=startDate,desc
```
**Access:** Public

---

### 2.3 Get Event by ID
```http
GET /api/events/{eventId}
```
**Access:** Public

---

### 2.4 Get Upcoming Events
```http
GET /api/events/upcoming?page=0&size=10
```
**Access:** Public

---

### 2.5 Get Past Events
```http
GET /api/events/past?page=0&size=10
```
**Access:** Public

---

### 2.6 Update Event
```http
PUT /api/events/{eventId}
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

---

### 2.7 Delete Event
```http
DELETE /api/events/{eventId}
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

---

### 2.8 Event Registration (Legacy)
```http
POST /api/events/{eventId}/register
```
**Access:** Public

**Request Body:**
```json
{
  "email": "attendee@example.com",
  "name": "Attendee Name",
  "phone": "+233244000000",
  "note": "Dietary requirements: Vegetarian"
}
```

---

### 2.9 Get Event Registrations
```http
GET /api/events/{eventId}/registrations?page=0&size=10
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

---

### 2.10 Download Registrations (CSV)
```http
GET /api/events/{eventId}/registrations/download
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

**Response:** CSV file download

---

### 2.11 Get Event Statistics
```http
GET /api/events/stats
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

**Response:**
```json
{
  "totalEvents": 25,
  "upcomingEvents": 5,
  "pastEvents": 20,
  "totalRegistrations": 1500,
  "averageAttendance": 60
}
```

---

### 2.12 Send Bulk SMS to Registrations
```http
POST /api/events/{eventId}/registrations/send-sms
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

**Request Body:**
```json
{
  "registrationIds": [1, 2, 3, 4],
  "message": "Reminder: Event starts tomorrow at 9 AM",
  "senderId": "MerbsConnect"
}
```

---

## 3. Event Check-in

**Base Path:** `/api/v1/events/{eventId}`

### 3.1 Register for Event (Enhanced)
```http
POST /api/v1/events/{eventId}/register-v2
```
**Access:** Public

**Request Body:**
```json
{
  "email": "attendee@example.com",
  "name": "Attendee Name",
  "phone": "+233244000000",
  "note": "Optional notes",
  "needsShirt": true,
  "shirtSize": "L"
}
```

**Shirt Sizes (ShirtSize Enum):**
| Value | Display Name |
|-------|--------------|
| `XS` | Extra Small |
| `S` | Small |
| `M` | Medium |
| `L` | Large |
| `XL` | Extra Large |
| `XXL` | 2X Large |
| `XXXL` | 3X Large |

**Response:** `201 Created`
```json
{
  "id": 1,
  "eventId": 5,
  "eventTitle": "Annual Tech Conference",
  "registrationToken": "abc123def456",
  "qrCodeBase64": "data:image/png;base64,iVBORw0...",
  "email": "attendee@example.com",
  "name": "Attendee Name",
  "phone": "+233244000000",
  "checkedIn": false,
  "registeredAt": "2026-01-12T10:00:00",
  "needsShirt": true,
  "shirtSize": "L"
}
```

**Side Effects:**
- ✉️ Confirmation email sent with QR code
- 📱 SMS notification sent to participant
- 👕 If `needsShirt=true`: SMS alert sent to support admin

---

### 3.2 Check-in Participant
```http
POST /api/v1/events/{eventId}/check-in
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

**Request Body:**
```json
{
  "registrationToken": "abc123def456"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Check-in successful! Welcome, Attendee Name",
  "participantName": "Attendee Name",
  "participantEmail": "attendee@example.com",
  "eventTitle": "Annual Tech Conference",
  "checkInTime": "2026-01-12T09:15:00"
}
```

---

### 3.3 Get Check-in Statistics
```http
GET /api/v1/events/{eventId}/check-in/stats
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

**Response:**
```json
{
  "eventId": 1,
  "eventTitle": "Annual Tech Conference",
  "totalRegistrations": 150,
  "checkedInCount": 45,
  "pendingCount": 105,
  "checkInPercentage": 30.0
}
```

---

### 3.4 Get Registration by Token
```http
GET /api/v1/events/{eventId}/registration?token={registrationToken}
```
**Access:** Public

---

### 3.5 Download Ticket PDF
```http
GET /api/v1/events/{eventId}/registration/ticket?token={registrationToken}
```
**Access:** Public

**Response:** PDF file download (`application/pdf`)

The PDF ticket includes:
- Event title and branding
- Participant name and email
- Event date, time, and location
- QR code for check-in
- T-shirt size (if selected)
- Modern design with red accent theme

---


## 4. Event Reviews

**Base Path:** `/api/v1/events/{eventId}/reviews`


### 4.1 Submit Review
```http
POST /api/v1/events/{eventId}/reviews
```
**Access:** Authenticated users

**Request Body:**
```json
{
  "rating": 5,
  "comment": "Excellent event! Well organized and informative."
}
```

**Response:** `201 Created`

---

### 4.2 Get Reviews
```http
GET /api/v1/events/{eventId}/reviews?page=0&size=10
```
**Access:** Public

**Response:**
```json
{
  "reviews": [...],
  "averageRating": 4.5,
  "totalReviews": 25,
  "ratingDistribution": {
    "5": 15,
    "4": 7,
    "3": 2,
    "2": 1,
    "1": 0
  }
}
```

---

### 4.3 Update Review
```http
PUT /api/v1/events/{eventId}/reviews/{reviewId}
```
**Access:** Review owner only

---

### 4.4 Delete Review
```http
DELETE /api/v1/events/{eventId}/reviews/{reviewId}
```
**Access:** Review owner or Admin

---

## 5. Event Gallery

**Base Path:** `/api/v1/events/{eventId}/gallery`

### 5.1 Upload Media
```http
POST /api/v1/events/{eventId}/gallery
Content-Type: multipart/form-data
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

**Form Data:**
- `file`: Image/Video file (max 10MB for images, 100MB for videos)
- `caption`: Optional description
- `type`: `IMAGE` or `VIDEO`

---

### 5.2 Get Gallery
```http
GET /api/v1/events/{eventId}/gallery
```
**Access:** Public

**Response:**
```json
{
  "eventId": 1,
  "items": [
    {
      "id": 1,
      "url": "https://storage.railway.app/...",
      "type": "IMAGE",
      "caption": "Opening ceremony",
      "uploadedAt": "2026-01-12T10:00:00"
    }
  ],
  "googleDriveFolderLink": "https://drive.google.com/..."
}
```

---

### 5.3 Delete Gallery Item
```http
DELETE /api/v1/events/{eventId}/gallery/{itemId}
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

---

### 5.4 Update Google Drive Link
```http
PUT /api/v1/events/{eventId}/gallery/drive-link
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

**Request Body:**
```json
{
  "googleDriveFolderLink": "https://drive.google.com/drive/folders/..."
}
```

---

## 6. Event Articles

**Base Path:** `/api/v1/events/{eventId}/articles`

### 6.1 Create Article
```http
POST /api/v1/events/{eventId}/articles
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

**Request Body:**
```json
{
  "title": "Conference Highlights",
  "content": "Full markdown-supported article content...",
  "author": "Editorial Team"
}
```

---

### 6.2 Get Articles
```http
GET /api/v1/events/{eventId}/articles
```
**Access:** Public

---

### 6.3 Get Article by ID
```http
GET /api/v1/events/{eventId}/articles/{articleId}
```
**Access:** Public

---

### 6.4 Update Article
```http
PUT /api/v1/events/{eventId}/articles/{articleId}
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

---

### 6.5 Delete Article
```http
DELETE /api/v1/events/{eventId}/articles/{articleId}
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

---

## 7. Admin - User Management

**Base Path:** `/api/v1/admin/users`

### 7.1 Get All Users
```http
GET /api/v1/admin/users?page=0&size=10
```
**Access:** `SUPER_ADMIN`, `ADMIN`

---

### 7.2 Get User by ID
```http
GET /api/v1/admin/users/{id}
```
**Access:** `SUPER_ADMIN`, `ADMIN`

---

### 7.3 Create User
```http
POST /api/v1/admin/users
```
**Access:** `SUPER_ADMIN` only

**Request Body:**
```json
{
  "firstName": "New",
  "lastName": "Admin",
  "email": "new.admin@example.com",
  "password": "TempPassword123!",
  "role": "ADMIN",
  "phone": "+233244000000"
}
```

---

### 7.4 Update User
```http
PUT /api/v1/admin/users/{id}
```
**Access:** `SUPER_ADMIN`, `ADMIN`

---

### 7.5 Delete User
```http
DELETE /api/v1/admin/users/{id}
```
**Access:** `SUPER_ADMIN` only

---

### 7.6 Get User Activity
```http
GET /api/v1/admin/users/{id}/activity?page=0&size=10
```
**Access:** `SUPER_ADMIN`, `ADMIN`

---

## 8. Admin - System Monitoring

**Base Path:** `/api/admin/system`

### 8.1 Get System Statistics
```http
GET /api/admin/system/stats
```
**Access:** `SUPER_ADMIN`, `ADMIN`, `SUPPORT_ADMIN`

**Response:**
```json
{
  "serverInfo": {
    "jvmVersion": "21.0.9",
    "osName": "Windows 11",
    "osVersion": "10.0"
  },
  "memoryInfo": {
    "heapUsed": "256MB",
    "heapMax": "512MB",
    "heapUsagePercentage": 50
  },
  "databaseStats": {
    "totalUsers": 150,
    "totalEvents": 25,
    "totalRegistrations": 1500
  },
  "uptime": "5 days, 3 hours"
}
```

---

### 8.2 Get Recent Logs
```http
GET /api/admin/system/logs?limit=100
```
**Access:** `SUPER_ADMIN`, `ADMIN`

---

## 9. Admin - Configuration

**Base Path:** `/api/admin/config`

### 9.1 Get All Configurations
```http
GET /api/admin/config
```
**Access:** `SUPER_ADMIN` only

---

### 9.2 Update Configuration
```http
PUT /api/admin/config
```
**Access:** `SUPER_ADMIN` only

**Request Body:**
```json
{
  "configKey": "app.max-file-upload-size",
  "configValue": "10485760",
  "description": "Maximum file upload size in bytes"
}
```

---

## 10. Admin - Security & Audit

**Base Path:** `/api/v1/admin`

### 10.1 Get Active Sessions
```http
GET /api/v1/admin/security/sessions
```
**Access:** `SUPER_ADMIN` only

---

### 10.2 Terminate Session
```http
DELETE /api/v1/admin/security/sessions/{sessionId}
```
**Access:** `SUPER_ADMIN` only

---

## 11. Academics

The academics module provides endpoints for managing colleges, faculties, departments, programs, courses, quizzes, and resources.

**Base Paths:**
- `/api/academics/colleges`
- `/api/academics/faculties`
- `/api/academics/departments`
- `/api/academics/programs`
- `/api/academics/courses`
- `/api/academics/quizzes`
- `/api/academics/resources`
- `/api/academics/reference-materials`

*Detailed documentation for academic endpoints follows standard CRUD patterns.*

---

## Error Responses

All endpoints return standard error responses:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "timestamp": "2026-01-12T10:00:00",
  "path": "/api/events"
}
```

### Common Error Codes
| Code | Description |
|------|-------------|
| `400` | Bad Request - Invalid input |
| `401` | Unauthorized - Authentication required |
| `403` | Forbidden - Insufficient permissions |
| `404` | Not Found - Resource doesn't exist |
| `409` | Conflict - Duplicate resource |
| `500` | Internal Server Error |

---

## Rate Limiting

API endpoints are rate-limited to prevent abuse:
- **Authentication endpoints:** 10 requests/minute
- **General endpoints:** 100 requests/minute

---

## Pagination

Paginated endpoints accept:
- `page`: Page number (0-indexed)
- `size`: Items per page (default: 20, max: 100)
- `sort`: Field and direction (e.g., `createdAt,desc`)

Response format:
```json
{
  "content": [...],
  "totalElements": 150,
  "totalPages": 15,
  "size": 10,
  "number": 0,
  "first": true,
  "last": false
}
```

---

## 12. Event Speakers V2 (Enhanced)

**Base Path:** `/api/v1/events/{eventId}/speakers-v2`

The enhanced speakers API supports S3 image uploads instead of providing image URLs.

### 12.1 Add Speaker to Event
```http
POST /api/v1/events/{eventId}/speakers-v2
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

**Request Body:**
```json
{
  "name": "Dr. Jane Smith",
  "title": "AI Research Lead",
  "bio": "Dr. Smith has over 15 years of experience in artificial intelligence...",
  "linkedinUrl": "https://linkedin.com/in/janesmith",
  "twitterUrl": "https://twitter.com/janesmith",
  "displayOrder": 1
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "name": "Dr. Jane Smith",
  "title": "AI Research Lead",
  "bio": "Dr. Smith has over 15 years of experience...",
  "imageUrl": null,
  "linkedinUrl": "https://linkedin.com/in/janesmith",
  "twitterUrl": "https://twitter.com/janesmith",
  "displayOrder": 1,
  "createdAt": "2026-01-25T09:00:00",
  "updatedAt": "2026-01-25T09:00:00"
}
```

---

### 12.2 Upload Speaker Image
```http
POST /api/v1/events/{eventId}/speakers-v2/{speakerId}/image
Content-Type: multipart/form-data
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

**Form Data:**
- `file`: Image file (JPEG, PNG, GIF, WebP - max 10MB)

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Dr. Jane Smith",
  "imageUrl": "https://storage.railway.app/bucket/events/1/gallery/images/speaker_abc123.jpg",
  ...
}
```

---

### 12.3 Get All Speakers
```http
GET /api/v1/events/{eventId}/speakers-v2
```
**Access:** Public

**Response:** `200 OK` - Array of speakers ordered by displayOrder

---

### 12.4 Get Speaker by ID
```http
GET /api/v1/events/{eventId}/speakers-v2/{speakerId}
```
**Access:** Public

---

### 12.5 Update Speaker
```http
PUT /api/v1/events/{eventId}/speakers-v2/{speakerId}
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

---

### 12.6 Delete Speaker
```http
DELETE /api/v1/events/{eventId}/speakers-v2/{speakerId}
```
**Access:** `ADMIN`, `SUPER_ADMIN`

---

### 12.7 Delete Speaker Image
```http
DELETE /api/v1/events/{eventId}/speakers-v2/{speakerId}/image
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

---

## 13. Event Itinerary (Program Lineup)

**Base Path:** `/api/v1/events/{eventId}/itinerary`

Manage the event program lineup with ordered activities.

### 13.1 Add Itinerary Item
```http
POST /api/v1/events/{eventId}/itinerary
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

**Request Body:**
```json
{
  "title": "Opening Prayer",
  "description": "Welcome and opening devotion",
  "startTime": "09:00:00",
  "endTime": "09:15:00",
  "speakerName": "Pastor John",
  "venue": "Main Hall",
  "displayOrder": 1,
  "itemType": "WORSHIP"
}
```

**Itinerary Item Types (ItineraryItemType Enum):**
| Value | Display Name |
|-------|--------------|
| `CEREMONY` | Ceremony |
| `SESSION` | Session/Talk |
| `WORKSHOP` | Workshop |
| `PANEL` | Panel Discussion |
| `BREAK` | Break |
| `MEAL` | Meal/Refreshments |
| `NETWORKING` | Networking |
| `WORSHIP` | Worship/Prayer |
| `REGISTRATION` | Registration/Check-in |
| `ENTERTAINMENT` | Entertainment |
| `AWARDS` | Awards/Recognition |
| `CLOSING` | Closing Ceremony |
| `OTHER` | Other |

**Response:** `201 Created`
```json
{
  "id": 1,
  "title": "Opening Prayer",
  "description": "Welcome and opening devotion",
  "startTime": "09:00:00",
  "endTime": "09:15:00",
  "speakerName": "Pastor John",
  "venue": "Main Hall",
  "displayOrder": 1,
  "itemType": "WORSHIP",
  "itemTypeDisplayName": "Worship/Prayer",
  "durationMinutes": 15
}
```

---

### 13.2 Bulk Add Itinerary Items
```http
POST /api/v1/events/{eventId}/itinerary/bulk
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

**Request Body:** Array of itinerary items
```json
[
  {
    "title": "Registration",
    "startTime": "08:00:00",
    "endTime": "09:00:00",
    "itemType": "REGISTRATION"
  },
  {
    "title": "Opening Ceremony",
    "startTime": "09:00:00",
    "endTime": "09:30:00",
    "itemType": "CEREMONY"
  }
]
```

---

### 13.3 Get Event Itinerary
```http
GET /api/v1/events/{eventId}/itinerary
```
**Access:** Public

**Response:** Array of itinerary items ordered by displayOrder and startTime

---

### 13.4 Update Itinerary Item
```http
PUT /api/v1/events/{eventId}/itinerary/{itemId}
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

---

### 13.5 Delete Itinerary Item
```http
DELETE /api/v1/events/{eventId}/itinerary/{itemId}
```
**Access:** `ADMIN`, `SUPER_ADMIN`

---

### 13.6 Reorder Itinerary
```http
PUT /api/v1/events/{eventId}/itinerary/reorder
```
**Access:** `ADMIN`, `SUPER_ADMIN`, `SUPPORT_ADMIN`

**Request Body:** Array of item IDs in desired order
```json
[3, 1, 2, 5, 4]
```

---

## 14. Enhanced Event Registration

The event registration now captures comprehensive university student information.

### 14.1 Register for Event (Enhanced)
```http
POST /api/v1/events/{eventId}/register
```
**Access:** Public

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john.doe@student.edu",
  "phone": "+233244000000",
  "note": "Looking forward to the event!",
  "needsShirt": true,
  "shirtSize": "L",
  "program": "Computer Science",
  "academicLevel": "LEVEL_300",
  "university": "University of Ghana",
  "department": "Computer Science Department",
  "referralSource": "SOCIAL_MEDIA",
  "referralSourceOther": null,
  "studentId": "10856789",
  "dietaryRestrictions": "Vegetarian",
  "emergencyContactName": "Jane Doe",
  "emergencyContactPhone": "+233244111111"
}
```

**Academic Level (AcademicLevel Enum):**
| Value | Display Name |
|-------|--------------|
| `LEVEL_100` | 100 Level - Freshman |
| `LEVEL_200` | 200 Level - Sophomore |
| `LEVEL_300` | 300 Level - Junior |
| `LEVEL_400` | 400 Level - Senior |
| `LEVEL_500` | 500 Level - Fifth Year |
| `LEVEL_600` | 600 Level - Sixth Year |
| `GRADUATE` | Graduate Student |
| `POSTGRADUATE` | Postgraduate Student |
| `PHD` | PhD Candidate |
| `ALUMNI` | Alumni |
| `OTHER` | Other |

**Referral Source (ReferralSource Enum):**
| Value | Display Name |
|-------|--------------|
| `SOCIAL_MEDIA` | Social Media (Facebook, Instagram, Twitter, etc.) |
| `WHATSAPP` | WhatsApp |
| `FRIEND_OR_COLLEAGUE` | Friend or Colleague |
| `UNIVERSITY_NOTICE` | University Notice Board/Announcement |
| `EMAIL` | Email |
| `WEBSITE` | Website/Blog |
| `FLYER_POSTER` | Flyer/Poster |
| `RADIO` | Radio |
| `CHURCH_FELLOWSHIP` | Church/Fellowship |
| `PREVIOUS_EVENT` | Attended Previous Event |
| `SPEAKER_RECOMMENDATION` | Speaker Recommendation |
| `DEPARTMENT_FACULTY` | Department/Faculty |
| `STUDENT_ORGANIZATION` | Student Organization |
| `OTHER` | Other |

**Response:** `201 Created`
```json
{
  "id": 1,
  "eventId": 5,
  "eventTitle": "Annual Tech Conference",
  "name": "John Doe",
  "email": "john.doe@student.edu",
  "phone": "+233244000000",
  "registrationToken": "abc123def456",
  "qrCodeBase64": "data:image/png;base64,iVBORw0...",
  "checkedIn": false,
  "registeredAt": "2026-01-25T10:00:00",
  "needsShirt": true,
  "shirtSize": "L",
  "program": "Computer Science",
  "academicLevel": "LEVEL_300",
  "university": "University of Ghana",
  "department": "Computer Science Department",
  "referralSource": "SOCIAL_MEDIA",
  "referralSourceOther": null,
  "studentId": "10856789",
  "dietaryRestrictions": "Vegetarian",
  "emergencyContactName": "Jane Doe",
  "emergencyContactPhone": "+233244111111"
}
```

---

## 15. Event with Theme

Events now support a theme field and return enhanced speakers and itinerary.

### 15.1 Create Event with Theme
```http
POST /api/v1/events
```

**Request Body:**
```json
{
  "title": "Annual Tech Conference 2026",
  "description": "A comprehensive technology conference",
  "location": "Accra International Conference Centre",
  "date": "2026-03-15",
  "time": "09:00:00",
  "imageUrl": "https://example.com/event-banner.jpg",
  "theme": "Innovation & Digital Transformation",
  "speakers": [...],
  "contacts": [...],
  "sponsors": [...]
}
```

### 15.2 Event Response (Enhanced)
```json
{
  "id": 1,
  "title": "Annual Tech Conference 2026",
  "description": "A comprehensive technology conference",
  "location": "Accra International Conference Centre",
  "date": "2026-03-15",
  "time": "09:00:00",
  "imageUrl": "https://example.com/event-banner.jpg",
  "theme": "Innovation & Digital Transformation",
  "speakers": [...],
  "speakersV2": [
    {
      "id": 1,
      "name": "Dr. Jane Smith",
      "title": "AI Research Lead",
      "bio": "...",
      "imageUrl": "https://storage.railway.app/...",
      "linkedinUrl": "...",
      "displayOrder": 1
    }
  ],
  "itinerary": [
    {
      "id": 1,
      "title": "Opening Ceremony",
      "startTime": "09:00:00",
      "endTime": "09:30:00",
      "itemType": "CEREMONY",
      "itemTypeDisplayName": "Ceremony"
    }
  ],
  "sponsors": [...],
  "contacts": [...],
  "videoUrl": null
}
```

---

