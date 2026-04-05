# Finance Dashboard Backend

A RESTful backend for a Finance Dashboard System built with **Spring Boot 3**, **Java 17**, **MySQL**, and **JWT authentication**. Features role-based access control, financial record management, and analytics APIs.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 3.2.5 | Application framework |
| Spring Security + JWT | Authentication & authorization |
| Spring Data JPA | Data persistence |
| MySQL 8 | Relational database |
| Maven | Build tool |

---

## Architecture

```
Controller Layer  →  Service Layer  →  Repository Layer  →  MySQL Database
     ↕                    ↕
  DTOs (Request/Response)
     ↕
  GlobalExceptionHandler + JwtAuthFilter
```

---

## Project Structure

```
src/main/java/com/finance/
├── FinanceApplication.java
├── config/
│   ├── SecurityConfig.java         # Spring Security + JWT filter chain
│   └── DataSeeder.java             # Seeds default users & sample records
├── controller/
│   ├── AuthController.java         # Register, Login
│   ├── UserController.java         # User CRUD (Admin only)
│   ├── FinancialRecordController.java
│   └── DashboardController.java    # Analytics endpoints
├── dto/
│   ├── request/                    # RegisterRequest, LoginRequest, RecordRequest
│   └── response/                   # ApiResponse, AuthResponse, DashboardSummary...
├── exception/
│   └── GlobalExceptionHandler.java
├── model/
│   ├── User.java
│   ├── FinancialRecord.java
│   ├── Role.java                   # VIEWER, ANALYST, ADMIN
│   └── RecordType.java             # INCOME, EXPENSE
├── repository/
│   ├── UserRepository.java
│   └── FinancialRecordRepository.java
├── security/
│   ├── JwtUtil.java
│   ├── JwtAuthFilter.java
│   └── CustomUserDetailsService.java
└── service/
    ├── AuthService.java
    ├── UserService.java
    ├── FinancialRecordService.java
    └── DashboardService.java
```

---

## Setup & Running

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8 running locally

### Steps

```bash
# 1. Clone the repo
git clone <repository-url>
cd finance-backend

# 2. Configure your MySQL credentials in:
# src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/finance_db
spring.datasource.username=root
spring.datasource.password=root

# 3. Build and run
mvn clean install
mvn spring-boot:run
```

Server starts at: **http://localhost:8080**

### Default Seeded Users

| Username | Password | Role |
|---|---|---|
| admin | admin123 | ADMIN |
| analyst | analyst123 | ANALYST |
| viewer | viewer123 | VIEWER |

---

## API Reference

### Authentication — Public

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive JWT token |

**Login request:**
```json
{ "username": "admin", "password": "admin123" }
```

**Login response:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGci...",
    "username": "admin",
    "role": "ADMIN"
  }
}
```

Use the token as `Authorization: Bearer <token>` on all subsequent requests.

---

### User Management — ADMIN only

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users` | List all users |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user role/status |
| DELETE | `/api/users/{id}` | Delete user |

---

### Financial Records

| Method | Endpoint | Roles | Description |
|---|---|---|---|
| POST | `/api/records` | ADMIN | Create a record |
| GET | `/api/records` | ADMIN, ANALYST | List records (filterable) |
| GET | `/api/records/{id}` | ADMIN, ANALYST | Get record by ID |
| PUT | `/api/records/{id}` | ADMIN | Update a record |
| DELETE | `/api/records/{id}` | ADMIN | Delete a record |

**Create record body:**
```json
{
  "amount": 50000,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-01",
  "description": "April salary"
}
```

**Filter parameters:**

| Param | Example |
|---|---|
| `type` | `INCOME` or `EXPENSE` |
| `category` | `Salary` |
| `startDate` | `2026-01-01` |
| `endDate` | `2026-12-31` |
| `page` | `0` |
| `size` | `10` |

---

### Dashboard Analytics — All authenticated users

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/dashboard/summary` | Total income, expenses, net balance |
| GET | `/api/dashboard/category-summary` | Category-wise totals |
| GET | `/api/dashboard/monthly-trends` | Monthly income vs expense |
| GET | `/api/dashboard/recent-activity` | Last 10 records |

---

## Role-Based Access Control

| Action | VIEWER | ANALYST | ADMIN |
|---|:---:|:---:|:---:|
| View dashboard & analytics | ✅ | ✅ | ✅ |
| View financial records | ❌ | ✅ | ✅ |
| Create financial records | ❌ | ❌ | ✅ |
| Update financial records | ❌ | ❌ | ✅ |
| Delete financial records | ❌ | ❌ | ✅ |
| Manage users | ❌ | ❌ | ✅ |

Enforced via `@PreAuthorize` annotations + Spring Security JWT filter chain.

---

## Error Handling

All errors return a consistent format:

```json
{
  "success": false,
  "message": "Validation failed",
  "data": { "field": "error description" }
}
```

| Scenario | Status |
|---|---|
| Validation error | 400 |
| Invalid credentials | 401 |
| Insufficient role | 403 |
| Resource not found | 404 |

---

## Assumptions & Design Decisions

| Decision | Reasoning |
|---|---|
| Default role is VIEWER on register | Only admins should promote users |
| JWT stateless auth | No server-side sessions needed |
| BCrypt password hashing | Industry standard for secure storage |
| `ddl-auto=update` | Convenient for development/evaluation |
| Inactive users blocked at login | Account deactivation without deletion |
| Pagination on records | Prevents unbounded result sets |
| Seed data on startup | Allows immediate testing without manual setup |
