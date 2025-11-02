ALTER TABLE barbers DROP COLUMN email;
ALTER TABLE barbers DROP COLUMN password;
ALTER TABLE barbers DROP COLUMN phone_number;
ALTER TABLE barbers ADD COLUMN user_id BIGINT;
-- Define a coluna como NOT NULL (necessário pelo @JoinColumn no JPA)
ALTER TABLE barbers ALTER COLUMN user_id SET NOT NULL;

-- Define a coluna como ÚNICA (necessário pela relação @OneToOne no JPA)
ALTER TABLE barbers ADD CONSTRAINT uk_barbers_user_id UNIQUE (user_id);

-- Cria a Chave Estrangeira
ALTER TABLE barbers ADD CONSTRAINT fk_barbers_user_id
    FOREIGN KEY (user_id)
    REFERENCES users (id) -- Referencia a tabela 'users' e a coluna 'id'
    ON DELETE CASCADE;   -- Garante que se o User for apagado, o Barber também é.