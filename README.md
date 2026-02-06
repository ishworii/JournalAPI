# Journal API

A secure RESTful API for managing personal journals with user authentication and role-based access control. Built with Spring Boot 4.0.2 and PostgreSQL.

## Overview

This application allows users to create, read, update, and delete journal entries with proper authentication and authorization. Each user can only access their own journals, while administrators have full access to all entries across the system.

## Features

- User registration and authentication with JWT tokens
- **Refresh token system with automatic token rotation** for enhanced security
- Role-based access control (USER and ADMIN roles)
- CRUD operations for journal entries with pagination support
- Journal ownership enforcement - users can only access their own journals
- Admin users have unrestricted access to all journals
- **Interactive API documentation with Swagger UI**
- Automatic timestamps (createdAt, updatedAt) on journal entries
- Comprehensive input validation with detailed error messages
- Comprehensive test coverage (40 tests)
- RESTful API design
- Stateless authentication

## Technology Stack

- **Framework**: Spring Boot 4.0.2
- **Language**: Java 17
- **Database**: PostgreSQL 16.11
- **Security**: Spring Security with JWT
- **ORM**: Hibernate / Spring Data JPA
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Build Tool**: Maven

## Prerequisites

- Java 17 or higher
- PostgreSQL 16 or higher
- Maven 3.6 or higher

## Database Setup

1. Create a PostgreSQL database:

```sql
CREATE DATABASE journaldb;
CREATE USER journaluser WITH PASSWORD 'journalpass';
GRANT ALL PRIVILEGES ON DATABASE journaldb TO journaluser;
```

2. The application will automatically create the required tables on startup using Hibernate's DDL auto-update feature.

## Installation

1. Clone the repository:

```bash
git clone <repository-url>
cd JournalAPI
```

2. Configure database connection (if different from defaults):

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/journaldb
spring.datasource.username=journaluser
spring.datasource.password=journalpass
```

3. Build the project:

```bash
mvn clean install
```

4. Run the application:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

Interactive API documentation is available via Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

The OpenAPI specification is available at:

```
http://localhost:8080/v3/api-docs
```

## API Endpoints

### Health Check

```
GET /health
```

Returns the health status of the application. No authentication required.

**Response**: `200 OK`
```
Ok
```

### Authentication

#### Register a new user

```
POST /auth/register
Content-Type: application/json
```

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123",
  "role": "USER"
}
```

**Response**: `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Error Response**: `409 Conflict` (Email already registered)
```json
{
  "status": 409,
  "error": "Email already registered",
  "message": "Email already registered: user@example.com"
}
```

#### Login

```
POST /auth/login
Content-Type: application/json
```

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response**: `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Error Response**: `401 Unauthorized` (Invalid credentials)
```json
{
  "status": 401,
  "error": "Authentication failed",
  "message": "Bad credentials"
}
```

#### Refresh Access Token

```
POST /auth/refresh
Content-Type: application/json
```

**Request Body**:
```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response**: `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "660f9500-f39c-52e5-b827-557766551111"
}
```

**Notes**:
- The refresh token is automatically rotated on each refresh (old token becomes invalid)
- Access tokens are valid for 15 minutes
- Refresh tokens are valid for 24 hours
- You can refresh at any time while the refresh token is valid (don't need to wait for access token to expire)

**Error Response**: `401 Unauthorized` (Invalid or expired refresh token)
```json
{
  "status": 401,
  "error": "Invalid or expired refresh token",
  "message": "Refresh token expired. Please login again."
}
```

#### Logout

```
POST /auth/logout
Authorization: Bearer <token>
```

**Response**: `200 OK`
```json
{
  "message": "Logged out successfully",
  "note": "Your refresh token has been revoked. Access token will remain valid until expiration."
}
```

**Note**: Logout invalidates the refresh token. The access token remains valid until it naturally expires (15 minutes).

### User Management

#### Get current user information

```
GET /me
Authorization: Bearer <token>
```

**Response**: `200 OK`
```json
{
  "id": 1,
  "email": "user@example.com",
  "role": "USER"
}
```

### Journal Management

All journal endpoints require authentication.

#### Get all journals (paginated)

```
GET /journal?page=0&size=15
Authorization: Bearer <token>
```

**Query Parameters**:
- `page` (optional): Page number, starts at 0 (default: 0)
- `size` (optional): Number of items per page (default: 15)

**Behavior**:
- Regular users: Returns only their own journals
- Admin users: Returns all journals from all users

**Response**: `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "title": "My First Journal",
      "content": "Today was a great day...",
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
    },
    {
      "id": 2,
      "title": "Another Entry",
      "content": "Reflections on the week...",
      "createdAt": "2024-01-16T14:20:00Z",
      "updatedAt": "2024-01-16T14:20:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 15
  },
  "totalElements": 2,
  "totalPages": 1,
  "last": true,
  "first": true
}
```

#### Get journal by ID

```
GET /journal/{id}
Authorization: Bearer <token>
```

**Behavior**:
- Regular users: Can only access their own journals (404 if not owner)
- Admin users: Can access any journal

**Response**: `200 OK`
```json
{
  "id": 1,
  "title": "My First Journal",
  "content": "Today was a great day...",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

#### Create a journal

```
POST /journal
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body**:
```json
{
  "title": "My Journal Entry",
  "content": "Journal content here..."
}
```

**Response**: `201 Created`
```json
{
  "id": 1,
  "title": "My Journal Entry",
  "content": "Journal content here...",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

#### Update a journal

```
PUT /journal/{id}
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body**:
```json
{
  "title": "Updated Title",
  "content": "Updated content..."
}
```

**Behavior**:
- Regular users: Can only update their own journals (404 if not owner)
- Admin users: Can update any journal

**Response**: `200 OK`
```json
{
  "id": 1,
  "title": "Updated Title",
  "content": "Updated content...",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-16T09:15:00Z"
}
```

#### Delete a journal

```
DELETE /journal/{id}
Authorization: Bearer <token>
```

**Behavior**:
- Regular users: Can only delete their own journals (404 if not owner)
- Admin users: Can delete any journal

**Response**: `204 No Content`

## Authentication

The API uses JWT (JSON Web Token) for authentication with refresh token rotation for enhanced security.

### Token Types

1. **Access Token**: Short-lived JWT token (15 minutes)
   - Use for authenticating API requests
   - Include in `Authorization: Bearer <access-token>` header
   - Expires after 15 minutes

2. **Refresh Token**: Long-lived token (24 hours)
   - Use to obtain new access tokens without re-login
   - Automatically rotated on each refresh (old token invalidated)
   - Stored in database and can be revoked on logout
   - Expires after 24 hours

### Authentication Flow

1. **Initial Login/Register**: Receive both access and refresh tokens
2. **API Requests**: Use access token in Authorization header
3. **Token Refresh**: When access token expires (or before), use refresh token to get new tokens
4. **Logout**: Explicitly revoke refresh token

### Using Tokens

Include the access token in subsequent requests:

```
Authorization: Bearer <your-access-token>
```

## Error Responses

### 401 Unauthorized
Returned when authentication is required but not provided or token is invalid.

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Unauthorized"
}
```

### 400 Bad Request
Returned when request validation fails.

```json
{
  "status": 400,
  "error": "Validation Failed",
  "validationErrors": {
    "email": "Email is required",
    "password": "Password must be at least 8 characters"
  }
}
```

### 404 Not Found
Returned when a requested journal does not exist or the user doesn't have permission to access it.

```json
{
  "status": 404,
  "error": "Not found",
  "message": "Journal not found with id:123"
}
```

### 409 Conflict
Returned when attempting to register with an email that already exists.

```json
{
  "status": 409,
  "error": "Email already registered",
  "message": "Email already registered: user@example.com"
}
```

## Database Schema

### users table

| Column        | Type         | Constraints           |
|--------------|--------------|----------------------|
| id           | BIGINT       | PRIMARY KEY, AUTO INCREMENT |
| email        | VARCHAR(255) | UNIQUE, NOT NULL     |
| password_hash| VARCHAR(255) | NOT NULL             |
| role         | VARCHAR(50)  | NOT NULL, DEFAULT 'USER' |

### journals table

| Column     | Type         | Constraints                    |
|------------|--------------|--------------------------------|
| id         | BIGINT       | PRIMARY KEY, AUTO INCREMENT    |
| title      | VARCHAR(255) | NOT NULL                       |
| content    | TEXT         | NOT NULL                       |
| owner_id   | BIGINT       | FOREIGN KEY (users.id), NOT NULL |
| created_at | TIMESTAMP    | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP    | NOT NULL, DEFAULT CURRENT_TIMESTAMP |

### refresh_tokens table

| Column      | Type         | Constraints                    |
|-------------|--------------|--------------------------------|
| id          | BIGINT       | PRIMARY KEY, AUTO INCREMENT    |
| token       | VARCHAR(255) | UNIQUE, NOT NULL               |
| user_id     | BIGINT       | FOREIGN KEY (users.id), UNIQUE, NOT NULL |
| expiry_date | TIMESTAMP    | NOT NULL                       |

## Running Tests

Run all tests:

```bash
mvn test
```

Run specific test class:

```bash
mvn test -Dtest=JournalControllerTest
mvn test -Dtest=AuthControllerTest
mvn test -Dtest=UserControllerTest
mvn test -Dtest=HealthCheckControllerTest
```

Run all controller tests:

```bash
mvn test -Dtest="*ControllerTest"
```

### Test Coverage

- **Total Tests**: 40
- **AuthControllerTest**: 11 tests
  - Registration and login tests
  - Refresh token tests with token rotation
  - Logout tests
  - Public endpoint accessibility tests
- **JournalControllerTest**: 20 tests
  - CRUD operations with pagination
  - Ownership and authorization tests
  - Admin privilege tests
- **UserControllerTest**: 8 tests
  - User profile endpoint tests
- **JournalApiApplicationTests**: 1 test
  - Application context loading test

All tests include:
- Happy path scenarios
- Error handling
- Authentication requirements
- Authorization and ownership validation
- Admin privilege verification
- Token refresh and rotation validation

## Security Features

1. **Password Hashing**: User passwords are hashed using BCrypt before storage
2. **JWT Authentication**: Stateless authentication using short-lived JWT access tokens (15 minutes)
3. **Refresh Token Rotation**: Automatic token rotation on refresh for enhanced security
4. **Token Revocation**: Logout invalidates refresh tokens immediately
5. **Role-Based Access Control**: USER and ADMIN roles with different permissions
6. **Ownership Enforcement**: Users can only access their own resources
7. **Admin Privileges**: Admin users have unrestricted access for management purposes
8. **Input Validation**: Comprehensive validation with detailed error messages

## Configuration

Key configuration properties in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/journaldb
spring.datasource.username=journaluser
spring.datasource.password=journalpass

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=<your-secret-key>
jwt.expiration=900000  # 15 minutes in milliseconds
```

## Development

### Project Structure

```
src/main/java/com/ishwor/helloworld/
├── config/              # Security and application configuration
├── controller/          # REST controllers
├── dto/                # Data Transfer Objects
├── entity/             # JPA entities
├── exception/          # Custom exceptions and handlers
├── mapper/             # Entity-DTO mappers
├── repository/         # Data access layer
├── security/           # JWT filters and security components
└── service/            # Business logic layer
    └── impl/           # Service implementations
```

### Adding a New Feature

1. Create entity in `entity/` package
2. Create repository in `repository/` package
3. Create DTOs in `dto/` package
4. Create mapper in `mapper/` package
5. Create service interface in `service/` and implementation in `service/impl/`
6. Create controller in `controller/` package
7. Add comprehensive tests in `src/test/java/`

## Future Improvements

### High Priority

1. ✅ **Pagination and Sorting** - COMPLETED
   - ✅ Pagination for journal list endpoints
   - Add sorting capabilities (by date, title, etc.)
   - Add filtering options (date range, search by title/content)

2. ✅ **Input Validation** - COMPLETED
   - ✅ Comprehensive request validation using Bean Validation (JSR-380)
   - ✅ Validate email format, password strength requirements
   - ✅ Maximum length constraints for title and content
   - ✅ Detailed validation error messages

3. ✅ **API Documentation** - COMPLETED
   - ✅ Swagger/OpenAPI for interactive API documentation
   - ✅ Auto-generate API documentation from code annotations
   - ✅ Request/response examples with schemas

4. ✅ **Enhanced Security** - PARTIALLY COMPLETED
   - ✅ Refresh tokens with automatic token rotation
   - ✅ Token revocation on logout
   - Implement rate limiting to prevent abuse
   - Add CORS configuration for production environments
   - Move JWT secret to environment variables or secure vault

### Medium Priority

5. **Journal Features** - PARTIALLY COMPLETED
   - ✅ Automatic timestamps (createdAt, updatedAt) on journal entries
   - Support for tags/categories for better organization
   - Add journal sharing capabilities between users
   - Implement soft delete for journals (archive instead of permanent delete)

6. **User Management**
   - Add user profile management endpoints
   - Implement password reset functionality
   - Add email verification for new registrations
   - Support for password change endpoint

7. **Search and Analytics**
   - Full-text search across journal content
   - User analytics dashboard (journal count, activity over time)
   - Export journals to PDF or other formats

8. **Performance Optimization**
   - Add Redis caching for frequently accessed data
   - Optimize database queries with proper indexing
   - Implement database connection pooling configuration
   - Add database query performance monitoring

### Low Priority

9. **Enhanced Testing**
   - Add integration tests with test containers
   - Implement performance/load testing
   - Add code coverage reporting (JaCoCo)
   - Implement contract testing for API stability

10. **DevOps and Deployment**
    - Create Docker containerization setup
    - Add CI/CD pipeline configuration
    - Implement proper logging with structured logging (JSON format)
    - Add application monitoring and health metrics (Actuator endpoints)
    - Environment-specific configuration profiles

11. **Advanced Features**
    - Multi-language support (i18n)
    - Rich text editor support for journal content
    - Image/file attachment support for journals
    - Journal templates for common use cases
    - Reminder/notification system for regular journaling

12. **API Versioning**
    - Implement API versioning strategy (URL-based or header-based)
    - Maintain backward compatibility for breaking changes

13. **Audit Trail**
    - Log all journal modifications with timestamps
    - Track who (admin) accessed/modified other users' journals
    - Implement compliance and audit reporting

## License

This project is licensed under the MIT License.

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Support

For issues, questions, or contributions, please open an issue in the repository.
