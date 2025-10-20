CREATE TABLE appointments (
    -- Coluna de ID auto-incrementável (BIGSERIAL é um BIGINT auto-incrementável no Postgres)
    id BIGSERIAL PRIMARY KEY,

    -- Chaves Estrangeiras
    barber_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,

    -- Campos de Data e Hora (LocalDateTime mapeia para TIMESTAMP)
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,

    duration_in_minutes INTEGER NOT NULL,
    status VARCHAR(20), -- Tamanho para o Enum (ex: 'PENDING', 'CONFIRMED', 'CANCELLED')

    -- Definição das Constraints de Chave Estrangeira
    CONSTRAINT fk_appointments_barber
        FOREIGN KEY(barber_id)
        REFERENCES barbers(id),

    CONSTRAINT fk_appointments_client
        FOREIGN KEY(client_id)
        REFERENCES clients(id),

    CONSTRAINT fk_appointments_service
        FOREIGN KEY(service_id)
        REFERENCES services(id)
);