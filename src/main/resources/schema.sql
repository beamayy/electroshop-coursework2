CREATE TABLE IF NOT EXISTS user_roles (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS accounts (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (role_id) REFERENCES user_roles(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS staff_profiles (
    id INT NOT NULL AUTO_INCREMENT,
    account_id INT NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    PRIMARY KEY (id),
    FOREIGN KEY (account_id) REFERENCES accounts(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS product_categories (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS hardware_inventories (
    id INT NOT NULL AUTO_INCREMENT,
    category_id INT NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    technical_spec TEXT,
    retail_price DECIMAL(10,2) NOT NULL,
    stock_balance INT NOT NULL DEFAULT 0,
    warranty_months INT NOT NULL DEFAULT 12,
    PRIMARY KEY (id),
    FOREIGN KEY (category_id) REFERENCES product_categories(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS transactions (
    id INT NOT NULL AUTO_INCREMENT,
    account_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(12,2) NOT NULL,
    customer_name VARCHAR(200),
    PRIMARY KEY (id),
    FOREIGN KEY (account_id) REFERENCES accounts(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS transaction_items (
    id INT NOT NULL AUTO_INCREMENT,
    transaction_id INT NOT NULL,
    inventory_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (transaction_id) REFERENCES transactions(id),
    FOREIGN KEY (inventory_id) REFERENCES hardware_inventories(id)
) ENGINE=InnoDB;
