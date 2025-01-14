#!/bin/bash
echo "Initializing monitoring user..."

mysql -u root -p"${MARIADB_ROOT_PASSWORD}" <<EOF
CREATE USER IF NOT EXISTS 'monitoring'@'localhost' IDENTIFIED BY '${MONITORING_PASSWORD}';

GRANT PROCESS, REPLICATION CLIENT, SELECT ON performance_schema.* TO 'monitoring'@'localhost';
FLUSH PRIVILEGES;

-- Log success
SELECT User, Host FROM mysql.user WHERE User = 'monitoring';
EOF

echo "Monitoring user initialized successfully."
