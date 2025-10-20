# Category Management Feature

## Tổng quan
Feature Category Management cung cấp đầy đủ CRUD operations cho danh mục sản phẩm với khả năng tổ chức theo cấu trúc phân cấp (hierarchical/tree structure), SEO slug, và tự động xóa cascade khi xóa danh mục cha.

## Entities

### Category (Enhanced)
```java
- id: Long (Primary Key)
- name: String (Required)
- slug: String (Required, Unique)
- description: String (TEXT)
- parent: Category (ManyToOne, self-reference)
- children: List<Category> (OneToMany, cascade delete)
```

**Đặc điểm:**
- Self-referencing relationship (tự tham chiếu)
- Cascade delete: Khi xóa cha → tự động xóa tất cả con
- Orphan removal: Khi remove child → tự động xóa khỏi database
- Có thể lồng nhau nhiều cấp (unlimited depth)

## API Endpoints

### 1. GET /api/categories
**Mô tả**: Lấy danh sách categories (flat list hoặc hierarchical tree)

**Parameters**:
- `tree` (boolean, optional, default: false): 
  - `false`: Trả về flat list (tất cả categories)
  - `true`: Trả về hierarchical tree (chỉ root categories với children lồng nhau)

**Example Requests**:
```bash
# Get flat list (all categories)
GET /api/categories

# Get hierarchical tree
GET /api/categories?tree=true
```

**Response (Flat List)**:
```json
[
  {
    "id": 1,
    "name": "Electronics",
    "slug": "electronics",
    "description": "All electronic devices",
    "parentId": null,
    "parentName": null,
    "children": null,
    "productCount": 0
  },
  {
    "id": 3,
    "name": "Laptops",
    "slug": "laptops",
    "description": "High-performance laptops",
    "parentId": 1,
    "parentName": "Electronics",
    "children": null,
    "productCount": 2
  }
]
```

**Response (Hierarchical Tree)**:
```json
[
  {
    "id": 1,
    "name": "Electronics",
    "slug": "electronics",
    "description": "All electronic devices",
    "parentId": null,
    "parentName": null,
    "productCount": 0,
    "children": [
      {
        "id": 3,
        "name": "Laptops",
        "slug": "laptops",
        "description": "High-performance laptops",
        "parentId": 1,
        "parentName": "Electronics",
        "productCount": 2,
        "children": [
          {
            "id": 6,
            "name": "Gaming Laptops",
            "slug": "gaming-laptops",
            "description": "Gaming laptops",
            "parentId": 3,
            "parentName": "Laptops",
            "productCount": 1,
            "children": []
          }
        ]
      }
    ]
  }
]
```

### 2. GET /api/categories/{id}
**Mô tả**: Lấy thông tin chi tiết của một category với children lồng nhau

**Path Parameter**:
- `id` (Long): Category ID

**Example Request**:
```bash
GET /api/categories/1
```

**Response**:
```json
{
  "id": 1,
  "name": "Electronics",
  "slug": "electronics",
  "description": "All electronic devices and gadgets",
  "parentId": null,
  "parentName": null,
  "productCount": 0,
  "children": [
    {
      "id": 3,
      "name": "Laptops",
      "slug": "laptops",
      "parentId": 1,
      "productCount": 4,
      "children": [...]
    }
  ]
}
```

### 3. GET /api/categories/slug/{slug}
**Mô tả**: Lấy category theo slug (SEO-friendly URL)

**Path Parameter**:
- `slug` (String): Category slug

**Example Request**:
```bash
GET /api/categories/slug/electronics
```

**Response**: Same as GET by ID

### 4. GET /api/categories/{id}/children
**Mô tả**: Lấy danh sách children trực tiếp của một category (không bao gồm grandchildren)

**Path Parameter**:
- `id` (Long): Parent category ID

**Example Request**:
```bash
GET /api/categories/1/children
```

**Response**:
```json
[
  {
    "id": 3,
    "name": "Laptops",
    "slug": "laptops",
    "parentId": 1,
    "parentName": "Electronics",
    "children": null,
    "productCount": 4
  },
  {
    "id": 4,
    "name": "Smartphones",
    "slug": "smartphones",
    "parentId": 1,
    "parentName": "Electronics",
    "children": null,
    "productCount": 2
  }
]
```

### 5. POST /api/categories
**Mô tả**: Tạo category mới

**Request Body**:
```json
{
  "name": "Gaming Accessories",
  "slug": "gaming-accessories",
  "description": "Accessories for gaming",
  "parentId": 2
}
```

**Validation Rules**:
- `name`: Required, not blank
- `slug`: Required, not blank, must be unique
- `description`: Optional
- `parentId`: Optional (null = root category, must exist if provided)

**Response** (201 Created):
```json
{
  "id": 11,
  "name": "Gaming Accessories",
  "slug": "gaming-accessories",
  "description": "Accessories for gaming",
  "parentId": 2,
  "parentName": "Accessories",
  "children": [],
  "productCount": 0
}
```

**Error Response** (400 - Duplicate slug):
```json
{
  "timestamp": "2025-10-20T12:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Category with slug 'gaming-accessories' already exists"
}
```

### 6. PUT /api/categories/{id}
**Mô tả**: Cập nhật category (partial update)

**Path Parameter**:
- `id` (Long): Category ID

**Request Body** (all fields optional):
```json
{
  "name": "Updated Name",
  "slug": "updated-slug",
  "description": "Updated description",
  "parentId": 5
}
```

**Validation Rules**:
- Slug must be unique if changed
- Cannot set self as parent
- Cannot set a descendant as parent (prevents circular reference)
- Parent must exist if provided

**Response** (200 OK):
```json
{
  "id": 3,
  "name": "Updated Name",
  "slug": "updated-slug",
  "description": "Updated description",
  "parentId": 5,
  "parentName": "Tablets",
  "children": [],
  "productCount": 0
}
```

**Error Response** (400 - Circular reference):
```json
{
  "timestamp": "2025-10-20T12:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Cannot set a descendant category as parent (circular reference)"
}
```

### 7. DELETE /api/categories/{id}
**Mô tả**: Xóa category và tất cả children (cascade delete)

**Path Parameter**:
- `id` (Long): Category ID

**Example Request**:
```bash
DELETE /api/categories/1
```

**Response** (204 No Content):
```
(empty body)
```

**Notes**:
- Cascade delete: Xóa category cha sẽ tự động xóa tất cả children và grandchildren
- Products thuộc category bị xóa sẽ có `categoryId = null`

## Features Chi Tiết

### 🌳 Hierarchical Structure (Cấu trúc phân cấp)
- Category có thể có parent (danh mục cha)
- Category có thể có nhiều children (danh mục con)
- Không giới hạn độ sâu (unlimited depth)
- Example hierarchy:
  ```
  Electronics (root)
  ├── Laptops
  │   ├── Gaming Laptops
  │   ├── Business Laptops
  │   └── Ultrabooks
  ├── Smartphones
  └── Tablets
  Accessories (root)
  ├── Phone Accessories
  └── Laptop Accessories
  ```

### 🔗 Self-Referencing Relationship
- ManyToOne: `parent` - Reference đến category cha
- OneToMany: `children` - Collection các category con
- Cascade delete: `CascadeType.ALL` + `orphanRemoval = true`

### ✅ Validation

#### Unique Slug
- Mỗi category phải có slug duy nhất
- Validate khi CREATE
- Validate khi UPDATE (nếu thay đổi slug)
- Error message: "Category with slug 'xxx' already exists"

#### Prevent Self-Parent
- Category không thể là parent của chính nó
- Error: "Category cannot be its own parent"

#### Prevent Circular Reference
- Category không thể set một descendant (con/cháu) làm parent
- Example: Nếu A là cha của B, và B là cha của C, thì C không thể set A hoặc B làm parent
- Error: "Cannot set a descendant category as parent (circular reference)"

#### Parent Existence
- Parent category phải tồn tại nếu cung cấp parentId
- Error: "Parent category not found with id: xxx"

### 🗑️ Cascade Delete
- Khi xóa category cha → tự động xóa tất cả children
- Khi xóa category → tự động xóa tất cả descendants (con, cháu, chắt...)
- JPA handles cascade with `CascadeType.ALL` và `orphanRemoval = true`
- Products trong category bị xóa không bị xóa (chỉ set `categoryId = null`)

### 📊 Product Count
- Mỗi category response bao gồm số lượng products
- Count được tính real-time từ database
- Chỉ count products trực tiếp trong category đó (không bao gồm subcategories)

### 🔍 SEO Slug
- Mỗi category có slug duy nhất
- Slug dùng cho SEO-friendly URLs
- Có thể query category theo slug: `GET /api/categories/slug/{slug}`

## Database Schema

### categories Table
```sql
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    parent_id BIGINT,
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE CASCADE
);
```

**Indexes**:
- Primary key: `id`
- Unique: `slug`
- Foreign key: `parent_id` (self-reference)

## Sample Data

### Hierarchical Category Structure

```
Electronics (id=1, root)
├── Laptops (id=3)
│   ├── Gaming Laptops (id=6)
│   ├── Business Laptops (id=7)
│   └── Ultrabooks (id=8)
├── Smartphones (id=4)
└── Tablets (id=5)

Accessories (id=2, root)
├── Phone Accessories (id=9)
└── Laptop Accessories (id=10)
```

### Products Assignment
- Laptop Dell XPS 13 → Ultrabooks (id=8)
- iPhone 13 Pro → Smartphones (id=4)
- Samsung Galaxy S21 → Smartphones (id=4)
- MacBook Pro 14 → Business Laptops (id=7)
- iPad Air → Tablets (id=5)
- ASUS ROG Strix → Gaming Laptops (id=6)
- AirPods Pro → Phone Accessories (id=9)

## Testing Guide

### 1. Get All Categories (Flat List)
```bash
curl http://localhost:8080/api/categories
```

### 2. Get Category Tree
```bash
curl http://localhost:8080/api/categories?tree=true
```

### 3. Get Single Category
```bash
curl http://localhost:8080/api/categories/1
```

### 4. Get Category by Slug
```bash
curl http://localhost:8080/api/categories/slug/electronics
```

### 5. Get Children
```bash
curl http://localhost:8080/api/categories/1/children
```

### 6. Create Root Category
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Home Appliances",
    "slug": "home-appliances",
    "description": "Appliances for home"
  }'
```

### 7. Create Child Category
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Kitchen Appliances",
    "slug": "kitchen-appliances",
    "description": "Appliances for kitchen",
    "parentId": 11
  }'
```

### 8. Update Category
```bash
curl -X PUT http://localhost:8080/api/categories/3 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Notebooks",
    "description": "Updated description"
  }'
```

### 9. Move Category to Different Parent
```bash
curl -X PUT http://localhost:8080/api/categories/6 \
  -H "Content-Type: application/json" \
  -d '{
    "parentId": 5
  }'
```

### 10. Delete Category (Cascade)
```bash
# This will delete category 3 and all its children (6, 7, 8)
curl -X DELETE http://localhost:8080/api/categories/3
```

### 11. Test Unique Slug Validation
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Another Electronics",
    "slug": "electronics"
  }'
# Expected: 400 Bad Request
```

### 12. Test Circular Reference Prevention
```bash
# Try to set child as parent of its parent
curl -X PUT http://localhost:8080/api/categories/1 \
  -H "Content-Type: application/json" \
  -d '{
    "parentId": 3
  }'
# Expected: 400 Bad Request (circular reference)
```

## DTOs

### CategoryDto (Response)
```java
{
  Long id;
  String name;
  String slug;
  String description;
  Long parentId;
  String parentName;
  List<CategoryDto> children;  // Nested children (null for flat list)
  Integer productCount;
}
```

### CreateCategoryRequest
```java
{
  @NotBlank String name;
  @NotBlank String slug;
  String description;
  Long parentId;  // null = root category
}
```

### UpdateCategoryRequest
```java
{
  String name;
  String slug;
  String description;
  Long parentId;
}
```

## Architecture

```
┌─────────────────────────────────────┐
│      CategoryController             │
│  - GET /api/categories              │
│  - GET /api/categories/{id}         │
│  - GET /api/categories/slug/{slug}  │
│  - GET /api/categories/{id}/children│
│  - POST /api/categories             │
│  - PUT /api/categories/{id}         │
│  - DELETE /api/categories/{id}      │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│      CategoryServiceImpl            │
│  - CRUD operations                  │
│  - Hierarchy building               │
│  - Slug validation                  │
│  - Circular reference prevention    │
│  - DTO conversion                   │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│      CategoryRepository             │
│  - findBySlug()                     │
│  - existsBySlug()                   │
│  - findRootCategories()             │
│  - findByParentId()                 │
└──────────────┬──────────────────────┘
               │
               ▼
         ┌───────────┐
         │   H2 DB   │
         │ categories│
         └───────────┘
```

## Error Handling

### 404 Not Found
- Category không tồn tại (by ID or slug)
- Parent category không tồn tại

### 400 Bad Request
- Validation errors (missing required fields)
- Duplicate slug
- Self-parent attempt
- Circular reference
- Invalid data format

### 500 Internal Server Error
- Database errors
- Unexpected exceptions

## Notes

- ✅ Full CRUD operations
- ✅ Hierarchical parent-child structure
- ✅ Unlimited nesting depth
- ✅ Cascade delete (auto-delete children)
- ✅ Unique slug validation
- ✅ SEO-friendly slugs
- ✅ Circular reference prevention
- ✅ Product count per category
- ✅ Flat list và tree view
- ✅ Comprehensive validation
- ✅ DTO pattern for clean API

## Future Enhancements

- [ ] Breadcrumb generation (path from root to current)
- [ ] Category reordering (sort order)
- [ ] Category image/icon
- [ ] Category visibility toggle
- [ ] Featured categories
- [ ] Category metadata for SEO
- [ ] Bulk operations
- [ ] Category merge functionality
- [ ] Category move with drag-and-drop
- [ ] Category statistics dashboard
