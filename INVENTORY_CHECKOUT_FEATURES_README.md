# Inventory Management & Checkout Process - Features Documentation

## 📦 Feature 6: Inventory Management

### Overview
Complete inventory tracking system with transaction history for all stock movements (imports, exports, sales, returns, adjustments).

### Entities

#### Inventory
```java
- id: Long (Primary Key)
- product: Product (ManyToOne, Required)
- quantity: Integer (Amount of change)
- transactionType: InventoryTransactionType (ENUM)
- reason: String (Optional explanation)
- createdAt: LocalDateTime (Auto-set)
```

#### InventoryTransactionType (Enum)
```java
- IMPORT: Adding stock (purchase, restock)
- EXPORT: Removing stock manually
- SALE: Stock reduced due to order
- RETURN: Stock increased due to order return
- ADJUSTMENT: Manual stock adjustment
```

### API Endpoints

#### 1. GET /api/inventory
**Description**: Get all products with their current stock levels

**Authentication**: ADMIN only (Basic Auth)

**Response**:
```json
[
  {
    "productId": 1,
    "productName": "Laptop Dell XPS 13",
    "productSlug": "laptop-dell-xps-13",
    "currentStock": 10,
    "price": 30000.0
  },
  {
    "productId": 2,
    "productName": "iPhone 13 Pro",
    "productSlug": "iphone-13-pro",
    "currentStock": 25,
    "price": 25000.0
  }
]
```

#### 2. GET /api/inventory/{productId}
**Description**: Get inventory details for a specific product

**Authentication**: ADMIN only

**Path Parameter**:
- `productId` (Long): Product ID

**Response**:
```json
{
  "productId": 1,
  "productName": "Laptop Dell XPS 13",
  "productSlug": "laptop-dell-xps-13",
  "currentStock": 10,
  "price": 30000.0
}
```

#### 3. PUT /api/inventory/{productId}
**Description**: Update inventory for a product (import/export stock)

**Authentication**: ADMIN only

**Path Parameter**:
- `productId` (Long): Product ID

**Request Body**:
```json
{
  "quantity": 50,
  "transactionType": "IMPORT",
  "reason": "New stock arrival from supplier"
}
```

**Transaction Types**:
- `IMPORT`: Adds quantity to stock
- `EXPORT`: Subtracts quantity from stock
- `SALE`: Subtracts quantity (usually automatic from orders)
- `RETURN`: Adds quantity back (order return)
- `ADJUSTMENT`: Sets stock to exact quantity

**Response** (200 OK):
```json
{
  "productId": 1,
  "productName": "Laptop Dell XPS 13",
  "productSlug": "laptop-dell-xps-13",
  "currentStock": 60,
  "price": 30000.0
}
```

**Error Response** (400 - Insufficient stock):
```json
{
  "timestamp": "2025-10-20T19:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Insufficient stock. Current: 10, Requested: 20"
}
```

### Features

✅ **View All Inventory** - Get stock levels for all products  
✅ **View Product Inventory** - Get stock for specific product  
✅ **Import Stock** - Add stock (purchases, restocks)  
✅ **Export Stock** - Remove stock manually  
✅ **Stock Adjustment** - Set exact stock quantity  
✅ **Transaction History** - All stock movements tracked  
✅ **Automatic Sale Recording** - Orders auto-reduce stock  

### Business Rules

**Stock Calculation**:
```
IMPORT/RETURN: newStock = currentStock + quantity
EXPORT/SALE: newStock = currentStock - quantity
ADJUSTMENT: newStock = quantity (direct set)
```

**Validations**:
- Cannot export/sale more than current stock
- Quantity must be positive
- Transaction type required
- All transactions logged in database

---

## 💳 Feature 7: Checkout Process

### Overview
Complete checkout flow with session management, coupon support, stock validation, payment processing, and email confirmation.

### Entities

#### CheckoutSession
```java
- id: Long
- customer: Customer (ManyToOne)
- appliedCoupon: Coupon (ManyToOne, Optional)
- subtotal: Double
- discountAmount: Double
- totalPrice: Double
- paymentStatus: PaymentStatus (ENUM)
- order: Order (ManyToOne, after payment)
- createdAt: LocalDateTime
- expiresAt: LocalDateTime (30 min)
- items: List<CheckoutItem>
```

#### CheckoutItem (Embeddable)
```java
- productId: Long
- productName: String
- quantity: Integer
- price: Double
- totalPrice: Double
```

#### Coupon
```java
- id: Long
- code: String (UNIQUE)
- discountType: DiscountType (PERCENTAGE/FIXED_AMOUNT)
- discountValue: Double
- minimumOrderValue: Double (Optional)
- maxUsageCount: Integer (Optional)
- currentUsageCount: Integer
- validFrom: LocalDateTime (Optional)
- validUntil: LocalDateTime (Optional)
- active: Boolean
```

#### PaymentStatus (Enum)
```java
- PENDING: Not yet attempted
- PROCESSING: In progress
- COMPLETED: Successful
- FAILED: Failed
- CANCELLED: Cancelled
```

### API Endpoints

#### 1. POST /api/checkout
**Description**: Create checkout session with items

**Authentication**: JWT (Customer)

**Request Body**:
```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 5,
      "quantity": 1
    }
  ],
  "couponCode": "WELCOME10"
}
```

**Response** (201 Created):
```json
{
  "sessionId": 1,
  "orderId": null,
  "items": [
    {
      "productId": 1,
      "productName": "Laptop Dell XPS 13",
      "quantity": 2,
      "price": 30000.0,
      "totalPrice": 60000.0
    },
    {
      "productId": 5,
      "productName": "iPad Air",
      "quantity": 1,
      "price": 15000.0,
      "totalPrice": 15000.0
    }
  ],
  "subtotal": 75000.0,
  "appliedCouponCode": "WELCOME10",
  "discountAmount": 7500.0,
  "totalPrice": 67500.0,
  "paymentStatus": "PENDING",
  "createdAt": "2025-10-20T19:30:00",
  "expiresAt": "2025-10-20T20:00:00"
}
```

**Features**:
- Validates all products exist
- Checks stock availability
- Creates checkout session with 30-min expiration
- Optionally applies coupon code
- Returns session ID for subsequent operations

#### 2. POST /api/checkout/apply-coupon
**Description**: Apply or change coupon code on existing session

**Authentication**: JWT (Customer)

**Request Body**:
```json
{
  "sessionId": 1,
  "couponCode": "BLACKFRIDAY"
}
```

**Response** (200 OK):
```json
{
  "sessionId": 1,
  "orderId": null,
  "items": [...],
  "subtotal": 75000.0,
  "appliedCouponCode": "BLACKFRIDAY",
  "discountAmount": 15000.0,
  "totalPrice": 60000.0,
  "paymentStatus": "PENDING",
  "createdAt": "2025-10-20T19:30:00",
  "expiresAt": "2025-10-20T20:00:00"
}
```

**Coupon Validation**:
- Coupon must be active
- Must be within valid date range
- Usage count must not exceed limit
- Order subtotal must meet minimum requirement

**Error Response** (400 - Invalid coupon):
```json
{
  "timestamp": "2025-10-20T19:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Order subtotal must be at least $100000.0 to use this coupon"
}
```

#### 3. POST /api/checkout/payment
**Description**: Process payment and create order

**Authentication**: JWT (Customer)

**Request Body**:
```json
{
  "sessionId": 1,
  "paymentMethod": "CREDIT_CARD",
  "cardNumber": "4111111111111111",
  "cardHolderName": "John Doe",
  "expiryDate": "12/25",
  "cvv": "123"
}
```

**Response** (200 OK):
```json
{
  "sessionId": 1,
  "orderId": 11,
  "items": [...],
  "subtotal": 75000.0,
  "appliedCouponCode": "WELCOME10",
  "discountAmount": 7500.0,
  "totalPrice": 67500.0,
  "paymentStatus": "COMPLETED",
  "createdAt": "2025-10-20T19:30:00",
  "expiresAt": "2025-10-20T20:00:00"
}
```

**Payment Process**:
1. Validate session exists and not expired
2. Check session not already paid
3. Update status to PROCESSING
4. Process payment (simulated for demo)
5. Check stock availability again
6. Reduce stock for each product
7. Create order with PAID status
8. Increment coupon usage count
9. Update session status to COMPLETED
10. Link order to session
11. **Send order confirmation email**
12. **Send payment confirmation email**

**Email Confirmation**:
```
Subject: Order Confirmation - Order #11

Dear John Doe,

Thank you for your order!
Order ID: #11
Order Date: 2025-10-20T19:35:00
Order Status: PAID
Total Amount: $67500.0

Items:
  - Laptop Dell XPS 13 x 2 = $60000.0
  - iPad Air x 1 = $15000.0

We will process your order shortly.
```

### Sample Coupons

| Code | Type | Value | Min Order | Max Usage | Valid Until | Status |
|------|------|-------|-----------|-----------|-------------|--------|
| WELCOME10 | PERCENTAGE | 10% | $10,000 | 100 | 2025-12-31 | ✅ Active |
| SAVE50K | FIXED | $50,000 | $200,000 | 50 | 2025-12-31 | ✅ Active |
| BLACKFRIDAY | PERCENTAGE | 20% | $50,000 | 1000 | 2025-11-30 | ✅ Active |
| NEWYEAR | PERCENTAGE | 15% | None | 200 | 2026-01-31 | ✅ Active |
| EXPIRED | PERCENTAGE | 50% | None | 10 | 2025-01-31 | ❌ Inactive |

### Features

✅ **Checkout Session** - Create with 30-min expiration  
✅ **Coupon Support** - Apply discount codes  
✅ **Stock Validation** - Check availability before payment  
✅ **Payment Processing** - Simulated payment gateway  
✅ **Order Creation** - Auto-create order after payment  
✅ **Stock Reduction** - Auto-reduce stock on payment  
✅ **Email Confirmation** - Send order & payment emails  
✅ **Coupon Tracking** - Track usage count  
✅ **Session Security** - Customer can only access own sessions  

### Checkout Workflow

```
1. Customer creates checkout session
   POST /api/checkout
   ↓
   Session created (30-min expiration)
   Subtotal calculated
   Coupon applied (optional)
   Total price calculated

2. Customer applies/changes coupon (optional)
   POST /api/checkout/apply-coupon
   ↓
   Coupon validated
   Discount recalculated
   Total price updated

3. Customer processes payment
   POST /api/checkout/payment
   ↓
   Stock validated again
   Payment processed
   Stock reduced
   Order created
   Emails sent
   ↓
   Order #11 created ✅
```

### Business Rules

**Coupon Validation**:
```
1. Coupon must exist and be active
2. Must be within valid date range
3. Usage count must not exceed max
4. Order subtotal must meet minimum
5. Discount cannot exceed subtotal
```

**Discount Calculation**:
```java
if (PERCENTAGE):
  discount = subtotal × (value / 100)
else if (FIXED_AMOUNT):
  discount = value

// Ensure discount doesn't exceed subtotal
discount = min(discount, subtotal)

totalPrice = subtotal - discount
```

**Stock Management**:
```
1. On checkout creation: Check stock availability
2. On payment: Check stock AGAIN (prevent race condition)
3. If insufficient: Payment fails
4. If sufficient: Reduce stock, create order
```

---

## 🧪 Testing Guide

### Inventory Management

#### Test 1: View All Inventory
```bash
curl -u admin:admin123 http://localhost:8080/api/inventory
```

#### Test 2: Import Stock
```bash
curl -X PUT http://localhost:8080/api/inventory/1 \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 50,
    "transactionType": "IMPORT",
    "reason": "Restock from supplier"
  }'
```

#### Test 3: Export Stock
```bash
curl -X PUT http://localhost:8080/api/inventory/1 \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 5,
    "transactionType": "EXPORT",
    "reason": "Damaged items removed"
  }'
```

#### Test 4: Stock Adjustment
```bash
curl -X PUT http://localhost:8080/api/inventory/1 \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 100,
    "transactionType": "ADJUSTMENT",
    "reason": "Physical inventory count correction"
  }'
```

### Checkout Process

#### Test 1: Create Checkout Session
```bash
# Login first
curl -X POST http://localhost:8080/api/customers/login \
  -H "Content-Type: application/json" \
  -d '{"email":"nguyenvana@example.com","password":"password123"}'

# Save TOKEN
TOKEN="your-jwt-token"

# Create checkout
curl -X POST http://localhost:8080/api/checkout \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "items": [
      {"productId": 1, "quantity": 2},
      {"productId": 5, "quantity": 1}
    ],
    "couponCode": "WELCOME10"
  }'

# Save SESSION_ID from response
```

#### Test 2: Apply Different Coupon
```bash
curl -X POST http://localhost:8080/api/checkout/apply-coupon \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "sessionId": 1,
    "couponCode": "BLACKFRIDAY"
  }'
```

#### Test 3: Process Payment
```bash
curl -X POST http://localhost:8080/api/checkout/payment \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "sessionId": 1,
    "paymentMethod": "CREDIT_CARD",
    "cardNumber": "4111111111111111",
    "cardHolderName": "Nguyen Van A",
    "expiryDate": "12/25",
    "cvv": "123"
  }'

# Check console logs for email confirmation output
```

#### Test 4: Verify Order Created
```bash
# Get orders to see new order from checkout
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/orders
```

#### Test 5: Verify Stock Reduced
```bash
# Check product stock was reduced
curl http://localhost:8080/api/products/1
```

---

## 🔒 Security

### Inventory Endpoints
- **Authentication**: Basic Auth (admin:admin123)
- **Authorization**: ADMIN role required
- **Access**: Only administrators can manage inventory

### Checkout Endpoints
- **Authentication**: JWT (Customer token)
- **Authorization**: Customer can only access own sessions
- **Access**: Session ownership validated on all operations

---

## 📊 Database Schema

### inventory Table
```sql
CREATE TABLE inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id)
);
```

### coupon Table
```sql
CREATE TABLE coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) UNIQUE NOT NULL,
    discount_type VARCHAR(20) NOT NULL,
    discount_value DOUBLE NOT NULL,
    minimum_order_value DOUBLE,
    max_usage_count INTEGER,
    current_usage_count INTEGER DEFAULT 0,
    valid_from TIMESTAMP,
    valid_until TIMESTAMP,
    active BOOLEAN DEFAULT TRUE
);
```

### checkout_session Table
```sql
CREATE TABLE checkout_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    coupon_id BIGINT,
    subtotal DOUBLE NOT NULL,
    discount_amount DOUBLE DEFAULT 0,
    total_price DOUBLE NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    order_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (coupon_id) REFERENCES coupon(id),
    FOREIGN KEY (order_id) REFERENCES orders(id)
);
```

### checkout_session_items Table
```sql
CREATE TABLE checkout_session_items (
    session_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255),
    quantity INTEGER NOT NULL,
    price DOUBLE NOT NULL,
    total_price DOUBLE NOT NULL,
    FOREIGN KEY (session_id) REFERENCES checkout_session(id)
);
```

---

## 📝 Summary

### Inventory Management (Feature 6)
- **3 endpoints**: View all, view by product, update inventory
- **5 transaction types**: Import, Export, Sale, Return, Adjustment
- **Full history**: All stock movements tracked
- **Admin only**: Secure access control

### Checkout Process (Feature 7)
- **3 endpoints**: Create session, apply coupon, process payment
- **Coupon support**: Percentage and fixed amount discounts
- **Email notifications**: Order and payment confirmations
- **Stock management**: Validates and reduces stock
- **Session-based**: 30-minute expiration for security

### Total New Endpoints: 6
- 3 Inventory endpoints (ADMIN)
- 3 Checkout endpoints (JWT)

---

**Implementation Date**: 2025-10-20  
**Build Status**: ✅ SUCCESS  
**Features**: 6 & 7 of 7 COMPLETE
