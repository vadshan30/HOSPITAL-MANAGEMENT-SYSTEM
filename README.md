# Hospital Management System

A production-ready REST API backend for managing hospital operations including patients, doctors, appointments, medical records, and administrators.

## Features

- Patient management (CRUD)
- Doctor management (CRUD + pagination)
- Appointment scheduling and status tracking
- Medical records per patient
- Admin management with BCrypt password hashing
- JWT-based stateless authentication
- Role-based access control: ADMIN / DOCTOR / PATIENT
- Bean Validation on all inputs
- Global exception handling with consistent JSON error responses
- Swagger/OpenAPI documentation
- AOP-based service logging

## Technology Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.4.x |
| Language | Java 17 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Persistence | Spring Data JPA + Hibernate |
| Database (prod) | MySQL 8 |
| Database (test) | H2 (in-memory) |
| Validation | Jakarta Bean Validation |
| Documentation | SpringDoc OpenAPI / Swagger UI |
| Build | Maven |

## Project Structure

```
src/main/java/com/examly/springapp/
├── aop/                  # AOP logging aspect
├── configuration/        # SecurityConfig, TestSecurityConfig
├── controller/           # REST controllers
├── exception/            # GlobalExceptionHandler, ResourceNotFoundException
├── model/                # JPA entities
├── repository/           # Spring Data JPA repositories
├── security/             # JWT utilities, filter, UserDetailsService
└── service/              # Service interfaces and implementations
```

## Requirements

- Java 21+
- Maven 3.8+
- MySQL 8 (for production)

## Environment Variables

The following environment variables **must** be set before running the application:

| Variable | Description | Example |
|---|---|---|
| `DB_PASSWORD` | MySQL root password | `<your-mysql-password>` |
| `JWT_SECRET` | JWT signing secret (min 32 bytes) | `<your-256-bit-secret-key>` |

**Never commit real values for these variables.**

Example (Linux/macOS):
```bash
export DB_PASSWORD=<your-mysql-password>
export JWT_SECRET=<your-secure-jwt-secret-at-least-32-chars>
```

Example (Windows CMD):
```cmd
set DB_PASSWORD=<your-mysql-password>
set JWT_SECRET=<your-secure-jwt-secret-at-least-32-chars>
```

## Database Setup

1. Ensure MySQL 8 is running locally on port 3306.
2. The application will auto-create the `appdb` database on first run (`createDatabaseIfNotExist=true`).
3. Set `DB_PASSWORD` to your MySQL root password.

> **Note:** The local MySQL environment may require authentication configuration. If the application fails to connect, verify that the MySQL root user accepts password authentication:
> ```sql
> ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '<your-password>';
> FLUSH PRIVILEGES;
> ```

## How to Run

```bash
# Set environment variables first (see above)
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

## How to Run Tests

Tests use an isolated H2 in-memory database and do not require MySQL:

```bash
./mvnw clean test
```

Expected: **202 tests passing**.

## Swagger / OpenAPI

Once the application is running:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

To test secured endpoints in Swagger:
1. Call `POST /api/auth/login` to obtain a JWT token.
2. Click **Authorize** and enter: `Bearer <your-token>`

## Authentication

### Login

```
POST /api/auth/login
Content-Type: application/json

{
  "username": "<username>",
  "password": "<password>"
}
```

Response:
```json
{
  "token": "<jwt-token>",
  "username": "<username>",
  "role": "ADMIN|DOCTOR|PATIENT"
}
```

Use the token in subsequent requests:
```
Authorization: Bearer <jwt-token>
```

## API Groups

| Group | Base Path | Roles |
|---|---|---|
| Authentication | `/api/auth` | Public |
| Admin | `/api/admin` | ADMIN only |
| Patients | `/patients` | GET: all roles; POST/PUT/DELETE: ADMIN |
| Doctors | `/doctors` | GET: all roles; POST/PUT/DELETE: ADMIN |
| Appointments | `/appointments` | GET: all roles; POST: all roles; PUT: ADMIN/DOCTOR |
| Medical Records | `/medicalrecords` | GET: all roles; POST/PUT: ADMIN/DOCTOR |

## Security Notes

- Passwords are hashed with BCrypt before storage.
- JWT secrets and database passwords are never committed to source control.
- Sessions are stateless (no server-side session state).
- Password fields are never returned in API responses.
