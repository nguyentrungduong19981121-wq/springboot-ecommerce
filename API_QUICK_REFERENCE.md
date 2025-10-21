# 🚀 API Quick Reference Card

## Complete E-Commerce API - All 24 Endpoints

---

## 🔐 Authentication

### Admin Endpoints
```bash
# Use Basic Authentication
-u admin:admin123
```

### Customer Endpoints
```bash
# 1. Login first
curl -X POST http://localhost:8080/api/customers/login \
  -H "Content-Type: application/json" \
  -d '{"email":"nguyenvana@example.com","password":"password123"}'

# 2. Use JWT token
-H "Authorization: Bearer {token}"
```

---

## 📊 Admin Dashboard (2 endpoints)

### Get Dashboard Overview
```bash
curl -u admin:admin123 http://localhost:8080/api/admin/dashboard
```

### Get Sales Report
```bash
curl -u admin:admin123 \
  "http://localhost:8080/api/admin/reports/sales?period=daily&startDate=2025-10-01&endDate=2025-10-20"
```

---

## 📦 Product Management (5 endpoints)

### List Products
```bash
# All products
curl http://localhost:8080/api/products

# Search by name
curl "http://localhost:8080/api/products?name=laptop"

# Filter by category
curl "http://localhost:8080/api/products?categoryId=1"

# Sort by price
curl "http://localhost:8080/api/products?sortBy=price_asc"

# Combined
curl "http://localhost:8080/api/products?name=phone&categoryId=2&sortBy=price_desc"
```

### Get Product
```bash
curl http://localhost:8080/api/products/1
```

### Create Product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Product",
    "slug": "new-product",
    "price": 1000.0,
    "stock": 50,
    "categoryId": 1
  }'
```

### Update Product
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{"price": 2000.0, "stock": 100}'
```

### Delete Product
```bash
curl -X DELETE http://localhost:8080/api/products/1
```

---

## 🗂️ Category Management (7 endpoints)

### List Categories
```bash
# Flat list
curl http://localhost:8080/api/categories

# Hierarchical tree
curl "http://localhost:8080/api/categories?tree=true"
```

### Get Category
```bash
# By ID
curl http://localhost:8080/api/categories/1

# By slug
curl http://localhost:8080/api/categories/slug/electronics

# Get children
curl http://localhost:8080/api/categories/1/children
```

### Create Category
```bash
# Root category
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Category",
    "slug": "new-category",
    "description": "Description"
  }'

# Child category
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Subcategory",
    "slug": "subcategory",
    "parentId": 1
  }'
```

### Update Category
```bash
curl -X PUT http://localhost:8080/api/categories/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Updated Name"}'
```

### Delete Category
```bash
curl -X DELETE http://localhost:8080/api/categories/1
```

---

## 👤 Customer Management (6 endpoints)

### Register
```bash
curl -X POST http://localhost:8080/api/customers/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "phone": "0909999999"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/customers/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
# Returns: {"token": "...", "customerId": ..., ...}
```

### Get Profile
```bash
curl http://localhost:8080/api/customers/profile \
  -H "Authorization: Bearer $TOKEN"
```

### Update Profile
```bash
curl -X PUT http://localhost:8080/api/customers/profile \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Updated Name",
    "phone": "0911111111"
  }'
```

### Get Addresses
```bash
curl http://localhost:8080/api/customers/addresses \
  -H "Authorization: Bearer $TOKEN"
```

### Add Address
```bash
curl -X POST http://localhost:8080/api/customers/addresses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "street": "123 Main St",
    "city": "Ho Chi Minh",
    "postalCode": "700000",
    "country": "Vietnam"
  }'
```

---

## 🧾 Order Management (4 endpoints)

### Get Orders
```bash
# Customer's orders (JWT)
curl http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN"

# All orders (Admin)
curl -u admin:admin123 http://localhost:8080/api/orders

# Filter by status
curl "http://localhost:8080/api/orders?status=PENDING" \
  -H "Authorization: Bearer $TOKEN"
```

### Get Order Details
```bash
curl http://localhost:8080/api/orders/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Create Order
```bash
# Stock will be reduced automatically!
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "items": [
      {"productId": 1, "quantity": 2},
      {"productId": 5, "quantity": 1}
    ]
  }'
```

### Update Order Status
```bash
# Customer cancels (PENDING/PAID only)
curl -X PUT http://localhost:8080/api/orders/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"status": "CANCELLED"}'

# Admin updates to any status
curl -X PUT http://localhost:8080/api/orders/1/status \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"status": "SHIPPED"}'
```

---

## 🔑 Credentials

### Admin
```
Username: admin
Password: admin123
Auth Type: Basic Auth
```

### Sample Customer
```
Email: nguyenvana@example.com
Password: password123
Auth Type: JWT (login to get token)
```

---

## 📋 Common Workflows

### Complete Shopping Flow
```bash
# 1. Browse products
curl "http://localhost:8080/api/products?categoryId=1"

# 2. Register customer
curl -X POST http://localhost:8080/api/customers/register \
  -H "Content-Type: application/json" \
  -d '{"name":"User","email":"user@test.com","password":"pass123"}'

# 3. Login
curl -X POST http://localhost:8080/api/customers/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","password":"pass123"}'
# Save TOKEN

# 4. Add address
curl -X POST http://localhost:8080/api/customers/addresses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"street":"123 St","city":"HCM","country":"Vietnam"}'

# 5. Create order (stock reduces!)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"items":[{"productId":1,"quantity":1}]}'

# 6. View order
curl http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🧪 Test Scripts

```bash
# Product tests
./test-product-api.sh

# Category tests
./test-category-api.sh

# Order tests (includes customer login)
./test-order-api.sh
```

---

## 📊 Order Status Values

```
PENDING    - Order created, awaiting payment
PAID       - Payment received
SHIPPED    - Order shipped
DELIVERED  - Order delivered
CANCELLED  - Order cancelled
```

---

## ⚡ Quick Commands

### Start Application
```bash
# Start Redis (for admin dashboard)
redis-server

# Run application
./mvnw spring-boot:run

# Access at: http://localhost:8080
```

### Access H2 Console
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:ecommercedb
Username: sa
Password: (empty)
```

---

## 📚 Documentation Files

```
Admin Dashboard:
  - ADMIN_DASHBOARD_README.md
  - QUICK_START_GUIDE.md

Product Management:
  - PRODUCT_MANAGEMENT_README.md
  - test-product-api.sh

Category Management:
  - CATEGORY_MANAGEMENT_README.md
  - test-category-api.sh

Customer Management:
  - CUSTOMER_MANAGEMENT_README.md

Order Management:
  - ORDER_MANAGEMENT_README.md
  - test-order-api.sh

Summary:
  - FINAL_ALL_FEATURES_COMPLETE.md
  - API_QUICK_REFERENCE.md (this file)
```

---

## 🎯 Status Indicators

| Feature | Status |
|---------|--------|
| Admin Dashboard | ✅ COMPLETE |
| Product Management | ✅ COMPLETE |
| Category Management | ✅ COMPLETE |
| Customer Management | ✅ COMPLETE |
| Order Management | ✅ COMPLETE |
| **Overall** | **✅ 100% COMPLETE** |

---

**Last Updated**: 2025-10-20  
**Total Endpoints**: 24  
**Build**: ✅ SUCCESS
