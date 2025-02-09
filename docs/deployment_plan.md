## **Deployment Plan**

### **1. Overview**

#### **Purpose**

Deploy the wedding site application to:

- **Staging Environment:** Render.com free plan for testing and validation.
- **Production Environment:** SuperHosting with specified constraints and features.

#### **Environments**

1. **Staging:**

    - Hosted on Render.com.
    - Dockerized services for backend, frontend, and monitoring stack.
2. **Production:**

    - Hosted on SuperHosting.
    - Includes backend, frontend, RabbitMQ, MariaDB, and the monitoring stack.

---

### **2. Prerequisites**

#### **Tools and Dependencies**

- **Docker Compose:** For local development and production setup.
- **MariaDB:** Database for metadata and application data.
- **RabbitMQ:** For media processing.
- **Prometheus + Grafana + AlertManager:** For monitoring and alerting.
- **Spring Actuator:** Exposes metrics for monitoring.
- **Spring Security:** Secures sensitive endpoints.
- **GitHub Actions:** Automates CI/CD.

#### **Access Credentials**

- SuperHosting SSH and cPanel credentials.
- Render.com credentials for staging.
- MariaDB credentials for database access.
- RabbitMQ credentials.
- Email credentials for AlertManager notifications.

#### **Supported Platforms**

- Linux-based hosting for production.
- Dockerized setup for staging on Render.

---

### **3. Deployment Process**

#### **Staging Deployment**

1. **Pull the Code**

2. **Build and Push Docker Images**

3. **Deploy on Render**

    - Configure Render services for backend and frontend using Docker images.
    - Deploy monitoring stack (Prometheus, Grafana, AlertManager) using Docker Compose.
4. **Verify Deployment**

    - Access Prometheus and Grafana dashboards to ensure metrics are being scraped.
    - Test alerts by simulating service failures or high resource usage.

---

#### **Production Deployment**

1. **Pull the Code**

2. **Configure Docker Compose**

    - Create a `docker-compose.yml`:

        ```yaml
        version: "3.9"
        services:
          backend:
            build: ./backend
            environment:
              DB_HOST: mariadb
              RABBITMQ_HOST: rabbitmq
            depends_on:
              - mariadb
        
          frontend:
            build: ./frontend
            ports:
              - "80:80"
        
          mariadb:
            image: mariadb:10.6
            volumes:
              - db_data:/var/lib/mysql
            environment:
              MYSQL_ROOT_PASSWORD: rootpassword
        
          rabbitmq:
            image: rabbitmq:3-management
            ports:
              - "15672:15672"
              - "5672:5672"
        
          prometheus:
            image: prom/prometheus:latest
            volumes:
              - ./prometheus.yml:/etc/prometheus/prometheus.yml
            ports:
              - "9090:9090"
        
          grafana:
            image: grafana/grafana:latest
            ports:
              - "3000:3000"
            environment:
              - GF_SECURITY_ADMIN_USER=admin
              - GF_SECURITY_ADMIN_PASSWORD=admin
        
          alertmanager:
            image: prom/alertmanager:latest
            ports:
              - "9093:9093"
            volumes:
              - ./alertmanager.yml:/etc/alertmanager/alertmanager.yml
        
        volumes:
          db_data:
        ```

3. **Start Services**

    ```bash
    docker-compose up -d
    ```

4. **Verify Deployment**

    - Check API health:
    - Verify Prometheus and Grafana:
        - Access Prometheus at port 9090.
        - Access Grafana at at port 3000.
    - Test application functionality:
        - User registration, RSVP, booking, and media uploads.

---

### **4. Rollback Plan**

#### **Staging Rollback**

1. Revert to the last stable Docker image on Render.
2. Confirm rollback functionality.

#### **Production Rollback**

1. Stop the current deployment:

    ```bash
    docker-compose down
    ```

2. Revert to the last stable version:

    ```bash
    git checkout tags/stable-release
    docker-compose up -d
    ```

3. Restore MariaDB backup if necessary.

---

### **5. Verification**

1. **Metrics and Alerts:**
    - Confirm Prometheus is scraping metrics from the backend.
    - Verify AlertManager sends notifications.
2. **API Health:**
    - Ensure `/health` endpoint returns `200 OK`.
3. **Frontend Functionality:**
    - Test registration, RSVP, and media uploads.

---

### **6. CI/CD Integration**

#### **GitHub Actions Workflow**

Automate build, test, and deployment for staging.

```yaml
name: Staging Deployment

on:
  push:
    branches:
      - main

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v2
      - name: Build Backend
        run: ./gradlew build
      - name: Build Frontend
        run: |
          cd frontend
          npm install
          npm run build

  deploy:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - name: Deploy to Render
        run: |
          docker push docker-repo/wedding-backend
          docker push docker-repo/wedding-frontend
```

---
