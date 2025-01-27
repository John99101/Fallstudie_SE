  -- First, create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS shop_n_bake;
USE shop_n_bake;

-- Grant privileges if needed
GRANT ALL PRIVILEGES ON shop_n_bake.* TO 'root'@'localhost';
FLUSH PRIVILEGES;

-- Drop tables if they exist
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cakes;
DROP TABLE IF EXISTS users;

-- Create users table with new fields
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,  -- Neues Feld
    role VARCHAR(20) NOT NULL DEFAULT 'customer',
    is_company BOOLEAN DEFAULT false,  -- Neues Feld
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create cakes table
CREATE TABLE cakes (
    cake_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    available BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create orders table
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    delivery_type VARCHAR(20),
    payment_method VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create order_items table
CREATE TABLE order_items (
    order_id INT,
    cake_id INT,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (order_id, cake_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (cake_id) REFERENCES cakes(cake_id)
);

-- Insert test data
INSERT INTO users (email, password, role, name, is_company) VALUES
('admin@shop.com', 'admin', 'employee', 'Admin User', false),
('test@test.com', 'test', 'customer', 'Test Customer', false);

-- Insert sample cakes
INSERT INTO cakes (name, description, price, available) VALUES
('Chocolate Cake', 'Rich chocolate cake with chocolate frosting', 24.99, true),
('Vanilla Cake', 'Classic vanilla cake with vanilla buttercream', 19.99, true),
('Strawberry Cheesecake', 'Fresh strawberry cheesecake with graham cracker crust', 29.99, true),
('Carrot Cake', 'Moist carrot cake with cream cheese frosting', 22.99, true),
('Black Forest Cake', 'Classic German chocolate cake with cherries', 27.99, true),
('Red Velvet Cake', 'Red velvet cake with cream cheese frosting', 25.99, true),
('Lemon Pound Cake', 'Tangy lemon cake with lemon glaze', 18.99, true),
('Tiramisu', 'Italian coffee-flavored dessert', 26.99, true);

-- Insert sample orders
INSERT INTO orders (user_id, total, status, delivery_type, payment_method, address) VALUES
(2, 44.98, 'pending', 'delivery', 'credit_card', '123 Main St, City');

-- Insert sample order items
INSERT INTO order_items (order_id, cake_id, quantity, price) VALUES
(1, 1, 1, 24.99),  -- order 1, Chocolate Cake
(1, 2, 1, 19.99);  -- order 1, Vanilla Cake

-- Check current table structure
DESCRIBE cakes;

-- Erst die Struktur der cakes Tabelle anzeigen
DESCRIBE cakes;

-- Nur den MODIFY COLUMN Befehl ausführen, da description bereits existiert
ALTER TABLE cakes 
MODIFY COLUMN price DECIMAL(10,2);

-- Insert some sample data if table is empty
INSERT IGNORE INTO cakes (name, description, price) VALUES 
('Chocolate Cake', 'Rich chocolate cake with chocolate frosting', 24.99),
('Vanilla Bean Cake', 'Classic vanilla cake with vanilla buttercream', 22.99),
('Red Velvet', 'Traditional red velvet cake with cream cheese frosting', 26.99);

-- Überprüfen Sie die aktuelle Datenbank
SELECT DATABASE();

-- Zeigen Sie die Tabellen an
SHOW TABLES;

-- Zeigen Sie die Struktur der users-Tabelle an (falls vorhanden)
DESCRIBE users;

-- Add user permissions (with a stronger password for production)
CREATE USER IF NOT EXISTS 'root'@'localhost' IDENTIFIED BY 'MySQL123!';
GRANT ALL PRIVILEGES ON shop_n_bake.* TO 'root'@'localhost';
FLUSH PRIVILEGES;