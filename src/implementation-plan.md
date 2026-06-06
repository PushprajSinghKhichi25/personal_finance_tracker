# Personal Finance Tracker - Implementation Plan

## Project Overview
A full-stack personal finance management application with Spring Boot backend, React/TypeScript frontend, MySQL database, and JWT-based authentication.

**Tech Stack:**
- Backend: Spring Boot 3.x with Spring Security + JWT
- Frontend: React 18 with TypeScript
- Database: MySQL 8
- Build Tool: Maven
- Structure: Monorepo (backend and frontend in separate directories)

---

## Phase 1: Project Setup & Infrastructure

### 1.1 Backend Configuration
**Files to create/modify:**
- `pom.xml` - Add Spring Boot parent, Spring Security, JWT, JPA, MySQL dependencies
- `src/main/resources/application.yml` - Database, JWT, server config
- `src/main/java/org/example/config/` - Security config, JWT config

**Tasks:**
1. Add Spring Boot dependencies to pom.xml:
    - spring-boot-starter-web
    - spring-boot-starter-security
    - spring-boot-starter-data-jpa
    - spring-boot-starter-validation
    - mysql-connector-java
    - jjwt (JWT library)
    - lombok (optional, for reducing boilerplate)

2. Create application.yml with:
    - MySQL connection properties
    - JWT secret key and expiration time
    - Server port (8080)
    - JPA/Hibernate config

3. Set up Spring Security configuration class

### 1.2 Database Schema Design
**Files to create:**
- Database migration scripts or JPA entity files

**Schema:**
- `users` table: id, username, email, password_hash, created_at, updated_at
- `expenses` table: id, user_id, amount, description, category, date, created_at, updated_at
- `categories` table: id, name, description (for predefined tags: travel, food, etc)
- `expense_categories` junction table: expense_id, category_id
- `budgets` table: id, user_id, category_id, monthly_limit, current_month, created_at, updated_at
- `income` table: id, user_id, amount, description, source, date, created_at, updated_at

### 1.3 Frontend Project Setup
**Files to create:**
- `frontend/` directory with React/TypeScript setup
- Use Vite or Create React App with TypeScript template
- Install dependencies: axios, react-router-dom, chart.js/recharts, zustand/redux (state management), tailwindcss/material-ui (styling)

---

## Phase 2: Backend - Authentication & Core Infrastructure

### 2.1 User Management
**Package:** `org.example.auth`

**Files to create:**
- `User.java` - JPA entity
- `UserRepository.java` - Spring Data JPA repository
- `UserService.java` - Business logic
- `UserController.java` - REST endpoints

**Endpoints:**
- POST `/api/auth/register` - Create new user
- POST `/api/auth/login` - Authenticate and return JWT token
- POST `/api/auth/refresh` - Refresh expired JWT

### 2.2 JWT & Security Configuration
**Package:** `org.example.config`

**Files to create:**
- `JwtUtil.java` - JWT token generation, validation, extraction
- `SecurityConfig.java` - Spring Security configuration
- `JwtAuthenticationFilter.java` - Filter to intercept and validate JWT tokens

**Key Features:**
- Token generation with user claims
- Token expiration validation
- Exception handling for invalid/expired tokens

### 2.3 Common Infrastructure
**Package:** `org.example.common`

**Files to create:**
- `ApiResponse.java` - Standardized API response wrapper
- `ErrorHandler.java` - Global exception handling
- `User` details in authenticated context

---

## Phase 3: Backend - Core Features

### 3.1 Expense Management
**Package:** `org.example.expense`

**Entities:**
- `Expense.java` - JPA entity with fields: id, user, amount, description, date, categories
- `Category.java` - JPA entity for tags (travel, food, etc)

**Files to create:**
- `ExpenseRepository.java` - Query expenses by user, date range, category
- `CategoryRepository.java`
- `ExpenseService.java` - Business logic
- `ExpenseController.java` - REST endpoints

**Endpoints:**
- POST `/api/expenses` - Create expense
- GET `/api/expenses` - List expenses (with filters: date range, category)
- GET `/api/expenses/{id}` - Get expense details
- PUT `/api/expenses/{id}` - Update expense
- DELETE `/api/expenses/{id}` - Delete expense
- GET `/api/categories` - List all categories

**Features:**
- Pagination support
- Filtering by date range and category
- User isolation (users only see their own expenses)

### 3.2 Income Management
**Package:** `org.example.income`

**Entities:**
- `Income.java` - JPA entity with fields: id, user, amount, description, source, date

**Files to create:**
- `IncomeRepository.java`
- `IncomeService.java`
- `IncomeController.java`

**Endpoints:**
- POST `/api/income` - Add income
- GET `/api/income` - List income (with date filters)
- PUT `/api/income/{id}` - Update income
- DELETE `/api/income/{id}` - Delete income

### 3.3 Budget Management
**Package:** `org.example.budget`

**Entities:**
- `Budget.java` - JPA entity with fields: id, user, category, monthly_limit, current_month

**Files to create:**
- `BudgetRepository.java`
- `BudgetService.java`
- `BudgetController.java`

**Endpoints:**
- POST `/api/budgets` - Create budget for category
- GET `/api/budgets` - List budgets for current month
- PUT `/api/budgets/{id}` - Update budget
- DELETE `/api/budgets/{id}` - Delete budget
- GET `/api/budgets/{id}/status` - Get budget spending status

---

## Phase 4: Backend - Reporting & Analytics

### 4.1 Reports Service
**Package:** `org.example.reports`

**Files to create:**
- `ReportService.java` - Calculate income, expenses, net, by category
- `ReportController.java` - REST endpoints

**Endpoints:**
- GET `/api/reports/monthly?month=YYYY-MM` - Monthly income vs expenses summary
- GET `/api/reports/category-breakdown?month=YYYY-MM` - Spending by category
- GET `/api/reports/trend?months=3` - Historical trend (last N months)

**Response Format:**
```json
{
  "month": "2025-03",
  "totalIncome": 5000,
  "totalExpenses": 3000,
  "net": 2000,
  "byCategory": {
    "food": 500,
    "travel": 800,
    "utilities": 700
  },
  "budgetStatus": {
    "food": { "limit": 600, "spent": 500, "remaining": 100 }
  }
}
```
 
---

## Phase 5: Frontend - Core Structure

### 5.1 Project Setup
**Structure:**
```
frontend/
├── src/
│   ├── components/
│   ├── pages/
│   ├── services/
│   ├── store/
│   ├── hooks/
│   ├── types/
│   ├── styles/
│   └── App.tsx
├── public/
└── package.json
```

### 5.2 Authentication Pages
**Components:**
- `LoginPage.tsx` - Username/email + password login form
- `RegisterPage.tsx` - User registration form
- `ProtectedRoute.tsx` - Guard routes requiring authentication
- `JWT token storage` - localStorage or sessionStorage

**Features:**
- Form validation
- Error messages
- Redirect to dashboard on successful login
- Redirect to login on token expiration

### 5.3 State Management
**Setup:**
- Use Zustand or Redux Toolkit
- Store: user auth state, currentUser, token
- Actions: login, logout, register, token refresh

---

## Phase 6: Frontend - Dashboard & Features

### 6.1 Dashboard Page
**Components:**
- `Dashboard.tsx` - Main layout with nav, sidebar
- Navigation: Dashboard, Expenses, Income, Budgets, Reports, Settings
- Key metrics display: Total Balance, This Month Income, This Month Expenses

### 6.2 Expense Management UI
**Components:**
- `ExpenseList.tsx` - Table view with filters (date, category)
- `ExpenseForm.tsx` - Modal/form for add/edit expense
- `CategoryFilter.tsx` - Multi-select category filter
- `DateRangePicker.tsx` - Date range selection

**Features:**
- Inline actions: Edit, Delete
- Pagination
- Category badges/chips
- Quick add expense button

### 6.3 Income Management UI
**Components:**
- `IncomeList.tsx` - Similar to expenses
- `IncomeForm.tsx` - Add/edit income

### 6.4 Budget Management UI
**Components:**
- `BudgetList.tsx` - Show budgets with progress bars
- `BudgetForm.tsx` - Add/edit budget
- Visual indicators: On track (green), Warning (yellow), Over budget (red)

### 6.5 Reports & Analytics Page
**Components:**
- `MonthlyReportCard.tsx` - Summary card: Income, Expenses, Net
- `IncomeVsExpensesChart.tsx` - Bar or line chart
- `CategoryBreakdownChart.tsx` - Pie chart for expense distribution
- `TrendChart.tsx` - Line chart for last 3-6 months trend
- `DateSelector.tsx` - Month/year picker for report filtering

**Libraries:**
- Recharts or Chart.js for charts

---

## Phase 7: Frontend - UI Polish & Integration

### 7.1 Styling & Design System
- Tailwind CSS or Material-UI
- Consistent color scheme, spacing, typography
- Dark mode support (optional)
- Responsive design for mobile/tablet

### 7.2 Error Handling & Loading States
- Loading spinners for async operations
- Toast notifications for success/error messages
- Modal confirmations for delete operations
- Form validation with error messages

### 7.3 API Integration
**Files to create:**
- `services/api.ts` - Axios instance with auth headers
- `services/authService.ts` - Login, register, token refresh
- `services/expenseService.ts` - Expense CRUD operations
- `services/incomeService.ts` - Income CRUD operations
- `services/budgetService.ts` - Budget operations
- `services/reportService.ts` - Fetch reports and analytics

---

## Phase 8: Testing & Deployment Preparation

### 8.1 Backend Testing
- Unit tests for services (business logic)
- Integration tests for controllers
- Test JWT token generation and validation
- Test authorization (user isolation)

### 8.2 Frontend Testing
- Component unit tests (React Testing Library)
- Integration tests for pages
- Test form submissions and validations

### 8.3 Security Checklist
- ✓ Password hashing (BCrypt)
- ✓ JWT secret stored in environment variables
- ✓ CORS configuration
- ✓ User isolation on all endpoints
- ✓ XSS protection in React
- ✓ SQL injection protection (using JPA/parameterized queries)

---

## Implementation Order (Recommended)

1. **Backend Phase 1** - Project setup, database schema, Spring Security
2. **Backend Phase 2** - User authentication, JWT flow
3. **Backend Phase 3** - Expense, Income, Budget entities and endpoints
4. **Backend Phase 4** - Reports and analytics endpoints
5. **Frontend Phase 1** - Project setup, auth pages, state management
6. **Frontend Phase 2-4** - Core feature components (expenses, income, budgets)
7. **Frontend Phase 5** - Reports and charts
8. **Frontend Phase 6** - Styling, error handling, API integration
9. **Phase 8** - Testing, security review, deployment preparation

---

## Key Files to Create (Summary)

**Backend:**
- `pom.xml` (update)
- `application.yml`
- `User`, `Expense`, `Category`, `Income`, `Budget` entities
- `*Repository` interfaces (5 total)
- `*Service` classes (4-5 total)
- `*Controller` classes (4-5 total)
- `JwtUtil`, `SecurityConfig`, `JwtAuthenticationFilter`
- `ApiResponse`, `ErrorHandler` utilities

**Frontend:**
- `src/pages/LoginPage`, `RegisterPage`, `Dashboard`
- `src/components/ExpenseList`, `ExpenseForm`, `IncomeList`, etc.
- `src/services/api`, `authService`, `expenseService`, etc.
- `src/store/` - State management setup
- `src/types/` - TypeScript interfaces
- `package.json` with dependencies

---

## Verification & Testing

After implementation:
1. Start backend: `mvn spring-boot:run`
2. Start frontend: `npm run dev`
3. Register new user account
4. Login and verify JWT token is stored
5. Create expenses with categories, income, and budgets
6. View dashboard with correct calculations
7. Check monthly report and charts render correctly
8. Verify user isolation (logged-in user only sees their data)
9. Test logout and token refresh
10. Check responsive design on mobile
 