CREATE DATABASE IF NOT EXISTS shop_n_bake;
USE shop_n_bake;

-- Grant privileges if needed
GRANT ALL PRIVILEGES ON shop_n_bake.* TO 'root'@'localhost';
FLUSH PRIVILEGES;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_employee BOOLEAN NOT NULL
);

-- Create cakes table
CREATE TABLE IF NOT EXISTS cakes (
    cake_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    available BOOLEAN DEFAULT true
);

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
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

-- Create order_details table
CREATE TABLE IF NOT EXISTS order_details (
    order_id INT NOT NULL,
    cake_id INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (cake_id) REFERENCES cakes(cake_id),
    PRIMARY KEY (order_id, cake_id)
);

-- Insert a test employee user
INSERT INTO users (email, name, password, is_employee) 
VALUES ('test@test.de', 'Test Employee', 'test', true);

-- Insert sample cakes
INSERT INTO cakes (name, description, price, available) 
VALUES 
('Chocolate Cake', 'Rich chocolate cake with chocolate frosting', 24.99, true),
('Vanilla Bean Cake', 'Classic vanilla cake with vanilla buttercream', 22.99, true),
('Red Velvet', 'Traditional red velvet cake with cream cheese frosting', 26.99, true),
('Carrot Cake', 'Spiced carrot cake with cream cheese frosting', 25.99, true),
('Lemon Drizzle', 'Light lemon cake with lemon glaze', 23.99, true);

-- Check current table structure
DESCRIBE cakes;

-- If needed, add the missing columns
ALTER TABLE cakes 
ADD COLUMN IF NOT EXISTS description TEXT AFTER name,
MODIFY COLUMN price DECIMAL(10,2);

-- Insert some sample data if table is empty
INSERT IGNORE INTO cakes (name, description, price) VALUES 
('Chocolate Cake', 'Rich chocolate cake with chocolate frosting', 24.99),
('Vanilla Bean Cake', 'Classic vanilla cake with vanilla buttercream', 22.99),
('Red Velvet', 'Traditional red velvet cake with cream cheese frosting', 26.99);