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

-- Orders Table
CREATE TABLE orders (
                        order_id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        delivery_type VARCHAR(50) NOT NULL,
                        address TEXT,
                        payment_method VARCHAR(50) NOT NULL,
                        total_price DECIMAL(10, 2) NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Order Details Table
CREATE TABLE order_details (
                               order_id INT NOT NULL,
                               cake_id INT NOT NULL,
                               quantity INT NOT NULL,
                               FOREIGN KEY (order_id) REFERENCES orders(order_id),
                               FOREIGN KEY (cake_id) REFERENCES cakes(cake_id)
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