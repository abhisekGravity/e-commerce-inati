# 🛒 Multi-Tenant E-Commerce API

A robust, multi-tenant e-commerce backend built with **Spring Boot 3.2**, **MongoDB**, and **JWT Authentication**. This project provides RESTful APIs for managing tenants, products, shopping carts, orders, and user authentication.

---

## ✨ Features

- **Multi-Tenancy Support** - Isolated data per tenant with tenant-aware entities
- **JWT Authentication** - Secure login/register with access and refresh tokens
- **Product Management** - CRUD operations with filtering, sorting, and pagination
- **Shopping Cart** - Add/remove items with inventory validation
- **Order Processing** - Idempotent order placement with transactional support
- **Rate Limiting** - Built-in request rate limiting
- **API Documentation** - Swagger UI integration
- **Request Logging** - Comprehensive request/response logging

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Language |
| Spring Boot | 3.2.4 | Framework |
| MongoDB | - | Database |
| Spring Security | - | Authentication & Authorization |
| JWT (jjwt) | 0.11.5 | Token-based auth |
| Lombok | - | Boilerplate reduction |
| SpringDoc OpenAPI | 2.3.0 | API Documentation |

---

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **MongoDB** running on `localhost:27017`
- **Maven** (or use the included wrapper)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/e-commerce-inati.git
   cd e-commerce-inati
   ```

2. **Start MongoDB**
   ```bash
   # Using Docker
   docker run -d -p 27017:27017 --name mongodb mongo:latest
   
   # Or start your local MongoDB service
   ```

3. **Run the application**
   ```bash
   # Using Maven Wrapper (Windows)
   .\mvnw.cmd spring-boot:run
   
   # Using Maven Wrapper (Linux/Mac)
   ./mvnw spring-boot:run
   
   # Or using Maven directly
   mvn spring-boot:run
   ```

4. **Access the application**
   - **API Base URL**: `http://localhost:8080`
   - **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`

---

## 📁 Project Structure

```
src/main/java/com/example/ecommerce/
├── auth/           # Authentication (login, register, refresh, logout)
├── cart/           # Shopping cart management
├── common/         # Shared configs, filters, rate limiting
├── exception/      # Global exception handling
├── inventory/      # Inventory management service
├── order/          # Order processing
├── pricing/        # Pricing logic
├── product/        # Product CRUD operations
├── security/       # JWT & security configuration
├── tenant/         # Multi-tenant support
└── User/           # User domain & repository
```

---

## 🔌 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Login and get tokens |
| POST | `/auth/refresh` | Refresh access token |
| POST | `/auth/logout` | Logout user |

### Tenant Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/tenants/create` | Create a new tenant |
| GET | `/tenants/getAll` | Get all tenants |

### Products
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/products` | Create a new product |
| GET | `/products` | List products with filters |

**Query Parameters for GET `/products`:**
- `sku` - Filter by SKU
- `name` - Filter by name
- `minPrice` / `maxPrice` - Price range filter
- `inStock` - Stock availability filter
- `sortBy` - Sort by `PRICE`, `INVENTORY`, or `NAME`
- `direction` - `ASC` or `DESC`
- `limit` / `offset` - Pagination

### Shopping Cart
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/cart` | Get current cart |
| POST | `/cart/add` | Add item to cart |

### Orders
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/orders` | Place an order (requires `Idempotency-Key` header) |

---

## 🔐 Authentication Flow

1. **Register** a new user with email and password
2. **Login** to receive an access token (and refresh token in cookie)
3. Include the access token in the `Authorization` header:
   ```
   Authorization: Bearer <access_token>
   ```
4. **Refresh** the token when expired using the refresh token cookie
5. **Logout** to invalidate the refresh token

---

## ⚙️ Configuration

Edit `src/main/resources/application.yml` to customize:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/e-commerce

server:
  port: 8080

logging:
  level:
    root: INFO
    org.springframework.web: DEBUG
```

---

## 🧪 Testing

Run the test suite:

```bash
# Windows
.\mvnw.cmd test

# Linux/Mac
./mvnw test
```

---

## 📬 Postman Collection

A Postman collection is included in the project root: `postman_collection.json`

Import it into Postman to quickly test all available endpoints.

---

## 📖 API Documentation

Full OpenAPI specification is available at:
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `openapi.json` (included in project root)

---

## 👤 Author

**Abhisek Nayak**
- Email: abhiseknayak84@gmail.com
- Portfolio: [port-folio-iota-seven.vercel.app](http://port-folio-iota-seven.vercel.app/)

---

## 📄 License

This project is licensed under the terms specified in the LICENSE file.

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request
