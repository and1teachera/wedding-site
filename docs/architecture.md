This document provides a comprehensive overview of the architecture for the wedding site application, including its characteristics, decisions, logical components, and architectural style. The architecture is designed to balance performance, maintainability, and scalability while adhering to the defined requirements and hosting constraints.

---

## **1. Architectural Characteristics**

### **Performance**

- Optimized for 150–200 active guests, ensuring smooth operation for RSVP management, accommodation booking, and media sharing.

- Efficient handling of media uploads and news feed updates using asynchronous processing via RabbitMQ.


### **Availability**

- High availability achieved through Dockerized deployments and fallback mechanisms during system downtime.

- Monitoring and alerting tools (Prometheus, AlertManager) ensure rapid detection and resolution of issues.


### **Maintainability**

- Modular design with clearly defined services and logical separation of responsibilities.

- Docker Compose used for local and production orchestration, simplifying setup and updates.


### **Security**

- Password-protected guest access with Spring Security for backend endpoints.

- Role-based access control ensures only admins can access monitoring tools and sensitive metrics.


### **Usability**

- Intuitive user experience for RSVP, accommodation booking, and media uploads.

- Responsive frontend built with Angular to provide seamless interactions across devices.


### **Testability**

- CI/CD pipelines with GitHub Actions for automated testing and staging deployments.

- Structured logging enables easy debugging and monitoring during development and production.


---

## **2. Architectural Decisions**

### **Database**

- **MariaDB**:

  - Stores all metadata, including guest details, RSVP status, and media URIs.

  - Full-Text Indexing is used for guest search functionality.

  - Persistent volumes ensure data durability.


### **Media Handling**

- Media files (photos and videos) are stored on disk, with metadata and file URIs saved in MariaDB.

- RabbitMQ queues handle media upload tasks asynchronously, ensuring non-blocking performance.


### **Service Containers**

- **Backend Service**: Combines core functionality (RSVP, booking) and media handling (news feed, uploads).

- **RabbitMQ**: Queue for asynchronous media processing

- **Frontend Service**: Serves the Angular application.

- **Monitoring Stack**:

  - **Prometheus**: Collects system and application metrics.

  - **Grafana**: Visualizes metrics.

  - **AlertManager**: Sends alerts for defined conditions.


### **Orchestration**

- **Docker Compose** orchestrates all services locally and in production.

- Hosting constraints (e.g., 50GB disk space, 2GB RAM) are carefully managed.


### **Monitoring and Logging**

- **Spring Actuator** exposes metrics for Prometheus.

- **Structured Logging**:

  - JSON-based logs for easier integration with monitoring tools.

  - Logs include errors, RabbitMQ task failures, and security events.


### **Security**

- Spring Security protects sensitive endpoints (e.g., `/actuator/prometheus`) and restricts access to admin roles.


---

## **3. Logical Components**

### **Backend Services**

- **Core Features**:

  - Guest registration and management.

  - RSVP tracking and accommodation booking.

- **Media Handling**:

  - Upload and storage of media files.

  - News feed updates and interactions.


### **Frontend**

- Responsive Angular-based UI.

- Communicates with backend APIs for dynamic data rendering.


### **Database**

- Schema includes:

  - Guest records.

  - RSVP statuses.

  - Media file metadata (e.g., file names, URIs, upload timestamps).


### **Queue**

- RabbitMQ for asynchronous processing:

  - **Queue:** Processes media uploads (e.g., thumbnail generation).


### **Monitoring Stack**

- **Prometheus** collects metrics from:

  - Backend services via Spring Actuator.

  - RabbitMQ (optional plugin).

- **Grafana** visualizes metrics and system health.

- **AlertManager** sends email alerts for critical issues.


---

## **4. Architectural Style**

### **Style**

- **Modular Monolithic Architecture**:

  - Single backend service to simplify deployment and resource utilization.

  - Decoupled asynchronous processing using RabbitMQ queues.


### **Deployment**

- **Dockerized Services**:

  - Backend, frontend, RabbitMQ, MariaDB, and monitoring tools are containerized.

- **Hosting**:

  - Staging: Render.com free plan.

  - Production: hosting with resource constraints.


---

## **5. Development Workflow**

### **Local Development**

- Docker Compose orchestrates local containers.

- Code changes are tested locally with integrated logging and monitoring.


### **CI/CD Pipeline**

- GitHub Actions automate:

  - Code testing and validation.

  - Deployment to staging environments.


---

## **6. Security Measures**

### **Endpoint Protection**

- Sensitive endpoints (e.g., `/actuator/**`) secured with Spring Security.

- Admin role required for accessing monitoring tools.


### **Monitoring Tool Security**

- Grafana and Prometheus protected by Spring Security.
 