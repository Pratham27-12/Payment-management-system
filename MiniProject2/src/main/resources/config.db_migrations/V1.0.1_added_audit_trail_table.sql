CREATE TYPE payment_revision_type AS ENUM ( 'CREATE', 'UPDATE', 'DELETE' );

CREATE TABLE IF NOT EXISTS payment_system.audit_trail (
    payment_id bigint NOT NULL,
    revision_count bigint default 1 NOT NULL,
    revision_type varchar(20) NOT NULL,
    amount varchar(100) NOT NULL,
    currency varchar(3) NOT null,
    payment_type varchar(20) NOT NULL,
    category varchar(20) NOT NULL,
    created_by varchar(30) NOT NULL,
    account_name varchar(30) NOT NULL,
    status varchar(20) NOT NULL,
    created_at bigint NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric),
    updated_at bigint NOT NULL DEFAULT (EXTRACT(epoch FROM now()) * 1000::numeric),
    CONSTRAINT audit_trail_pkey PRIMARY KEY (payment_id, revision_count)
);