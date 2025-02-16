-- V3__Add_Login_Attempts_Table.sql
CREATE TABLE login_attempts (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                attempt_time TIMESTAMP NOT NULL,
                                ip_address VARCHAR(45) NOT NULL,
                                successful BOOLEAN NOT NULL,
                                username VARCHAR(100) NOT NULL,
                                failure_reason VARCHAR(255),
                                user_agent VARCHAR(255) NOT NULL,
                                country_code VARCHAR(2),

    -- Add indexes for common queries
                                INDEX idx_attempt_time (attempt_time),
                                INDEX idx_ip_address (ip_address),
                                INDEX idx_successful (successful)
);
