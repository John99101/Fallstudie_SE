CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(50),
    role ENUM('Customer', 'Employee'),
    full_name VARCHAR(100),
    email VARCHAR(100),
    address TEXT,
    phone VARCHAR(15)
);

CREATE TABLE Cakes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50),
    variation VARCHAR(100),
    price DECIMAL(10, 2),
    stock INT
);

CREATE TABLE Orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    cake_id INT,
    quantity INT,
    status ENUM('Recipe Being Prepared', 'Cake Being Baked', 'Cake Being Decorated', 'Cake Being Packed', 'Cake Ready'),
    delivery_method ENUM('Pick-Up', 'Delivery'),
    FOREIGN KEY (customer_id) REFERENCES Users(id),
    FOREIGN KEY (cake_id) REFERENCES Cakes(id)
);

CREATE TABLE Stock (
    id INT AUTO_INCREMENT PRIMARY KEY,
    material_name VARCHAR(50),
    quantity INT,
    threshold INT
);
