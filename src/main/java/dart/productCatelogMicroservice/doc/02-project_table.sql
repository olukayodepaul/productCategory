
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    parentid VARCHAR DEFAULT NULL,
    isactive BOOLEAN DEFAULT TRUE,
	createdat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedat TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);
-- Index for quick search on category name
CREATE INDEX idx_categories_name ON categories(name);



--Table for product in another service
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    category_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- Index for quick search on product name
CREATE INDEX idx_products_name ON products(name);

-- Index for faster lookups by category
CREATE INDEX idx_products_category_id ON products(category_id);