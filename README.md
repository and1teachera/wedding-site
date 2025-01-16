# Wedding Site

This repository contains the code for a wedding website built specifically for an upcoming event. It is designed to share essential wedding information, help guests RSVP, and manage accommodations efficiently. The site also includes a private space for sharing photos, videos, and updates before and after the wedding.

## CI/CD pipeline status
[![Service Validation](https://github.com/and1teachera/wedding-site/actions/workflows/ci.yml/badge.svg)](https://github.com/and1teachera/wedding-site/actions/workflows/ci.yml)

## Features

### Guest-Facing:

- **Event Details:** Information about the venue, schedule, and dress code.
- **RSVP:** Guests can confirm attendance for themselves and their family members.
- **Accommodation Booking:** Reserve rooms with clear availability and a waiting list for overflow.
- **Media Sharing:** Guests can upload photos and videos to share moments from the celebration.
- **FAQ:** Answers to common questions about the event.

### Admin-Facing:

- **Guest Management:** Import guest lists and manage their statuses.
- **Accommodation Monitoring:** Track room bookings and waiting lists.
- **Activity Monitoring:** View media uploads and manage disk space usage.
- **Post Moderation:** Delete inappropriate content with ease.

## Technology Stack

- **Frontend:** Angular with Tailwind CSS
- **Backend:** Spring Boot with microservices architecture
- **Database:** PostgreSQL (optional, depending on further requirements)
- **Queue System:** RabbitMQ for handling accommodation booking logic
- **Deployment:** Docker Compose for easy setup and hosting
- **Monitoring:** Prometheus and Grafana for performance tracking