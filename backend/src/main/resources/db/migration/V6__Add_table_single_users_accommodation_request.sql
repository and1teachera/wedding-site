CREATE TABLE single_user_accommodation_requests
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT      NOT NULL,
    request_date   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status         VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes          TEXT,
    processed      BOOLEAN              DEFAULT FALSE,
    processed_date TIMESTAMP   NULL,
    group_id       BIGINT      NULL,
    CONSTRAINT fk_suar_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_suar_group FOREIGN KEY (group_id) REFERENCES user_groups (id)
);