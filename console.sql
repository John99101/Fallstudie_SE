CREATE DATABASE bakery_db;
USE bakery_db;

-- Users Table
CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       name VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       is_employee BOOLEAN NOT NULL
);

-- Employees Table (Optional)
CREATE TABLE employees (
                           employee_id INT AUTO_INCREMENT PRIMARY KEY,
                           user_id INT NOT NULL,
                           position VARCHAR(100),
                           hire_date DATE,
                           FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Cakes Table
CREATE TABLE cakes (
                       cake_id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       description TEXT,
                       price DECIMAL(10, 2) NOT NULL,
                       stock_availability INT NOT NULL
);

-- Drop the existing orders table if needed
DROP TABLE IF EXISTS order_details;
DROP TABLE IF EXISTS orders;

-- Create the orders table with the correct structure
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'Processing',
    delivery_type VARCHAR(50) NOT NULL,
    address VARCHAR(255),
    payment_method VARCHAR(50) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create the order_details table
CREATE TABLE order_details (
    order_id INT NOT NULL,
    cake_id INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (cake_id) REFERENCES cakes(cake_id),
    PRIMARY KEY (order_id, cake_id)
);

-- Stock Table
CREATE TABLE stock (
                       ingredient_id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       quantity INT NOT NULL
);

-- Delivery Assignments Table (Optional)
CREATE TABLE delivery_assignments (
                                      delivery_id INT AUTO_INCREMENT PRIMARY KEY,
                                      order_id INT NOT NULL,
                                      employee_id INT NOT NULL,
                                      delivery_date DATE,
                                      status VARCHAR(50) NOT NULL,
                                      FOREIGN KEY (order_id) REFERENCES orders(order_id),
                                      FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

-- Payments Table (Optional)
CREATE TABLE payments (
                          payment_id INT AUTO_INCREMENT PRIMARY KEY,
                          order_id INT NOT NULL,
                          payment_method VARCHAR(50) NOT NULL,
                          payment_status VARCHAR(50) NOT NULL,
                          amount DECIMAL(10, 2) NOT NULL,
                          payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

-- Audit Log Table (Optional)
CREATE TABLE audit_log (
                           log_id INT AUTO_INCREMENT PRIMARY KEY,
                           employee_id INT NOT NULL,
                           action VARCHAR(255) NOT NULL,
                           timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

INSERT INTO cakes (name, description, price, stock_availability) VALUES 
('Chocolate Cake', 'Rich chocolate cake with dark chocolate frosting', 24.99, 10),
('Vanilla Bean Cake', 'Classic vanilla cake with buttercream frosting', 22.99, 15),
('Red Velvet', 'Traditional red velvet with cream cheese frosting', 26.99, 8),
('Carrot Cake', 'Spiced carrot cake with walnuts and cream cheese frosting', 25.99, 12),
('Lemon Drizzle', 'Light lemon cake with citrus glaze', 23.99, 10),
('Black Forest', 'Chocolate cake with cherries and whipped cream', 28.99, 6),
('Strawberry Shortcake', 'Light vanilla cake with fresh strawberries', 27.99, 9),
('Cheesecake', 'New York style cheesecake with berry compote', 29.99, 7);


-- If the column doesn't exist, add it (and this time make sure it executes)
ALTER TABLE orders ADD COLUMN IF NOT EXISTS order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP;


DROP TABLE IF EXISTS order_details;
DROP TABLE IF EXISTS orders;

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'Processing',
    delivery_type VARCHAR(50) NOT NULL,
    address VARCHAR(255),
    payment_method VARCHAR(50) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insert some sample data if needed
INSERT INTO orders (user_id, delivery_type, payment_method, total_price) 
VALUES (1, 'Delivery', 'Credit Card', 49.98);

-- Check the current structure of the orders table
SHOW CREATE TABLE orders;