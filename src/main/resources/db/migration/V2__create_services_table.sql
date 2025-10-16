CREATE TABLE services (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(200) NOT NULL CHECK (LENGTH(description) >= 50),
    value NUMERIC(10,2) NOT NULL CHECK (value >= 0.00),
    image_url VARCHAR(255),
    duration_in_minutes INT CHECK (duration_in_minutes BETWEEN 5 AND 180),
    category VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE
);