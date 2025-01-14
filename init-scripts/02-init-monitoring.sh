#!/bin/bash
echo "Initializing monitoring user..."

# Connect to the database and create the monitoring user with necessary privileges
mysql -u root -p"${MARIADB_ROOT_PASSWORD}" <<EOF
-- Create the monitoring user if it does not exist
CREATE USER IF NOT EXISTS 'monitoring'@'localhost' IDENTIFIED BY '${MONITORING_PASSWORD}';

-- Grant global privileges
GRANT PROCESS, REPLICATION CLIENT ON *.* TO 'monitoring'@'localhost';

-- Grant specific database privileges (if needed)
GRANT SELECT ON performance_schema.* TO 'monitoring'@'localhost';

-- Apply changes
FLUSH PRIVILEGES;

-- Log the created user
SELECT User, Host FROM mysql.user WHERE User = 'monitoring';
EOF

echo "Monitoring user initialized successfully."
