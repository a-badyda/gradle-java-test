# Case Management API
Spring Boot 3.x RESTful Service for Legal Case Tracking - code exercise backend version

---

## 🛠 Features
* **Case Lifecycle Management**: CRUD operations for cases using ULID identifiers.
* **Security (OWASP A03)**: Built-in protection against SQL Injection and XSS via centralized validation.
* **Error Handling**:
  * Structured `404 Not Found` responses including machine-readable error codes and searched fields for JS consumers.
  * Detailed `400 Bad Request` responses mapping specific field errors.
* **Performance Caching**: Optimized read operations with Spring Cache for individual cases and paginated lists.
---

## Getting Started

### Prerequisites
* **Java 21 JDK** (Project toolchain version)
* **Gradle 8.x** (Using included wrapper)
* **PostgreSQL** or **H2** (for testing)

### Configuration
Update your `.env` file with your database credentials:
```properties
DB_PORT;
DB_USER_NAME;
DB_NAME;
DB_HOST;
DB_PASSWORD;
