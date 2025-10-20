# Admin Dashboard Feature

## Tổng quan
Feature Admin Dashboard cung cấp các API endpoints để quản trị viên xem thống kê tổng quan về hệ thống e-commerce, bao gồm doanh thu, đơn hàng, khách hàng và sản phẩm bán chạy.

## Entities

### 1. Customer (Mới)
```java
- id: Long (Primary Key)
- name: String (Required)
- email: String (Required, Email format)
- phone: String
- registeredDate: LocalDate
```

### 2. Order (Đã có)
```java
- id: Long
- dateCreated: LocalDate
- status: String
- orderProducts: List<OrderProduct>
```

### 3. Product (Đã có)
```java
- id: Long
- name: String
- price: Double
- pictureUrl: String
```

## API Endpoints

### 1. GET /api/admin/dashboard
**Mô tả**: Trả về thống kê tổng quan hệ thống

**Yêu cầu**: Phải đăng nhập với role ADMIN

**Response**:
```json
{
  "totalRevenue": 150000.0,
  "totalOrders": 45,
  "newCustomersToday": 3,
  "newCustomersThisMonth": 28,
  "bestSellingProducts": [
    {
      "productId": 1,
      "productName": "Laptop Dell XPS 13",
      "quantitySold": 25,
      "totalRevenue": 75000.0
    },
    {
      "productId": 2,
      "productName": "iPhone 13 Pro",
      "quantitySold": 20,
      "totalRevenue": 50000.0
    }
  ]
}
```

**Cache**: Kết quả được cache 10 phút trong Redis

### 2. GET /api/admin/reports/sales
**Mô tả**: Trả về biểu đồ doanh thu theo ngày hoặc tháng

**Yêu cầu**: Phải đăng nhập với role ADMIN

**Parameters**:
- `period` (optional, default: "daily"): "daily" hoặc "monthly"
- `startDate` (required): Ngày bắt đầu (format: yyyy-MM-dd)
- `endDate` (required): Ngày kết thúc (format: yyyy-MM-dd)

**Example Request**:
```
GET /api/admin/reports/sales?period=daily&startDate=2025-10-01&endDate=2025-10-20
```

**Response**:
```json
{
  "period": "daily",
  "startDate": "01/10/2025",
  "endDate": "20/10/2025",
  "salesData": [
    {
      "date": "01/10/2025",
      "revenue": 5000.0,
      "orderCount": 3
    },
    {
      "date": "02/10/2025",
      "revenue": 7500.0,
      "orderCount": 5
    }
  ]
}
```

**Cache**: Kết quả được cache 10 phút trong Redis với key theo format `{period}_{startDate}_{endDate}`

## Xác thực & Phân quyền

### Security Configuration
- Framework: Spring Security
- Authentication: HTTP Basic Authentication
- Authorization: Role-based (ADMIN role required)

### Default Users
1. **Admin User**
   - Username: `admin`
   - Password: `admin123`
   - Role: `ADMIN`

2. **Regular User**
   - Username: `user`
   - Password: `user123`
   - Role: `USER`

### Truy cập API
Sử dụng HTTP Basic Authentication trong request header:
```bash
curl -u admin:admin123 http://localhost:8080/api/admin/dashboard
```

## Redis Caching

### Configuration
- Host: localhost
- Port: 6379
- Timeout: 60 seconds
- TTL: 10 minutes (600,000 ms)

### Cache Keys
1. **dashboardStats**: Cache cho endpoint dashboard overview
2. **salesReport**: Cache cho endpoint sales reports với key format `{period}_{startDate}_{endDate}`

### Lưu ý về Cache
- Cache được clear tự động sau 10 phút
- Cache không lưu giá trị null
- Sử dụng JSON serialization cho Redis values

## Dependencies Mới

Đã thêm vào `pom.xml`:
```xml
<!-- Spring Data Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Spring Cache -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

## Cách chạy

### Yêu cầu
1. Java 21
2. Maven
3. Redis Server (phải chạy trên localhost:6379)

### Khởi động Redis
```bash
# Linux/Mac
redis-server

# Docker
docker run -d -p 6379:6379 redis:latest
```

### Khởi động Application
```bash
./mvnw spring-boot:run
```

### Test API với curl

#### 1. Get Dashboard Overview
```bash
curl -u admin:admin123 http://localhost:8080/api/admin/dashboard
```

#### 2. Get Daily Sales Report
```bash
curl -u admin:admin123 "http://localhost:8080/api/admin/reports/sales?period=daily&startDate=2025-10-01&endDate=2025-10-20"
```

#### 3. Get Monthly Sales Report
```bash
curl -u admin:admin123 "http://localhost:8080/api/admin/reports/sales?period=monthly&startDate=2025-01-01&endDate=2025-12-31"
```

## Kiến trúc

```
controller/
├── AdminDashboardController.java    # REST endpoints

service/
├── AdminDashboardService.java       # Service interface
└── AdminDashboardServiceImpl.java   # Business logic + caching

repository/
├── CustomerRepository.java          # Customer data access
├── OrderRepository.java             # Order queries
└── ProductRepository.java           # Product statistics

model/
├── Customer.java                    # Customer entity
├── Order.java                       # Order entity
└── Product.java                     # Product entity

dto/
├── DashboardStatsDto.java           # Dashboard response DTO
├── BestSellingProductDto.java       # Best-selling product DTO
└── SalesReportDto.java              # Sales report DTO

config/
├── RedisConfig.java                 # Redis configuration
└── SecurityConfig.java              # Spring Security configuration
```

## Notes

1. **Performance**: Tất cả queries được tối ưu với caching Redis
2. **Security**: Tất cả admin endpoints yêu cầu ADMIN role
3. **Scalability**: Redis cache giúp giảm tải database cho các truy vấn phức tạp
4. **Data Integrity**: Sử dụng @Transactional để đảm bảo tính nhất quán
5. **Flexibility**: Sales report hỗ trợ cả daily và monthly aggregation

## Future Enhancements

- [ ] Export reports to PDF/Excel
- [ ] Real-time dashboard updates với WebSocket
- [ ] Advanced filtering options
- [ ] Custom date range presets (Last 7 days, Last month, etc.)
- [ ] Revenue comparison charts (Year over Year)
- [ ] Customer segmentation analytics
