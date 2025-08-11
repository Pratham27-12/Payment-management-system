CREATE TYPE user_role AS ENUM ('ADMIN', 'FINANCE_MANAGER', 'VIEWER');

CREATE TABLE IF NOT EXISTS payment_system.user_details (
    user_id varchar(50) NOT NULL,
    user_name varchar(50) NOT NULL,
    user_role user_role  NOT NULL,
    password varchar NOT null,
    CONSTRAINT user_details_pkey PRIMARY KEY (user_id),
    CONSTRAINT user_details_user_name UNIQUE (user_name)
);