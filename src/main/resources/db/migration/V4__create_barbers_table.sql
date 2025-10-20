CREATE TABLE barbers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    -- Mapeamento de camelCase (phoneNumber) para snake_case (phone_number)
    phone_number VARCHAR(50),
    password VARCHAR(255),
    -- Mapeamento de camelCase (isActive) para snake_case (is_active)
    is_active BOOLEAN,
    -- Mapeamento de camelCase (profileImgURL) para snake_case (profile_img_url)
    profile_img_url VARCHAR(1024),
    -- Mapeamento de BigDecimal para NUMERIC (ex: 10.50)
    -- NUMERIC(5, 2) permite números até 999.99
    default_commission_percentage NUMERIC(5, 2)
);