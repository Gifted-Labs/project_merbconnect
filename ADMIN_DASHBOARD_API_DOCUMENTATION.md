# MerbsConnect Admin Dashboard - Complete API Documentation

## Table of Contents
- [Overview](#overview)
- [Authentication](#authentication)
- [Common Response Structure](#common-response-structure)
- [Admin Endpoints](#admin-endpoints)
- [Event Management Endpoints](#event-management-endpoints)
- [Academic Management Endpoints](#academic-management-endpoints)
- [SMS Communication Endpoints](#sms-communication-endpoints)
- [Request DTOs](#request-dtos)
- [Response DTOs](#response-dtos)
- [Integration Guide](#integration-guide)
- [Error Handling](#error-handling)

---

## Overview

**Base URL:** `http://localhost:9000`  
**API Version:** `v1`  
**Content-Type:** `application/json`  
**Authentication:** JWT Bearer Token

### Technology Stack (Backend)
- **Framework:** Spring Boot 3.4.4
- **Language:** Java 21
- **Database:** PostgreSQL
- **Security:** JWT Authentication with Role-Based Access Control

---

## Authentication

### Login
**Endpoint:** `POST /api/v:1/auth/login`

**Request Body:**
```json
{
  "email": "admin@merbsconnect.com",
  "password": "SecurePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "username": "admin@merbsconnect.com",
  "roles": ["ROLE_ADMIN", "ROLE_USER"]
}
```

### Using the Token
Include the JWT token in all subsequent API requests:

```javascript
headers: {
  'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...',
  'Content-Type': 'application/json'
}
```

### Register New User
**Endpoint:** `POST /api/v1/auth/signup`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "SecurePassword123",
  "phoneNumber": "+1234567890"
}
```

### Other Authentication Endpoints
- `GET /api/v1/auth/verify-email?token={verificationToken}` - Email verification
- `POST /api/v1/auth/forgot-password?email={email}` - Initiate password reset
- `POST /api/v1/auth/reset-password` - Reset password with token
- `POST /api/v1/auth/refresh-token` - Refresh JWT token

---

## Common Response Structure

All endpoints follow this standard response format:

```typescript
interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data?: T;
  timestamp?: string;
}
```

### Pagination Response
```typescript
interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}
```

---

## Admin Endpoints

### User Management

#### Get All Users
**Endpoint:** `GET /api/v1/admin/users`  
**Auth Required:** `ADMIN` or `SUPER_ADMIN`  
**Query Parameters:**
- `page` (default: 0) - Page number
- `size` (default: 10) - Page size

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "phoneNumber": "+1234567890",
      "role": "USER",
      "status": "ACTIVE",
      "isEnabled": true,
      "createdAt": "2024-01-15T10:30:00",
      "lastLogin": "2024-01-20T14:22:00"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 150,
  "totalPages": 15,
  "last": false,
  "first": true
}
```

#### Get User by ID
**Endpoint:** `GET /api/v1/admin/users/{id}`  
**Auth Required:** `ADMIN` or `SUPER_ADMIN`

**Response:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+1234567890",
  "role": "USER",
  "status": "ACTIVE",
  "isEnabled": true,
  "createdAt": "2024-01-15T10:30:00",
  "lastLogin": "2024-01-20T14:22:00"
}
```

#### Create User
**Endpoint:** `POST /api/v1/admin/users`  
**Auth Required:** `SUPER_ADMIN`

**Request Body:**
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@example.com",
  "phoneNumber": "+0987654321",
  "password": "SecurePassword123",
  "role": "ADMIN"
}
```

**Response:** Same as Get User by ID

#### Update User
**Endpoint:** `PUT /api/v1/admin/users/{id}`  
**Auth Required:** `ADMIN` or `SUPER_ADMIN`

**Request Body:**
```json
{
  "firstName": "Jane",
  "lastName": "Smith Updated",
  "email": "jane.smith.updated@example.com",
  "phoneNumber": "+0987654321",
  "role": "ADMIN",
  "status": "ACTIVE",
  "isEnabled": true
}
```

**Note:** All fields are optional in update request

#### Delete User
**Endpoint:** `DELETE /api/v1/admin/users/{id}`  
**Auth Required:** `SUPER_ADMIN`

**Response:**
```json
{
  "message": "User deleted successfully"
}
```

#### Get User Activity
**Endpoint:** `GET /api/v1/admin/users/{id}/activity`  
**Auth Required:** `ADMIN` or `SUPER_ADMIN`  
**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

**Response:**
```json
{
  "userId": 1,
  "username": "john.doe@example.com",
  "activities": [
    {
      "activityType": "LOGIN",
      "endpoint": "/api/v1/auth/login",
      "timestamp": "2024-01-20T14:22:00",
      "details": "Successful login from 192.168.1.100"
    },
    {
      "activityType": "VIEW",
      "endpoint": "/api/v1/events/5",
      "timestamp": "2024-01-20T14:25:00",
      "details": "Viewed event: Tech Conference 2024"
    }
  ]
}
```

### System Monitoring

#### Get System Statistics
**Endpoint:** `GET /api/admin/system/stats`  
**Auth Required:** `ADMIN`, `SUPER_ADMIN`, or `SUPPORT_ADMIN`

**Response:**
```json
{
  "totalUsers": 1250,
  "activeUsers": 980,
  "totalEvents": 45,
  "totalRegistrations": 3400,
  "totalAuditLogs": 12500,
  "uptime": "15 days, 6 hours, 23 minutes",
  "metrics": {
    "memoryUsed": "2.4 GB",
    "memoryTotal": "8 GB",
    "memoryUsagePercentage": 30,
    "cpuUsage": "45%",
    "diskUsage": "65%",
    "activeConnections": 42
  }
}
```

#### Get Application Logs
**Endpoint:** `GET /api/admin/system/logs`  
**Auth Required:** `ADMIN` or `SUPER_ADMIN`  
**Query Parameters:**
- `limit` (default: 100) - Number of log entries to retrieve

**Response:**
```json
[
  "2024-01-20 15:30:00 INFO  --- [http-nio-9000-exec-1] c.m.e.c.EventController : REST request to create Event",
  "2024-01-20 15:30:01 INFO  --- [http-nio-9000-exec-2] c.m.e.s.i.EventServiceImpl : Creating event: Tech Conference 2024",
  "2024-01-20 15:30:02 ERROR --- [http-nio-9000-exec-3] c.m.a.c.AdminUserController : Error fetching user: User not found"
]
```

### Audit & Security

#### Get Audit Logs
**Endpoint:** `GET /api/v1/admin/audit-logs`  
**Auth Required:** `ADMIN` or `SUPER_ADMIN`  
**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "action": "CREATE",
      "entityType": "Event",
      "entityId": 5,
      "performedBy": "admin@merbsconnect.com",
      "timestamp": "2024-01-20T15:30:00",
      "details": "{\"title\":\"Tech Conference 2024\",\"date\":\"2024-02-15\"}",
      "ipAddress": "192.168.1.100"
    },
    {
      "id": 2,
      "action": "UPDATE",
      "entityType": "User",
      "entityId": 12,
      "performedBy": "superadmin@merbsconnect.com",
      "timestamp": "2024-01-20T15:35:00",
      "details": "{\"role\":\"ADMIN\"}",
      "ipAddress": "192.168.1.101"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 12500,
  "totalPages": 1250,
  "last": false,
  "first": true
}
```

#### Get Active Sessions
**Endpoint:** `GET /api/v1/admin/security/sessions`  
**Auth Required:** `SUPER_ADMIN`

**Response:**
```json
[
  {
    "sessionId": "abc123xyz",
    "userId": 1,
    "userEmail": "john.doe@example.com",
    "loginTime": "2024-01-20T14:22:00",
    "lastActivity": "2024-01-20T15:30:00",
    "ipAddress": "192.168.1.100",
    "userAgent": "Mozilla/5.0..."
  }
]
```

**Note:** This is a placeholder endpoint. Actual implementation depends on session management configuration.

#### Terminate Session
**Endpoint:** `DELETE /api/v1/admin/security/sessions/{sessionId}`  
**Auth Required:** `SUPER_ADMIN`

**Response:**
```json
{
  "message": "Session terminated successfully"
}
```

### Configuration Management

#### Get All Configurations
**Endpoint:** `GET /api/admin/config`  
**Auth Required:** `SUPER_ADMIN`

**Response:**
```json
[
  {
    "configKey": "app.jwt.expiration",
    "configValue": "86400",
    "description": "JWT token expiration time in seconds",
    "updatedAt": "2024-01-15T10:00:00",
    "updatedBy": "superadmin@merbsconnect.com"
  },
  {
    "configKey": "app.email.enabled",
    "configValue": "true",
    "description": "Enable/disable email notifications",
    "updatedAt": "2024-01-16T12:00:00",
    "updatedBy": "superadmin@merbsconnect.com"
  }
]
```

#### Update Configuration
**Endpoint:** `PUT /api/admin/config`  
**Auth Required:** `SUPER_ADMIN`

**Request Body:**
```json
{
  "configKey": "app.jwt.expiration",
  "configValue": "172800",
  "description": "JWT token expiration time in seconds (48 hours)"
}
```

**Response:**
```json
{
  "configKey": "app.jwt.expiration",
  "configValue": "172800",
  "description": "JWT token expiration time in seconds (48 hours)",
  "updatedAt": "2024-01-20T15:40:00",
  "updatedBy": "superadmin@merbsconnect.com"
}
```

---

## Event Management Endpoints

### Event CRUD Operations

#### Get All Events
**Endpoint:** `GET /api/v1/events`  
**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Tech Conference 2024",
      "description": "Annual technology conference featuring industry leaders",
      "location": "Convention Center, New York",
      "date": "2024-02-15",
      "time": "09:00:00",
      "imageUrl": "https://example.com/images/tech-conf-2024.jpg",
      "videoUrl": "https://youtube.com/watch?v=xyz123",
      "speakers": [
        {
          "name": "Dr. Jane Smith",
          "title": "Chief Technology Officer",
          "bio": "Expert in AI and Machine Learning",
          "imageUrl": "https://example.com/speakers/jane-smith.jpg"
        }
      ],
      "sponsors": [
        {
          "name": "Tech Corp",
          "logoUrl": "https://example.com/sponsors/techcorp.png",
          "website": "https://techcorp.com"
        }
      ],
      "contacts": [
        {
          "name": "John Admin",
          "email": "admin@techconference.com",
          "phone": "+1234567890"
        }
      ],
      "testimonials": []
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 45,
  "totalPages": 5
}
```

#### Get Event by ID
**Endpoint:** `GET /api/v1/events/{id}`

**Response:** Same structure as individual event in Get All Events

#### Create Event
**Endpoint:** `POST /api/v1/events`  
**Auth Required:** `ADMIN`

**Request Body:**
```json
{
  "title": "Tech Conference 2024",
  "description": "Annual technology conference",
  "location": "Convention Center, New York",
  "date": "2024-02-15",
  "time": "09:00:00",
  "imageUrl": "https://example.com/images/event.jpg",
  "speakers": [
    {
      "name": "Dr. Jane Smith",
      "title": "CTO",
      "bio": "Expert in AI",
      "imageUrl": "https://example.com/speakers/jane.jpg"
    }
  ],
  "contacts": [
    {
      "name": "John Admin",
      "email": "admin@event.com",
      "phone": "+1234567890"
    }
  ],
  "sponsors": []
}
```

#### Update Event
**Endpoint:** `PUT /api/v1/events/{id}`  
**Auth Required:** `ADMIN`

**Request Body:** Same as Create Event (all fields optional)

#### Delete Event
**Endpoint:** `DELETE /api/v1/events/{id}`  
**Auth Required:** `ADMIN`

**Response:**
```json
{
  "message": "Event deleted successfully"
}
```

### Event Analytics & Statistics

#### Get Event Statistics
**Endpoint:** `GET /api/v1/events/admin/stats`  
**Auth Required:** `ADMIN`, `SUPER_ADMIN`, or `SUPPORT_ADMIN`

**Response:**
```json
{
  "totalEvents": 45,
  "upcomingEvents": 12,
  "pastEvents": 33,
  "totalRegistrations": 3400,
  "averageRegistrationsPerEvent": 75.5
}
```

#### Get Event Analytics
**Endpoint:** `GET /api/v1/events/{eventId}/analytics`  
**Auth Required:** `ADMIN`, `SUPER_ADMIN`, or `SUPPORT_ADMIN`

**Response:**
```json
{
  "eventId": 5,
  "eventTitle": "Tech Conference 2024",
  "eventDate": "2024-02-15",
  "totalRegistrations": 450,
  "speakerCount": 8,
  "eventStatus": "UPCOMING"
}
```

#### Get Registration Statistics
**Endpoint:** `GET /api/v1/events/admin/registrations/stats`  
**Auth Required:** `ADMIN`, `SUPER_ADMIN`, or `SUPPORT_ADMIN`  
**Query Parameters:**
- `startDate` (optional) - Format: YYYY-MM-DD
- `endDate` (optional) - Format: YYYY-MM-DD

**Response:**
```json
{
  "totalRegistrations": 3400,
  "registrationsByEvent": [
    {
      "eventName": "Tech Conference 2024",
      "count": 450
    },
    {
      "eventName": "Business Summit 2024",
      "count": 320
    }
  ],
  "topEventsByRegistrations": [
    {
      "eventTitle": "Tech Conference 2024",
      "registrationCount": 450
    }
  ]
}
```

#### Get Dashboard Data (Cached)
**Endpoint:** `GET /api/v1/events/admin/dashboard`  
**Auth Required:** `ADMIN`, `SUPER_ADMIN`, or `SUPPORT_ADMIN`

**Response:**
```json
{
  "overallStats": {
    "totalEvents": 45,
    "upcomingEvents": 12,
    "pastEvents": 33,
    "totalRegistrations": 3400,
    "averageRegistrationsPerEvent": 75.5
  },
  "recentEvents": [
    {
      "id": 1,
      "title": "Tech Conference 2024",
      "date": "2024-02-15",
      "location": "New York"
    }
  ],
  "topEvents": [
    {
      "eventTitle": "Tech Conference 2024",
      "registrationCount": 450
    }
  ],
  "systemStatus": "HEALTHY"
}
```

### Event Speakers

#### Get Event Speakers
**Endpoint:** `GET /api/v1/events/{eventId}/speakers`  
**Auth Required:** `ADMIN`, `SUPER_ADMIN`, or `SUPPORT_ADMIN`

**Response:**
```json
[
  {
    "name": "Dr. Jane Smith",
    "title": "Chief Technology Officer",
    "bio": "Expert in AI and Machine Learning with 15 years of experience",
    "imageUrl": "https://example.com/speakers/jane-smith.jpg"
  },
  {
    "name": "John Doe",
    "title": "Senior Developer",
    "bio": "Full-stack developer and open-source contributor",
    "imageUrl": "https://example.com/speakers/john-doe.jpg"
  }
]
```

#### Add Speaker to Event
**Endpoint:** `POST /api/v1/events/{eventId}/speakers`  
**Auth Required:** `ADMIN`

**Request Body:**
```json
{
  "name": "Dr. Jane Smith",
  "title": "Chief Technology Officer",
  "bio": "Expert in AI and Machine Learning",
  "imageUrl": "https://example.com/speakers/jane-smith.jpg"
}
```

**Response:**
```json
{
  "message": "Speaker added successfully"
}
```

#### Update Speaker
**Endpoint:** `PUT /api/v1/events/{eventId}/speakers/update`  
**Auth Required:** `ADMIN`

**Request Body:** Same as Add Speaker

#### Remove Speaker
**Endpoint:** `DELETE /api/v1/events/{eventId}/speakers`  
**Auth Required:** `ADMIN`  
**Query Parameters:**
- `speakerName` (required) - Name of the speaker to remove

**Response:**
```json
{
  "message": "Speaker removed successfully"
}
```

### Event Registrations

#### Get Event Registrations
**Endpoint:** `GET /api/v1/events/{eventId}/registrations`  
**Auth Required:** `ADMIN`  
**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

**Response:**
```json
{
  "content": [
    {
      "email": "attendee1@example.com",
      "name": "Alice Johnson",
      "phone": "+1234567890",
      "note": "Interested in AI workshops"
    },
    {
      "email": "attendee2@example.com",
      "name": "Bob Williams",
      "phone": "+0987654321",
      "note": ""
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 450,
  "totalPages": 45
}
```

#### Register for Event (Public)
**Endpoint:** `POST /api/v1/events/{eventId}/register`  
**Auth Required:** None

**Request Body:**
```json
{
  "email": "attendee@example.com",
  "name": "Alice Johnson",
  "phone": "+1234567890",
  "note": "Interested in AI workshops"
}
```

**Response:**
```json
{
  "message": "Registration successful"
}
```

#### Delete Registration
**Endpoint:** `DELETE /api/v1/events/{eventId}/registrations/{email}`  
**Auth Required:** `ADMIN` or `SUPER_ADMIN`

**Response:**
```json
{
  "message": "Registration deleted successfully"
}
```

#### Bulk Delete Registrations
**Endpoint:** `DELETE /api/v1/events/{eventId}/registrations/bulk`  
**Auth Required:** `ADMIN` or `SUPER_ADMIN`

**Request Body:**
```json
{
  "registrationEmails": [
    "attendee1@example.com",
    "attendee2@example.com",
    "attendee3@example.com"
  ]
}
```

**Response:**
```json
{
  "message": "3 registrations deleted successfully"
}
```

#### Export Registrations to CSV
**Endpoint:** `GET /api/v1/events/{eventId}/registrations/export`  
**Auth Required:** `ADMIN`

**Response:** CSV file download

### Event Filters

#### Get Upcoming Events
**Endpoint:** `GET /api/v1/events/upcoming`  
**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

**Response:** Same structure as Get All Events

#### Get Past Events
**Endpoint:** `GET /api/v1/events/past`  
**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

**Response:** Same structure as Get All Events

#### Get Event by Year
**Endpoint:** `GET /api/v1/events/year/{year}`

**Response:** Single event object

---

## Academic Management Endpoints

### Colleges

#### Get All Colleges
**Endpoint:** `GET /api/v1/colleges`

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "collegeName": "College of Engineering"
    },
    {
      "id": 2,
      "collegeName": "College of Business"
    }
  ]
}
```

#### Get All Colleges (Paginated)
**Endpoint:** `GET /api/v1/colleges/paged`  
**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [...],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 25,
    "totalPages": 3
  }
}
```

#### Get College by ID
**Endpoint:** `GET /api/v1/colleges/{id}`

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "collegeName": "College of Engineering"
  }
}
```

#### Get College with Faculties
**Endpoint:** `GET /api/v1/colleges/{id}/with-faculties`

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "collegeName": "College of Engineering",
    "faculties": [
      {
        "id": 1,
        "facultyName": "Faculty of Computer Science"
      },
      {
        "id": 2,
        "facultyName": "Faculty of Electrical Engineering"
      }
    ]
  }
}
```

#### Create College
**Endpoint:** `POST /api/v1/colleges`  
**Auth Required:** `ADMIN`

**Request Body:**
```json
{
  "collegeName": "College of Arts and Sciences"
}
```

**Response:**
```json
{
  "success": true,
  "message": "College created successfully",
  "data": {
    "id": 3,
    "collegeName": "College of Arts and Sciences"
  }
}
```

#### Update College
**Endpoint:** `PUT /api/v1/colleges/{id}`  
**Auth Required:** `ADMIN`

**Request Body:**
```json
{
  "collegeName": "College of Arts and Sciences (Updated)"
}
```

#### Delete College
**Endpoint:** `DELETE /api/v1/colleges/{id}`  
**Auth Required:** `ADMIN`

**Response:**
```json
{
  "success": true,
  "message": "College deleted successfully"
}
```

#### Search Colleges
**Endpoint:** `GET /api/v1/colleges/search`  
**Query Parameters:**
- `name` (required) - Search term

**Response:** Same structure as Get All Colleges

### Faculties

#### Get All Faculties
**Endpoint:** `GET /api/v1/faculties`

#### Get Faculties by College
**Endpoint:** `GET /api/v1/faculties/college/{collegeId}`

#### Create Faculty
**Endpoint:** `POST /api/v1/faculties`  
**Auth Required:** `ADMIN`

**Request Body:**
```json
{
  "facultyName": "Faculty of Computer Science",
  "collegeId": 1
}
```

#### Update Faculty
**Endpoint:** `PUT /api/v1/faculties/{id}`  
**Auth Required:** `ADMIN`

#### Delete Faculty
**Endpoint:** `DELETE /api/v1/faculties/{id}`  
**Auth Required:** `ADMIN`

### Departments

#### Get All Departments
**Endpoint:** `GET /api/v1/departments`

#### Get Departments by Faculty
**Endpoint:** `GET /api/v1/departments/faculty/{facultyId}`

#### Create Department
**Endpoint:** `POST /api/v1/departments`  
**Auth Required:** `ADMIN`

**Request Body:**
```json
{
  "departmentName": "Computer Science Department",
  "facultyId": 1
}
```

#### Update Department
**Endpoint:** `PUT /api/v1/departments/{id}`  
**Auth Required:** `ADMIN`

#### Delete Department
**Endpoint:** `DELETE /api/v1/departments/{id}`  
**Auth Required:** `ADMIN`

### Programs

#### Get All Programs
**Endpoint:** `GET /api/v1/programs`

#### Get Programs by Department
**Endpoint:** `GET /api/v1/programs/department/{departmentId}`

#### Create Program
**Endpoint:** `POST /api/v1/programs`  
**Auth Required:** `ADMIN`

**Request Body:**
```json
{
  "programName": "Bachelor of Computer Science",
  "programCode": "BCS",
  "departmentId": 1
}
```

#### Update Program
**Endpoint:** `PUT /api/v1/programs/{id}`  
**Auth Required:** `ADMIN`

#### Delete Program
**Endpoint:** `DELETE /api/v1/programs/{id}`  
**Auth Required:** `ADMIN`

### Courses

#### Get All Courses
**Endpoint:** `GET /api/v1/courses`

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "courseCode": "CS101",
      "courseName": "Introduction to Computer Science",
      "courseDescription": "Fundamentals of computer science and programming",
      "semester": "FIRST",
      "departmentId": 1,
      "departmentName": "Computer Science"
    }
  ]
}
```

#### Get Courses by Department
**Endpoint:** `GET /api/v1/courses/department/{departmentId}`

#### Get Courses by Semester
**Endpoint:** `GET /api/v1/courses/semester/{semester}`  
**Path Parameters:**
- `semester` - Enum: FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH, SEVENTH, EIGHTH

#### Get Courses by Program
**Endpoint:** `GET /api/v1/courses/program/{programId}`

#### Get Courses by Faculty
**Endpoint:** `GET /api/v1/courses/faculty/{facultyId}`

#### Get Courses by College
**Endpoint:** `GET /api/v1/courses/college/{collegeId}`

#### Create Course
**Endpoint:** `POST /api/v1/courses`  
**Auth Required:** `ADMIN`

**Request Body:**
```json
{
  "courseCode": "CS301",
  "courseName": "Data Structures and Algorithms",
  "courseDescription": "Advanced study of data structures",
  "semester": "THIRD",
  "departmentId": 1
}
```

#### Update Course
**Endpoint:** `PUT /api/v1/courses/{id}`  
**Auth Required:** `ADMIN`

#### Delete Course
**Endpoint:** `DELETE /api/v1/courses/{id}`  
**Auth Required:** `ADMIN`

### Resources & Reference Materials

#### Get All Resources
**Endpoint:** `GET /api/v1/academics/resources`

#### Get Resources by Course
**Endpoint:** `GET /api/v1/academics/resources/course/{courseId}`  
**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

#### Get All Reference Materials
**Endpoint:** `GET /api/v1/academics/reference-materials`

#### Get Reference Material by ID
**Endpoint:** `GET /api/v1/academics/reference-materials/{id}`

#### Get Reference Materials by Course
**Endpoint:** `GET /api/v1/academics/reference-materials/course/{courseId}`

#### Create Reference Material
**Endpoint:** `POST /api/v1/academics/reference-materials`  
**Auth Required:** `ADMIN`

**Request Body:**
```json
{
  "title": "Introduction to Algorithms",
  "description": "Comprehensive guide to algorithms",
  "courseId": 1,
  "author": "Thomas H. Cormen",
  "publisher": "MIT Press",
  "isbn": "978-0262033848",
  "edition": "3rd Edition",
  "publicationYear": "2009",
  "language": "English",
  "numberOfPages": "1312",
  "format": "PDF",
  "fileSize": "15MB",
  "fileUrl": "https://storage.example.com/books/algorithms.pdf",
  "downloadLink": "https://download.example.com/algorithms.pdf",
  "coverImageUrl": "https://images.example.com/covers/algorithms.jpg"
}
```

#### Update Reference Material
**Endpoint:** `PUT /api/v1/academics/reference-materials/{id}`  
**Auth Required:** `ADMIN`

#### Delete Reference Material
**Endpoint:** `DELETE /api/v1/academics/reference-materials/{id}`  
**Auth Required:** `ADMIN`

### Quizzes

#### Get All Quizzes
**Endpoint:** `GET /api/v1/quizzes`  
**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

#### Get Quiz by ID
**Endpoint:** `GET /api/v1/quizzes/{id}`

#### Get Quizzes by Course
**Endpoint:** `GET /api/v1/quizzes/course/{courseId}`

#### Create Quiz
**Endpoint:** `POST /api/v1/quizzes`  
**Auth Required:** `ADMIN`

**Request Body:**
```json
{
  "title": "Midterm Exam - Data Structures",
  "description": "Comprehensive exam covering arrays, linked lists, and trees",
  "courseId": 1,
  "numberOfQuestions": 20,
  "difficultyLevel": "INTERMEDIATE",
  "quizType": "MIDTERM",
  "yearGiven": 2024
}
```

#### Update Quiz
**Endpoint:** `PUT /api/v1/quizzes/{id}`  
**Auth Required:** `ADMIN`

#### Delete Quiz
**Endpoint:** `DELETE /api/v1/quizzes/{id}`  
**Auth Required:** `ADMIN`

---

## SMS Communication Endpoints

### SMS Templates

#### Create SMS Template
**Endpoint:** `POST /api/v1/events/sms/template`

**Request Body:**
```json
{
  "title": "Event Reminder",
  "content": "Hi {name}, this is a reminder for {eventTitle} on {date}. See you there!"
}
```

**Response:**
```json
{
  "templateId": "tmpl_123456",
  "title": "Event Reminder",
  "content": "Hi {name}, this is a reminder for {eventTitle} on {date}. See you there!",
  "createdAt": "2024-01-20T15:00:00"
}
```

#### Get All Templates
**Endpoint:** `GET /api/v1/events/sms/templates`

**Response:**
```json
{
  "templates": [
    {
      "templateId": "tmpl_123456",
      "title": "Event Reminder",
      "content": "Hi {name}, this is a reminder..."
    }
  ]
}
```

#### Get Template by ID
**Endpoint:** `GET /api/v1/events/sms/templates/{templateId}`

### Send Bulk SMS

#### Send Bulk SMS (General)
**Endpoint:** `POST /api/v1/events/sms/send-bulk-sms`

**Request Body:**
```json
{
  "recipients": [
    {
      "phoneNumber": "+1234567890",
      "name": "John Doe"
    },
    {
      "phoneNumber": "+0987654321",
      "name": "Jane Smith"
    }
  ],
  "message": "Hi {name}, this is a reminder for the event!",
  "templateId": "tmpl_123456"
}
```

**Response:**
```json
{
  "totalSent": 2,
  "successful": 2,
  "failed": 0,
  "deliveryStatus": [
    {
      "phoneNumber": "+1234567890",
      "status": "SENT",
      "messageId": "msg_abc123"
    },
    {
      "phoneNumber": "+0987654321",
      "status": "SENT",
      "messageId": "msg_def456"
    }
  ]
}
```

#### Send SMS to Event Registrations
**Endpoint:** `POST /api/v1/events/{eventId}/registrations/send-sms`  
**Auth Required:** `ADMIN`

**Request Body:**
```json
{
  "eventId": 5,
  "registrationEmails": [
    "attendee1@example.com",
    "attendee2@example.com"
  ],
  "message": "Reminder: Tech Conference 2024 starts tomorrow!",
  "templateId": "tmpl_123456"
}
```

**Response:** Same as Send Bulk SMS

---

## Request DTOs

### Admin Request DTOs

#### CreateUserRequest
```typescript
interface CreateUserRequest {
  firstName: string;          // Required
  lastName: string;           // Required
  email: string;              // Required, must be valid email
  phoneNumber: string;        // Required
  password: string;           // Required
  role: UserRole;             // Required: "USER" | "ADMIN" | "SUPER_ADMIN"
}
```

#### UpdateUserRequest
```typescript
interface UpdateUserRequest {
  firstName?: string;
  lastName?: string;
  email?: string;             // Must be valid email if provided
  phoneNumber?: string;
  role?: UserRole;            // "USER" | "ADMIN" | "SUPER_ADMIN"
  status?: UserStatus;        // "ACTIVE" | "INACTIVE" | "SUSPENDED"
  isEnabled?: boolean;
}
```

#### UpdateConfigRequest
```typescript
interface UpdateConfigRequest {
  configKey: string;          // Required
  configValue: string;        // Required
  description?: string;
}
```

### Event Request DTOs

#### CreateEventRequest
```typescript
interface CreateEventRequest {
  title: string;
  description: string;
  location: string;
  date: string;               // Format: YYYY-MM-DD
  time: string;               // Format: HH:mm:ss
  imageUrl?: string;
  speakers: Speaker[];
  contacts: Contact[];
  sponsors: Sponsor[];
}

interface Speaker {
  name: string;               // Required
  title?: string;
  bio?: string;
  imageUrl?: string;
}

interface Contact {
  name: string;
  email: string;
  phone: string;
}

interface Sponsor {
  name: string;
  logoUrl?: string;
  website?: string;
}
```

#### UpdateEventRequest
```typescript
// Same as CreateEventRequest but all fields are optional
```

#### EventRegistrationDto
```typescript
interface EventRegistrationDto {
  email: string;              // Required
  name: string;               // Required
  phone: string;              // Required
  note?: string;
}
```

#### BulkDeleteRequest
```typescript
interface BulkDeleteRequest {
  registrationEmails: string[];   // Array of email addresses
}
```

#### SendBulkSmsToRegistrationsRequest
```typescript
interface SendBulkSmsToRegistrationsRequest {
  eventId: number;
  registrationEmails: string[];
  message: string;
  templateId?: string;
}
```

### Academic Request DTOs

#### CreateCollegeRequest
```typescript
interface CreateCollegeRequest {
  collegeName: string;        // Required, 2-100 characters
}
```

#### CreateFacultyRequest
```typescript
interface CreateFacultyRequest {
  facultyName: string;
  collegeId: number;
}
```

#### CreateDepartmentRequest
```typescript
interface CreateDepartmentRequest {
  departmentName: string;
  facultyId: number;
}
```

#### CreateProgramRequest
```typescript
interface CreateProgramRequest {
  programName: string;
  programCode: string;        // Unique identifier
  departmentId: number;
}
```

#### CreateCourseRequest
```typescript
interface CreateCourseRequest {
  courseCode: string;
  courseName: string;
  courseDescription: string;
  semester: Semester;         // FIRST, SECOND, ..., EIGHTH
  departmentId: number;
}
```

#### CreateReferenceMaterialRequest
```typescript
interface CreateReferenceMaterialRequest {
  title: string;
  description: string;
  courseId: number;
  author?: string;
  publisher?: string;
  isbn?: string;
  edition?: string;
  publicationYear?: string;
  language?: string;
  numberOfPages?: string;
  format?: string;            // PDF, EPUB, etc.
  fileSize?: string;
  fileUrl?: string;
  downloadLink?: string;
  coverImageUrl?: string;
}
```

#### CreateQuizRequest
```typescript
interface CreateQuizRequest {
  title: string;
  description: string;
  courseId: number;
  numberOfQuestions: number;
  difficultyLevel: string;    // BEGINNER, INTERMEDIATE, ADVANCED
  quizType: string;           // MIDTERM, FINAL, QUIZ, PRACTICE
  yearGiven: number;
}
```

---

## Response DTOs

### Admin Response DTOs

#### UserResponse
```typescript
interface UserResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  role: UserRole;
  status: UserStatus;
  isEnabled: boolean;
  createdAt: string;          // ISO 8601 format
  lastLogin: string;          // ISO 8601 format
}
```

#### SystemStatsResponse
```typescript
interface SystemStatsResponse {
  totalUsers: number;
  activeUsers: number;
  totalEvents: number;
  totalRegistrations: number;
  totalAuditLogs: number;
  uptime: string;
  metrics: {
    memoryUsed: string;
    memoryTotal: string;
    memoryUsagePercentage?: number;
    cpuUsage: string;
    diskUsage: string;
    activeConnections?: number;
    [key: string]: any;
  };
}
```

#### AuditLogResponse
```typescript
interface AuditLogResponse {
  id: number;
  action: string;             // CREATE, UPDATE, DELETE, LOGIN, etc.
  entityType: string;         // User, Event, Config, etc.
  entityId: number;
  performedBy: string;        // User email
  timestamp: string;          // ISO 8601 format
  details: string;            // JSON string
  ipAddress: string;
}
```

#### ConfigResponse
```typescript
interface ConfigResponse {
  configKey: string;
  configValue: string;
  description: string;
  updatedAt: string;          // ISO 8601 format
  updatedBy: string;
}
```

#### UserActivityResponse
```typescript
interface UserActivityResponse {
  userId: number;
  username: string;
  activities: ActivityLogDto[];
}

interface ActivityLogDto {
  activityType: string;
  endpoint: string;
  timestamp: string;          // ISO 8601 format
  details: string;
}
```

### Event Response DTOs

#### EventResponse
```typescript
interface EventResponse {
  id: number;
  title: string;
  description: string;
  location: string;
  date: string;               // YYYY-MM-DD
  time: string;               // HH:mm:ss
  imageUrl: string;
  videoUrl?: string;
  speakers: Speaker[];
  sponsors: Sponsor[];
  contacts: Contact[];
  testimonials: Testimonial[];
}
```

#### EventStatsResponse
```typescript
interface EventStatsResponse {
  totalEvents: number;
  upcomingEvents: number;
  pastEvents: number;
  totalRegistrations: number;
  averageRegistrationsPerEvent: number;
}
```

#### EventAnalyticsResponse
```typescript
interface EventAnalyticsResponse {
  eventId: number;
  eventTitle: string;
  eventDate: string;          // YYYY-MM-DD
  totalRegistrations: number;
  speakerCount: number;
  eventStatus: string;        // UPCOMING, PAST, ONGOING
}
```

#### RegistrationStatsResponse
```typescript
interface RegistrationStatsResponse {
  totalRegistrations: number;
  registrationsByEvent: EventRegistrationCount[];
  topEventsByRegistrations: TopEventDto[];
}

interface EventRegistrationCount {
  eventName: string;
  count: number;
}

interface TopEventDto {
  eventTitle: string;
  registrationCount: number;
}
```

#### DashboardResponse
```typescript
interface DashboardResponse {
  overallStats: EventStatsResponse;
  recentEvents: EventResponse[];
  topEvents: TopEventDto[];
  systemStatus: string;       // HEALTHY, WARNING, ERROR
}
```

### Academic Response DTOs

#### CollegeResponse
```typescript
interface CollegeResponse {
  id: number;
  collegeName: string;
  faculties?: FacultyResponse[];  // When fetching with faculties
}
```

#### FacultyResponse
```typescript
interface FacultyResponse {
  id: number;
  facultyName: string;
  collegeId: number;
  collegeName?: string;
  departments?: DepartmentResponse[];  // When fetching with departments
}
```

#### DepartmentResponse
```typescript
interface DepartmentResponse {
  id: number;
  departmentName: string;
  facultyId: number;
  facultyName?: string;
  programs?: ProgramResponse[];
}
```

#### ProgramResponse
```typescript
interface ProgramResponse {
  id: number;
  programName: string;
  programCode: string;
  departmentId: number;
  departmentName?: string;
}
```

#### CourseResponse
```typescript
interface CourseResponse {
  id: number;
  courseCode: string;
  courseName: string;
  courseDescription: string;
  semester: Semester;
  departmentId: number;
  departmentName?: string;
}
```

### Authentication Response DTOs

#### JwtResponse
```typescript
interface JwtResponse {
  token: string;              // JWT access token
  type: string;               // "Bearer"
  refreshToken: string;       // Refresh token
  id: number;                 // User ID
  username: string;           // User email
  roles: string[];            // e.g., ["ROLE_ADMIN", "ROLE_USER"]
}
```

#### MessageResponse
```typescript
interface MessageResponse {
  message: string;
}
```

---

## Integration Guide

### Setting Up the Frontend

#### 1. Install Dependencies

For a React/Next.js project:
```bash
npm install axios
# or
yarn add axios
```

#### 2. Create API Client

Create `lib/api.ts` or `utils/api.js`:

```typescript
import axios from 'axios';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:9000';

// Create axios instance
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid - redirect to login
      localStorage.removeItem('jwt_token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

#### 3. Create API Service Functions

Create `services/adminService.ts`:

```typescript
import apiClient from '@/lib/api';

// User Management
export const getAllUsers = async (page = 0, size = 10) => {
  const response = await apiClient.get(`/api/v1/admin/users`, {
    params: { page, size }
  });
  return response.data;
};

export const getUserById = async (id: number) => {
  const response = await apiClient.get(`/api/v1/admin/users/${id}`);
  return response.data;
};

export const createUser = async (userData: CreateUserRequest) => {
  const response = await apiClient.post('/api/v1/admin/users', userData);
  return response.data;
};

export const updateUser = async (id: number, userData: UpdateUserRequest) => {
  const response = await apiClient.put(`/api/v1/admin/users/${id}`, userData);
  return response.data;
};

export const deleteUser = async (id: number) => {
  const response = await apiClient.delete(`/api/v1/admin/users/${id}`);
  return response.data;
};

// System Monitoring
export const getSystemStats = async () => {
  const response = await apiClient.get('/api/admin/system/stats');
  return response.data;
};

export const getAuditLogs = async (page = 0, size = 10) => {
  const response = await apiClient.get('/api/v1/admin/audit-logs', {
    params: { page, size }
  });
  return response.data;
};
```

Create `services/eventService.ts`:

```typescript
import apiClient from '@/lib/api';

export const getAllEvents = async (page = 0, size = 10) => {
  const response = await apiClient.get('/api/v1/events', {
    params: { page, size }
  });
  return response.data;
};

export const getEventById = async (id: number) => {
  const response = await apiClient.get(`/api/v1/events/${id}`);
  return response.data;
};

export const createEvent = async (eventData: CreateEventRequest) => {
  const response = await apiClient.post('/api/v1/events', eventData);
  return response.data;
};

export const getEventStats = async () => {
  const response = await apiClient.get('/api/v1/events/admin/stats');
  return response.data;
};

export const getDashboardData = async () => {
  const response = await apiClient.get('/api/v1/events/admin/dashboard');
  return response.data;
};
```

Create `services/authService.ts`:

```typescript
import apiClient from '@/lib/api';

export const login = async (email: string, password: string) => {
  const response = await apiClient.post('/api/v1/auth/login', {
    email,
    password
  });
  
  // Store token
  if (response.data.token) {
    localStorage.setItem('jwt_token', response.data.token);
    localStorage.setItem('user', JSON.stringify({
      id: response.data.id,
      username: response.data.username,
      roles: response.data.roles
    }));
  }
  
  return response.data;
};

export const logout = () => {
  localStorage.removeItem('jwt_token');
  localStorage.removeItem('user');
  window.location.href = '/login';
};

export const getCurrentUser = () => {
  const userStr = localStorage.getItem('user');
  return userStr ? JSON.parse(userStr) : null;
};

export const isAuthenticated = () => {
  return !!localStorage.getItem('jwt_token');
};

export const hasRole = (role: string) => {
  const user = getCurrentUser();
  return user?.roles?.includes(role) || false;
};
```

#### 4. Use in Components

Example React component:

```tsx
import React, { useEffect, useState } from 'react';
import { getAllUsers, deleteUser } from '@/services/adminService';
import { UserResponse } from '@/types/api';

export default function UsersPage() {
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadUsers();
  }, [page]);

  const loadUsers = async () => {
    try {
      setLoading(true);
      const response = await getAllUsers(page, 10);
      setUsers(response.content);
      setTotalPages(response.totalPages);
    } catch (error) {
      console.error('Error loading users:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to delete this user?')) {
      try {
        await deleteUser(id);
        loadUsers(); // Reload the list
      } catch (error) {
        console.error('Error deleting user:', error);
      }
    }
  };

  if (loading) return <div>Loading...</div>;

  return (
    <div>
      <h1>Users</h1>
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Role</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td>{user.firstName} {user.lastName}</td>
              <td>{user.email}</td>
              <td>{user.role}</td>
              <td>{user.status}</td>
              <td>
                <button onClick={() => handleDelete(user.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      
      {/* Pagination */}
      <div>
        <button 
          onClick={() => setPage(p => Math.max(0, p - 1))}
          disabled={page === 0}
        >
          Previous
        </button>
        <span>Page {page + 1} of {totalPages}</span>
        <button 
          onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
          disabled={page === totalPages - 1}
        >
          Next
        </button>
      </div>
    </div>
  );
}
```

### TypeScript Type Definitions

Create `types/api.ts`:

```typescript
// Enums
export enum UserRole {
  USER = 'USER',
  ADMIN = 'ADMIN',
  SUPER_ADMIN = 'SUPER_ADMIN'
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED'
}

export enum Semester {
  FIRST = 'FIRST',
  SECOND = 'SECOND',
  THIRD = 'THIRD',
  FOURTH = 'FOURTH',
  FIFTH = 'FIFTH',
  SIXTH = 'SIXTH',
  SEVENTH = 'SEVENTH',
  EIGHTH = 'EIGHTH'
}

// All the interfaces from the Response DTOs section
export interface UserResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  role: UserRole;
  status: UserStatus;
  isEnabled: boolean;
  createdAt: string;
  lastLogin: string;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}

// ... add all other interfaces
```

---

## Error Handling

### Standard Error Response

```json
{
  "success": false,
  "message": "Error description",
  "timestamp": "2024-01-20T15:45:00Z"
}
```

### HTTP Status Codes

- **200 OK** - Successful GET, PUT requests
- **201 Created** - Successful POST requests
- **204 No Content** - Successful DELETE requests
- **400 Bad Request** - Invalid request data, validation errors
- **401 Unauthorized** - Missing or invalid JWT token
- **403 Forbidden** - User lacks required permissions
- **404 Not Found** - Resource not found
- **409 Conflict** - Resource already exists (e.g., duplicate email)
- **500 Internal Server Error** - Server error

### Error Handling Example

```typescript
try {
  const user = await createUser(userData);
  // Success
} catch (error) {
  if (axios.isAxiosError(error)) {
    const status = error.response?.status;
    const message = error.response?.data?.message || 'An error occurred';
    
    switch (status) {
      case 400:
        console.error('Validation error:', message);
        break;
      case 401:
        console.error('Unauthorized - please login');
        break;
      case 403:
        console.error('Permission denied');
        break;
      case 404:
        console.error('Resource not found');
        break;
      case 409:
        console.error('Resource already exists');
        break;
      default:
        console.error('Server error:', message);
    }
  }
}
```

---

## CORS Configuration

The backend is configured to accept requests from:
- `http://localhost:8080`
- `http://localhost:5173`

If deploying to production, update CORS origins in the backend configuration.

---

## Environment Variables

Create `.env.local` in your frontend project:

```env
NEXT_PUBLIC_API_URL=http://localhost:9000
```

---

## Testing API Endpoints

### Using curl

```bash
# Login
curl -X POST http://localhost:9000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"password123"}'

# Get users (with token)
curl -X GET http://localhost:9000/api/v1/admin/users?page=0&size=10 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Create event
curl -X POST http://localhost:9000/api/v1/events \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Event","description":"Test","location":"NYC","date":"2024-02-15","time":"09:00:00","speakers":[],"contacts":[],"sponsors":[]}'
```

### Using Postman

1. Import the API endpoints
2. Set up an environment with `BASE_URL = http://localhost:9000`
3. Create a login request and save the token
4. Use `{{token}}` in Authorization header for protected endpoints

---

## Best Practices

1. **Token Management**
   - Store JWT securely (httpOnly cookies or secure localStorage)
   - Implement token refresh mechanism
   - Clear token on logout

2. **Error Handling**
   - Always wrap API calls in try-catch
   - Show user-friendly error messages
   - Log errors for debugging

3. **Loading States**
   - Show loading indicators during API calls
   - Disable buttons during submissions
   - Implement skeleton loaders

4. **Pagination**
   - Always implement pagination for list endpoints
   - Allow users to change page size
   - Show total count

5. **Validation**
   - Validate on frontend before sending to backend
   - Handle backend validation errors gracefully
   - Show field-specific error messages

6. **Caching**
   - Cache dashboard data (5-minute TTL recommended)
   - Invalidate cache after mutations
   - Use React Query or SWR for data fetching

---

## Support

For questions or issues, please contact the development team or refer to the main README.md documentation.
