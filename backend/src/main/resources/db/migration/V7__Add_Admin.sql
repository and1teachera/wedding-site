use wedding_site;

INSERT INTO users (first_name, last_name, password, is_child, is_admin, last_login)
VALUES ('admin', 'admin', '$2a$10$x.ny427i0SIuexDxvOaTGepYe/PspI2pR5xj7PejWvqiVR29KrdyS', false, true, CURRENT_TIMESTAMP);

UPDATE users
SET password = '$2a$10$2mHGwrXnFpyP0jtc4mriPeoUGZJs3XhezU0DFmEO6tKGXQXYQp6iC'
WHERE first_name = 'admin' AND last_name = 'admin';