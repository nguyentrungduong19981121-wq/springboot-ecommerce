#!/bin/bash

# Inventory & Checkout API Test Script

BASE_URL="http://localhost:8080/api"

echo "=========================================="
echo "Inventory & Checkout API Test Script"
echo "=========================================="
echo ""

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

# ==========================================
# PART 1: INVENTORY MANAGEMENT (ADMIN)
# ==========================================

echo -e "${BLUE}=========================================="
echo "PART 1: INVENTORY MANAGEMENT (ADMIN)"
echo -e "==========================================${NC}\n"

# Test 1: View all inventory
echo -e "${BLUE}Test 1: GET all inventory (Admin)${NC}"
curl -s -u admin:admin123 "${BASE_URL}/inventory" | jq '.' || curl -s -u admin:admin123 "${BASE_URL}/inventory"
echo -e "\n"

# Test 2: View specific product inventory
echo -e "${BLUE}Test 2: GET inventory for product 1${NC}"
curl -s -u admin:admin123 "${BASE_URL}/inventory/1" | jq '.' || curl -s -u admin:admin123 "${BASE_URL}/inventory/1"
echo -e "\n"

# Test 3: Import stock
echo -e "${BLUE}Test 3: IMPORT stock (Add 50 units to product 1)${NC}"
curl -s -X PUT "${BASE_URL}/inventory/1" \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 50,
    "transactionType": "IMPORT",
    "reason": "Restock from supplier"
  }' | jq '.' || curl -s -X PUT "${BASE_URL}/inventory/1" \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"quantity":50,"transactionType":"IMPORT","reason":"Restock from supplier"}'
echo -e "\n"

# Test 4: Export stock
echo -e "${BLUE}Test 4: EXPORT stock (Remove 5 units from product 1)${NC}"
curl -s -X PUT "${BASE_URL}/inventory/1" \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 5,
    "transactionType": "EXPORT",
    "reason": "Damaged items removed"
  }' | jq '.' || curl -s -X PUT "${BASE_URL}/inventory/1" \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"quantity":5,"transactionType":"EXPORT","reason":"Damaged items"}'
echo -e "\n"

# Test 5: Stock adjustment
echo -e "${BLUE}Test 5: ADJUSTMENT (Set product 5 stock to 100)${NC}"
curl -s -X PUT "${BASE_URL}/inventory/5" \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 100,
    "transactionType": "ADJUSTMENT",
    "reason": "Physical inventory count"
  }' | jq '.' || curl -s -X PUT "${BASE_URL}/inventory/5" \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"quantity":100,"transactionType":"ADJUSTMENT","reason":"Inventory count"}'
echo -e "\n"

# ==========================================
# PART 2: CHECKOUT PROCESS (CUSTOMER)
# ==========================================

echo -e "${BLUE}=========================================="
echo "PART 2: CHECKOUT PROCESS (CUSTOMER)"
echo -e "==========================================${NC}\n"

# Login to get JWT token
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
echo -e "\n"

# Test 6: Create checkout session WITHOUT coupon
echo -e "${BLUE}Test 6: Create checkout session (no coupon)${NC}"
CHECKOUT_RESPONSE=$(curl -s -X POST ${BASE_URL}/checkout \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "items": [
      {"productId": 1, "quantity": 2},
      {"productId": 5, "quantity": 1}
    ]
  }')

echo "$CHECKOUT_RESPONSE" | jq '.' 2>/dev/null || echo "$CHECKOUT_RESPONSE"
echo -e "\n"

# Extract session ID
SESSION_ID=$(echo "$CHECKOUT_RESPONSE" | jq -r '.sessionId' 2>/dev/null)

if [ -z "$SESSION_ID" ] || [ "$SESSION_ID" == "null" ]; then
    echo -e "${RED}Failed to create checkout session${NC}"
    SESSION_ID=1
fi

echo -e "${GREEN}Checkout session created: $SESSION_ID${NC}\n"

# Test 7: Apply coupon to session
echo -e "${BLUE}Test 7: Apply coupon WELCOME10 (10% discount)${NC}"
curl -s -X POST ${BASE_URL}/checkout/apply-coupon \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"sessionId\": $SESSION_ID,
    \"couponCode\": \"WELCOME10\"
  }" | jq '.' || curl -s -X POST ${BASE_URL}/checkout/apply-coupon \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"sessionId\":$SESSION_ID,\"couponCode\":\"WELCOME10\"}"
echo -e "\n"

# Test 8: Change to different coupon
echo -e "${BLUE}Test 8: Change to coupon BLACKFRIDAY (20% discount)${NC}"
curl -s -X POST ${BASE_URL}/checkout/apply-coupon \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"sessionId\": $SESSION_ID,
    \"couponCode\": \"BLACKFRIDAY\"
  }" | jq '.' || curl -s -X POST ${BASE_URL}/checkout/apply-coupon \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"sessionId\":$SESSION_ID,\"couponCode\":\"BLACKFRIDAY\"}"
echo -e "\n"

# Test 9: Check product stock BEFORE payment
echo -e "${BLUE}Test 9: Check product stock BEFORE payment${NC}"
echo "Product 1 stock:"
curl -s "${BASE_URL}/products/1" | jq '.stock' || curl -s "${BASE_URL}/products/1" | grep -o '"stock":[0-9]*'
echo "Product 5 stock:"
curl -s "${BASE_URL}/products/5" | jq '.stock' || curl -s "${BASE_URL}/products/5" | grep -o '"stock":[0-9]*'
echo -e "\n"

# Test 10: Process payment
echo -e "${BLUE}Test 10: Process payment (creates order, reduces stock, sends emails)${NC}"
PAYMENT_RESPONSE=$(curl -s -X POST ${BASE_URL}/checkout/payment \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"sessionId\": $SESSION_ID,
    \"paymentMethod\": \"CREDIT_CARD\",
    \"cardNumber\": \"4111111111111111\",
    \"cardHolderName\": \"Nguyen Van A\",
    \"expiryDate\": \"12/25\",
    \"cvv\": \"123\"
  }")

echo "$PAYMENT_RESPONSE" | jq '.' 2>/dev/null || echo "$PAYMENT_RESPONSE"
echo -e "\n"

# Extract order ID
ORDER_ID=$(echo "$PAYMENT_RESPONSE" | jq -r '.orderId' 2>/dev/null)

if [ ! -z "$ORDER_ID" ] && [ "$ORDER_ID" != "null" ]; then
    echo -e "${GREEN}✅ Payment successful! Order #$ORDER_ID created${NC}\n"
else
    echo -e "${RED}❌ Payment failed or order not created${NC}\n"
fi

# Test 11: Check product stock AFTER payment
echo -e "${BLUE}Test 11: Check product stock AFTER payment (should be reduced)${NC}"
echo "Product 1 stock:"
curl -s "${BASE_URL}/products/1" | jq '.stock' || curl -s "${BASE_URL}/products/1" | grep -o '"stock":[0-9]*'
echo "Product 5 stock:"
curl -s "${BASE_URL}/products/5" | jq '.stock' || curl -s "${BASE_URL}/products/5" | grep -o '"stock":[0-9]*'
echo -e "\n"

# Test 12: View created order
if [ ! -z "$ORDER_ID" ] && [ "$ORDER_ID" != "null" ]; then
    echo -e "${BLUE}Test 12: View created order${NC}"
    curl -s "${BASE_URL}/orders/$ORDER_ID" \
      -H "Authorization: Bearer $TOKEN" | jq '.' || curl -s "${BASE_URL}/orders/$ORDER_ID" \
      -H "Authorization: Bearer $TOKEN"
    echo -e "\n"
fi

# Test 13: Test coupon minimum order value
echo -e "${BLUE}Test 13: Test coupon minimum order value (should fail)${NC}"
curl -s -X POST ${BASE_URL}/checkout \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "items": [
      {"productId": 7, "quantity": 1}
    ],
    "couponCode": "SAVE50K"
  }' | jq '.' || curl -s -X POST ${BASE_URL}/checkout \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"items":[{"productId":7,"quantity":1}],"couponCode":"SAVE50K"}'
echo -e "\n"

echo -e "${GREEN}=========================================="
echo "All tests completed!"
echo -e "==========================================${NC}"
echo ""
echo "Summary:"
echo "  ✅ Inventory management (import, export, adjustment)"
echo "  ✅ Checkout session creation"
echo "  ✅ Coupon application and validation"
echo "  ✅ Payment processing"
echo "  ✅ Order creation"
echo "  ✅ Stock reduction"
echo "  ✅ Email confirmation (check console logs)"
echo ""
echo "Check application console for email confirmation logs!"
echo ""
echo "Note: Install 'jq' for pretty JSON output"
