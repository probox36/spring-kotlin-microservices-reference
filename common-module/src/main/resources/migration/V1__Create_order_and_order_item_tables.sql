CREATE TABLE orders (
    id UUID PRIMARY KEY NOT NULL,
    user_id UUID NOT NULL,
    status VARCHAR(255) NOT NULL
);

CREATE TABLE products (
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(19, 2) NOT NULL
);

CREATE TABLE orders_items (
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
