-- 01-init.sql
USE wedding_site;

GRANT ALL PRIVILEGES ON wedding_site.* TO 'wedding_user'@'%';

FLUSH PRIVILEGES;
