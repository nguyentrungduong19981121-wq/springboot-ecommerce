# Product Management Feature

## Tổng quan
Feature Product Management cung cấp đầy đủ CRUD operations cho sản phẩm với các chức năng tìm kiếm, lọc theo danh mục, sắp xếp theo giá và quản lý hình ảnh sản phẩm.

## Entities

### 1. Product (Enhanced)
```java
- id: Long (Primary Key)
- name: String (Required)
- slug: String (Required, Unique)
- description: String (TEXT)
- price: Double (Required, >= 0)
- stock: Integer (>= 0)
- pictureUrl: String
- category: Category (ManyToOne)
- images: List<ProductImage> (OneToMany, Cascade)
```

### 2. ProductImage (New)
```java
- id: Long (Primary Key)
- url: String (Required)
- product: Product (ManyToOne, Required)
```

### 3. Category (New)
```java
- id: Long (Primary Key)
- name: String (Required)
- description: String
```

## API Endpoints

### 1. GET /api/products
**Mô tả**: Lấy danh sách sản phẩm với tìm kiếm, lọc và sắp xếp

**Parameters** (tất cả optional):
- `name` (String): Tìm kiếm theo tên sản phẩm (case-insensitive, partial match)
- `categoryId` (Long): Lọc theo category
- `sortBy` (String): Sắp xếp theo giá
  - `price_asc`: Giá tăng dần
  - `price_desc`: Giá giảm dần

**Example Requests**:
```bash
# Get all products
GET /api/products

# Search by name
GET /api/products?name=laptop

# Filter by category
GET /api/products?categoryId=1

# Sort by price ascending
GET /api/products?sortBy=price_asc

# Combined: search + filter + sort
GET /api/products?name=iphone&categoryId=2&sortBy=price_desc
```

**Response**:
```json
[
  {
    "id": 1,
    "name": "Laptop Dell XPS 13",
    "slug": "laptop-dell-xps-13",
    "description": "Ultra-thin and powerful laptop...",
    "price": 30000.0,
    "stock": 10,
    "pictureUrl": "https://example.com/laptop.jpg",
    "categoryId": 1,
    "categoryName": "Laptops",
    "imageUrls": [
      "https://example.com/laptop-front.jpg",
      "https://example.com/laptop-side.jpg"
    ]
  }
]
```

### 2. GET /api/products/{id}
**Mô tả**: Lấy thông tin chi tiết của một sản phẩm

**Path Parameter**:
- `id` (Long): Product ID

**Example Request**:
```bash
GET /api/products/1
```

**Response**:
```json
{
  "id": 1,
  "name": "Laptop Dell XPS 13",
  "slug": "laptop-dell-xps-13",
  "description": "Ultra-thin and powerful laptop with 13-inch display...",
  "price": 30000.0,
  "stock": 10,
  "pictureUrl": "https://example.com/laptop.jpg",
  "categoryId": 1,
  "categoryName": "Laptops",
  "imageUrls": [
    "https://example.com/laptop-front.jpg",
    "https://example.com/laptop-side.jpg"
  ]
}
```

**Error Response** (404):
```json
{
  "timestamp": "2025-10-20T10:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999"
}
```

### 3. POST /api/products
**Mô tả**: Tạo sản phẩm mới

**Request Body**:
```json
{
  "name": "Samsung Galaxy Tab S8",
  "slug": "samsung-galaxy-tab-s8",
  "description": "Premium Android tablet with S Pen",
  "price": 18000.0,
  "stock": 20,
  "pictureUrl": "https://example.com/tab-s8.jpg",
  "categoryId": 3,
  "imageUrls": [
    "https://example.com/tab-front.jpg",
    "https://example.com/tab-back.jpg"
  ]
}
```

**Validation Rules**:
- `name`: Required, not blank
- `slug`: Required, not blank, must be unique
- `price`: Required, must be >= 0
- `stock`: Optional, must be >= 0 if provided
- `description`: Optional
- `pictureUrl`: Optional
- `categoryId`: Optional (must exist if provided)
- `imageUrls`: Optional array of URLs

**Response** (201 Created):
```json
{
  "id": 6,
  "name": "Samsung Galaxy Tab S8",
  "slug": "samsung-galaxy-tab-s8",
  "description": "Premium Android tablet with S Pen",
  "price": 18000.0,
  "stock": 20,
  "pictureUrl": "https://example.com/tab-s8.jpg",
  "categoryId": 3,
  "categoryName": "Tablets",
  "imageUrls": [
    "https://example.com/tab-front.jpg",
    "https://example.com/tab-back.jpg"
  ]
}
```

**Error Response** (400 - Duplicate slug):
```json
{
  "timestamp": "2025-10-20T10:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Product with slug 'samsung-galaxy-tab-s8' already exists"
}
```

### 4. PUT /api/products/{id}
**Mô tả**: Cập nhật sản phẩm (partial update)

**Path Parameter**:
- `id` (Long): Product ID

**Request Body** (tất cả fields optional):
```json
{
  "name": "Laptop Dell XPS 13 (2025)",
  "slug": "laptop-dell-xps-13-2025",
  "description": "Updated description",
  "price": 32000.0,
  "stock": 15,
  "pictureUrl": "https://example.com/new-laptop.jpg",
  "categoryId": 1,
  "imageUrls": [
    "https://example.com/new-image1.jpg",
    "https://example.com/new-image2.jpg"
  ]
}
```

**Notes**:
- Chỉ các fields được gửi sẽ được cập nhật
- Slug validation: nếu thay đổi slug, phải đảm bảo unique
- Category validation: nếu cung cấp categoryId, category phải tồn tại
- Images: nếu cung cấp imageUrls, sẽ thay thế toàn bộ images cũ

**Response** (200 OK):
```json
{
  "id": 1,
  "name": "Laptop Dell XPS 13 (2025)",
  "slug": "laptop-dell-xps-13-2025",
  "description": "Updated description",
  "price": 32000.0,
  "stock": 15,
  "pictureUrl": "https://example.com/new-laptop.jpg",
  "categoryId": 1,
  "categoryName": "Laptops",
  "imageUrls": [
    "https://example.com/new-image1.jpg",
    "https://example.com/new-image2.jpg"
  ]
}
```

### 5. DELETE /api/products/{id}
**Mô tả**: Xóa sản phẩm

**Path Parameter**:
- `id` (Long): Product ID

**Example Request**:
```bash
DELETE /api/products/1
```

**Response** (204 No Content):
```
(empty body)
```

**Notes**:
- Cascade delete: tất cả images liên quan sẽ được xóa tự động
- Orphan removal: nếu remove image khỏi product, image sẽ bị xóa

## Features Chi Tiết

### 🔍 Search (Tìm kiếm)
- Tìm kiếm theo tên sản phẩm
- Case-insensitive (không phân biệt hoa thường)
- Partial match (tìm một phần tên)
- Example: `name=phone` sẽ tìm "iPhone", "Smartphone", etc.

### 🏷️ Filter (Lọc)
- Lọc theo category ID
- Có thể kết hợp với search
- Example: `categoryId=2` (chỉ smartphones)

### 📊 Sort (Sắp xếp)
- Sắp xếp theo giá tăng dần: `sortBy=price_asc`
- Sắp xếp theo giá giảm dần: `sortBy=price_desc`
- Có thể kết hợp với search và filter

### ✅ Validation

#### Unique Slug
- Mỗi product phải có slug duy nhất
- Validate khi CREATE
- Validate khi UPDATE (nếu thay đổi slug)
- Error message: "Product with slug 'xxx' already exists"

#### Price Validation
- Price phải >= 0
- Required khi create
- Optional khi update

#### Stock Validation
- Stock phải >= 0
- Default: 0 nếu không cung cấp

#### Category Validation
- Category phải tồn tại nếu cung cấp categoryId
- Error: "Category not found with id: xxx"

### 🖼️ Image Management
- Mỗi product có thể có nhiều images
- OneToMany relationship với cascade
- Khi delete product → tự động xóa images
- Khi update images → thay thế toàn bộ (clear old, add new)

## Database Schema

### Tables

#### product
```sql
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    price DOUBLE NOT NULL,
    stock INTEGER,
    picture_url VARCHAR(500),
    category_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

#### product_images
```sql
CREATE TABLE product_images (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    url VARCHAR(500) NOT NULL,
    product_id BIGINT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);
```

#### categories
```sql
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500)
);
```

## Sample Data

Application bao gồm sample data trong `data.sql`:

### Categories (4)
1. Laptops
2. Smartphones
3. Tablets
4. Accessories

### Products (5)
1. Laptop Dell XPS 13 - $30,000 (10 in stock)
2. iPhone 13 Pro - $25,000 (25 in stock)
3. Samsung Galaxy S21 - $20,000 (20 in stock)
4. MacBook Pro 14 - $45,000 (8 in stock)
5. iPad Air - $15,000 (15 in stock)

### Product Images (8)
- Multiple images per product for demonstration

## Testing Guide

### 1. Get All Products
```bash
curl http://localhost:8080/api/products
```

### 2. Search Products
```bash
# Search by name
curl "http://localhost:8080/api/products?name=laptop"

# Filter by category
curl "http://localhost:8080/api/products?categoryId=1"

# Sort by price
curl "http://localhost:8080/api/products?sortBy=price_asc"

# Combined
curl "http://localhost:8080/api/products?name=iphone&categoryId=2&sortBy=price_desc"
```

### 3. Get Product by ID
```bash
curl http://localhost:8080/api/products/1
```

### 4. Create Product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "AirPods Pro",
    "slug": "airpods-pro",
    "description": "Active Noise Cancellation wireless earbuds",
    "price": 5000.0,
    "stock": 50,
    "categoryId": 4,
    "imageUrls": ["https://example.com/airpods.jpg"]
  }'
```

### 5. Update Product
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "price": 28000.0,
    "stock": 12
  }'
```

### 6. Delete Product
```bash
curl -X DELETE http://localhost:8080/api/products/1
```

### 7. Test Unique Slug Validation
```bash
# Try to create product with existing slug
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Another Laptop",
    "slug": "laptop-dell-xps-13",
    "price": 25000.0
  }'

# Expected: 400 Bad Request with error message
```

## DTOs

### ProductDto (Response)
```java
{
  Long id;
  String name;
  String slug;
  String description;
  Double price;
  Integer stock;
  String pictureUrl;
  Long categoryId;
  String categoryName;
  List<String> imageUrls;
}
```

### CreateProductRequest
```java
{
  @NotBlank String name;
  @NotBlank String slug;
  String description;
  @NotNull @Min(0) Double price;
  @Min(0) Integer stock;
  String pictureUrl;
  Long categoryId;
  List<String> imageUrls;
}
```

### UpdateProductRequest
```java
{
  String name;
  String slug;
  String description;
  @Min(0) Double price;
  @Min(0) Integer stock;
  String pictureUrl;
  Long categoryId;
  List<String> imageUrls;
}
```

## Architecture

```
┌─────────────────────────────────────┐
│      ProductController              │
│  - GET /api/products                │
│  - GET /api/products/{id}           │
│  - POST /api/products               │
│  - PUT /api/products/{id}           │
│  - DELETE /api/products/{id}        │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│      ProductServiceImpl             │
│  - CRUD operations                  │
│  - Search & filter logic            │
│  - Slug validation                  │
│  - DTO conversion                   │
└──────────────┬──────────────────────┘
               │
     ┌─────────┼─────────┐
     ▼         ▼         ▼
┌────────┐ ┌────────┐ ┌────────┐
│Product │ │Product │ │Category│
│Repo    │ │Image   │ │Repo    │
│        │ │Repo    │ │        │
└────────┘ └────────┘ └────────┘
     │         │         │
     └─────────┴─────────┘
               ▼
         ┌───────────┐
         │   H2 DB   │
         └───────────┘
```

## Error Handling

### 404 Not Found
- Product không tồn tại
- Category không tồn tại

### 400 Bad Request
- Validation errors (missing required fields, negative values)
- Duplicate slug
- Invalid data format

### 500 Internal Server Error
- Database errors
- Unexpected exceptions

## Notes

- ✅ Full CRUD operations
- ✅ Search by name (case-insensitive)
- ✅ Filter by category
- ✅ Sort by price (asc/desc)
- ✅ Unique slug validation
- ✅ Image management with cascade
- ✅ Category relationship
- ✅ Comprehensive validation
- ✅ DTO pattern for clean API
- ✅ Sample data included

## Future Enhancements

- [ ] Pagination for product list
- [ ] Advanced filtering (price range, stock availability)
- [ ] Product variants (size, color)
- [ ] File upload for images
- [ ] Product reviews and ratings
- [ ] SEO optimization with slug
- [ ] Product inventory tracking
- [ ] Bulk operations
