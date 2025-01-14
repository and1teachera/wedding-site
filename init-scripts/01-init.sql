-- 01-init.sql
USE wedding_site;

CREATE USER 'monitoring'@'localhost' IDENTIFIED BY '${MONITORING_PASSWORD}';
GRANT SELECT, SHOW DATABASES, PROCESS, REPLICATION CLIENT ON *.* TO 'monitoring'@'localhost';

GRANT ALL PRIVILEGES ON wedding_site.* TO 'wedding_user'@'%';

FLUSH PRIVILEGES;
