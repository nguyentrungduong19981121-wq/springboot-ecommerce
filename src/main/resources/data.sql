-- Sample data for testing Admin Dashboard, Product Management, and Category Management

-- Insert hierarchical categories
-- Root categories (no parent)
INSERT INTO categories (id, name, slug, description, parent_id) VALUES
(1, 'Electronics', 'electronics', 'All electronic devices and gadgets', NULL),
(2, 'Accessories', 'accessories', 'Accessories for electronic devices', NULL);

-- Level 1 categories (children of Electronics)
INSERT INTO categories (id, name, slug, description, parent_id) VALUES
(3, 'Laptops', 'laptops', 'High-performance laptops and notebooks', 1),
(4, 'Smartphones', 'smartphones', 'Latest smartphones and mobile devices', 1),
(5, 'Tablets', 'tablets', 'Tablets and iPad devices', 1);

-- Level 2 categories (children of Laptops)
INSERT INTO categories (id, name, slug, description, parent_id) VALUES
(6, 'Gaming Laptops', 'gaming-laptops', 'High-performance gaming laptops', 3),
(7, 'Business Laptops', 'business-laptops', 'Professional laptops for business', 3),
(8, 'Ultrabooks', 'ultrabooks', 'Thin and light ultrabook laptops', 3);

-- Level 1 categories (children of Accessories)
INSERT INTO categories (id, name, slug, description, parent_id) VALUES
(9, 'Phone Accessories', 'phone-accessories', 'Cases, chargers, and accessories for phones', 2),
(10, 'Laptop Accessories', 'laptop-accessories', 'Bags, mice, keyboards for laptops', 2);

-- Insert sample products with hierarchical categories
INSERT INTO product (id, name, slug, description, price, stock, picture_url, category_id) VALUES
(1, 'Laptop Dell XPS 13', 'laptop-dell-xps-13', 'Ultra-thin and powerful laptop with 13-inch display, Intel Core i7, 16GB RAM', 30000.0, 10, 'https://example.com/laptop.jpg', 8),
(2, 'iPhone 13 Pro', 'iphone-13-pro', 'Latest iPhone with A15 Bionic chip, Pro camera system, and Super Retina XDR display', 25000.0, 25, 'https://example.com/iphone.jpg', 4),
(3, 'Samsung Galaxy S21', 'samsung-galaxy-s21', 'Premium Android smartphone with 5G capability and professional-grade camera', 20000.0, 20, 'https://example.com/samsung.jpg', 4),
(4, 'MacBook Pro 14', 'macbook-pro-14', 'Apple MacBook Pro with M1 Pro chip, 14-inch Liquid Retina XDR display', 45000.0, 8, 'https://example.com/macbook.jpg', 7),
(5, 'iPad Air', 'ipad-air', 'Powerful and colorful iPad Air with M1 chip and 10.9-inch display', 15000.0, 15, 'https://example.com/ipad.jpg', 5),
(6, 'ASUS ROG Strix', 'asus-rog-strix', 'High-performance gaming laptop with RTX 3070, RGB keyboard', 35000.0, 12, 'https://example.com/asus-rog.jpg', 6),
(7, 'AirPods Pro', 'airpods-pro', 'Active Noise Cancellation wireless earbuds', 5000.0, 50, 'https://example.com/airpods.jpg', 9);

-- Insert sample product images
INSERT INTO product_images (id, url, product_id) VALUES
(1, 'https://example.com/laptop-front.jpg', 1),
(2, 'https://example.com/laptop-side.jpg', 1),
(3, 'https://example.com/iphone-gold.jpg', 2),
(4, 'https://example.com/iphone-silver.jpg', 2),
(5, 'https://example.com/samsung-black.jpg', 3),
(6, 'https://example.com/macbook-space-gray.jpg', 4),
(7, 'https://example.com/ipad-blue.jpg', 5),
(8, 'https://example.com/ipad-pink.jpg', 5);

-- Insert sample customers (password for all: "password123")
-- BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMye6J8YLYjPzQdBqKZk0whE6tpMKmQ9Cqu
INSERT INTO customers (id, name, email, password_hash, phone, registered_date) VALUES
(1, 'Nguyen Van A', 'nguyenvana@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye6J8YLYjPzQdBqKZk0whE6tpMKmQ9Cqu', '0901234567', '2025-10-01'),
(2, 'Tran Thi B', 'tranthib@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye6J8YLYjPzQdBqKZk0whE6tpMKmQ9Cqu', '0902345678', '2025-10-05'),
(3, 'Le Van C', 'levanc@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye6J8YLYjPzQdBqKZk0whE6tpMKmQ9Cqu', '0903456789', '2025-10-10'),
(4, 'Pham Thi D', 'phamthid@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye6J8YLYjPzQdBqKZk0whE6tpMKmQ9Cqu', '0904567890', '2025-10-15'),
(5, 'Hoang Van E', 'hoangvane@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye6J8YLYjPzQdBqKZk0whE6tpMKmQ9Cqu', '0905678901', '2025-10-20'),
(6, 'Vu Thi F', 'vuthif@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye6J8YLYjPzQdBqKZk0whE6tpMKmQ9Cqu', '0906789012', '2025-10-20');

-- Insert sample addresses
INSERT INTO addresses (id, street, city, postal_code, country, customer_id) VALUES
(1, '123 Nguyen Trai', 'Ho Chi Minh', '700000', 'Vietnam', 1),
(2, '456 Le Loi', 'Ho Chi Minh', '700000', 'Vietnam', 1),
(3, '789 Tran Hung Dao', 'Hanoi', '100000', 'Vietnam', 2),
(4, '321 Ly Thuong Kiet', 'Da Nang', '550000', 'Vietnam', 3),
(5, '654 Hai Ba Trung', 'Hanoi', '100000', 'Vietnam', 4);

-- Insert sample orders with customer relationship
INSERT INTO orders (id, customer_id, created_at, status) VALUES
(1, 1, '2025-10-01 10:30:00', 'PAID'),
(2, 2, '2025-10-02 14:15:00', 'PAID'),
(3, 1, '2025-10-03 09:45:00', 'SHIPPED'),
(4, 3, '2025-10-05 16:20:00', 'PAID'),
(5, 2, '2025-10-08 11:00:00', 'DELIVERED'),
(6, 4, '2025-10-10 13:30:00', 'PAID'),
(7, 1, '2025-10-12 15:45:00', 'SHIPPED'),
(8, 5, '2025-10-15 10:15:00', 'PAID'),
(9, 3, '2025-10-18 12:00:00', 'PENDING'),
(10, 6, '2025-10-20 09:30:00', 'PENDING');

-- Insert sample order products (order_product table)
-- Order 1: 2 laptops
INSERT INTO order_product (order_id, product_id, quantity) VALUES (1, 1, 2);

-- Order 2: 1 iPhone
INSERT INTO order_product (order_id, product_id, quantity) VALUES (2, 2, 1);

-- Order 3: 1 Samsung + 1 iPad
INSERT INTO order_product (order_id, product_id, quantity) VALUES (3, 3, 1);
INSERT INTO order_product (order_id, product_id, quantity) VALUES (3, 5, 1);

-- Order 4: 1 MacBook
INSERT INTO order_product (order_id, product_id, quantity) VALUES (4, 4, 1);

-- Order 5: 2 iPhones
INSERT INTO order_product (order_id, product_id, quantity) VALUES (5, 2, 2);

-- Order 6: 3 iPads
INSERT INTO order_product (order_id, product_id, quantity) VALUES (6, 5, 3);

-- Order 7: 1 Laptop + 1 iPhone
INSERT INTO order_product (order_id, product_id, quantity) VALUES (7, 1, 1);
INSERT INTO order_product (order_id, product_id, quantity) VALUES (7, 2, 1);

-- Order 8: 2 Samsung
INSERT INTO order_product (order_id, product_id, quantity) VALUES (8, 3, 2);

-- Order 9: 1 MacBook + 2 iPads
INSERT INTO order_product (order_id, product_id, quantity) VALUES (9, 4, 1);
INSERT INTO order_product (order_id, product_id, quantity) VALUES (9, 5, 2);

-- Order 10: 1 Laptop
INSERT INTO order_product (order_id, product_id, quantity) VALUES (10, 1, 1);

-- Total Revenue Summary:
-- Order 1: 2 * 30000 = 60,000
-- Order 2: 1 * 25000 = 25,000
-- Order 3: 1 * 20000 + 1 * 15000 = 35,000
-- Order 4: 1 * 45000 = 45,000
-- Order 5: 2 * 25000 = 50,000
-- Order 6: 3 * 15000 = 45,000
-- Order 7: 1 * 30000 + 1 * 25000 = 55,000
-- Order 8: 2 * 20000 = 40,000
-- Order 9: 1 * 45000 + 2 * 15000 = 75,000
-- Order 10: 1 * 30000 = 30,000
-- TOTAL: 460,000

-- Best Selling Products:
-- Laptop: 4 units (Orders: 1, 7, 10)
-- iPhone: 4 units (Orders: 2, 5, 7)
-- iPad: 6 units (Orders: 3, 6, 9)
-- Samsung: 3 units (Orders: 3, 8)
-- MacBook: 2 units (Orders: 4, 9)
