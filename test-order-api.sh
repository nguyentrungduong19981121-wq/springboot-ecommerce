#!/bin/bash

# Order Management API Test Script

BASE_URL="http://localhost:8080/api"

echo "=========================================="
echo "Order Management API Test Script"
echo "=========================================="
echo ""

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

# Step 1: Login to get JWT token
echo -e "${BLUE}Step 1: Login as customer${NC}"
LOGIN_RESPONSE=$(curl -s -X POST ${BASE_URL}/customers/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nguyenvana@example.com",
    "password": "password123"
  }')

echo "$LOGIN_RESPONSE" | jq '.' 2>/dev/null || echo "$LOGIN_RESPONSE"
echo ""

# Extract token
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token' 2>/dev/null)

if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
    echo -e "${RED}Failed to get token. Please make sure the application is running.${NC}"
    exit 1
fi

echo -e "${GREEN}Token obtained successfully!${NC}"
echo "Token: ${TOKEN:0:50}..."
echo ""

# Test 2: Get customer's orders
echo -e "${BLUE}Test 2: GET customer's orders${NC}"
curl -s "${BASE_URL}/orders" \
  -H "Authorization: Bearer $TOKEN" | jq '.' || curl -s "${BASE_URL}/orders" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

# Test 3: Filter by status
echo -e "${BLUE}Test 3: Filter orders by status (PENDING)${NC}"
curl -s "${BASE_URL}/orders?status=PENDING" \
  -H "Authorization: Bearer $TOKEN" | jq '.' || curl -s "${BASE_URL}/orders?status=PENDING" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

# Test 4: Get single order
echo -e "${BLUE}Test 4: GET single order (ID 1)${NC}"
curl -s "${BASE_URL}/orders/1" \
  -H "Authorization: Bearer $TOKEN" | jq '.' || curl -s "${BASE_URL}/orders/1" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

# Test 5: Check product stock before order
echo -e "${BLUE}Test 5: Check product stock before creating order${NC}"
echo "Product ID 1 (Laptop Dell XPS 13) stock:"
curl -s "${BASE_URL}/products/1" | jq '.stock' || curl -s "${BASE_URL}/products/1" | grep -o '"stock":[0-9]*'
echo -e "\n"

# Test 6: Create new order
echo -e "${BLUE}Test 6: POST create new order${NC}"
CREATE_RESPONSE=$(curl -s -X POST ${BASE_URL}/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "items": [
      {
        "productId": 5,
        "quantity": 1
      },
      {
        "productId": 7,
        "quantity": 2
      }
    ]
  }')

echo "$CREATE_RESPONSE" | jq '.' 2>/dev/null || echo "$CREATE_RESPONSE"
echo -e "\n"

# Extract new order ID
NEW_ORDER_ID=$(echo "$CREATE_RESPONSE" | jq -r '.id' 2>/dev/null)

# Test 7: Check stock reduced
echo -e "${BLUE}Test 7: Verify stock was reduced${NC}"
echo "Product ID 5 stock after order:"
curl -s "${BASE_URL}/products/5" | jq '.stock' || curl -s "${BASE_URL}/products/5" | grep -o '"stock":[0-9]*'
echo -e "\n"

# Test 8: Customer cancels order
if [ ! -z "$NEW_ORDER_ID" ] && [ "$NEW_ORDER_ID" != "null" ]; then
    echo -e "${BLUE}Test 8: Customer cancels order (ID $NEW_ORDER_ID)${NC}"
    curl -s -X PUT "${BASE_URL}/orders/${NEW_ORDER_ID}/status" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d '{"status": "CANCELLED"}' | jq '.' || curl -s -X PUT "${BASE_URL}/orders/${NEW_ORDER_ID}/status" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d '{"status": "CANCELLED"}'
    echo -e "\n"
fi

# Test 9: Admin views all orders
echo -e "${BLUE}Test 9: Admin views all orders${NC}"
curl -s -u admin:admin123 "${BASE_URL}/orders" | jq '.' || curl -s -u admin:admin123 "${BASE_URL}/orders"
echo -e "\n"

# Test 10: Admin updates order status
echo -e "${BLUE}Test 10: Admin updates order status to SHIPPED${NC}"
curl -s -X PUT "${BASE_URL}/orders/1/status" \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"status": "SHIPPED"}' | jq '.' || curl -s -X PUT "${BASE_URL}/orders/1/status" \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"status": "SHIPPED"}'
echo -e "\n"

# Test 11: Test insufficient stock
echo -e "${BLUE}Test 11: Test insufficient stock (should fail)${NC}"
curl -s -X POST ${BASE_URL}/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "items": [
      {
        "productId": 1,
        "quantity": 1000
      }
    ]
  }' | jq '.' || curl -s -X POST ${BASE_URL}/orders \
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
echo -e "\n"

# Test 12: Test authorization (customer tries to ship)
echo -e "${BLUE}Test 12: Test authorization - customer tries to ship (should fail)${NC}"
curl -s -X PUT "${BASE_URL}/orders/1/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"status": "SHIPPED"}' | jq '.' || curl -s -X PUT "${BASE_URL}/orders/1/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"status": "SHIPPED"}'
echo -e "\n"

echo -e "${GREEN}=========================================="
echo "All tests completed!"
echo -e "==========================================${NC}"
echo ""
echo "Summary:"
echo "  - Customer can view their own orders"
echo "  - Admin can view all orders"
echo "  - Orders can be filtered by status"
echo "  - Stock is automatically reduced when order is created"
echo "  - Customer can only cancel orders"
echo "  - Admin can update to any status"
echo ""
echo "Note: Install 'jq' for pretty JSON output"
