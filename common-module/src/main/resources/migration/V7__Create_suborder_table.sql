CREATE TABLE suborders (
    id UUID PRIMARY KEY NOT NULL,
    order_id UUID NOT NULL,
    restaurant_id UUID NOT NULL,
    status VARCHAR(255) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE suborders_items (
    suborder_id UUID NOT NULL,
    product_id UUID NOT NULL,
    FOREIGN KEY (suborder_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

ALTER TABLE users
ADD PRIMARY KEY (id);

ALTER TABLE orders
ADD CONSTRAINT fk_orders_user_id
FOREIGN KEY (user_id) REFERENCES users(id);
