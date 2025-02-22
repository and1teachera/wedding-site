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
                                                  (101, true), (102, true), (103, true), (104, true), (105, true),
                                                  (106, true), (107, true), (108, true), (109, true), (110, true),
                                                  (201, true), (202, true), (203, true), (204, true), (205, true),
                                                  (206, true), (207, true), (208, true), (209, true), (210, true),
                                                  (301, true), (302, true), (303, true), (304, true), (305, true),
                                                  (306, true), (307, true), (308, true), (309, true), (310, true);