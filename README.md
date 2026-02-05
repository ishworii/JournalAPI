# Journal API

A secure RESTful API for managing personal journals with user authentication and role-based access control. Built with Spring Boot 4.0.2 and PostgreSQL.

## Overview

This application allows users to create, read, update, and delete journal entries with proper authentication and authorization. Each user can only access their own journals, while administrators have full access to all entries across the system.

## Features

- User registration and authentication with JWT tokens
- Role-based access control (USER and ADMIN roles)
- CRUD operations for journal entries
- Journal ownership enforcement - users can only access their own journals
- Admin users have unrestricted access to all journals
- Comprehensive test coverage (33 tests)
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
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
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
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

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

#### Get all journals

```
GET /journal
Authorization: Bearer <token>
```

**Behavior**:
- Regular users: Returns only their own journals
- Admin users: Returns all journals from all users

**Response**: `200 OK`
```json
[
  {
    "id": 1,
    "title": "My First Journal",
    "content": "Today was a great day..."
  }
]
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
  "content": "Today was a great day..."
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
  "content": "Journal content here..."
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
  "content": "Updated content..."
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

The API uses JWT (JSON Web Token) for authentication. After registering or logging in, include the token in subsequent requests:

```
Authorization: Bearer <your-jwt-token>
```

JWT tokens expire after 1 hour (3600000 milliseconds).

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

### 404 Not Found
Returned when a requested journal does not exist or the user doesn't have permission to access it.

```json
{
  "status": 404,
  "error": "Not found",
  "message": "Journal not found with id:123"
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

| Column   | Type         | Constraints                    |
|----------|--------------|--------------------------------|
| id       | BIGINT       | PRIMARY KEY, AUTO INCREMENT    |
| title    | VARCHAR(255) | NOT NULL                       |
| content  | TEXT         | NOT NULL                       |
| owner_id | BIGINT       | FOREIGN KEY (users.id), NOT NULL |

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

- **Total Tests**: 33
- **HealthCheckControllerTest**: 3 tests
- **UserControllerTest**: 5 tests
- **AuthControllerTest**: 5 tests
- **JournalControllerTest**: 20 tests
  - 11 basic CRUD and authentication tests
  - 9 ownership and authorization tests

All tests include:
- Happy path scenarios
- Error handling
- Authentication requirements
- Authorization and ownership validation
- Admin privilege verification

## Security Features

1. **Password Hashing**: User passwords are hashed using BCrypt before storage
2. **JWT Authentication**: Stateless authentication using JWT tokens
3. **Role-Based Access Control**: USER and ADMIN roles with different permissions
4. **Ownership Enforcement**: Users can only access their own resources
5. **Admin Privileges**: Admin users have unrestricted access for management purposes

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
jwt.expiration=3600000
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

1. **Pagination and Sorting**
   - Implement pagination for journal list endpoints
   - Add sorting capabilities (by date, title, etc.)
   - Add filtering options (date range, search by title/content)

2. **Input Validation**
   - Add comprehensive request validation using Bean Validation (JSR-380)
   - Validate email format, password strength requirements
   - Add maximum length constraints for title and content
   - Return detailed validation error messages

3. **API Documentation**
   - Integrate Swagger/OpenAPI for interactive API documentation
   - Auto-generate API documentation from code annotations
   - Include request/response examples

4. **Enhanced Security**
   - Implement refresh tokens for better token management
   - Add token revocation/blacklist mechanism
   - Implement rate limiting to prevent abuse
   - Add CORS configuration for production environments
   - Move JWT secret to environment variables or secure vault

### Medium Priority

5. **Journal Features**
   - Add timestamps (createdAt, updatedAt) to journal entries
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
