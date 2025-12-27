```markdown
# Library Management API (Java Edition)

A RESTful API built with **Java 17** and **Spring Boot** to manage a simple library system. This solution implements a clean architecture pattern (Controller-Service-Repository), automated logging via SLF4J, and comprehensive unit testing.

## Table of Contents
1. [Getting Started](#getting-started)
2. [API Endpoints](#api-endpoints)
3. [12 Factor App Conformance](#12-factor-app-conformance)
4. [Data Models & Assumptions](#data-models--assumptions)
5. [Tech Stack](#tech-stack)

---

## Getting Started

### Prerequisites
* [Java 17 SDK](https://adoptium.net/temurin/releases/)
* [Maven](https://maven.apache.org/)
* Docker (Optional, for containerization)

### Running the Application Locally
1.  **Navigate to the project folder:**
    ```bash
    cd LibraryAssignment
    ```

2.  **Build the project:**
    ```bash
    mvn clean install
    ```

3.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```

4.  **Access the API:**
    * The API will start at `http://localhost:8080`.
    * **Swagger UI:** Navigate to `http://localhost:8080/swagger-ui.html` to test endpoints interactively.

### Running Unit Tests
To run the JUnit 5 test suite:
```bash
mvn test

```

### Running with Docker

```bash
docker build -t library-api-java .
docker run -p 8080:8080 library-api-java

```

---

## API Endpoints

### 1. Register a Borrower

Registers a new user in the system.

* **URL:** `POST /api/library/borrowers`
* **Body:**
```json
{
  "name": "Wei Lee",
  "email": "weilee@example.com"
}

```


* **Response (201 Created):** Returns the created borrower object with `id`.
* **Error (400 Bad Request):** If the email is already registered.

### 2. Register a Book

Registers a new physical book copy.

* **URL:** `POST /api/library/books`
* **Body:**
```json
{
  "isbn": "978-3-16-148410-0",
  "title": "Clean Code",
  "author": "Robert C. Martin"
}

```


* **Response (201 Created):** Returns the created book with a unique `id`.
* **Error (400 Bad Request):** If the ISBN exists but the Title/Author does not match (per validation rules).

### 3. Get All Books

Retrieves a list of all books in the library.

* **URL:** `GET /api/library/books`
* **Response (200 OK):**
```json
[
  {
    "id": 1,
    "isbn": "978-3-16-148410-0",
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "currentBorrowerId": null
  }
]

```



### 4. Borrow a Book

Allows a registered borrower to borrow a specific book copy.

* **URL:** `POST /api/library/borrow`
* **Query Parameters:**
* `borrowerId` (int): The ID of the borrower.
* `bookId` (int): The unique ID of the book copy.


* **Example:** `/api/library/borrow?borrowerId=1&bookId=5`
* **Response (200 OK):** "Book borrowed successfully."
* **Error (409 Conflict):** If the book is already borrowed by someone else.

### 5. Return a Book

Returns a borrowed book to the library.

* **URL:** `POST /api/library/return/{bookId}`
* **Example:** `/api/library/return/5`
* **Response (200 OK):** "Book returned."
* **Error (404 Not Found):** If the book ID does not exist.

---

## 12 Factor App Conformance

This application follows the [12-Factor App methodology](https://12factor.net/) to ensure scalability and maintainability.

| Factor | Principle | Implementation in this Project |
| --- | --- | --- |
| **I. Codebase** | One codebase, many deploys. | Single Git repository containing the API and Tests. |
| **II. Dependencies** | Explicitly declare dependencies. | Managed via **Maven** (`pom.xml`). No implicit system-wide packages. |
| **III. Config** | Store config in the environment. | Configuration is managed via `application.properties` and can be overridden by Environment Variables. |
| **IV. Backing Services** | Treat backing services as attached resources. | Database is accessed via **Spring Data JPA**; swapping H2 for MySQL/PostgreSQL is a config change only. |
| **V. Build, Release, Run** | Strictly separate stages. | **Dockerfile** uses multi-stage builds (Maven for build, OpenJDK for execution). |
| **XI. Logs** | Treat logs as event streams. | **SLF4J** writes to Console (stdout) for container aggregation. |

---

## Data Models & Assumptions

### Assumptions

1. **Book ID vs. ISBN:**
* **ISBN** represents the *intellectual work* (metadata).
* **ID** represents the *physical copy*.
* *Constraint:* The system allows multiple physical copies (different IDs) to share the same ISBN. However, if a new book uses an existing ISBN, its Title and Author MUST match the existing record.


2. **Borrowing Limits:**
* A book copy (Unique ID) can only be borrowed by one person at a time.
* There is currently no limit on *how many* books a single borrower can hold simultaneously (unless specified otherwise).


3. **Database:**
* **H2 Database (In-Memory)** was chosen for this implementation to ensure the application is self-contained and requires no external setup for the reviewer.



---

## Tech Stack

* **Language:** Java 17
* **Framework:** Spring Boot 3.x
* **Database:** H2 Database (Spring Data JPA)
* **Logging:** SLF4J
* **Documentation:** Swagger / OpenAPI
* **Testing:** JUnit 5 + Mockito

```

```