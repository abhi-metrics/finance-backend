# Finance Data Processing & Access Control Backend

A robust Spring Boot backend for a finance dashboard system with role-based access control (RBAC), financial records management, and analytics APIs.

## Tech Stack

| Technology | Purpose |
|------------|---------|
| **Java 17** | Programming language |
| **Spring Boot 3.2.5** | Application framework |
| **Spring Security** | Authentication & authorization |
| **Spring Data JPA** | Data persistence layer |
| **MySQL 8** | Relational database |
| **JWT (jjwt 0.12.5)** | Stateless token-based authentication |
| **Lombok** | Boilerplate reduction |
| **Jakarta Validation** | Input validation |
| **Maven** | Build & dependency management |

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                   Controller Layer                   │
│  AuthController │ UserController │ RecordController  │
│                 │ DashboardController                │
├─────────────────────────────────────────────────────┤
│                    Service Layer                     │
│  AuthService │ UserService │ RecordService           │
│              │ DashboardService                      │
├─────────────────────────────────────────────────────┤
│                  Repository Layer                    │
│       UserRepository │ FinancialRecordRepository     │
├─────────────────────────────────────────────────────┤
│                   MySQL Database                     │
│            users │ financial_records                  │
└─────────────────────────────────────────────────────┘
```

## Project Structure

```
src/main/java/com/finance/
├── FinanceApplication.java          # Main Spring Boot application
├── config/
│   ├── SecurityConfig.java          # Spring Security + JWT configuration
│   └── DataSeeder.java             # Seeds default users & sample data
├── controller/
│   ├── AuthController.java          # POST /api/auth/register, /login
│   ├── UserController.java          # CRUD user management (Admin only)
│   ├── FinancialRecordController.java  # CRUD financial records
│   └── DashboardController.java     # Aggregated analytics endpoints
├── dto/
│   ├── request/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── RecordRequest.java
│   │   └── UpdateUserRequest.java
│   └── response/
│       ├── ApiResponse.java         # Generic response wrapper
│       ├── AuthResponse.java
│       ├── UserResponse.java
│       ├── RecordResponse.java
│       ├── DashboardSummary.java
│       ├── CategorySummary.java
│       └── MonthlyTrend.java
├── exception/
│   ├── GlobalExceptionHandler.java  # Centralized error handling
│   ├── ResourceNotFoundException.java
│   └── BadRequestException.java
├── model/
│   ├── User.java                    # JPA entity (implements UserDetails)
│   ├── FinancialRecord.java         # JPA entity
│   ├── Role.java                    # Enum: VIEWER, ANALYST, ADMIN
│   ├── UserStatus.java             # Enum: ACTIVE, INACTIVE
│   └── RecordType.java             # Enum: INCOME, EXPENSE
├── repository/
│   ├── UserRepository.java
│   └── FinancialRecordRepository.java
├── security/
│   ├── JwtUtil.java                 # JWT token generation & validation
│   ├── JwtAuthFilter.java          # JWT authentication filter
│   └── CustomUserDetailsService.java
└── service/
    ├── AuthService.java
    ├── UserService.java
    ├── FinancialRecordService.java
    └── DashboardService.java
```



### 1. Clone the repository

```bash
git clone <repository-url>
cd finance-backend
```

### 2. Create MySQL database

```sql
CREATE DATABASE finance_db;
```

> **Note:** The application will auto-create the database if `createDatabaseIfNotExist=true` is set (default). You just need MySQL running.

### 3. Configure database credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/finance_db
spring.datasource.username=root
spring.datasource.password=root
```

### 4. Build and run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The server starts at **http://localhost:8080**

### 5. Default seeded users

On first startup, the following users are automatically created:

| Username | Password | Role | Permissions |
|----------|----------|------|-------------|
| `admin` | `admin123` | ADMIN | Full access — CRUD users, records, dashboard |
| `analyst` | `analyst123` | ANALYST | Read records, access dashboard |
| `viewer` | `viewer123` | VIEWER | View dashboard only |

---

## API Reference

### Authentication (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and get JWT token |

#### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "admin",
    "role": "ADMIN",
    "message": "Login successful"
  }
}
```

> Use the `token` value in the `Authorization: Bearer <token>` header for all subsequent requests.

---

### User Management (Admin Only)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | List all users |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user (email, role, status, password) |
| DELETE | `/api/users/{id}` | Delete user |

#### Update User Role
```bash
curl -X PUT http://localhost:8080/api/users/2 \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "role": "ANALYST"
  }'
```

---

### Financial Records

| Method | Endpoint | Roles | Description |
|--------|----------|-------|-------------|
| POST | `/api/records` | ADMIN | Create a record |
| GET | `/api/records` | ADMIN, ANALYST | List records (with filters) |
| GET | `/api/records/{id}` | ADMIN, ANALYST | Get record by ID |
| PUT | `/api/records/{id}` | ADMIN | Update a record |
| DELETE | `/api/records/{id}` | ADMIN | Delete a record |

#### Create Record
```bash
curl -X POST http://localhost:8080/api/records \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50000,
    "type": "INCOME",
    "category": "Salary",
    "date": "2026-04-01",
    "description": "April salary"
  }'
```

#### Filter Records
```bash
# By type
curl "http://localhost:8080/api/records?type=INCOME" -H "Authorization: Bearer <TOKEN>"

# By category
curl "http://localhost:8080/api/records?category=Salary" -H "Authorization: Bearer <TOKEN>"

# By date range
curl "http://localhost:8080/api/records?startDate=2026-01-01&endDate=2026-12-31" -H "Authorization: Bearer <TOKEN>"

# Combined filters with pagination
curl "http://localhost:8080/api/records?type=EXPENSE&category=Rent&page=0&size=5" -H "Authorization: Bearer <TOKEN>"
```

---

### Dashboard Analytics (All Authenticated Users)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard/summary` | Total income, expenses, net balance, record count |
| GET | `/api/dashboard/category-summary` | Category-wise totals (optional `?type=INCOME`) |
| GET | `/api/dashboard/monthly-trends` | Monthly income/expense trends |
| GET | `/api/dashboard/recent-activity` | Last 10 financial records |

#### Get Summary
```bash
curl http://localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer <TOKEN>"
```

**Response:**
```json
{
  "success": true,
  "message": "Dashboard summary retrieved",
  "data": {
    "totalIncome": 140000.00,
    "totalExpenses": 48000.00,
    "netBalance": 92000.00,
    "totalRecords": 8
  }
}
```

#### Get Monthly Trends
```bash
curl http://localhost:8080/api/dashboard/monthly-trends \
  -H "Authorization: Bearer <TOKEN>"
```

**Response:**
```json
{
  "success": true,
  "message": "Monthly trends retrieved",
  "data": [
    { "year": 2026, "month": 4, "income": 90000.00, "expense": 36000.00, "net": 54000.00 },
    { "year": 2026, "month": 3, "income": 50000.00, "expense": 12000.00, "net": 38000.00 }
  ]
}
```

---

## Role-Based Access Control (RBAC)

| Action | VIEWER | ANALYST | ADMIN |
|--------|:------:|:-------:|:-----:|
| View dashboard summary | ✅ | ✅ | ✅ |
| View category summary | ✅ | ✅ | ✅ |
| View monthly trends | ✅ | ✅ | ✅ |
| View recent activity | ✅ | ✅ | ✅ |
| View financial records | ❌ | ✅ | ✅ |
| Create financial records | ❌ | ❌ | ✅ |
| Update financial records | ❌ | ❌ | ✅ |
| Delete financial records | ❌ | ❌ | ✅ |
| Manage users | ❌ | ❌ | ✅ |

Access control is enforced via:
- **`@PreAuthorize`** annotations at the controller method level
- **JWT-based authentication** (stateless, no sessions)
- **Spring Security filter chain** with custom `JwtAuthFilter`

---

## Error Handling

All error responses follow a consistent format:

```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Please provide a valid email address",
    "password": "Password must be between 6 and 100 characters"
  }
}
```

| Scenario | HTTP Status | Example |
|----------|-------------|---------|
| Validation error | 400 | Missing required field |
| Duplicate username/email | 400 | Already registered |
| Invalid credentials | 401 | Wrong password |
| Insufficient role | 403 | Viewer creating records |
| Resource not found | 404 | Invalid user/record ID |
| Server error | 500 | Unexpected exception |

---

## Data Model

### Users Table

| Column | Type | Constraints |
|--------|------|------------|
| id | BIGINT | PK, Auto-increment |
| username | VARCHAR(50) | Unique, Not null |
| email | VARCHAR(100) | Unique, Not null |
| password | VARCHAR(255) | Not null (BCrypt hashed) |
| role | ENUM | VIEWER, ANALYST, ADMIN |
| status | ENUM | ACTIVE, INACTIVE |
| created_at | DATETIME | Auto-set |
| updated_at | DATETIME | Auto-updated |

### Financial Records Table

| Column | Type | Constraints |
|--------|------|------------|
| id | BIGINT | PK, Auto-increment |
| amount | DECIMAL(15,2) | Not null, Positive |
| type | ENUM | INCOME, EXPENSE |
| category | VARCHAR(100) | Not null |
| date | DATE | Not null |
| description | VARCHAR(500) | Optional |
| created_by | BIGINT | FK → users.id |
| created_at | DATETIME | Auto-set |
| updated_at | DATETIME | Auto-updated |

---

## Assumptions & Design Decisions

1. **Default Role**: New users are assigned the `VIEWER` role upon registration. Only an Admin can promote users.
2. **Seed Data**: The app seeds 3 default users (admin, analyst, viewer) and 8 sample financial records on first run.
3. **JWT Expiration**: Tokens expire after 24 hours (configurable in `application.properties`).
4. **Password Hashing**: BCrypt is used for secure password storage.
5. **Pagination**: Financial records support pagination (default page size: 10).
6. **Stateless Auth**: No server-side sessions — all state is carried in JWT tokens.
7. **Inactive Users**: Users with `INACTIVE` status cannot authenticate (locked account).
8. **Schema Management**: JPA `ddl-auto=update` manages schema automatically (suitable for development).

---





