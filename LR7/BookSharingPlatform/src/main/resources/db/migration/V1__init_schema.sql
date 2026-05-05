CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rating NUMERIC(3,2) NOT NULL DEFAULT 0.00
);

CREATE TABLE books (
    id UUID PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(32) UNIQUE,
    rating NUMERIC(3,2) NOT NULL DEFAULT 0.00
);

CREATE TABLE inventory (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    book_id UUID NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    status VARCHAR(32) NOT NULL,
    condition_value VARCHAR(120) NOT NULL,
    UNIQUE (user_id, book_id)
);

CREATE INDEX idx_inventory_user_id ON inventory(user_id);
CREATE INDEX idx_inventory_book_id ON inventory(book_id);
CREATE INDEX idx_inventory_status ON inventory(status);

CREATE TABLE exchanges (
    id UUID PRIMARY KEY,
    requester_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    owner_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    inventory_id UUID NOT NULL REFERENCES inventory(id) ON DELETE CASCADE,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_exchanges_requester ON exchanges(requester_id);
CREATE INDEX idx_exchanges_owner ON exchanges(owner_id);
CREATE INDEX idx_exchanges_inventory ON exchanges(inventory_id);

CREATE TABLE book_movements (
    id UUID PRIMARY KEY,
    exchange_id UUID NOT NULL REFERENCES exchanges(id) ON DELETE CASCADE,
    inventory_id UUID NOT NULL REFERENCES inventory(id) ON DELETE CASCADE,
    from_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    to_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_movements_from_user ON book_movements(from_user_id);
CREATE INDEX idx_movements_to_user ON book_movements(to_user_id);
CREATE INDEX idx_movements_exchange ON book_movements(exchange_id);

CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    exchange_id UUID NOT NULL REFERENCES exchanges(id) ON DELETE CASCADE,
    author_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_type VARCHAR(16) NOT NULL,
    target_id UUID NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment VARCHAR(2000),
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_reviews_target ON reviews(target_type, target_id);
CREATE INDEX idx_reviews_exchange ON reviews(exchange_id);
