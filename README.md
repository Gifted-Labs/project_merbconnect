# MerbsConnect - Academic Management System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [API Documentation](#-api-documentation)
- [Database Schema](#-database-schema)
- [Security](#-security)
- [Setup and Installation](#-setup-and-installation)
- [Configuration](#-configuration)
- [Running the Application](#-running-the-application)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Contributing](#-contributing)
- [License](#-license)

## 🎯 Project Overview

**MerbsConnect** is a comprehensive Academic Management System designed to streamline educational institution operations. The platform provides robust tools for managing academic hierarchies, course materials, quizzes, and educational resources.

### Business Context

MerbsConnect serves educational institutions by providing:
- **Hierarchical Academic Management**: Organize colleges, faculties, departments, and programs
- **Course Management**: Comprehensive course catalog with detailed information
- **Resource Management**: Digital library for academic materials and reference books
- **Assessment Tools**: Quiz creation and management system with question banks
- **User Management**: Role-based access control for administrators and users

### Key Capabilities

- **Multi-tenant Architecture**: Support for multiple educational institutions
- **RESTful API**: Complete REST API with comprehensive endpoint coverage
- **Modern Frontend**: Responsive web interface with Apple-inspired design
- **Security**: JWT-based authentication with role-based authorization
- **Email Integration**: Automated email notifications and verification
- **Real-time Updates**: Dynamic content management with instant updates

## ✨ Features

### Core Academic Management
- **College Management**: Create and manage educational colleges
- **Faculty Organization**: Organize faculties within colleges
- **Department Structure**: Manage departments under faculties
- **Program Administration**: Define and manage academic programs
- **Course Catalog**: Comprehensive course management with detailed metadata

### Resource Management
- **Digital Library**: Upload and manage academic resources
- **Reference Materials**: Organize books, papers, and educational content
- **File Management**: Support for various file formats (PDF, EPUB, etc.)
- **Metadata Management**: Rich metadata for all resources including authors, publishers, ISBN

### Assessment System
- **Quiz Creation**: Build comprehensive quizzes with multiple question types
- **Question Bank**: Centralized question repository with tagging and difficulty levels
- **Assessment Analytics**: Track usage statistics and success rates
- **Flexible Scoring**: Configurable scoring and grading systems

### User Management
- **Authentication**: Secure user registration and login
- **Authorization**: Role-based access control (ADMIN, USER)
- **Profile Management**: User profile and preference management
- **Email Verification**: Automated email verification workflow

### Frontend Interface
- **Responsive Design**: Mobile-first responsive web interface
- **Apple-inspired UI**: Clean, modern design with SF Pro fonts
- **Dark Mode**: Toggle between light and dark themes
- **Interactive Dashboard**: Real-time statistics and data visualization
- **Admin Panel**: Comprehensive administrative interface

## 🛠 Technology Stack

### Backend Technologies
- **Framework**: Spring Boot 3.4.4
- **Language**: Java 21
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security with JWT
- **Email**: Spring Mail with SMTP
- **Validation**: Bean Validation (JSR-303)
- **Build Tool**: Maven
- **Documentation**: OpenAPI/Swagger

### Frontend Technologies
- **Framework**: Vanilla JavaScript (ES6+)
- **Styling**: Tailwind CSS
- **Icons**: Font Awesome 6.4.0
- **Charts**: Chart.js
- **Animations**: AOS (Animate On Scroll)
- **Notifications**: SweetAlert2

### Development Tools
- **Testing**: JUnit 5, Spring Boot Test
- **Code Quality**: Lombok for boilerplate reduction
- **API Testing**: Spring Security Test
- **Version Control**: Git
- **IDE Support**: IntelliJ IDEA, VS Code

### Infrastructure
- **Application Server**: Embedded Tomcat
- **Database**: PostgreSQL with connection pooling
- **Email Service**: SMTP with async processing
- **File Storage**: Local file system (configurable)
- **Monitoring**: Spring Boot Actuator

## 🏗 Architecture

### System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (Web UI)      │◄──►│   (Spring Boot) │◄──►│   (PostgreSQL)  │
│                 │    │                 │    │                 │
│ • HTML/CSS/JS   │    │ • REST API      │    │ • Academic Data │
│ • Tailwind CSS  │    │ • JWT Security  │    │ • User Data     │
│ • Responsive    │    │ • Email Service │    │ • Resources     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Application Layers

1. **Presentation Layer**
   - REST Controllers (`@RestController`)
   - Request/Response DTOs
   - Input validation (`@Valid`)
   - Error handling

2. **Service Layer**
   - Business logic implementation
   - Transaction management (`@Transactional`)
   - Service interfaces and implementations

3. **Data Access Layer**
   - JPA Repositories
   - Entity mappings
   - Database relationships

4. **Security Layer**
   - JWT authentication
   - Role-based authorization
   - CORS configuration
   - Security filters

### Package Structure

```
com.merbsconnect/
├── academics/           # Academic domain
│   ├── controller/      # REST controllers
│   ├── domain/          # JPA entities
│   ├── dto/            # Data transfer objects
│   ├── repository/     # Data repositories
│   └── service/        # Business services
├── authentication/     # Authentication & security
│   ├── controller/     # Auth controllers
│   ├── domain/         # User entities
│   ├── dto/           # Auth DTOs
│   ├── security/      # Security configuration
│   └── service/       # Auth services
├── config/            # Configuration classes
├── util/              # Utility classes
└── MerbsconnectApplication.java
```

## 📚 API Documentation

### Base URL
```
http://localhost:9000/api/v1
```

### Authentication Endpoints

#### POST /auth/signup
Register a new user account.

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

**Response:**
```json
{
  "message": "User registered successfully. Please check your email for verification."
}
```

#### POST /auth/login
Authenticate user and receive JWT token.

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john.doe@example.com",
  "email": "john.doe@example.com",
  "roles": ["ROLE_USER"]
}
```

#### GET /auth/verify-email
Verify user email with token.

**Query Parameters:**
- `token` (required): Email verification token

#### POST /auth/forgot-password
Request password reset.

**Query Parameters:**
- `email` (required): User email address

#### POST /auth/reset-password
Reset user password with token.

**Request Body:**
```json
{
  "token": "reset-token",
  "newPassword": "NewSecurePassword123"
}
```

### Academic Management Endpoints

#### Colleges

##### POST /colleges
Create a new college. **Requires ADMIN role.**

**Request Body:**
```json
{
  "collegeName": "College of Engineering"
}
```

##### GET /colleges
Retrieve all colleges with pagination.

**Query Parameters:**
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Page size

##### GET /colleges/{id}
Retrieve college by ID.

##### PUT /colleges/{id}
Update college. **Requires ADMIN role.**

##### DELETE /colleges/{id}
Delete college. **Requires ADMIN role.**

##### GET /colleges/with-faculties
Retrieve all colleges with their faculties.

#### Faculties

##### POST /faculties
Create a new faculty. **Requires ADMIN role.**

**Request Body:**
```json
{
  "facultyName": "Faculty of Computer Science",
  "collegeId": 1
}
```

##### GET /faculties
Retrieve all faculties with pagination.

##### GET /faculties/{id}
Retrieve faculty by ID.

##### GET /faculties/college/{collegeId}
Retrieve faculties by college ID.

##### PUT /faculties/{id}
Update faculty. **Requires ADMIN role.**

##### DELETE /faculties/{id}
Delete faculty. **Requires ADMIN role.**

#### Departments

##### POST /departments
Create a new department. **Requires ADMIN role.**

**Request Body:**
```json
{
  "departmentName": "Computer Science Department",
  "facultyId": 1
}
```

##### GET /departments
Retrieve all departments with pagination.

##### GET /departments/{id}
Retrieve department by ID.

##### GET /departments/faculty/{facultyId}
Retrieve departments by faculty ID.

##### PUT /departments/{id}
Update department. **Requires ADMIN role.**

##### DELETE /departments/{id}
Delete department. **Requires ADMIN role.**

#### Programs

##### POST /programs
Create a new program. **Requires ADMIN role.**

**Request Body:**
```json
{
  "programName": "Bachelor of Computer Science",
  "programCode": "BCS",
  "departmentId": 1
}
```

##### GET /programs
Retrieve all programs with pagination.

##### GET /programs/{id}
Retrieve program by ID.

##### GET /programs/department/{departmentId}
Retrieve programs by department ID.

##### PUT /programs/{id}
Update program. **Requires ADMIN role.**

##### DELETE /programs/{id}
Delete program. **Requires ADMIN role.**

#### Courses

##### POST /courses
Create a new course. **Requires ADMIN role.**

**Request Body:**
```json
{
  "courseCode": "CS101",
  "courseName": "Introduction to Computer Science",
  "courseDescription": "Fundamentals of computer science and programming",
  "semester": "FIRST",
  "departmentId": 1
}
```

##### GET /courses
Retrieve all courses with pagination.

##### GET /courses/{id}
Retrieve course by ID.

##### GET /courses/department/{departmentId}
Retrieve courses by department ID.

##### PUT /courses/{id}
Update course. **Requires ADMIN role.**

##### DELETE /courses/{id}
Delete course. **Requires ADMIN role.**

### Resource Management Endpoints

#### Academic Resources

##### GET /academics/resources/{id}
Retrieve resource by ID.

##### GET /academics/resources
Retrieve all resources.

##### GET /academics/resources/paged
Retrieve resources with pagination.

**Query Parameters:**
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Page size

##### GET /academics/resources/course/{courseId}
Retrieve resources by course ID with pagination.

#### Reference Materials

##### POST /academics/reference-materials
Create a new reference material. **Requires ADMIN role.**

**Request Body:**
```json
{
  "title": "Introduction to Algorithms",
  "description": "Comprehensive guide to algorithms and data structures",
  "courseId": 1,
  "author": "Thomas H. Cormen",
  "publisher": "MIT Press",
  "isbn": "978-0262033848",
  "edition": "3rd Edition",
  "publicationYear": "2009",
  "language": "English",
  "numberOfPages": "1312",
  "format": "PDF",
  "fileSize": "15MB"
}
```

##### GET /academics/reference-materials
Retrieve all reference materials.

##### GET /academics/reference-materials/{id}
Retrieve reference material by ID.

##### PUT /academics/reference-materials/{id}
Update reference material. **Requires ADMIN role.**

##### DELETE /academics/reference-materials/{id}
Delete reference material. **Requires ADMIN role.**

##### GET /academics/reference-materials/course/{courseId}
Retrieve reference materials by course ID.

### Quiz Management Endpoints

#### Quizzes

##### POST /quizzes
Create a new quiz. **Requires ADMIN role.**

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

##### GET /quizzes
Retrieve all quizzes with pagination.

##### GET /quizzes/{id}
Retrieve quiz by ID.

##### PUT /quizzes/{id}
Update quiz. **Requires ADMIN role.**

##### DELETE /quizzes/{id}
Delete quiz. **Requires ADMIN role.**

##### GET /quizzes/course/{courseId}
Retrieve quizzes by course ID.

### Error Responses

All endpoints return standardized error responses:

```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Common HTTP Status Codes

- `200 OK`: Successful GET, PUT requests
- `201 Created`: Successful POST requests
- `204 No Content`: Successful DELETE requests
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Authentication required
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource already exists
- `500 Internal Server Error`: Server error

## 🗄 Database Schema

### Entity Relationship Diagram

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   College   │    │   Faculty   │    │ Department  │    │   Program   │
│             │◄──►│             │◄──►│             │◄──►│             │
│ • id        │    │ • id        │    │ • id        │    │ • id        │
│ • name      │    │ • name      │    │ • name      │    │ • name      │
└─────────────┘    │ • collegeId │    │ • facultyId │    │ • code      │
                   └─────────────┘    └─────────────┘    │ • deptId    │
                                                         └─────────────┘
                                                                │
                                                                ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│    User     │    │   Course    │    │  Resource   │    │    Quiz     │
│             │    │             │◄──►│             │◄──►│             │
│ • id        │    │ • id        │    │ • id        │    │ • id        │
│ • firstName │    │ • code      │    │ • title     │    │ • title     │
│ • lastName  │    │ • name      │    │ • desc      │    │ • desc      │
│ • email     │    │ • desc      │    │ • courseId  │    │ • questions │
│ • password  │    │ • semester  │    │ • createdAt │    │ • difficulty│
│ • phone     │    │ • deptId    │    │ • updatedAt │    │ • type      │
│ • role      │    └─────────────┘    └─────────────┘    │ • year      │
│ • enabled   │                              │           └─────────────┘
│ • createdAt │                              ▼
│ • updatedAt │                    ┌─────────────┐    ┌─────────────┐
└─────────────┘                    │ RefMaterial │    │  Question   │
                                   │             │    │             │
                                   │ • fileUrl   │    │ • id        │
                                   │ • author    │    │ • text      │
                                   │ • publisher │    │ • answers   │
                                   │ • isbn      │    │ • correct   │
                                   │ • edition   │    │ • steps     │
                                   │ • year      │    │ • difficulty│
                                   │ • language  │    │ • tags      │
                                   │ • pages     │    │ • usage     │
                                   │ • format    │    │ • success   │
                                   │ • size      │    └─────────────┘
                                   └─────────────┘
```

### Core Tables

#### users
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) UNIQUE,
    role INTEGER NOT NULL, -- 0: USER, 1: ADMIN
    is_enabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### college
```sql
CREATE TABLE college (
    id BIGSERIAL PRIMARY KEY,
    college_name VARCHAR(255) UNIQUE NOT NULL
);
```

#### faculty
```sql
CREATE TABLE faculty (
    id BIGSERIAL PRIMARY KEY,
    faculty_name VARCHAR(255) NOT NULL,
    college_id BIGINT REFERENCES college(id) ON DELETE CASCADE
);
```

#### department
```sql
CREATE TABLE department (
    id BIGSERIAL PRIMARY KEY,
    department_name VARCHAR(255) NOT NULL,
    faculty_id BIGINT NOT NULL REFERENCES faculty(id) ON DELETE CASCADE
);
```

#### program
```sql
CREATE TABLE program (
    id BIGSERIAL PRIMARY KEY,
    program_name VARCHAR(255) NOT NULL,
    program_code VARCHAR(255) UNIQUE NOT NULL,
    department_id BIGINT REFERENCES department(id) ON DELETE SET NULL
);
```

#### course
```sql
CREATE TABLE course (
    id BIGSERIAL PRIMARY KEY,
    course_code VARCHAR(255),
    course_name VARCHAR(255),
    course_description TEXT,
    semester INTEGER, -- ENUM: FIRST, SECOND, THIRD, etc.
    department_id BIGINT NOT NULL REFERENCES department(id) ON DELETE CASCADE
);
```

#### resource (Abstract table)
```sql
CREATE TABLE resource (
    id BIGSERIAL PRIMARY KEY,
    resource_type VARCHAR(255) NOT NULL, -- Discriminator: QUIZ, BOOK
    title VARCHAR(255),
    description TEXT,
    course_id BIGINT NOT NULL REFERENCES course(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### quiz (Extends resource)
```sql
CREATE TABLE quiz (
    id BIGINT PRIMARY KEY REFERENCES resource(id) ON DELETE CASCADE,
    number_of_questions INTEGER,
    difficulty_level VARCHAR(255) NOT NULL,
    year_given INTEGER,
    quiz_type VARCHAR(255) NOT NULL -- ENUM: MIDTERM, FINAL, QUIZ, etc.
);
```

#### reference_resource (Extends resource)
```sql
CREATE TABLE reference_resource (
    id BIGINT PRIMARY KEY REFERENCES resource(id) ON DELETE CASCADE,
    file_url VARCHAR(255),
    author VARCHAR(255),
    publisher VARCHAR(255),
    isbn VARCHAR(255),
    edition VARCHAR(255),
    publication_year VARCHAR(255),
    language VARCHAR(255),
    number_of_pages VARCHAR(255),
    format VARCHAR(255), -- PDF, EPUB, etc.
    file_size VARCHAR(255),
    download_link VARCHAR(255),
    cover_image_url VARCHAR(255)
);
```

#### question
```sql
CREATE TABLE question (
    id BIGSERIAL PRIMARY KEY,
    question_text TEXT,
    correct_answer VARCHAR(255),
    difficulty_level VARCHAR(255),
    usage_count INTEGER DEFAULT 0,
    success_rate DOUBLE PRECISION,
    resource_id BIGINT REFERENCES resource(id) ON DELETE SET NULL,
    quiz_id BIGINT REFERENCES quiz(id) ON DELETE CASCADE
);
```

#### Junction Tables

##### program_course (Many-to-Many)
```sql
CREATE TABLE program_course (
    program_id BIGINT REFERENCES program(id) ON DELETE CASCADE,
    course_id BIGINT REFERENCES course(id) ON DELETE CASCADE,
    PRIMARY KEY (program_id, course_id)
);
```

### Relationships

1. **College → Faculty**: One-to-Many
2. **Faculty → Department**: One-to-Many
3. **Department → Program**: One-to-Many
4. **Department → Course**: One-to-Many
5. **Program ↔ Course**: Many-to-Many (via program_course)
6. **Course → Resource**: One-to-Many
7. **Resource → Quiz**: Inheritance (JOINED strategy)
8. **Resource → ReferenceMaterial**: Inheritance (JOINED strategy)
9. **Quiz → Question**: One-to-Many
10. **Resource → Question**: One-to-Many (for reference)

### Indexes

```sql
-- Performance indexes
CREATE INDEX idx_faculty_college_id ON faculty(college_id);
CREATE INDEX idx_department_faculty_id ON department(faculty_id);
CREATE INDEX idx_program_department_id ON program(department_id);
CREATE INDEX idx_course_department_id ON course(department_id);
CREATE INDEX idx_resource_course_id ON resource(course_id);
CREATE INDEX idx_question_quiz_id ON question(quiz_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone_number);

-- Unique constraints
ALTER TABLE college ADD CONSTRAINT uk_college_name UNIQUE (college_name);
ALTER TABLE program ADD CONSTRAINT uk_program_code UNIQUE (program_code);
ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);
ALTER TABLE users ADD CONSTRAINT uk_users_phone UNIQUE (phone_number);
```

## 🔐 Security

### Authentication & Authorization

#### JWT Implementation
- **Token Type**: JSON Web Tokens (JWT)
- **Algorithm**: HMAC SHA-256 (HS256)
- **Token Expiration**: Configurable via `app.jwt.expiration`
- **Refresh Tokens**: Supported for extended sessions
- **Token Storage**: Client-side storage (localStorage)

#### Security Configuration
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    // JWT-based stateless authentication
    // CORS configuration for cross-origin requests
    // Role-based access control
}
```

#### Role-Based Access Control (RBAC)

**User Roles:**
- `USER`: Standard user with read access to academic resources
- `ADMIN`: Administrative user with full CRUD operations

**Permission Matrix:**

| Endpoint | USER | ADMIN |
|----------|------|-------|
| Authentication | ✅ | ✅ |
| View Colleges/Faculties/Departments | ✅ | ✅ |
| View Programs/Courses | ✅ | ✅ |
| View Resources/Quizzes | ✅ | ✅ |
| Create/Update/Delete Academic Data | ❌ | ✅ |
| User Management | ❌ | ✅ |

#### Security Headers
- **CORS**: Configured for cross-origin requests
- **CSRF**: Disabled for stateless JWT authentication
- **Content Security Policy**: Implemented for XSS protection
- **Session Management**: Stateless (no server-side sessions)

#### Password Security
- **Hashing**: BCrypt with configurable strength
- **Validation**: Strong password requirements
- **Reset**: Secure token-based password reset
- **Encryption**: Passwords never stored in plain text

#### API Security
- **Rate Limiting**: Configurable request throttling
- **Input Validation**: Bean Validation (JSR-303) on all inputs
- **SQL Injection**: Protected via JPA/Hibernate parameterized queries
- **XSS Protection**: Input sanitization and output encoding

### Email Security
- **SMTP Authentication**: Required for email services
- **TLS/SSL**: Encrypted email transmission
- **Token Verification**: Secure email verification tokens
- **Async Processing**: Non-blocking email operations

### Environment Security
- **Secrets Management**: Environment variables for sensitive data
- **Database Security**: Connection encryption and authentication
- **Logging**: Sensitive data excluded from logs
- **Error Handling**: Generic error messages to prevent information disclosure

## ⚙️ Setup and Installation

### Prerequisites

#### System Requirements
- **Java**: OpenJDK 21 or higher
- **Maven**: 3.8.0 or higher
- **PostgreSQL**: 12.0 or higher
- **Node.js**: 16.0 or higher (for frontend development)
- **Git**: Latest version

#### Development Tools (Recommended)
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code
- **Database Client**: pgAdmin, DBeaver, or similar
- **API Testing**: Postman, Insomnia, or curl
- **Browser**: Chrome, Firefox, or Safari (latest versions)

### Installation Steps

#### 1. Clone the Repository
```bash
git clone https://github.com/your-username/merbsconnect.git
cd merbsconnect
```

#### 2. Database Setup

**Create PostgreSQL Database:**
```sql
-- Connect to PostgreSQL as superuser
CREATE DATABASE merbsconnect;
CREATE USER merbsconnect_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE merbsconnect TO merbsconnect_user;

-- Connect to the merbsconnect database
\c merbsconnect;
GRANT ALL ON SCHEMA public TO merbsconnect_user;
```

**Verify Database Connection:**
```bash
psql -h localhost -U merbsconnect_user -d merbsconnect
```

#### 3. Environment Configuration

**Create Environment File:**
```bash
# Copy the example environment file
cp .env.example .env
```

**Configure Environment Variables:**
```bash
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/merbsconnect
DB_USERNAME=merbsconnect_user
DB_PASSWORD=your_database_password

# JWT Configuration
JWT_SECRET=your-256-bit-secret-key-base64-encoded
JWT_EXPIRATION_IN_MS=86400000
JWT_REFRESH_EXPIRATION_IN_MS=604800000
JWT_ISSUER=merbsconnect

# Email Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
CONNECTION_TIMEOUT=5000
READ_TIMEOUT=5000
WRITE_TIMEOUT=5000

# Application Configuration
BASE_URL=http://localhost:9000
```

#### 4. Build and Run

**Using Maven:**
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn package

# Run application
mvn spring-boot:run
```

**Using Java:**
```bash
# Build JAR file
mvn clean package

# Run JAR file
java -jar target/merbsconnect-0.0.1-SNAPSHOT.jar
```

#### 5. Verify Installation

**Check Application Status:**
```bash
# Health check endpoint
curl http://localhost:9000/actuator/health

# API documentation
curl http://localhost:9000/v3/api-docs
```

**Access Web Interface:**
- **Main Application**: http://localhost:9000
- **Admin Panel**: http://localhost:9000/admin.html
- **API Documentation**: http://localhost:9000/swagger-ui.html

### Docker Setup (Optional)

#### Docker Compose Configuration
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: merbsconnect
      POSTGRES_USER: merbsconnect_user
      POSTGRES_PASSWORD: your_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  app:
    build: .
    ports:
      - "9000:9000"
    depends_on:
      - postgres
    environment:
      - DB_URL=jdbc:postgresql://postgres:5432/merbsconnect
      - DB_USERNAME=merbsconnect_user
      - DB_PASSWORD=your_password

volumes:
  postgres_data:
```

#### Run with Docker
```bash
# Build and start services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

## 🔧 Configuration

### Application Configuration

#### Profile-based Configuration
The application supports multiple profiles for different environments:

- **Development** (`dev`): Local development with debug logging
- **Production** (`prod`): Production-ready configuration
- **Test** (`test`): Testing environment configuration

#### Core Configuration Files

**application.yaml** (Main configuration)
```yaml
spring:
  application:
    name: Merbs Connect
  profiles:
    active: dev
```

**application-dev.yaml** (Development profile)
```yaml
server:
  port: 9000

spring:
  datasource:
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    url: ${DB_URL}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  mail:
    host: ${MAIL_HOST}
    port: ${MAIL_PORT}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

app:
  jwt:
    secret: ${JWT_SECRET}
    expiration: ${JWT_EXPIRATION_IN_MS}
    refresh-token-expiration: ${JWT_REFRESH_EXPIRATION_IN_MS}
    issuer: ${JWT_ISSUER}
  base-url: ${BASE_URL}
  email:
    from: no-reply@merbsconnect.com

logging:
  level:
    com.merbsconnect: debug
    org.springframework.security: debug
```

### Environment Variables

#### Required Environment Variables
```bash
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/merbsconnect
DB_USERNAME=merbsconnect_user
DB_PASSWORD=your_secure_password

# JWT Security
JWT_SECRET=your-base64-encoded-256-bit-secret-key
JWT_EXPIRATION_IN_MS=86400000
JWT_REFRESH_EXPIRATION_IN_MS=604800000
JWT_ISSUER=merbsconnect

# Email Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-specific-password
CONNECTION_TIMEOUT=5000
READ_TIMEOUT=5000
WRITE_TIMEOUT=5000

# Application Settings
BASE_URL=http://localhost:9000
```

#### Optional Environment Variables
```bash
# Logging Configuration
LOG_LEVEL=INFO
LOG_FILE_PATH=/var/log/merbsconnect/app.log

# Performance Tuning
HIKARI_MAXIMUM_POOL_SIZE=20
HIKARI_MINIMUM_IDLE=5
HIKARI_CONNECTION_TIMEOUT=30000

# Email Threading
EMAIL_THREAD_POOL_SIZE=5
EMAIL_QUEUE_CAPACITY=25
```

### Security Configuration

#### JWT Configuration
```yaml
app:
  jwt:
    secret: ${JWT_SECRET}                    # Base64 encoded 256-bit key
    expiration: 86400000                     # 24 hours in milliseconds
    refresh-token-expiration: 604800000      # 7 days in milliseconds
    issuer: merbsconnect                     # Token issuer
```

#### CORS Configuration
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization", "Content-Type", "X-Requested-With",
        "Accept", "Origin", "Access-Control-Request-Method",
        "Access-Control-Request-Headers", "X-Auth-Token"
    ));
    configuration.setMaxAge(3600L);
    return source;
}
```

### Database Configuration

#### Connection Pool Settings
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
```

#### JPA/Hibernate Configuration
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update                    # Use 'validate' in production
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
```

### Email Configuration

#### SMTP Settings
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
          ssl:
            enable: false
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000
```

#### Email Templates
Email templates are located in `src/main/resources/email-templates/`:
- `verification-email.html`: User email verification
- `password-reset-email.html`: Password reset instructions

### Monitoring Configuration

#### Actuator Endpoints
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env
  endpoint:
    health:
      show-details: when-authorized
```

#### Available Endpoints
- `/actuator/health`: Application health status
- `/actuator/info`: Application information
- `/actuator/metrics`: Application metrics
- `/actuator/env`: Environment properties

## 🚀 Running the Application

### Development Mode

#### Using Maven
```bash
# Run with development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run with custom port
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8080

# Run with debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

#### Using IDE
1. **IntelliJ IDEA**:
   - Open the project
   - Navigate to `MerbsconnectApplication.java`
   - Right-click and select "Run 'MerbsconnectApplication'"
   - Set VM options: `-Dspring.profiles.active=dev`

2. **Eclipse**:
   - Import as Maven project
   - Right-click project → Run As → Spring Boot App
   - Configure environment variables in Run Configuration

3. **VS Code**:
   - Install Spring Boot Extension Pack
   - Open Command Palette (Ctrl+Shift+P)
   - Run "Spring Boot: Run"

### Production Mode

#### JAR Deployment
```bash
# Build production JAR
mvn clean package -Pprod

# Run with production profile
java -jar -Dspring.profiles.active=prod target/merbsconnect-0.0.1-SNAPSHOT.jar

# Run with custom configuration
java -jar -Dspring.config.location=classpath:/application.yaml,/path/to/external/config.yaml target/merbsconnect-0.0.1-SNAPSHOT.jar
```

#### System Service (Linux)
```bash
# Create systemd service file
sudo nano /etc/systemd/system/merbsconnect.service
```

```ini
[Unit]
Description=MerbsConnect Academic Management System
After=network.target

[Service]
Type=simple
User=merbsconnect
ExecStart=/usr/bin/java -jar /opt/merbsconnect/merbsconnect.jar
Restart=always
RestartSec=10
Environment=SPRING_PROFILES_ACTIVE=prod

[Install]
WantedBy=multi-user.target
```

```bash
# Enable and start service
sudo systemctl enable merbsconnect
sudo systemctl start merbsconnect
sudo systemctl status merbsconnect
```

### Frontend Access

#### Web Interface URLs
- **Landing Page**: http://localhost:9000/
- **User Dashboard**: http://localhost:9000/home.html
- **Admin Panel**: http://localhost:9000/admin.html
- **API Documentation**: http://localhost:9000/swagger-ui.html

#### Default Admin Account
For initial setup, create an admin account via API:
```bash
curl -X POST http://localhost:9000/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Admin",
    "lastName": "User",
    "email": "admin@merbsconnect.com",
    "password": "AdminPassword123",
    "phoneNumber": "+1234567890"
  }'
```

### Application Logs

#### Log Locations
- **Console Output**: Standard output during development
- **File Logging**: Configurable via `logging.file.path`
- **Application Logs**: `/var/log/merbsconnect/app.log` (production)

#### Log Levels
```yaml
logging:
  level:
    root: INFO
    com.merbsconnect: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Performance Monitoring

#### JVM Monitoring
```bash
# Monitor JVM metrics
curl http://localhost:9000/actuator/metrics/jvm.memory.used

# Monitor HTTP requests
curl http://localhost:9000/actuator/metrics/http.server.requests

# Monitor database connections
curl http://localhost:9000/actuator/metrics/hikaricp.connections.active
```

#### Health Checks
```bash
# Basic health check
curl http://localhost:9000/actuator/health

# Detailed health check (requires authentication)
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" http://localhost:9000/actuator/health
```

## 🧪 Testing

### Testing Framework

The application uses a comprehensive testing strategy with multiple testing frameworks:

- **Unit Tests**: JUnit 5 with Mockito
- **Integration Tests**: Spring Boot Test with TestContainers
- **Security Tests**: Spring Security Test
- **Web Layer Tests**: MockMvc for controller testing

### Test Structure

```
src/test/java/
├── com/merbsconnect/
│   ├── academics/
│   │   ├── controller/          # Controller tests
│   │   ├── service/             # Service layer tests
│   │   └── repository/          # Repository tests
│   ├── authentication/
│   │   ├── controller/          # Auth controller tests
│   │   ├── service/             # Auth service tests
│   │   └── security/            # Security tests
│   └── integration/             # Integration tests
```

### Running Tests

#### All Tests
```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Run tests in specific profile
mvn test -Dspring.profiles.active=test
```

#### Specific Test Categories
```bash
# Run unit tests only
mvn test -Dtest="*Test"

# Run integration tests only
mvn test -Dtest="*IT"

# Run controller tests only
mvn test -Dtest="*ControllerTest"

# Run service tests only
mvn test -Dtest="*ServiceTest"
```

### Test Configuration

#### Test Application Properties
```yaml
# src/test/resources/application-test.yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

  mail:
    host: localhost
    port: 2525

app:
  jwt:
    secret: dGVzdC1zZWNyZXQtZm9yLWp1bml0LXRlc3RzLW9ubHktZG8tbm90LXVzZS1pbi1wcm9kdWN0aW9u
    expiration: 3600000
    issuer: merbsconnect-test

logging:
  level:
    com.merbsconnect: DEBUG
```

### Sample Test Cases

#### Unit Test Example
```java
@ExtendWith(MockitoExtension.class)
class CollegeServiceTest {

    @Mock
    private CollegeRepository collegeRepository;

    @InjectMocks
    private CollegeServiceImpl collegeService;

    @Test
    void createCollege_ShouldReturnCollegeResponse_WhenValidRequest() {
        // Given
        CreateCollegeRequest request = new CreateCollegeRequest("Engineering College");
        College college = College.builder()
                .id(1L)
                .collegeName("Engineering College")
                .build();

        when(collegeRepository.save(any(College.class))).thenReturn(college);

        // When
        CollegeResponse response = collegeService.createCollege(request);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCollegeName()).isEqualTo("Engineering College");
        verify(collegeRepository).save(any(College.class));
    }
}
```

#### Integration Test Example
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yaml")
class CollegeControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CollegeRepository collegeRepository;

    @Test
    void createCollege_ShouldReturn201_WhenValidRequest() {
        // Given
        CreateCollegeRequest request = new CreateCollegeRequest("Test College");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAdminToken());
        HttpEntity<CreateCollegeRequest> entity = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                "/api/v1/colleges", entity, ApiResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(collegeRepository.findByCollegeName("Test College")).isPresent();
    }
}
```

#### Security Test Example
```java
@WebMvcTest(CollegeController.class)
@Import(SecurityConfig.class)
class CollegeControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CollegeService collegeService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCollege_ShouldReturn201_WhenAdminUser() throws Exception {
        // Given
        CreateCollegeRequest request = new CreateCollegeRequest("Test College");
        CollegeResponse response = new CollegeResponse(1L, "Test College", null);

        when(collegeService.createCollege(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/colleges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.collegeName").value("Test College"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createCollege_ShouldReturn403_WhenRegularUser() throws Exception {
        // Given
        CreateCollegeRequest request = new CreateCollegeRequest("Test College");

        // When & Then
        mockMvc.perform(post("/api/v1/colleges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpected(status().isForbidden());
    }
}
```

### Test Data Management

#### Test Data Builders
```java
public class TestDataBuilder {

    public static College.CollegeBuilder defaultCollege() {
        return College.builder()
                .collegeName("Test College")
                .faculties(new HashSet<>());
    }

    public static User.UserBuilder defaultUser() {
        return User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .password("encodedPassword")
                .phoneNumber("+1234567890")
                .role(UserRole.USER)
                .isEnabled(true);
    }
}
```

#### Database Cleanup
```java
@TestMethodOrder(OrderAnnotation.class)
@Transactional
@Rollback
class DatabaseIntegrationTest {

    @BeforeEach
    void setUp() {
        // Clean database before each test
        collegeRepository.deleteAll();
        userRepository.deleteAll();
    }
}
```

### Test Coverage

#### Coverage Goals
- **Line Coverage**: Minimum 80%
- **Branch Coverage**: Minimum 70%
- **Method Coverage**: Minimum 85%

#### Generate Coverage Report
```bash
# Generate JaCoCo coverage report
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

#### Coverage Configuration
```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Continuous Integration

#### GitHub Actions Workflow
```yaml
# .github/workflows/ci.yml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
          POSTGRES_DB: merbsconnect_test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 21
      uses: actions/setup-java@v3
      with:
        java-version: '21'
        distribution: 'temurin'

    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}

    - name: Run tests
      run: mvn clean test

    - name: Generate test report
      run: mvn jacoco:report

    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
```

## 🚀 Deployment

### Production Deployment

#### Prerequisites for Production
- **Server**: Linux server (Ubuntu 20.04+ recommended)
- **Java**: OpenJDK 21
- **Database**: PostgreSQL 12+
- **Reverse Proxy**: Nginx or Apache
- **SSL Certificate**: Let's Encrypt or commercial certificate
- **Monitoring**: Application monitoring tools

#### Server Setup

##### 1. System Preparation
```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Install Java 21
sudo apt install openjdk-21-jdk -y

# Install PostgreSQL
sudo apt install postgresql postgresql-contrib -y

# Install Nginx
sudo apt install nginx -y

# Create application user
sudo useradd -m -s /bin/bash merbsconnect
sudo mkdir -p /opt/merbsconnect
sudo chown merbsconnect:merbsconnect /opt/merbsconnect
```

##### 2. Database Setup
```bash
# Switch to postgres user
sudo -u postgres psql

-- Create production database
CREATE DATABASE merbsconnect_prod;
CREATE USER merbsconnect_prod WITH PASSWORD 'secure_production_password';
GRANT ALL PRIVILEGES ON DATABASE merbsconnect_prod TO merbsconnect_prod;

-- Configure PostgreSQL
sudo nano /etc/postgresql/14/main/postgresql.conf
# Set: listen_addresses = 'localhost'

sudo nano /etc/postgresql/14/main/pg_hba.conf
# Add: local   merbsconnect_prod   merbsconnect_prod   md5

sudo systemctl restart postgresql
```

##### 3. Application Deployment
```bash
# Copy JAR file to server
scp target/merbsconnect-0.0.1-SNAPSHOT.jar user@server:/opt/merbsconnect/

# Create production configuration
sudo nano /opt/merbsconnect/application-prod.yaml
```

**Production Configuration:**
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/merbsconnect_prod
    username: merbsconnect_prod
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

  mail:
    host: ${MAIL_HOST}
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}

app:
  jwt:
    secret: ${JWT_SECRET}
    expiration: 86400000
    issuer: merbsconnect-prod

logging:
  level:
    root: INFO
    com.merbsconnect: INFO
  file:
    path: /var/log/merbsconnect/
```

##### 4. Environment Variables
```bash
# Create environment file
sudo nano /opt/merbsconnect/.env
```

```bash
# Production environment variables
DB_PASSWORD=secure_production_password
JWT_SECRET=your-production-jwt-secret-base64-encoded
MAIL_HOST=smtp.gmail.com
MAIL_USERNAME=noreply@yourdomain.com
MAIL_PASSWORD=your-app-password
```

##### 5. Systemd Service
```bash
# Create systemd service
sudo nano /etc/systemd/system/merbsconnect.service
```

```ini
[Unit]
Description=MerbsConnect Academic Management System
After=network.target postgresql.service

[Service]
Type=simple
User=merbsconnect
Group=merbsconnect
WorkingDirectory=/opt/merbsconnect
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod -Dspring.config.location=classpath:/application.yaml,/opt/merbsconnect/application-prod.yaml merbsconnect-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=merbsconnect
EnvironmentFile=/opt/merbsconnect/.env

[Install]
WantedBy=multi-user.target
```

```bash
# Enable and start service
sudo systemctl daemon-reload
sudo systemctl enable merbsconnect
sudo systemctl start merbsconnect
sudo systemctl status merbsconnect
```

#### Nginx Configuration

##### SSL Certificate (Let's Encrypt)
```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx -y

# Obtain SSL certificate
sudo certbot --nginx -d yourdomain.com
```

##### Nginx Virtual Host
```bash
sudo nano /etc/nginx/sites-available/merbsconnect
```

```nginx
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

    # SSL configuration
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # Security headers
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;

    # Proxy to Spring Boot application
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }

    # Static files caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # API rate limiting
    location /api/ {
        limit_req zone=api burst=20 nodelay;
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

# Rate limiting configuration
http {
    limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
}
```

```bash
# Enable site and restart Nginx
sudo ln -s /etc/nginx/sites-available/merbsconnect /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### Monitoring and Maintenance

#### Application Monitoring
```bash
# Monitor application logs
sudo journalctl -u merbsconnect -f

# Monitor system resources
htop
iostat -x 1
free -h

# Monitor database connections
sudo -u postgres psql -c "SELECT * FROM pg_stat_activity WHERE datname='merbsconnect_prod';"
```

#### Backup Strategy
```bash
# Database backup script
#!/bin/bash
BACKUP_DIR="/opt/backups/merbsconnect"
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p $BACKUP_DIR

# Create database backup
sudo -u postgres pg_dump merbsconnect_prod > $BACKUP_DIR/db_backup_$DATE.sql

# Compress backup
gzip $BACKUP_DIR/db_backup_$DATE.sql

# Remove backups older than 30 days
find $BACKUP_DIR -name "*.sql.gz" -mtime +30 -delete
```

#### Health Monitoring
```bash
# Create health check script
#!/bin/bash
HEALTH_URL="http://localhost:8080/actuator/health"
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $HEALTH_URL)

if [ $RESPONSE -eq 200 ]; then
    echo "Application is healthy"
    exit 0
else
    echo "Application health check failed with status: $RESPONSE"
    # Send alert notification
    exit 1
fi
```

## 🤝 Contributing

### Development Workflow

#### Getting Started
1. **Fork the Repository**
   ```bash
   git clone https://github.com/your-username/merbsconnect.git
   cd merbsconnect
   git remote add upstream https://github.com/original-repo/merbsconnect.git
   ```

2. **Set Up Development Environment**
   ```bash
   # Install dependencies
   mvn clean install

   # Set up pre-commit hooks
   cp .githooks/pre-commit .git/hooks/
   chmod +x .git/hooks/pre-commit
   ```

3. **Create Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

#### Code Standards

##### Java Code Style
- **Formatting**: Follow Google Java Style Guide
- **Naming**: Use descriptive names for classes, methods, and variables
- **Documentation**: Javadoc for public APIs
- **Testing**: Minimum 80% test coverage for new code

##### Code Quality Tools
```xml
<!-- pom.xml -->
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.7.3.0</version>
</plugin>

<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>3.9.1.2184</version>
</plugin>
```

##### Frontend Standards
- **JavaScript**: ES6+ features, consistent formatting
- **CSS**: BEM methodology for class naming
- **HTML**: Semantic markup, accessibility compliance
- **Performance**: Optimize images and minimize bundle size

#### Commit Guidelines

##### Commit Message Format
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

**Example:**
```
feat(academics): add quiz creation functionality

- Implement QuizController with CRUD operations
- Add QuizService with business logic
- Create Quiz entity with JPA mappings
- Add comprehensive test coverage

Closes #123
```

#### Pull Request Process

1. **Before Submitting**
   ```bash
   # Run tests
   mvn clean test

   # Check code quality
   mvn spotbugs:check

   # Format code
   mvn spring-javaformat:apply
   ```

2. **Pull Request Template**
   ```markdown
   ## Description
   Brief description of changes

   ## Type of Change
   - [ ] Bug fix
   - [ ] New feature
   - [ ] Breaking change
   - [ ] Documentation update

   ## Testing
   - [ ] Unit tests pass
   - [ ] Integration tests pass
   - [ ] Manual testing completed

   ## Checklist
   - [ ] Code follows style guidelines
   - [ ] Self-review completed
   - [ ] Documentation updated
   - [ ] No breaking changes
   ```

3. **Review Process**
   - Minimum 2 approvals required
   - All CI checks must pass
   - No merge conflicts
   - Documentation updated if needed

#### Issue Reporting

##### Bug Reports
```markdown
**Bug Description**
Clear description of the bug

**Steps to Reproduce**
1. Step one
2. Step two
3. Step three

**Expected Behavior**
What should happen

**Actual Behavior**
What actually happens

**Environment**
- OS: [e.g., Ubuntu 20.04]
- Java Version: [e.g., OpenJDK 21]
- Browser: [e.g., Chrome 91]

**Additional Context**
Screenshots, logs, etc.
```

##### Feature Requests
```markdown
**Feature Description**
Clear description of the proposed feature

**Use Case**
Why is this feature needed?

**Proposed Solution**
How should this feature work?

**Alternatives Considered**
Other approaches considered

**Additional Context**
Mockups, examples, etc.
```

### Development Guidelines

#### API Development
- **RESTful Design**: Follow REST principles
- **Versioning**: Use URL versioning (/api/v1/)
- **Documentation**: OpenAPI/Swagger documentation
- **Error Handling**: Consistent error response format
- **Validation**: Input validation on all endpoints

#### Database Changes
- **Migrations**: Use Flyway for database migrations
- **Backward Compatibility**: Ensure migrations are backward compatible
- **Testing**: Test migrations on sample data
- **Documentation**: Document schema changes

#### Security Considerations
- **Authentication**: JWT token validation
- **Authorization**: Role-based access control
- **Input Validation**: Sanitize all user inputs
- **SQL Injection**: Use parameterized queries
- **XSS Protection**: Escape output data

#### Performance Guidelines
- **Database**: Use appropriate indexes
- **Caching**: Implement caching where appropriate
- **Pagination**: Implement pagination for large datasets
- **Lazy Loading**: Use lazy loading for relationships
- **Monitoring**: Add performance metrics

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### MIT License

```
MIT License

Copyright (c) 2024 MerbsConnect

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 📞 Support

### Getting Help

#### Documentation
- **API Documentation**: http://localhost:9000/swagger-ui.html
- **Technical Documentation**: This README file
- **Code Examples**: See `/examples` directory

#### Community Support
- **GitHub Issues**: Report bugs and request features
- **Discussions**: Join community discussions
- **Wiki**: Additional documentation and guides

#### Professional Support
For enterprise support and consulting services, please contact:
- **Email**: support@merbsconnect.com
- **Website**: https://merbsconnect.com
- **LinkedIn**: [MerbsConnect](https://linkedin.com/company/merbsconnect)

### Frequently Asked Questions

#### Q: How do I reset the admin password?
A: Use the password reset API endpoint or directly update the database with a new BCrypt hash.

#### Q: Can I customize the email templates?
A: Yes, email templates are located in `src/main/resources/email-templates/` and can be customized.

#### Q: How do I add a new academic entity?
A: Follow the existing pattern: create entity, repository, service, controller, and DTOs. See the College implementation as an example.

#### Q: Is there a mobile app?
A: Currently, only the responsive web interface is available. The API can be used to build mobile applications.

#### Q: How do I backup the database?
A: Use the provided backup scripts in the deployment section or standard PostgreSQL backup tools.

---

**Built with ❤️ by the MerbsConnect Team**

*Empowering education through technology*