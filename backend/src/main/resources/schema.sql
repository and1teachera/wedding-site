USE wedding_site;

CREATE TABLE families (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          family_name VARCHAR(100) NOT NULL
);

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       email VARCHAR(255) UNIQUE,
                       phone VARCHAR(15) UNIQUE,
                       password VARCHAR(60) NOT NULL,
                       is_child BOOLEAN DEFAULT FALSE,
                       is_admin BOOLEAN DEFAULT FALSE,
                       last_login TIMESTAMP,
                       family_id BIGINT NULL,
                       CONSTRAINT fk_family FOREIGN KEY (family_id) REFERENCES families(id)
);

CREATE TABLE user_groups (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE group_members (
                               group_id BIGINT,
                               user_id BIGINT,
                               PRIMARY KEY (group_id, user_id),
                               FOREIGN KEY (group_id) REFERENCES user_groups(id),
                               FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE user_responses (
                                user_id BIGINT PRIMARY KEY,
                                response_status ENUM('MAYBE', 'YES', 'NO') DEFAULT 'MAYBE',
                                dietary_notes VARCHAR(100),
                                additional_notes VARCHAR(100),
                                FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE rooms (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       room_number INT NOT NULL CHECK (room_number BETWEEN 1 AND 50),
                       occupancy INT DEFAULT 0,
                       UNIQUE (room_number)
);

CREATE TABLE room_bookings (
                               room_id BIGINT,
                               group_id BIGINT,
                               booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (room_id),
                               FOREIGN KEY (room_id) REFERENCES rooms(id),
                               FOREIGN KEY (group_id) REFERENCES user_groups(id)
);

CREATE TABLE waiting_list (
                              position INT AUTO_INCREMENT,
                              group_id BIGINT,
                              PRIMARY KEY (position),
                              UNIQUE (group_id),
                              FOREIGN KEY (group_id) REFERENCES user_groups(id)
);
