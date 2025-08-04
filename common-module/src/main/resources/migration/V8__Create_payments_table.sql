CREATE TABLE payments (
    id UUID PRIMARY KEY NOT NULL,
    status VARCHAR(127) NOT NULL DEFAULT 'PENDING',
    order_id UUID NOT NULL,
    value BIGINT NOT NULL,
    value_paid BIGINT NOT NULL DEFAULT 0,
    time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_payment_order
        FOREIGN KEY (order_id)
        REFERENCES orders (id)
);