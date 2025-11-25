# Sectors Management Application

This project implements a complete solution for managing industrial sectors and user-specific sector selections.
It contains:

- A **Node.js** parser to convert a html file of sectors into SQL insert statements.
- A **Spring Boot** backend
- An **Angular** frontend
- **PostgreSQL** as the primary database
- **Flyway** for database migrations
- **Docker Compose** for deployment
- Two UI areas:
  - **Admin Panel** (sector CRUD)
  - **User Panel** (sector selection and restore)

## 1. Features

### 1.1 Sector Management (Admin Panel)
- View all sectors in a hierarchical structure  
- Create new sectors  
- Edit existing sectors  
- Assign a parent sector  
- Delete sectors  

### 1.2 User Sector Selection (User Panel)
- Enter username  
- Select multiple sectors  
- Save sector selection  
- Restore previously saved selection  

## 2. Technology Stack

| Layer      | Technology                  |
|------------|-----------------------------|
| Backend    | Java 21, Spring Boot 3.3.x  |
| ORM        | Hibernate / Spring Data JPA |
| Database   | PostgreSQL 13               |
| Migrations | Flyway                      |
| Frontend   | Angular 17                  |
| Packaging  | Docker, Docker Compose      |

## 3. Running with Docker

```
docker compose up --build
```

Backend: http://localhost:8085  
Frontend: http://localhost:4200  
Database: localhost:5434 (locally )

## 4. Local Development

Run backend:

```
cd sectors-service
./gradlew bootRun
```

Run frontend:

```
cd sectors-ui
npm install
ng serve
```

## 5. Migrations

Flyway applies:
- V1 — Schema + 79 initial sectors  
- V2 — Sequence synchronization  

## 6. UI Overview
### 6.1 User Panel
- Single-column centered card
- Multi-select with visual indentation for hierarchical sectors
- Save/Restore Buttons

### 6.2 Admin Panel
- Two-column layout
- Left pane: sector list
- Right pane: create/edit form

## 7. Testing
- controller-level tests with MockMvc
- service-level tests with Mockito
- repository-level tests with Testcontainers
- validation tests
To run tests:
```
./gradlew test
``` 

## 8. Summary
This application demonstrates:
-Full-stack implementation (Angular + Spring Boot)
-Clean REST architecture
-Hierarchical data management
-User selection persistence
-Complete CI-ready dockerized setup
-Proper schema migration strategy
-Structured, maintainable codebase

The project is ready for review, extension, or deployment.
