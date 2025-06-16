
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

Add a new transaction (income or expense).

Request Body:

```json
{
  "amount": 500.00,
  "date": "2025-06-10",
  "description": "Grocery shopping",
  "category": "Food"
}
```

Responses:

- **201 Created:** Transaction added.

- **400 Bad Request:** Amount is zero or negative.

- **404 Not Found:** Category doesn't exist.

#### `GET /api/transactions`

Retrieve all transactions for the authenticated user. Supports optional filters.

Query Parameters:

startDate: Optional (format: YYYY-MM-DD)

endDate: Optional (format: YYYY-MM-DD)

category: Optional (e.g., Food, Salary)

Example:

```bash

GET /api/transactions?startDate=2025-01-01&endDate=2025-06-01&category=Food

Response:

```json
{
  "transactions": [
    {
      "id": 12,
      "amount": 500,
      "date": "2025-06-10",
      "category": "Food",
      "description": "Grocery shopping",
      "type": "EXPENSE"
    },
    ...
  ]
}
```
If no records are found:

```json
{
  "message": "No transactions found"
}
```

#### `PUT /api/transactions/{id}`

Update an existing transaction’s amount or description.

Request Body:

```json
{
  "amount": 600.00,
  "description": "Updated grocery bill"
}
```

Responses:

- **200 OK:** Returns the updated transaction.

- **404 Not Found:** Transaction not found.

- **401 Unauthorized:** Trying to modify another user’s transaction.

#### `DELETE /api/transactions/{id}`

Deletes a transaction by its ID.

Responses:

- 200 OK: Transaction deleted successfully.

- 404 Not Found: If the transaction doesn't exist.

- 401 Unauthorized: If trying to delete someone else’s transaction.

---

### Goals API

#### `POST /api/goals`

Create a new savings goal.

Request Body:

```json
{
  "goalName": "Buy a Laptop",
  "targetAmount": 80000,
  "targetDate": "2025-12-31",
  "startDate": "2025-06-12" // optional; defaults to today
}
```
Responses:

- **200 OK:** Goal created successfully with progress.

- **400 Bad Request:** Invalid amount or date logic.

- **401 Unauthorized:** User not authenticated.

#### `GET /api/goals`

Fetch all savings goals for the current user.

Response:

```json
{
  "goals": [
    {
      "id": 1,
      "goalName": "Buy a Laptop",
      "targetAmount": 80000,
      "targetDate": "2025-12-31",
      "startDate": "2025-06-12",
      "currentProgress": "23000.00",
      "progressPercentage": "28.75",
      "remainingAmount": "57000.00"
    },
    ...
  ]
}
```

#### `GET /api/goals/{id}`

Fetch a specific goal by its ID.

Response:

```json
{
  "id": 1,
  "goalName": "Buy a Laptop",
  "targetAmount": 80000,
  "targetDate": "2025-12-31",
  "startDate": "2025-06-12",
  "currentProgress": "23000.00",
  "progressPercentage": "28.75",
  "remainingAmount": "57000.00"
}
```
Errors:

- **404 Not Found:** Goal doesn't exist.

- **403 Forbidden:** User is not the owner.

#### `PUT /api/goals/{id}`

Update an existing goal’s target amount or date.

Request Body:

```json
{
  "targetAmount": 85000,
  "targetDate": "2026-01-15"
}
```
Response:

- **200 OK:** Updated goal with recalculated progress.

#### `DELETE /api/goals/{id}`

Delete a goal by its ID.

Response:

```json
{
  "message": "Goal deleted successfully"
}
```

Errors:

- **404 Not Found:** Goal doesn't exist.

- **403 Forbidden:** Not authorized to delete.

### Category API

#### `GET /api/categories`

Fetches all categories available to the authenticated user (default + custom).

Response:

```json
{
  "categories": [
    {
      "name": "Salary",
      "type": "INCOME",
      "isCustom": false
    },
    {
      "name": "Food",
      "type": "EXPENSE",
      "isCustom": true
    }
  ]
}
```

#### `POST /api/categories`

Creates a custom category.

Request Body:

```json
{
  "name": "Freelance",
  "type": "INCOME"
}
```

Responses:

- **201:** Created if successful.

- **409:** Conflict if category already exists.

#### `DELETE /api/categories/{name}`

Deletes a custom category by name.

Responses:

- **200 OK:** if successfully deleted.

- **403 Forbidden:** if trying to delete a default category.

- **404 Not Found:** if the category doesn't exist.

### Report API

#### `GET /api/reports/monthly/{year}/{month}`

Fetches income, expenses, and savings summary for a given month.

Example: GET /api/reports/monthly/2025/6

Response:

```json
{
  "month": 6,
  "year": 2025,
  "totalIncome": {
    "Salary": 5000
  },
  "totalExpenses": {
    "Food": 1200,
    "Transport": 300
  },
  "netSavings": 3500
}
```

#### `GET /api/reports/yearly/{year}`

Fetches income, expenses, and savings summary for a full year.

Example: GET /api/reports/yearly/2025

Response:

```json
{
  "year": 2025,
  "totalIncome": {
    "Salary": 60000
  },
  "totalExpenses": {
    "Food": 12000,
    "Transport": 3600
  },
  "netSavings": 44400
}
```
---

## Design Decisions

- Used session-based auth for simplicity in a monolithic application.
- Passwords are encrypted using Spring Security's `PasswordEncoder`.
- Domain-driven service structure to isolate business logic.
- JavaDoc + JUnit + Mockito for maintainable code and testing.

---

## 📄 License

MIT License
