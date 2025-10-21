# Quick Start Guide - Admin Dashboard

## Bước 1: Cài đặt Redis

### Option 1: Redis trên Linux/Mac
```bash
# Install Redis (Ubuntu/Debian)
sudo apt-get update
sudo apt-get install redis-server

# Start Redis
redis-server

# Verify Redis is running
redis-cli ping
# Expected output: PONG
```

### Option 2: Redis với Docker
```bash
# Pull và run Redis container
docker run -d --name redis-ecommerce -p 6379:6379 redis:latest

# Verify
docker ps | grep redis
```

### Option 3: Redis trên Windows
```bash
# Download Redis for Windows from:
# https://github.com/microsoftarchive/redis/releases

# Or use WSL2 với Option 1
```

## Bước 2: Build và Run Application

```bash
cd /workspace

# Build project
./mvnw clean install

# Run application
./mvnw spring-boot:run
```

**Application sẽ chạy tại**: `http://localhost:8080`

## Bước 3: Test API Endpoints

### Test 1: Dashboard Overview
```bash
curl -u admin:admin123 http://localhost:8080/api/admin/dashboard
```

**Expected Response** (with sample data):
```json
{
  "totalRevenue": 460000.0,
  "totalOrders": 10,
  "newCustomersToday": 2,
  "newCustomersThisMonth": 6,
  "bestSellingProducts": [
    {
      "productId": 5,
      "productName": "iPad Air",
      "quantitySold": 6,
      "totalRevenue": 90000.0
    },
    {
      "productId": 1,
      "productName": "Laptop Dell XPS 13",
      "quantitySold": 4,
      "totalRevenue": 120000.0
    },
    {
      "productId": 2,
      "productName": "iPhone 13 Pro",
      "quantitySold": 4,
      "totalRevenue": 100000.0
    }
  ]
}
```

### Test 2: Daily Sales Report
```bash
curl -u admin:admin123 "http://localhost:8080/api/admin/reports/sales?period=daily&startDate=2025-10-01&endDate=2025-10-20"
```

**Expected Response**:
```json
{
  "period": "daily",
  "startDate": "01/10/2025",
  "endDate": "20/10/2025",
  "salesData": [
    {
      "date": "01/10/2025",
      "revenue": 60000.0,
      "orderCount": 1
    },
    {
      "date": "02/10/2025",
      "revenue": 25000.0,
      "orderCount": 1
    },
    {
      "date": "03/10/2025",
      "revenue": 35000.0,
      "orderCount": 1
    }
    // ... more data points
  ]
}
```

### Test 3: Monthly Sales Report
```bash
curl -u admin:admin123 "http://localhost:8080/api/admin/reports/sales?period=monthly&startDate=2025-01-01&endDate=2025-12-31"
```

### Test 4: Test Cache (call lại endpoint sau vài giây)
```bash
# First call - data from database
time curl -u admin:admin123 http://localhost:8080/api/admin/dashboard

# Second call - data from Redis cache (should be faster)
time curl -u admin:admin123 http://localhost:8080/api/admin/dashboard
```

### Test 5: Verify Redis Cache
```bash
# Connect to Redis
redis-cli

# List all keys
KEYS *

# Expected output should include:
# 1) "dashboardStats"
# 2) "salesReport::daily_2025-10-01_2025-10-20"

# Get cache value
GET dashboardStats

# Check TTL (Time To Live)
TTL dashboardStats
# Returns remaining seconds (should be around 600)
```

## Bước 4: Test với Postman

### Import Collection

**Dashboard Endpoint:**
- Method: GET
- URL: `http://localhost:8080/api/admin/dashboard`
- Auth: Basic Auth
  - Username: `admin`
  - Password: `admin123`

**Sales Report Endpoint:**
- Method: GET
- URL: `http://localhost:8080/api/admin/reports/sales`
- Auth: Basic Auth
  - Username: `admin`
  - Password: `admin123`
- Params:
  - `period`: `daily` or `monthly`
  - `startDate`: `2025-10-01`
  - `endDate`: `2025-10-20`

## Bước 5: Access H2 Console (Optional)

```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:ecommercedb
Username: sa
Password: (leave empty)
```

### Useful SQL Queries

**View all orders:**
```sql
SELECT * FROM orders;
```

**View all customers:**
```sql
SELECT * FROM customers;
```

**View order details:**
```sql
SELECT o.id, o.date_created, p.name, op.quantity, p.price
FROM orders o
JOIN order_product op ON o.id = op.order_id
JOIN product p ON op.product_id = p.id
ORDER BY o.date_created DESC;
```

**Calculate total revenue:**
```sql
SELECT SUM(op.quantity * p.price) as total_revenue
FROM order_product op
JOIN product p ON op.product_id = p.id;
```

## Sample Data

Application đã được configure để auto-load sample data từ `data.sql`:
- 5 Products
- 6 Customers (2 registered today)
- 10 Orders with various products
- Total Revenue: 460,000

## Troubleshooting

### Error: "Unable to connect to Redis"
```bash
# Check if Redis is running
redis-cli ping

# If not running, start Redis
redis-server

# Or with Docker
docker start redis-ecommerce
```

### Error: "Access Denied" (401 Unauthorized)
- Verify you're using correct credentials: `admin:admin123`
- Check Basic Auth header is set correctly

### Error: "Forbidden" (403)
- User role is not ADMIN
- Use `admin` user instead of `user`

### Cache not working
```bash
# Verify Redis connection
redis-cli
> PING
PONG

# Check application logs for Redis connection errors
./mvnw spring-boot:run | grep -i redis
```

### No data returned (empty arrays/zero values)
- Check if sample data loaded: Visit H2 console
- Verify `data.sql` file exists in `src/main/resources/`
- Check application logs during startup

## Performance Testing

### Test Cache Performance
```bash
# Install Apache Bench (ab) if not available
sudo apt-get install apache2-utils

# Test without auth (will fail but shows timing)
ab -n 100 -c 10 http://localhost:8080/api/admin/dashboard

# Test with auth (create a separate script)
# See: test-performance.sh
```

### Create `test-performance.sh`:
```bash
#!/bin/bash

echo "Testing Dashboard Endpoint Performance..."

# Clear Redis cache first
redis-cli FLUSHALL

echo "First call (no cache):"
time curl -s -u admin:admin123 http://localhost:8080/api/admin/dashboard > /dev/null

echo -e "\nSecond call (with cache):"
time curl -s -u admin:admin123 http://localhost:8080/api/admin/dashboard > /dev/null

echo -e "\nThird call (with cache):"
time curl -s -u admin:admin123 http://localhost:8080/api/admin/dashboard > /dev/null
```

## Next Steps

1. ✅ Test all endpoints with sample data
2. ✅ Verify Redis caching is working
3. ✅ Check security (try with user:user123, should get 403)
4. 🔄 Integrate with Angular frontend
5. 🔄 Add more sample data if needed
6. 🔄 Deploy to production environment

## Production Considerations

### Redis Configuration
- Set up Redis Sentinel for high availability
- Configure Redis password in production
- Use Redis Cluster for scaling

### Security
- Replace in-memory users with database-backed users
- Add JWT tokens for stateless authentication
- Implement rate limiting
- Enable HTTPS

### Monitoring
- Set up Spring Boot Actuator
- Configure Prometheus metrics
- Add logging with ELK stack
- Monitor Redis memory usage

## Support

For issues or questions:
1. Check logs: `./mvnw spring-boot:run`
2. Review documentation: `ADMIN_DASHBOARD_README.md`
3. Check implementation: `IMPLEMENTATION_SUMMARY.md`
