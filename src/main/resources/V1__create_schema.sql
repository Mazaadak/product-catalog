CREATE TABLE categories (
                            category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(50) NOT NULL UNIQUE,
                            parent_id BIGINT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (parent_id) REFERENCES categories(category_id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE products (
                          product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          seller_id BIGINT NOT NULL,
                          title VARCHAR(100) NOT NULL,
                          category_id BIGINT NOT NULL,
                          description TEXT,
                          price DECIMAL(10, 2),
                          type ENUM('auction', 'fixed') NOT NULL,
                          status ENUM('active', 'inactive', 'deleted') DEFAULT 'active',
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (category_id) REFERENCES categories(category_id)
) ENGINE=InnoDB;

CREATE TABLE product_images (
                                image_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                product_id BIGINT NOT NULL,
                                image_uri TEXT NOT NULL,
                                is_primary BOOLEAN DEFAULT FALSE,
                                position INT DEFAULT 0,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
                                INDEX idx_product_id (product_id)
) ENGINE=InnoDB;

CREATE TABLE product_ratings (
                                 rating_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 product_id BIGINT NOT NULL,
                                 user_id BIGINT NOT NULL,
                                 rating TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
                                 review_text TEXT,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 UNIQUE KEY unique_user_product (user_id, product_id),
                                 FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
                                 INDEX idx_product_id (product_id)
) ENGINE=InnoDB;

CREATE TABLE idempotency_records (
                                 idempotency_key VARCHAR(255) PRIMARY KEY,
                                 product_id BIGINT NULL,
                                 request_hash VARCHAR(64) NOT NULL,
                                 status ENUM('IN_PROGRESS', 'COMPLETED', 'FAILED') NOT NULL DEFAULT 'IN_PROGRESS',
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE SET NULL,
                                 INDEX idx_product_id (product_id)
) ENGINE=InnoDB;
