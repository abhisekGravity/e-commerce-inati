# Multi-Tenant E-Commerce Platform

A robust, full-stack multi-tenant e-commerce solution built with Spring Boot 3.2 (Backend) and React + Vite (Frontend). Features isolated data per tenant, secure JWT authentication with cookie storage, dynamic pricing strategies, and a responsive UI.

## Features

### Core
*   **Multi-Tenancy**: Verified data isolation per tenant across all resources.
*   **Secure Authentication**: JWT-based login/register with HttpOnly-ready Cookie storage for Access & Refresh tokens.
*   **Rate Limiting**: Built-in request limiting for API protection.

### Commerce & Pricing
*   **Dynamic Pricing Engine**: 
    *   **Base vs. Discounted Price**: Visual indicators for price drops (strikethrough original price).
    *   **Named Discounts**: Context-aware discounts (e.g., "10% Off Order > $100").
*   **Shopping Cart**: Real-time inventory validation, quantity management, and auto-recalculation.
*   **Order Systems**: Idempotent order placement to prevent duplicate charges.
*   **Product Management**: Advanced filtering (price, stock), sorting, and search.

### Frontend
*   **Modern UI**: Responsive design with Dark Mode aesthetics.
*   **State Management**: efficient Context API usage for Auth and Cart state.
*   **Optimized Storage**: Migrated from LocalStorage to Cookies for better security posture.

---

## Tech Stack

### Backend
*   **Framework**: Spring Boot 3.2, Spring Security
*   **Database**: MongoDB (Reactive & Template support)
*   **Language**: Java 17
*   **Documentation**: SpringDoc OpenAPI (Swagger UI)

### Frontend
*   **Framework**: React 18, Vite
*   **Styling**: Modern CSS3 (Variables, Flexbox/Grid)
*   **HTTP Client**: Axios (with Interceptors for auto-refresh)
*   **Utils**: `js-cookie`, `uuid`

---

## Getting Started

### Prerequisites
*   Java 17+
*   Node.js 18+ & npm
*   MongoDB (running on `localhost:27017`)

### 1. Backend Setup
```bash
cd backend
# Windows
.\mvnw.cmd spring-boot:run
# Linux/Mac
./mvnw spring-boot:run
```
*   API Base URL: `http://localhost:8080/api`
*   Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### 2. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
*   Access App: `http://localhost:5173` (Default Vite port)

---

## Project Structure

```
e-commerce-inati/
├── backend/src/main/java/com/example/ecommerce/
│   ├── auth/       # Auth Controller, Service, JWT logic
│   ├── cart/       # Cart domain & strategies
│   ├── order/      # Order processing & state machine
│   ├── pricing/    # Discount engines & strategies
│   ├── product/    # Product catalog logic
│   ├── tenant/     # Multi-tenancy context & filters
│   └── ...
└── frontend/src/
    ├── api/        # Axios setup & API endpoints
    ├── components/ # Reusable UI (Navbar, CartItem)
    ├── context/    # AuthContext (Cookie mgmt)
    ├── pages/      # Route pages (Home, Cart, Login)
    └── ...
```

## Authentication Flow (Cookie-Based)

1.  **Login**: Backend issues access/refresh tokens.
2.  **Storage**: Frontend intercepts response and stores tokens in **Cookies** (expires: 7 days for access, 30 days for refresh).
3.  **Requests**: Axios interceptor automatically attaches `Authorization: Bearer <token>` from cookie.
4.  **Auto-Refresh**: On 401, frontend automatically calls `/refresh`, gets new tokens, updates cookies, and retries the request.
5.  **Logout**: Frontend requests backend logout and clears all client-side cookies.

## API Documentation

*   **POST** `/auth/register` - Create account
*   **POST** `/auth/login` - Authenticate
*   **GET** `/products` - List/Filter products
*   **GET** `/cart` - View active cart
*   **POST** `/orders` - Place order (Idempotent)

## Author

**Abhisek Nayak**
*   Email: [abhiseknayak84@gmail.com](mailto:abhiseknayak84@gmail.com)
*   Portfolio: [port-folio-iota-seven.vercel.app](http://port-folio-iota-seven.vercel.app/)

## License

Licensed under the terms in the LICENSE file.
