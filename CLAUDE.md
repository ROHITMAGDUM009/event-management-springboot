# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Event Management System (EMS)** — Spring Boot 3.2.5 backend with JWT-based authentication, role-based access control (ROLE_USER, ROLE_ORGANIZER, ROLE_ADMIN), MySQL database, and full booking/payment flow.

## Quick Start Commands

```bash
# Run with MySQL
./mvnw spring-boot:run

# Build (skip tests)
./mvnw clean package -DskipTests

# Build with tests
./mvnw clean package

# Run single test
./mvnw test -Dtest=EmsApplicationTests

# Run all tests
./mvnw test
```

## Prerequisites

- **Java 21**
- **MySQL** running on `localhost:3306` (user: `root`, password: `root`)
- Database `event_db` is auto-created via `createDatabaseIfNotExist=true`

## Architecture

### Package Structure

```
src/main/java/com/event/ems/
├── config/             # Application config (DataInitializer, AdminInitializer, MethodSecurityConfig)
├── controller/         # REST endpoints (Auth, Event, Booking, Payment, Admin, User, OrganizerBooking, Test, AdminUser)
├── dto/                # Request/Response DTOs (Register, Login, Event, Booking, Dashboard, User, Revenue, Organizer)
├── entity/             # JPA entities (User, Role, Event, Booking, EventPackage) + enums
├── repository/         # Spring Data JPA repositories
├── security/           # JWT auth (JwtUtil, JwtAuthenticationFilter, SecurityConfig)
├── service/            # Service interfaces
└── service/impl/       # Service implementations
```

### Database Entities & Relationships

- **User** ↔ **Role** (ManyToMany via `user_roles`). Roles: USER, ORGANIZER, ADMIN, initialized at startup.
- **Event** ← Booking (ManyToOne). Created by organizer (email stored in `createdBy`). Has status: PENDING/APPROVED/REJECTED.
- **Event** ← **EventPackage** (OneToMany). Tiered packages (Silver, Gold, VIP).
- **Booking** contains `bookingStatus` (enum: PENDING/APPROVED/REJECTED) and `paymentStatus` (enum: PENDING/SUCCESS).
- Enums: `RoleName`, `EventStatus`, `EventType` (FREE/PAID/PACKAGE), `BookingApprovalType` (AUTO/MANUAL), `BookingStatus`, `PaymentStatus`.

### Security Model

- **JWT tokens** generated on login (24h expiry), validated via `JwtAuthenticationFilter` (runs before `UsernamePasswordAuthenticationFilter`).
- Stateless session, CSRF and CORS disabled.
- Route-level guards in `SecurityConfig`:
  - `/api/auth/**` — public
  - `/api/admin/**` — ROLE_ADMIN
  - `/api/organizer/**` — ROLE_ORGANIZER
  - `/api/user/**` — ROLE_USER
- Method-level guards via `@PreAuthorize` on controller endpoints (e.g., AdminController, OrganizerBookingController).
- Auto-provisioning: `AdminInitializer` seeds admin user if none exists; `DataInitializer` seeds roles.

### API Endpoints Summary

| Controller | Path Prefix | Auth | Purpose |
|---|---|---|---|
| AuthController | `/api/auth` | public | register, login |
| EventController | `/api/events` | varied | CRUD events, my-events, approved-events |
| BookingController | `/api/bookings` | user | create booking, my bookings |
| OrganizerBookingController | `/api/organizer/bookings` | organizer | view event bookings, approve booking |
| AdminController | `/api/admin` | admin | dashboard stats, all events, approve/reject events |
| PaymentController | `/api/payments` | none | mark booking as paid (simulated) |
| UserController | `/api/users` | user | profile, view approved events |
| AdminUserController | `/api/admin/users` | admin | manage users |

### Service Layer

- `UserService` / `UserServiceImpl` — auth, user profile
- `EventService` / `EventServiceImpl` — event CRUD with organizer scoping
- `BookingService` / `BookingServiceImpl` — booking creation, my bookings
- `AdminService` / `AdminServiceImpl` — dashboard, event approval
- `AdminUserService` / `AdminUserServiceImpl` — user management by admin

### Notes

- `CrossOrigin("http://localhost:5173")` on `AuthController` only — frontend assumed to be Vite/React on port 5173.
- `PaymentController.pay` is a simulated payment (just flips PaymentStatus). No real payment gateway.
- `JwtUtil` uses a hardcoded base64 secret key — suitable for dev only.
- No global exception handler exists; controller errors throw `RuntimeException` (will produce default 500 responses).
- Tests are minimal — only `contextLoads` smoke test.
