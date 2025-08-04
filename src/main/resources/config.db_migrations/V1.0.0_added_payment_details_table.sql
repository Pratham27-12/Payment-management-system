CREATE SCHEMA IF NOT EXISTS payment_system;

CREATE TYPE payment_category AS ENUM ('SALARY','INVOICE','REFUND','VENDOR_SETTLEMENT');
CREATE TYPE payment_status AS ENUM ('PENDING', 'PROCESSING', 'COMPLETED');
CREATE TYPE payment_type AS ENUM ('INCOMING','OUTGOING');

CREATE TABLE IF NOT EXISTS payment_system.payment_details (
    payment_id varchar(50) NOT NULL,
    amount varchar(100) NOT NULL,
    revision_count bigint NOT NULL DEFAULT 1,
    currency varchar(3) NOT null,
    payment_type payment_type NOT NULL,
    category payment_category NOT NULL,
    user_name varchar(30) NOT NULL,
    status  payment_status NOT NULL,
    created_at bigint NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric),
    updated_at bigint NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric),
    CONSTRAINT payment_pkey PRIMARY KEY (payment_id)
);
