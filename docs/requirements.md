### **Business Requirements**

1. Provide clear and comprehensive event information, including location, program, dress code, and gift preferences.
2. Enable guests to register and RSVP without a deadline.
3. Offer a limited number of free accommodations, with a transparent booking process.
4. Facilitate efficient management of guest lists, RSVPs, and room bookings.
5. Ensure secure access to the platform with password-protected accounts.
6. Deliver personalized welcome experiences for guests.
7. Communicate effectively with guests via email notifications for invitations and room availability.
8. Support visual appeal to align with the wedding’s theme and importance.

### **Functional Requirements**

1. **Event Information**:

    - Display venue details and a map.
    - List nearby accommodations with limited room availability.
    - Include a schedule for the wedding day.
    - Provide a customizable FAQ section.

2. **Guest Registration and RSVP**:

    - Allow family-based registrations and single attendees to add a companion.
    - Enable guests to modify room bookings after initial registration.

3. **Accommodation Booking**:

    - Show only the number of available rooms.
    - Reserve rooms for early access guests and manage a waiting list.
    - Notify guests via email when a room becomes available.
    - Implement a 5-day reservation hold for waiting list guests.

4. **Admin Features**:

    - Import guest data from external files.
    - Track room bookings and attendance status.
    - Provide metrics on disk space usage and site availability.

5. **Notifications**:

    - Send invitations via email.
    - Notify guests on the waiting list about room availability.
    - Admins can send bulk notifications via app or email for event details and updates.

6. **Welcome Page**:

    - Display a greeting message with quick navigation links for RSVP, bookings, and event details.

7. **Media Sharing and News Feed (Phase 2)**:

    - Enable photo and video uploads with captions.
    - Create a shared feed visible to all users.
    - Allow posts to receive "likes."
    - Uploaded content is public, and admins can delete posts if needed.

8. **General Requirements**:

    - Ensure platform stability by separating media handling into a dedicated service.
    - Use Docker Compose for local and cloud-based service orchestration.
    - Operate entirely in Bulgarian.
    - **Queue System**: Critical actions like room booking will be queued during downtime and processed once the system is back online.
    - **Contact Functionality**: Users will have a general email option to reach admins for urgent matters during downtime.
    - **Error Messaging**: A user-friendly error message will inform users about the issue and provide contact instructions.