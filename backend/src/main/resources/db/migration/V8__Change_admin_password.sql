use wedding_site;
UPDATE users
SET password = '$2a$10$2mHGwrXnFpyP0jtc4mriPeoUGZJs3XhezU0DFmEO6tKGXQXYQp6iC'
WHERE first_name = 'admin' AND last_name = 'admin';