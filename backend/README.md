# Multi-Tenant E-Commerce API

A robust, multi-tenant e-commerce backend built with Spring Boot 3.2, MongoDB, and JWT Authentication. Provides RESTful APIs for tenants, products, shopping carts, orders, and user authentication.

## Features

* Multi-Tenancy Support: Isolated data per tenant
* JWT Authentication: Secure login/register with access and refresh tokens
* Product Management: CRUD operations with filtering, sorting, and pagination
* Shopping Cart: Add/remove items with inventory validation
* Order Processing: Idempotent order placement with transactional support
* Rate Limiting: Built-in request rate limiting
* API Documentation: Swagger UI integration
* Request Logging: Comprehensive request/response logging

## Tech Stack

| Technology        | Version | Purpose                        |
| ----------------- | ------- | ------------------------------ |
| Java              | 17      | Language                       |
| Spring Boot       | 3.2.4   | Framework                      |
| MongoDB           | -       | Database                       |
| Spring Security   | -       | Authentication & Authorization |
| JWT (jjwt)        | 0.11.5  | Token-based auth               |
| Lombok            | -       | Boilerplate reduction          |
| SpringDoc OpenAPI | 2.3.0   | API Documentation              |

## Getting Started

### Prerequisites

* Java 17 or higher
* MongoDB running on localhost:27017
* Maven (or use the included wrapper)

### Installation

```bash
git clone https://github.com/your-username/e-commerce-inati.git
cd e-commerce-inati
```

Start MongoDB:

```bash
# Using Docker
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

Run the application:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

Access:

* API Base URL: `http://localhost:8080`
* Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Project Structure

```
src/main/java/com/example/ecommerce/
├── auth/           # Authentication
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

## API Endpoints

### Authentication

| Method | Endpoint         | Description          |
| ------ | ---------------- | -------------------- |
| POST   | `/auth/register` | Register a new user  |
| POST   | `/auth/login`    | Login and get tokens |
| POST   | `/auth/refresh`  | Refresh access token |
| POST   | `/auth/logout`   | Logout user          |

### Tenant Management

| Method | Endpoint          | Description         |
| ------ | ----------------- | ------------------- |
| POST   | `/tenants/create` | Create a new tenant |
| GET    | `/tenants/getAll` | Get all tenants     |

### Products

| Method | Endpoint    | Description                |
| ------ | ----------- | -------------------------- |
| POST   | `/products` | Create a new product       |
| GET    | `/products` | List products with filters |

**Query Parameters for GET `/products`:**

* sku, name, minPrice, maxPrice, inStock, sortBy (PRICE, INVENTORY, NAME), direction (ASC/DESC), limit, offset

### Shopping Cart

| Method | Endpoint    | Description      |
| ------ | ----------- | ---------------- |
| GET    | `/cart`     | Get current cart |
| POST   | `/cart/add` | Add item to cart |

### Orders

| Method | Endpoint  | Description                                        |
| ------ | --------- | -------------------------------------------------- |
| POST   | `/orders` | Place an order (requires `Idempotency-Key` header) |

## Authentication Flow

1. Register a new user with email and password
2. Login to receive an access token (and refresh token in cookie)
3. Include the access token in the `Authorization` header
4. Refresh the token when expired using the refresh token cookie
5. Logout to invalidate the refresh token

## Configuration

Edit `src/main/resources/application.yml`:

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
    org.springframework.web: INFO
```

## Testing

```bash
# Windows
.\mvnw.cmd test

# Linux/Mac
./mvnw test
```

## Postman Collection

Included in project root: `postman_collection.json`

## API Documentation

* Swagger UI: `http://localhost:8080/swagger-ui/index.html`
* OpenAPI JSON: `openapi.json`

## Author

**Abhisek Nayak**

* Email: [abhiseknayak84@gmail.com](mailto:abhiseknayak84@gmail.com)
* Portfolio: [port-folio-iota-seven.vercel.app](http://port-folio-iota-seven.vercel.app/)

## License

Licensed under the terms in the LICENSE file.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request
