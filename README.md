# Library Management API (Java Edition)

A RESTful API built with **Java 17** and **Spring Boot** to manage a library system. This solution implements a Clean Architecture pattern, utilizes **Docker** for containerized deployment, and is designed to adhere to **12-Factor App** principles.

## 🚀 Live Demo
* **Base URL:** `https://libraryassignment-java.onrender.com/`
* **Swagger Documentation:** `https://libraryassignment-java.onrender.com/swagger-ui.html`
* *Note: The application is hosted on a free tier instance. Please allow up to 1 minute for the initial request to wake up the server.*

---

## 🛡️ 12-Factor App Conformance
This application was built following the [12-Factor App methodology](https://12factor.net/) to ensure portability and resilience.

| Factor | Principle | Implementation in this Project |
| :--- | :--- | :--- |
| **I. Codebase** | One codebase, many deploys. | A single Git repository tracks the application. The same code is deployed to all environments (dev, test, production). |
| **II. Dependencies** | Explicitly declare dependencies. | All dependencies are explicitly declared in `pom.xml` (Maven). No implicit reliance on system-wide libraries. |
| **III. Config** | Store config in the environment. | Configuration is separated from code. Default values are in `application.properties`, but can be overridden via Environment Variables (e.g., `SERVER_PORT`, `DB_URL`) at runtime. |
| **IV. Backing Services** | Treat backing services as attached resources. | The database access is abstracted via **Spring Data JPA**. Switching from H2 (In-Memory) to MySQL or PostgreSQL only requires changing the connection string config, not the code. |
| **V. Build, Release, Run** | Strictly separate stages. | The deployment pipeline is separated: **Build** (Maven creates the JAR), **Release** (Docker images are tagged), and **Run** (Container execution). |
| **VI. Processes** | Execute the app as stateless processes. | The core application is stateless. Any persistence is delegated to the database (Backing Service), allowing horizontal scaling of the API service. |
| **XI. Logs** | Treat logs as event streams. | Logging is handled via **SLF4J**. Logs are written to `stdout` (console), allowing the execution environment (Docker/Cloud) to capture, aggregate, and archive them without app-level file management. |

---

## 🛠 Tech Stack
* **Language:** Java 17
* **Framework:** Spring Boot 3.x
* **Database:** H2 In-Memory Database
* **Logging:** SLF4J (Structured Logging)
* **Containerization:** Docker (Multi-stage build)
* **Testing:** JUnit 5 + Mockito

---

## ⚙️ Getting Started

### Prerequisites
* [Java 17 SDK](https://adoptium.net/temurin/releases/)
* [Maven](https://maven.apache.org/) (or use the included wrapper `./mvnw`)
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Optional)

### Option 1: Run Locally (Java)
1.  **Navigate to the project folder:**
    ```bash
    cd LibraryAssignment
    ```

2.  **Run the application:**
    ```bash
    # Using the Maven Wrapper (Recommended - No installation required)
    ./mvnw spring-boot:run
    ```

3.  **Access the API:**
    * API Root: `http://localhost:8080`
    * Swagger UI: `http://localhost:8080/swagger-ui.html`

### Option 2: Run with Docker (Recommended)
This ensures the application runs in the exact environment intended for production.

1.  **Build the Image:**
    ```bash
    docker build -t library-api .
    ```

2.  **Run the Container:**
    ```bash
    docker run -p 8080:8080 library-api
    ```

---

## 🧪 Running Tests
The solution includes a comprehensive unit test suite covering business logic and validation rules.

To execute tests:
```bash
./mvnw test

```

---

## 📡 API Endpoints

### 1. Register a Borrower

* **POST** `/api/library/borrowers`
* **Body:** `{"name": "Wei Lee", "email": "wei@example.com"}`
* *Business Rule:* Emails must be unique.

### 2. Register a Book

* **POST** `/api/library/books`
* **Body:** `{"isbn": "978-3-16-148410-0", "title": "Clean Code", "author": "Robert C. Martin"}`
* *Business Rule:* Multiple copies (IDs) can share an ISBN, but they must match the existing Title/Author metadata.

### 3. Borrow a Book

* **POST** `/api/library/borrow?borrowerId=1&bookId=5`
* *Business Rule:* A specific book copy cannot be borrowed if it is already checked out.

### 4. Return a Book

* **POST** `/api/library/return/{bookId}`

### 5. List All Books

* **GET** `/api/library/books`

---

## 📂 Project Structure

```text
src/
├── main/
│   ├── java/com/example/library/
│   │   ├── controller/    # API Layer (REST Endpoints)
│   │   ├── service/       # Business Logic Layer
│   │   ├── repository/    # Data Access Layer (JPA)
│   │   └── model/         # Domain Entities
│   └── resources/
│       └── application.properties # Configuration
└── test/                  # JUnit 5 Tests

```

```

```