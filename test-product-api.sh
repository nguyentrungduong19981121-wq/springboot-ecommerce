#!/bin/bash

# Product Management API Test Script
# This script tests all Product Management endpoints

BASE_URL="http://localhost:8080/api/products"

echo "=========================================="
echo "Product Management API Test Script"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Test 1: Get all products
echo -e "${BLUE}Test 1: GET all products${NC}"
curl -s $BASE_URL | jq '.' || curl -s $BASE_URL
echo -e "\n"

# Test 2: Search by name
echo -e "${BLUE}Test 2: Search by name (laptop)${NC}"
curl -s "${BASE_URL}?name=laptop" | jq '.' || curl -s "${BASE_URL}?name=laptop"
echo -e "\n"

# Test 3: Filter by category
echo -e "${BLUE}Test 3: Filter by category (Smartphones - ID 2)${NC}"
curl -s "${BASE_URL}?categoryId=2" | jq '.' || curl -s "${BASE_URL}?categoryId=2"
echo -e "\n"

# Test 4: Sort by price ascending
echo -e "${BLUE}Test 4: Sort by price (ascending)${NC}"
curl -s "${BASE_URL}?sortBy=price_asc" | jq '.' || curl -s "${BASE_URL}?sortBy=price_asc"
echo -e "\n"

# Test 5: Combined filters
echo -e "${BLUE}Test 5: Combined (search + filter + sort)${NC}"
curl -s "${BASE_URL}?name=phone&categoryId=2&sortBy=price_desc" | jq '.' || curl -s "${BASE_URL}?name=phone&categoryId=2&sortBy=price_desc"
echo -e "\n"

# Test 6: Get single product
echo -e "${BLUE}Test 6: GET product by ID (1)${NC}"
curl -s "${BASE_URL}/1" | jq '.' || curl -s "${BASE_URL}/1"
echo -e "\n"

# Test 7: Create new product
echo -e "${BLUE}Test 7: POST create new product${NC}"
curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "name": "AirPods Pro",
    "slug": "airpods-pro",
    "description": "Active Noise Cancellation wireless earbuds",
    "price": 5000.0,
    "stock": 50,
    "categoryId": 4,
    "pictureUrl": "https://example.com/airpods.jpg",
    "imageUrls": [
      "https://example.com/airpods-front.jpg",
      "https://example.com/airpods-case.jpg"
    ]
  }' | jq '.' || curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "name": "AirPods Pro",
    "slug": "airpods-pro",
    "description": "Active Noise Cancellation wireless earbuds",
    "price": 5000.0,
    "stock": 50,
    "categoryId": 4,
    "pictureUrl": "https://example.com/airpods.jpg",
    "imageUrls": [
      "https://example.com/airpods-front.jpg",
      "https://example.com/airpods-case.jpg"
    ]
  }'
echo -e "\n"

# Test 8: Update product
echo -e "${BLUE}Test 8: PUT update product (ID 6 - if created)${NC}"
curl -s -X PUT "${BASE_URL}/6" \
  -H "Content-Type: application/json" \
  -d '{
    "price": 4500.0,
    "stock": 60
  }' | jq '.' || curl -s -X PUT "${BASE_URL}/6" \
  -H "Content-Type: application/json" \
  -d '{
    "price": 4500.0,
    "stock": 60
  }'
echo -e "\n"

# Test 9: Test unique slug validation
echo -e "${BLUE}Test 9: Test duplicate slug (should fail with 400)${NC}"
curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Another Laptop",
    "slug": "laptop-dell-xps-13",
    "price": 25000.0
  }' | jq '.' || curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Another Laptop",
    "slug": "laptop-dell-xps-13",
    "price": 25000.0
  }'
echo -e "\n"

# Test 10: Test 404
echo -e "${BLUE}Test 10: GET non-existent product (should return 404)${NC}"
curl -s "${BASE_URL}/999" | jq '.' || curl -s "${BASE_URL}/999"
echo -e "\n"

# Test 11: Delete product (optional - uncomment to test)
# echo -e "${BLUE}Test 11: DELETE product (ID 6)${NC}"
# curl -s -X DELETE "${BASE_URL}/6" -w "\nHTTP Status: %{http_code}\n"
# echo -e "\n"

echo -e "${GREEN}=========================================="
echo "All tests completed!"
echo -e "==========================================${NC}"
echo ""
echo "Note: Install 'jq' for pretty JSON output"
echo "  Ubuntu/Debian: sudo apt-get install jq"
echo "  Mac: brew install jq"
