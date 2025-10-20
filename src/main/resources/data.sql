-- Sample data for testing Admin Dashboard

-- Insert sample products
INSERT INTO product (id, name, price, picture_url) VALUES
(1, 'Laptop Dell XPS 13', 30000.0, 'https://example.com/laptop.jpg'),
(2, 'iPhone 13 Pro', 25000.0, 'https://example.com/iphone.jpg'),
(3, 'Samsung Galaxy S21', 20000.0, 'https://example.com/samsung.jpg'),
(4, 'MacBook Pro 14', 45000.0, 'https://example.com/macbook.jpg'),
(5, 'iPad Air', 15000.0, 'https://example.com/ipad.jpg');

-- Insert sample customers
INSERT INTO customers (id, name, email, phone, registered_date) VALUES
(1, 'Nguyen Van A', 'nguyenvana@example.com', '0901234567', '2025-10-01'),
(2, 'Tran Thi B', 'tranthib@example.com', '0902345678', '2025-10-05'),
(3, 'Le Van C', 'levanc@example.com', '0903456789', '2025-10-10'),
(4, 'Pham Thi D', 'phamthid@example.com', '0904567890', '2025-10-15'),
(5, 'Hoang Van E', 'hoangvane@example.com', '0905678901', '2025-10-20'),
(6, 'Vu Thi F', 'vuthif@example.com', '0906789012', '2025-10-20');

-- Insert sample orders
INSERT INTO orders (id, date_created, status) VALUES
(1, '2025-10-01', 'PAID'),
(2, '2025-10-02', 'PAID'),
(3, '2025-10-03', 'PAID'),
(4, '2025-10-05', 'PAID'),
(5, '2025-10-08', 'PAID'),
(6, '2025-10-10', 'PAID'),
(7, '2025-10-12', 'PAID'),
(8, '2025-10-15', 'PAID'),
(9, '2025-10-18', 'PAID'),
(10, '2025-10-20', 'PAID');

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
