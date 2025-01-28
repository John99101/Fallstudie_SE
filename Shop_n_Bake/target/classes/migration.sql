-- Backup existing tables
CREATE TABLE users_backup AS SELECT * FROM users;
CREATE TABLE cakes_backup AS SELECT * FROM cakes;
CREATE TABLE orders_backup AS SELECT * FROM orders;
CREATE TABLE order_items_backup AS SELECT * FROM order_items;

-- Drop existing tables in correct order
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cakes;
DROP TABLE IF EXISTS users;

-- Create new tables in order of dependencies
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    is_employee BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE employees (
    employee_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    position VARCHAR(50) NOT NULL,
    hire_date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE stock (
    ingredient_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL DEFAULT 0
);

CREATE TABLE cakes (
    cake_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_availability INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    delivery_type VARCHAR(50) NOT NULL,
    address VARCHAR(255),
    payment_method VARCHAR(50) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_details (
    order_id INT NOT NULL,
    cake_id INT NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (order_id, cake_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (cake_id) REFERENCES cakes(cake_id)
);

CREATE TABLE delivery_assignments (
    delivery_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    employee_id INT NOT NULL,
    delivery_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

CREATE TABLE audit_log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    action VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

-- Migrate existing data
INSERT INTO users (id, email, name, password, is_employee)
SELECT id, email, name, password, 
       CASE WHEN role = 'employee' THEN true ELSE false END 
FROM users_backup;

-- Insert initial employee records for existing employee users
INSERT INTO employees (user_id, position, hire_date)
SELECT id, 'Baker', CURRENT_DATE
FROM users_backup
WHERE role = 'employee';

-- Migrate cakes data
INSERT INTO cakes (cake_id, name, description, price, stock_availability)
SELECT cake_id, name, description, price, stock
FROM cakes_backup;

-- Migrate orders and order details
INSERT INTO orders (order_id, user_id, status, delivery_type, address, payment_method, total_price)
SELECT order_id, user_id, status, delivery_type, 
       CONCAT(street, ', ', city, ' ', zip), 
       payment_method, total
FROM orders_backup;

INSERT INTO order_details (order_id, cake_id, quantity)
SELECT order_id, cake_id, quantity
FROM order_items_backup;

-- Initial stock items
INSERT INTO stock (name, quantity) VALUES
('Flour', 1000),
('Sugar', 1000),
('Eggs', 500),
('Milk', 300),
('Chocolate', 200),
('Vanilla Extract', 100),
('Butter', 400);

-- Clean up backup tables
DROP TABLE users_backup;
DROP TABLE cakes_backup;
DROP TABLE orders_backup;
DROP TABLE order_items_backup;

-- Drop the table if it exists to avoid partial creation issues
DROP TABLE IF EXISTS employees;

-- Create employees table with correct structure
CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    position VARCHAR(50) NOT NULL,
    hire_date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Add any existing employee data (removed salary field)
INSERT INTO employees (user_id, position, hire_date)
SELECT id, 'Manager', CURRENT_DATE
FROM users
WHERE is_employee = true; 