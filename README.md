# Payment-management-system

A Java-based payment management system for tracking, processing, and managing payments. Built using Maven and SQL for database operations.
A comprehensive Java-based payment management system designed for businesses and organizations to efficiently track, process, and manage payments. 
The system supports CRUD operations for payment records, advanced search and analytics, secure user authentication, multi-currency transactions, and integration with external payment gateways. 
Built using Maven for project management and SQL for robust database operations, it ensures reliability, scalability, and ease of maintenance.

### Clone the Repository
```bash
git clone https://github.com/Pratham27-12/Payment-management-system.git
```   

2. Create a new database:
```bash
psql -U postgres
CREATE DATABASE pms;
\q
```

### Update Configuration
Edit the `src/main/resources/application.properties` file with your PostgreSQL credentials:
```properties
db.driver=org.postgresql.Driver
db.url=jdbc:postgresql://localhost:5432/paymentsapp
db.username=postgres
db.password=password
```


## System Architecture
The application uses a layered architecture, with a clean separation of concerns:

* **UI Layer**: Console-based interface for user interaction (`ConsoleMenuSystem`)
* **Service Layer**: Business logic for users, payments, accounts, and reporting
* **DAO Layer**: Handles database interaction via JDBC
* **Model Layer**: POJOs representing the application's entities (`User`, `Payment`, `Account`, etc.)
* **Utility Layer**: Helper components like `DatabaseConnector`, `FileExporter`, and others


### Role-Based Access Control (RBAC)
Three predefined user roles provide scoped access:

* **Admin**
   * Full access to all system functionalities including user management

* **Finance Manager**
   * Access to account, payment processing, and reporting

* **Viewer**
   * Read-only access to view all payments and accounts

### Payment Processing Workflow
1. A new payment is created with a PENDING status
2. A Finance Manager processes the payment

## Technologies Used

- Java
- SQL (e.g., MySQL, PostgreSQL)
- Maven

## Prerequisites

- Java 17 or above
- Maven 3.8+
- SQL database (configured in `src/main/resources/application.properties`)

## Setup

1. Clone the repository:
   ```sh
   git clone https://github.com/Pratham27-12/payment-management-system.git
   cd payment-management-system
2. run sql commands mentioned inside `src/main/resources/config.db_migrations` folder
3. run ApplicationMain class