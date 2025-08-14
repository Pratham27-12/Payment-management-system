
# Payment Management System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue.svg)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)

A robust, enterprise-grade payment management system built with Spring Boot, featuring comprehensive user management, payment processing, and audit trail capabilities with JWT-based security.

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Security](#security)
- [Database](#database)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)

## 🎯 Overview

The Payment Management System is a secure, scalable Spring Boot application designed for financial institutions to manage payment transactions, user accounts, and maintain comprehensive audit trails. The system implements enterprise-level security with role-based access control and follows clean architecture principles.

### Key Capabilities
- **Payment Processing**: Complete payment lifecycle management
- **User Management**: Secure user registration, authentication, and role management
- **Audit Trail**: Comprehensive logging and tracking of all system operations
- **Security**: JWT-based authentication with role-based authorization
- **Scalability**: Designed for high-volume transaction processing

## 🏗️ Architecture

The application follows a layered architecture pattern ensuring separation of concerns and maintainability:

```
┌─────────────────────────────────────────────────────────────┐
│                     API Layer                               │
│  Controllers (REST Endpoints) + OpenAPI Contracts           │
├─────────────────────────────────────────────────────────────┤
│                   Service Layer                             │
│  Business Logic + Transaction Management                    │
├─────────────────────────────────────────────────────────────┤
│                  Repository Layer                           │
│  Data Access Objects + JPA Repositories                     │
├─────────────────────────────────────────────────────────────┤
│                   Entity Layer                              │
│  JPA Entities + Database Mappings                           │
├─────────────────────────────────────────────────────────────┤
│                  Database Layer                             │
│  PostgreSQL + Flyway Migrations + Triggers                  │
└─────────────────────────────────────────────────────────────┘
```

## ✨ Features

### 🔐 Authentication & Authorization
- **JWT Token-based Authentication**
- **Role-based Access Control** (ADMIN, FINANCE_MANAGER, VIEWER)
- **Secure Password Management** with BCrypt hashing
- **Session Management** with configurable token expiration

### 💰 Payment Management
- **Payment Creation & Processing**
- **Payment Status Tracking**
- **Payment History & Reporting**
- **Role-based Payment Operations**
- **Payment Validation & Error Handling**

### 👥 User Management
- **User Registration & Profile Management**
- **Password Change & Recovery**
- **Role Assignment & Management** (Admin only)
- **User Activity Monitoring**

### 📊 Audit & Compliance
- **Comprehensive Audit Trail**
- **Automatic Operation Logging**
- **Audit Report Generation**
- **Compliance Tracking**

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| **Framework** | Spring Boot | 3.x |
| **Language** | Java | 17+ |
| **Security** | Spring Security + JWT | 6.x |
| **Database** | PostgreSQL | 12+ |
| **Migration** | Flyway | Latest |
| **Build Tool** | Maven | 3.6+ |
| **Documentation** | OpenAPI 3.0 | Latest |
| **Testing** | JUnit 5 + Mockito | Latest |

## 📁 Project Structure

```
MiniProject2/
├── 📄 API Contracts
│   ├── audit-api-contract.yaml           # Audit API specification
│   ├── payments-api-contract.yaml        # Payment API specification
│   └── user-management-api-contract.yaml # User API specification
├── 🔧 Configuration
│   └── pom.xml                          # Maven dependencies & plugins
├── 📋 Documentation
│   ├── CompleteClassDiagram.puml        # System architecture diagram
│   └── PaymentSystemClassDiagram.png    # Visual class diagram
└── 🗂️ Source Code
└── src/
├── main/java/zeta/payments/
│   ├── 🚀 ApplicationMain.java          # Application entry point
│   ├── 📦 commons/                      # Shared utilities
│   │   ├── enums/                       # Application enums
│   │   └── route/                       # API route constants
│   ├── ⚙️ config/                       # Configuration classes
│   │   ├── DatabaseConfig.java          # Database configuration
│   │   ├── JwtAuthenticationFilter.java # JWT security filter
│   │   └── SecurityConfig.java          # Security configuration
│   ├── 🌐 controller/                   # REST API controllers
│   │   ├── AuditController.java         # Audit endpoints
│   │   ├── AuthController.java          # Authentication endpoints
│   │   ├── PaymentController.java       # Payment endpoints
│   │   └── UserController.java          # User management endpoints
│   ├── 📝 dto/                          # Data Transfer Objects
│   │   ├── request/                     # Request DTOs
│   │   └── response/                    # Response DTOs
│   ├── 🗃️ entity/                       # JPA entities
│   │   ├── Audit.java                   # Audit trail entity
│   │   ├── Payment.java                 # Payment entity
│   │   └── User.java                    # User entity
│   ├── ⚠️ exception/                    # Exception handling
│   │   ├── ErrorResponse.java           # Error response model
│   │   ├── PaymentExceptionHandler.java # Global exception handler
│   │   └── PaymentManagementException.java # Custom exceptions
│   ├── 🗄️ repository/                   # Data access layer
│   │   ├── AuditTrialRepository.java    # Audit data access
│   │   ├── PaymentRepository.java       # Payment data access
│   │   └── UserRepository.java          # User data access
│   ├── 🔧 service/                      # Business logic layer
│   │   ├── AuditTrialManagementService.java # Audit service
│   │   ├── PaymentManagementService.java    # Payment service
│   │   └── UserManagementService.java       # User service
│   └── 🛠️ util/                         # Utility classes
│       └── DateUtil.java               # Date utility functions
├── main/resources/
│   ├── application.properties           # Application configuration
│   └── config.db_migrations/            # Database migrations
│       ├── V1.0.0_added_payment_details_table.sql
│       ├── V1.0.1_added_audit_trail_table.sql
│       ├── V1.0.2_added_user_details_table.sql
│       ├── V1.0.3_added_trigger_for_audit_tbale.sql
│       └── V1.0.4_added_created_updated_at_trigger.sql
└── test/java/zeta/payments/            # Test suites
├── controller/                      # Controller tests
├── exception/                       # Exception tests
├── service/                         # Service tests
└── util/                           # Utility tests
```

## 🚀 Getting Started

### Prerequisites

Ensure you have the following installed:

- ☕ **Java 17+** - [Download OpenJDK](https://openjdk.java.net/install/)
- 📦 **Maven 3.6+** - [Installation Guide](https://maven.apache.org/install.html)
- 🐘 **PostgreSQL 12+** - [Download PostgreSQL](https://www.postgresql.org/download/)

### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Pratham27-12/Payment-management-system.git
   cd Payment-management-system/MiniProject2
   ```

2. **Database Setup**
   ```sql
   -- Connect to PostgreSQL and create database
   CREATE DATABASE payment_management;
   CREATE USER payment_user WITH PASSWORD 'secure_password';
   GRANT ALL PRIVILEGES ON DATABASE payment_management TO payment_user;
   ```

3. **Configure Application Properties**
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/payment_management
   spring.datasource.username=payment_user
   spring.datasource.password=secure_password
   
   # JWT Configuration
   jwt.secret=your-256-bit-secret-key
   jwt.expiration=86400000
   
   # Server Configuration
   server.port=8080
   ```

4. **Build and Run**
   ```bash
   # Clean and compile
   mvn clean compile
   
   # Run tests
   mvn test
   
   # Package application
   mvn clean package
   
   # Run the application
   mvn spring-boot:run
   
   # Or run the JAR directly
   java -jar target/payment-management-system-1.0.jar
   ```

5. **Verify Installation**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

## 📖 API Documentation

### OpenAPI Specifications

The system provides comprehensive API documentation through OpenAPI 3.0 specifications:

| Service | Contract File | Description |
|---------|---------------|-------------|
| **User Management** | `user-management-api-contract.yaml` | User registration, authentication, profile management |
| **Payment Processing** | `payments-api-contract.yaml` | Payment creation, processing, status tracking |
| **Audit Trail** | `audit-api-contract.yaml` | Audit log retrieval, compliance reporting |

### Core API Endpoints

#### 🔐 Authentication
```http
POST /api/v1/auth/login          # User authentication
POST /api/v1/auth/register       # User registration
POST /api/v1/auth/refresh        # Token refresh
```

#### 👥 User Management
```http
GET    /api/v1/users                    # List all users (Admin)
POST   /api/v1/users                    # Create user
PUT    /api/v1/users/{userName}/password # Update password
PUT    /api/v1/users/role               # Update user role (Admin)
DELETE /api/v1/users/{userName}          # Delete user (Admin)
```

#### 💰 Payment Operations
```http
GET    /api/v1/payments              # List payments
POST   /api/v1/payments              # Create payment
GET    /api/v1/payments/{id}         # Get payment details
PUT    /api/v1/payments/{id}         # Update payment
DELETE /api/v1/payments/{id}         # Delete payment
```

#### 📊 Audit Trail
```http
GET /api/v1/audit                    # Get audit logs
GET /api/v1/audit/user/{userId}      # User-specific audit logs
GET /api/v1/audit/date-range         # Audit logs by date range
```

### Testing APIs

Use the provided OpenAPI contracts with tools like:
- **Swagger UI**: Import contracts for interactive testing
- **Postman**: Generate collections from OpenAPI specs
- **Insomnia**: Direct import support for API testing

## 🔒 Security

### Authentication Flow
1. User submits credentials to `/auth/login`
2. System validates credentials against database
3. JWT token generated with user roles and permissions
4. Client includes token in `Authorization: Bearer <token>` header
5. System validates token on each protected endpoint

### Role-Based Access Control

| Role | Permissions |
|------|-------------|
| **ADMIN** | Full system access, user management, all payment operations |
| **FINANCE_MANAGER** | Payment creation/modification, user profile updates |
| **VIEWER** | Read-only access to payments and user profiles |

### Security Features
- 🔐 **JWT Token Authentication** with configurable expiration
- 🔒 **Password Encryption** using BCrypt with salt rounds
- 🛡️ **CORS Protection** with configurable origins
- 🚫 **SQL Injection Prevention** through JPA/Hibernate
- 🔍 **Input Validation** with Bean Validation annotations
- 📝 **Audit Logging** for all security-relevant operations

## 🗄️ Database

### Schema Design

The system uses PostgreSQL with the following core tables:

```sql
-- Users table with role-based access
users (id, username, email, password_hash, role, created_at, updated_at)

-- Payments table with status tracking
payments (id, user_id, amount, currency, status, description, created_at, updated_at)

-- Comprehensive audit trail
audit_trail (id, table_name, operation, old_values, new_values, user_id, timestamp)
```

### Migration Management

Database schema is managed through **Flyway** migrations:

| Version | Description |
|---------|-------------|
| `V1.0.0` | Payment details table creation |
| `V1.0.1` | Audit trail table setup |
| `V1.0.2` | User details table with roles |
| `V1.0.3` | Audit triggers for automatic logging |
| `V1.0.4` | Timestamp triggers for created/updated fields |

### Database Features
- 🔄 **Automatic Triggers** for audit trail generation
- 📅 **Timestamp Management** with created/updated tracking
- 🔗 **Foreign Key Constraints** ensuring data integrity
- 📊 **Indexes** optimized for query performance

## 🧪 Testing

### Test Structure
```bash
src/test/java/zeta/payments/
├── controller/          # Integration tests for REST endpoints
├── service/            # Unit tests for business logic
├── repository/         # Data access layer tests
└── util/              # Utility function tests
```

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=PaymentControllerTest

# Run tests with coverage
mvn test jacoco:report

# Integration tests only
mvn test -Dtest=**/*IntegrationTest

# Generate test reports
mvn surefire-report:report
```

### Test Coverage Goals
- **Unit Tests**: 80%+ coverage for service layer
- **Integration Tests**: All REST endpoints covered
- **Security Tests**: Authentication and authorization flows
- **Database Tests**: Repository layer and migrations

## 🚀 Deployment

### Production Configuration

Create `application-prod.properties`:
```properties
# Production Database
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Security
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:3600000}

# Logging
logging.level.zeta.payments=INFO
logging.file.name=logs/payment-system.log

# Performance
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
```

### Docker Deployment
```dockerfile
FROM openjdk:17-jre-slim

# Add application user
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Copy application
COPY target/payment-management-system-*.jar app.jar

# Set user
USER spring:spring

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080
```

### Environment Variables
```bash
# Required Environment Variables
export DB_URL="jdbc:postgresql://db-host:5432/payment_management"
export DB_USERNAME="payment_user"
export DB_PASSWORD="secure_production_password"
export JWT_SECRET="256-bit-base64-encoded-secret-key"

# Optional Configuration
export JWT_EXPIRATION="3600000"
export SERVER_PORT="8080"
export SPRING_PROFILES_ACTIVE="prod"
```

### Monitoring & Health Checks
- **Spring Boot Actuator** endpoints for health monitoring
- **Application metrics** via Micrometer
- **Custom health indicators** for database connectivity
- **Audit trail monitoring** for compliance tracking

## 🤝 Contributing

We welcome contributions! Please follow these guidelines:

### Development Workflow
1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Follow** coding standards and add tests
4. **Commit** changes (`git commit -m 'Add amazing feature'`)
5. **Push** to branch (`git push origin feature/amazing-feature`)
6. **Create** a Pull Request

### Code Standards
- ✅ Follow Java coding conventions
- ✅ Add comprehensive unit tests
- ✅ Update API documentation
- ✅ Include proper error handling
- ✅ Follow security best practices

### Pull Request Checklist
- [ ] Tests pass (`mvn test`)
- [ ] Code coverage maintained
- [ ] Documentation updated
- [ ] API contracts updated (if applicable)
- [ ] Security review completed

## 📞 Support & Contact

- **Issues**: [GitHub Issues](https://github.com/Pratham27-12/Payment-management-system/issues)
- **Developer**: Pratham Golwala
- **Organization**: Zeta Payments
- **Email**: [Contact Email]

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**Built with ❤️ using Spring Boot**

[![Java](https://img.shields.io/badge/Made%20with-Java-orange)](https://java.com)
[![Spring Boot](https://img.shields.io/badge/Powered%20by-Spring%20Boot-brightgreen)](https://spring.io/projects/spring-boot)

</div>