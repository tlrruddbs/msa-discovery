CREATE TABLE IF NOT EXISTS catalog (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       product_id VARCHAR(120) NOT NULL UNIQUE,
    product_name VARCHAR(120) NOT NULL,
    stock INT NOT NULL,
    unit_price INT NOT NULL,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
    );
