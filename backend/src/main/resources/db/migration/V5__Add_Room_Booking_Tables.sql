-- V5__Fix_Room_Booking_Tables.sql

ALTER TABLE rooms DROP COLUMN occupancy;
ALTER TABLE rooms ADD COLUMN is_available BOOLEAN DEFAULT TRUE;

ALTER TABLE room_bookings DROP FOREIGN KEY `room_bookings_ibfk_1`;
ALTER TABLE room_bookings DROP FOREIGN KEY `room_bookings_ibfk_2`;

ALTER TABLE room_bookings DROP PRIMARY KEY;
ALTER TABLE room_bookings ADD COLUMN id BIGINT AUTO_INCREMENT PRIMARY KEY;
ALTER TABLE room_bookings MODIFY room_id BIGINT;
ALTER TABLE room_bookings MODIFY group_id BIGINT NULL;
ALTER TABLE room_bookings ADD COLUMN status VARCHAR(10) DEFAULT 'CONFIRMED';
ALTER TABLE room_bookings ADD COLUMN notes VARCHAR(255);
ALTER TABLE room_bookings ADD COLUMN family_id BIGINT NULL;

ALTER TABLE room_bookings ADD CONSTRAINT fk_rb_room FOREIGN KEY (room_id) REFERENCES rooms(id);
ALTER TABLE room_bookings ADD CONSTRAINT fk_rb_group FOREIGN KEY (group_id) REFERENCES user_groups(id);
ALTER TABLE room_bookings ADD CONSTRAINT fk_rb_family FOREIGN KEY (family_id) REFERENCES families(id);

DROP TABLE IF EXISTS waiting_list;

CREATE TABLE waiting_list (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              request_date TIMESTAMP NOT NULL,
                              notification_sent BOOLEAN DEFAULT FALSE,
                              notes VARCHAR(255),
                              family_id BIGINT NULL,
                              group_id BIGINT NULL,
                              FOREIGN KEY (family_id) REFERENCES families(id),
                              FOREIGN KEY (group_id) REFERENCES user_groups(id),
                              CHECK ((family_id IS NULL AND group_id IS NOT NULL) OR (family_id IS NOT NULL AND group_id IS NULL))
);



INSERT INTO rooms (room_number, is_available) VALUES
                                                  (1, true), (2, true), (3, true), (4, true), (5, true),
                                                  (6, true), (7, true), (8, true), (9, true), (10, true),
                                                  (11, true), (12, true), (13, true), (14, true), (15, true),
                                                  (16, true), (17, true), (18, true), (19, true), (20, true),
                                                  (21, true), (22, true), (23, true), (24, true), (25, true),
                                                  (26, true), (27, true), (28, true), (29, true), (30, true);