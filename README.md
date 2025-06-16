
# Personal Finance Manager

A full-stack personal finance management web application built using Spring Boot (Java) for the backend. It allows users to manage their transactions, savings goals, and categories with user authentication and session-based login.

## Features

- User Registration and Login (with password encryption)
- Add, view, delete and filter financial transactions
- Define and track savings goals with live progress calculation
- Organize income and expenses into custom categories
- Generate monthly reports on spending and savings

## Tech Stack

- **Backend:** Java, Spring Boot, Spring Security
- **Database:** H2 , Spring Data JPA
- **Build Tool:** Maven

## Testing

- JUnit 5 & Mockito for unit testing services and controllers

---

## Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.6+

### Running Locally

```bash
git clone https://github.com/your-username/personal-finance-manager.git
cd personal-finance-manager
./mvnw spring-boot:run
```

The app will start at: `http://localhost:8080`

Open this address in your browser and it should diplay
```bash
Personal Finance Manager API is running.
```

You can test endpoints using Postman or the built-in Swagger if integrated.

---

## API Documentation

### Auth API

#### `POST /api/auth/register`
Registers a new user.
```json
{
  "username": "user@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phoneNumber": "1234567890"
}
```

#### `POST /api/auth/login`
Logs in a user.
```json
{
  "username": "user@example.com",
  "password": "password123"
}
```

---

### Transactions API

#### `POST /api/transactions`
Adds a new transaction.

#### `GET /api/transactions`
Fetches all transactions for the user.

#### `DELETE /api/transactions/{id}`
Deletes a transaction.

---

### Goals API

#### `POST /api/goals`
Creates a new savings goal.

#### `GET /api/goals`
Fetches all savings goals.

#### `GET /api/goals/{id}`
Fetches a specific goal.

#### `PUT /api/goals/{id}`
Updates a savings goal.

#### `DELETE /api/goals/{id}`
Deletes a goal.

---

## Design Decisions

- Used session-based auth for simplicity in a monolithic application.
- Passwords are encrypted using Spring Security's `PasswordEncoder`.
- Domain-driven service structure to isolate business logic.
- JavaDoc + JUnit + Mockito for maintainable code and testing.

---

## 📄 License

MIT License
