#!/bin/bash

# Category Management API Test Script
# This script tests all Category Management endpoints

BASE_URL="http://localhost:8080/api/categories"

echo "=========================================="
echo "Category Management API Test Script"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Test 1: Get all categories (flat list)
echo -e "${BLUE}Test 1: GET all categories (flat list)${NC}"
curl -s $BASE_URL | jq '.' || curl -s $BASE_URL
echo -e "\n"

# Test 2: Get category tree
echo -e "${BLUE}Test 2: GET category tree (hierarchical)${NC}"
curl -s "${BASE_URL}?tree=true" | jq '.' || curl -s "${BASE_URL}?tree=true"
echo -e "\n"

# Test 3: Get single category by ID
echo -e "${BLUE}Test 3: GET category by ID (1 - Electronics)${NC}"
curl -s "${BASE_URL}/1" | jq '.' || curl -s "${BASE_URL}/1"
echo -e "\n"

# Test 4: Get category by slug
echo -e "${BLUE}Test 4: GET category by slug (electronics)${NC}"
curl -s "${BASE_URL}/slug/electronics" | jq '.' || curl -s "${BASE_URL}/slug/electronics"
echo -e "\n"

# Test 5: Get children of a category
echo -e "${BLUE}Test 5: GET children of category (1 - Electronics)${NC}"
curl -s "${BASE_URL}/1/children" | jq '.' || curl -s "${BASE_URL}/1/children"
echo -e "\n"

# Test 6: Create root category
echo -e "${BLUE}Test 6: POST create root category${NC}"
curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Home Appliances",
    "slug": "home-appliances",
    "description": "Appliances for home use"
  }' | jq '.' || curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Home Appliances",
    "slug": "home-appliances",
    "description": "Appliances for home use"
  }'
echo -e "\n"

# Test 7: Create child category
echo -e "${BLUE}Test 7: POST create child category${NC}"
curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Kitchen Appliances",
    "slug": "kitchen-appliances",
    "description": "Appliances for kitchen",
    "parentId": 11
  }' | jq '.' || curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Kitchen Appliances",
    "slug": "kitchen-appliances",
    "description": "Appliances for kitchen",
    "parentId": 11
  }'
echo -e "\n"

# Test 8: Update category
echo -e "${BLUE}Test 8: PUT update category (ID 11)${NC}"
curl -s -X PUT "${BASE_URL}/11" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Home & Kitchen Appliances",
    "description": "Updated description for home appliances"
  }' | jq '.' || curl -s -X PUT "${BASE_URL}/11" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Home & Kitchen Appliances",
    "description": "Updated description for home appliances"
  }'
echo -e "\n"

# Test 9: Test duplicate slug validation
echo -e "${BLUE}Test 9: Test duplicate slug (should fail with 400)${NC}"
curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Another Electronics",
    "slug": "electronics"
  }' | jq '.' || curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Another Electronics",
    "slug": "electronics"
  }'
echo -e "\n"

# Test 10: Test circular reference prevention
echo -e "${BLUE}Test 10: Test circular reference (should fail with 400)${NC}"
echo "Try to set Electronics (1) parent to Laptops (3) - should fail"
curl -s -X PUT "${BASE_URL}/1" \
  -H "Content-Type: application/json" \
  -d '{
    "parentId": 3
  }' | jq '.' || curl -s -X PUT "${BASE_URL}/1" \
  -H "Content-Type: application/json" \
  -d '{
    "parentId": 3
  }'
echo -e "\n"

# Test 11: Get 404 for non-existent category
echo -e "${BLUE}Test 11: GET non-existent category (should return 404)${NC}"
curl -s "${BASE_URL}/999" | jq '.' || curl -s "${BASE_URL}/999"
echo -e "\n"

# Test 12: Delete category (optional - uncomment to test cascade delete)
# echo -e "${BLUE}Test 12: DELETE category (cascade delete children)${NC}"
# echo "WARNING: This will delete category 11 and all its children!"
# read -p "Press Enter to continue or Ctrl+C to cancel..."
# curl -s -X DELETE "${BASE_URL}/11" -w "\nHTTP Status: %{http_code}\n"
# echo -e "\n"

# Test 13: View category tree after changes
echo -e "${BLUE}Test 13: GET category tree after all operations${NC}"
curl -s "${BASE_URL}?tree=true" | jq '.' || curl -s "${BASE_URL}?tree=true"
echo -e "\n"

echo -e "${GREEN}=========================================="
echo "All tests completed!"
echo -e "==========================================${NC}"
echo ""
echo "Note: Install 'jq' for pretty JSON output"
echo "  Ubuntu/Debian: sudo apt-get install jq"
echo "  Mac: brew install jq"
echo ""
echo "Category Hierarchy:"
echo "  Electronics (1)"
echo "    ├── Laptops (3)"
echo "    │   ├── Gaming Laptops (6)"
echo "    │   ├── Business Laptops (7)"
echo "    │   └── Ultrabooks (8)"
echo "    ├── Smartphones (4)"
echo "    └── Tablets (5)"
echo "  Accessories (2)"
echo "    ├── Phone Accessories (9)"
echo "    └── Laptop Accessories (10)"
echo "  Home Appliances (11) - newly created"
echo "    └── Kitchen Appliances (12) - newly created"
