CREATE TABLE customer
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    name        VARCHAR(20)           NOT NULL,
    surname     VARCHAR(20)           NOT NULL,
    photo_id    VARCHAR(255)          NULL,
    created_by  VARCHAR(20)           NOT NULL,
    created_at  datetime              NOT NULL,
    modified_by VARCHAR(20)           NOT NULL,
    modified_at datetime              NOT NULL,
    CONSTRAINT pk_customer PRIMARY KEY (id)
);