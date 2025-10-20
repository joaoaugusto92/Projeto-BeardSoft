CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    -- Chave estrangeira para a tabela 'users' (UserEntity)
    -- É 'NOT NULL' (obrigatória) e 'UNIQUE' (força o 1-para-1)
    user_id BIGINT NOT NULL UNIQUE,
    -- Mapeamento de LocalDate para DATE
    birth_date DATE,
    -- Mapeamento de LocalDateTime para TIMESTAMP
    last_visit_date TIMESTAMP WITHOUT TIME ZONE,

    -- Constraint da chave estrangeira
    CONSTRAINT fk_clients_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
);