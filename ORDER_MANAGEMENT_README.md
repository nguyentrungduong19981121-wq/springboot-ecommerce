# Order Management Feature

## Tổng quan
Feature Order Management cung cấp đầy đủ chức năng quản lý đơn hàng bao gồm tạo đơn, xem trạng thái, cập nhật trạng thái, và tự động giảm tồn kho sản phẩm khi đặt hàng.

## Entities

### Order (Enhanced)
```java
- id: Long (Primary Key)
- customer: Customer (ManyToOne, Required)
- createdAt: LocalDateTime
- status: OrderStatus (Enum)
- orderProducts: List<OrderProduct> (OneToMany, cascade)
- totalPrice: Double (Calculated, Transient)
```

### OrderProduct (Existing)
```java
- pk: OrderProductPK (Composite key: orderId + productId)
- quantity: Integer
- totalPrice: Double (Calculated: price * quantity)
```

### OrderStatus (Enhanced Enum)
```java
- PENDING: Order created, awaiting payment
- PAID: Payment received
- SHIPPED: Order shipped to customer
- DELIVERED: Order delivered successfully
- CANCELLED: Order cancelled
```

## API Endpoints

### 1. GET /api/orders
**Mô tả**: Lấy danh sách đơn hàng

**Authentication**: Required (JWT or Admin Basic Auth)

**Authorization**:
- **Admin**: See all orders
- **Customer**: See only their own orders

**Parameters**:
- `status` (OrderStatus, optional): Filter by order status
  - Values: `PENDING`, `PAID`, `SHIPPED`, `DELIVERED`, `CANCELLED`

**Example Requests**:
```bash
# Get all orders (customer sees own, admin sees all)
GET /api/orders

# Filter by status
GET /api/orders?status=PENDING
GET /api/orders?status=PAID
GET /api/orders?status=SHIPPED
```

**Response** (Customer):
```json
[
  {
    "id": 1,
    "customerId": 7,
    "customerName": "John Doe",
    "customerEmail": "john@example.com",
    "createdAt": "2025-10-20T10:30:00",
    "status": "PAID",
    "items": [
      {
        "productId": 1,
        "productName": "Laptop Dell XPS 13",
        "quantity": 1,
        "price": 30000.0,
        "totalPrice": 30000.0
      }
    ],
    "totalPrice": 30000.0
  }
]
```

### 2. GET /api/orders/{id}
**Mô tả**: Lấy chi tiết đơn hàng

**Authentication**: Required (JWT or Admin Basic Auth)

**Authorization**:
- **Admin**: Can view any order
- **Customer**: Can only view their own orders

**Path Parameter**:
- `id` (Long): Order ID

**Example Request**:
```bash
GET /api/orders/1
```

**Response**:
```json
{
  "id": 1,
  "customerId": 7,
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "createdAt": "2025-10-20T10:30:00",
  "status": "PAID",
  "items": [
    {
      "productId": 1,
      "productName": "Laptop Dell XPS 13",
      "quantity": 1,
      "price": 30000.0,
      "totalPrice": 30000.0
    },
    {
      "productId": 2,
      "productName": "iPhone 13 Pro",
      "quantity": 2,
      "price": 25000.0,
      "totalPrice": 50000.0
    }
  ],
  "totalPrice": 80000.0
}
```

**Error Response** (403 - Not authorized):
```json
{
  "timestamp": "2025-10-20T16:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "You are not authorized to view this order"
}
```

### 3. POST /api/orders
**Mô tả**: Tạo đơn hàng mới (authenticated customer only)

**Authentication**: Required (JWT)

**Request Body**:
```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 1
    },
    {
      "productId": 2,
      "quantity": 2
    }
  ]
}
```

**Validation Rules**:
- `items`: Required, must have at least one item
- `productId`: Required, product must exist
- `quantity`: Required, must be > 0
- **Stock check**: Product must have sufficient stock

**Response** (201 Created):
```json
{
  "id": 11,
  "customerId": 7,
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "createdAt": "2025-10-20T16:30:00",
  "status": "PENDING",
  "items": [
    {
      "productId": 1,
      "productName": "Laptop Dell XPS 13",
      "quantity": 1,
      "price": 30000.0,
      "totalPrice": 30000.0
    }
  ],
  "totalPrice": 30000.0
}
```

**Error Response** (400 - Insufficient stock):
```json
{
  "timestamp": "2025-10-20T16:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Insufficient stock for product: Laptop Dell XPS 13. Available: 5, Requested: 10"
}
```

**Business Logic**:
1. Validate customer is authenticated
2. Validate all products exist
3. Check stock availability for each product
4. Reduce stock for each product
5. Create order with PENDING status
6. Return order details

### 4. PUT /api/orders/{id}/status
**Mô tả**: Cập nhật trạng thái đơn hàng

**Authentication**: Required (JWT or Admin Basic Auth)

**Authorization**:
- **Admin**: Can change to any status
- **Customer**: Can only cancel their own orders (PENDING/PAID → CANCELLED)

**Path Parameter**:
- `id` (Long): Order ID

**Request Body**:
```json
{
  "status": "SHIPPED"
}
```

**Valid Status Transitions**:
- **Admin**:
  - `PENDING` → `PAID`, `CANCELLED`
  - `PAID` → `SHIPPED`, `CANCELLED`
  - `SHIPPED` → `DELIVERED`
  - `Any` → `CANCELLED`

- **Customer**:
  - `PENDING` → `CANCELLED`
  - `PAID` → `CANCELLED` (maybe within time limit)

**Example Requests**:
```bash
# Admin changes status to SHIPPED
PUT /api/orders/1/status
Authorization: Basic admin:admin123
{"status": "SHIPPED"}

# Customer cancels their order
PUT /api/orders/1/status
Authorization: Bearer {jwt-token}
{"status": "CANCELLED"}
```

**Response** (200 OK):
```json
{
  "id": 1,
  "customerId": 7,
  "status": "SHIPPED",
  "createdAt": "2025-10-20T10:30:00",
  "items": [...],
  "totalPrice": 80000.0
}
```

**Error Response** (403 - Customer tries to ship):
```json
{
  "timestamp": "2025-10-20T16:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Customers can only cancel orders"
}
```

**Error Response** (400 - Invalid transition):
```json
{
  "timestamp": "2025-10-20T16:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Cannot cancel order that is already shipped or delivered"
}
```

## Features Chi Tiết

### ✅ Order Creation
- Customer creates order from cart/product selection
- Auto-calculates total price
- Sets initial status to PENDING
- Records creation timestamp
- Validates all products exist
- Checks stock availability
- **Reduces stock automatically**

### ✅ Stock Management
When creating an order:
1. Check product stock availability
2. If sufficient stock → reduce stock by quantity
3. If insufficient stock → return error, don't create order
4. Stock update is transactional (atomic)

**Example**:
- Product: Laptop Dell XPS 13, Stock: 10
- Customer orders quantity: 2
- After order: Stock becomes 8
- If another customer orders 9 → Error (insufficient stock)

### ✅ Status Filtering
Filter orders by status:
- `GET /api/orders?status=PENDING` - Unpaid orders
- `GET /api/orders?status=PAID` - Paid orders waiting shipment
- `GET /api/orders?status=SHIPPED` - Orders in transit
- `GET /api/orders?status=DELIVERED` - Completed orders
- `GET /api/orders?status=CANCELLED` - Cancelled orders

### ✅ Authorization
- **Admin** (Basic Auth):
  - View all orders
  - Update any order status
  - Full control

- **Customer** (JWT):
  - View only their own orders
  - Create orders for themselves
  - Cancel their own orders (PENDING/PAID only)
  - Cannot ship or mark as delivered

### ✅ Security
- JWT authentication for customers
- Basic Auth for admin
- Order ownership validation
- Prevent unauthorized status changes

## Database Schema

### orders Table (Enhanced)
```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,           ← New FK
    created_at TIMESTAMP NOT NULL,         ← Changed from date_created
    status VARCHAR(20) NOT NULL,           ← Changed to ENUM
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);
```

### order_product Table (Existing)
```sql
CREATE TABLE order_product (
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);
```

## Sample Data

### Orders (10 orders)
| ID | Customer | Created At | Status | Items |
|----|----------|------------|--------|-------|
| 1 | Nguyen Van A | 2025-10-01 10:30 | PAID | 1 product |
| 2 | Tran Thi B | 2025-10-02 14:15 | PAID | 1 product |
| 3 | Nguyen Van A | 2025-10-03 09:45 | SHIPPED | 2 products |
| 4 | Le Van C | 2025-10-05 16:20 | PAID | 1 product |
| 5 | Tran Thi B | 2025-10-08 11:00 | DELIVERED | 1 product |
| 6 | Pham Thi D | 2025-10-10 13:30 | PAID | 1 product |
| 7 | Nguyen Van A | 2025-10-12 15:45 | SHIPPED | 2 products |
| 8 | Hoang Van E | 2025-10-15 10:15 | PAID | 1 product |
| 9 | Le Van C | 2025-10-18 12:00 | PENDING | 2 products |
| 10 | Vu Thi F | 2025-10-20 09:30 | PENDING | 1 product |

**Total Revenue**: 460,000

## Testing Guide

### Setup
```bash
# 1. Login as customer
curl -X POST http://localhost:8080/api/customers/login \
  -H "Content-Type: application/json" \
  -d '{"email":"nguyenvana@example.com","password":"password123"}'

# Save the token!
TOKEN="your-jwt-token-here"
```

### Test 1: Get All Orders (Customer)
```bash
curl http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN"
```

### Test 2: Get All Orders (Admin)
```bash
curl -u admin:admin123 http://localhost:8080/api/orders
```

### Test 3: Filter by Status
```bash
# Customer's pending orders
curl "http://localhost:8080/api/orders?status=PENDING" \
  -H "Authorization: Bearer $TOKEN"

# Admin sees all paid orders
curl -u admin:admin123 "http://localhost:8080/api/orders?status=PAID"
```

### Test 4: Get Single Order
```bash
curl http://localhost:8080/api/orders/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Test 5: Create New Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "items": [
      {
        "productId": 1,
        "quantity": 1
      },
      {
        "productId": 5,
        "quantity": 2
      }
    ]
  }'
```

### Test 6: Check Stock Reduced
```bash
# Check product stock before order
curl http://localhost:8080/api/products/1

# Create order (stock will be reduced)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"items":[{"productId":1,"quantity":2}]}'

# Check product stock after order (should be reduced by 2)
curl http://localhost:8080/api/products/1
```

### Test 7: Test Insufficient Stock
```bash
# Try to order more than available stock
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "items": [
      {
        "productId": 1,
        "quantity": 1000
      }
    ]
  }'
# Expected: 400 Bad Request (Insufficient stock)
```

### Test 8: Update Order Status (Admin)
```bash
# Admin changes to SHIPPED
curl -X PUT http://localhost:8080/api/orders/1/status \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"status": "SHIPPED"}'
```

### Test 9: Customer Cancels Order
```bash
# Customer cancels their pending order
curl -X PUT http://localhost:8080/api/orders/9/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"status": "CANCELLED"}'
```

### Test 10: Test Authorization
```bash
# Customer tries to view another customer's order
curl http://localhost:8080/api/orders/2 \
  -H "Authorization: Bearer $TOKEN"
# Expected: 403 Forbidden (if token belongs to customer 1 and order 2 belongs to customer 2)
```

## DTOs

### OrderDto (Response)
```java
{
  Long id;
  Long customerId;
  String customerName;
  String customerEmail;
  LocalDateTime createdAt;
  OrderStatus status;
  List<OrderItemDto> items;
  Double totalPrice;
}
```

### OrderItemDto
```java
{
  Long productId;
  String productName;
  Integer quantity;
  Double price;
  Double totalPrice;
}
```

### CreateOrderRequest
```java
{
  @NotEmpty List<OrderItemRequest> items;
  
  OrderItemRequest {
    @NotNull Long productId;
    @NotNull Integer quantity;
  }
}
```

### UpdateOrderStatusRequest
```java
{
  @NotNull OrderStatus status;
}
```

## Architecture

```
┌─────────────────────────────────────┐
│   OrderManagementController         │
│  - GET /api/orders                  │
│  - GET /api/orders/{id}             │
│  - POST /api/orders                 │
│  - PUT /api/orders/{id}/status      │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  OrderManagementServiceImpl         │
│  - getAllOrders()                   │
│  - getOrderById()                   │
│  - createOrder() → Reduce stock!    │
│  - updateOrderStatus()              │
│  - getOrdersByStatus()              │
└──────────────┬──────────────────────┘
               │
     ┌─────────┼─────────┐
     ▼         ▼         ▼
┌─────────┐ ┌─────────┐ ┌─────────┐
│ Order   │ │Customer │ │ Product │
│ Repo    │ │ Repo    │ │ Repo    │
└─────────┘ └─────────┘ └─────────┘
```

## Business Rules

### Stock Management
✅ **When creating order**:
- Check stock availability for each product
- Reduce stock by ordered quantity
- Transaction is atomic (all or nothing)
- If any product has insufficient stock → entire order fails

✅ **Stock validation**:
```
If product.stock >= quantity:
  product.stock = product.stock - quantity
  Create order
Else:
  Throw error: "Insufficient stock"
```

### Status Transitions

**Valid transitions**:
```
PENDING → PAID (after payment)
PAID → SHIPPED (admin ships order)
SHIPPED → DELIVERED (admin confirms delivery)
Any → CANCELLED (admin or customer)
```

**Invalid transitions**:
- SHIPPED → PENDING (cannot unship)
- DELIVERED → Any other status (cannot undo delivery)
- CANCELLED → Any other status (cannot resurrect cancelled order)

### Authorization Rules

**Admin** (Basic Auth):
- ✅ View all orders
- ✅ Create orders for any customer
- ✅ Update any order to any status
- ✅ Filter all orders by status

**Customer** (JWT):
- ✅ View only their own orders
- ✅ Create orders for themselves
- ✅ Cancel their own orders (before shipped)
- ✅ Filter their own orders by status
- ❌ Cannot view others' orders
- ❌ Cannot ship or deliver orders
- ❌ Cannot cancel shipped/delivered orders

## Error Handling

### 400 Bad Request
- Missing required fields
- Empty items list
- Insufficient stock
- Invalid quantity (< 1)
- Invalid status transition

### 401 Unauthorized
- No authentication token
- Invalid or expired JWT token

### 403 Forbidden
- Customer tries to view another customer's order
- Customer tries to ship/deliver order
- Customer tries to cancel shipped/delivered order

### 404 Not Found
- Order not found
- Product not found
- Customer not found

## Configuration

### Security Settings
- JWT authentication for customers
- Basic Auth for admin
- Stateless sessions
- Protected order endpoints

## Notes

- ✅ Full order management (view, create, update status)
- ✅ JWT and Basic Auth support
- ✅ Automatic stock reduction when creating order
- ✅ Stock validation before order creation
- ✅ Filter by status (pending, paid, shipped, delivered, cancelled)
- ✅ Authorization (admin vs customer)
- ✅ Order ownership validation
- ✅ Status transition rules
- ✅ Comprehensive error handling
- ✅ DTO pattern for clean API

## Future Enhancements

- [ ] Restore stock when order is cancelled
- [ ] Order payment integration
- [ ] Order tracking (shipping carrier integration)
- [ ] Order history and timeline
- [ ] Email notifications for status changes
- [ ] Partial order fulfillment
- [ ] Order refunds
- [ ] Bulk status updates
- [ ] Order export (PDF invoice)
- [ ] Order search and advanced filtering
