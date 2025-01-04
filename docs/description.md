### Wedding Site Project Description

The wedding site is designed to provide an engaging and efficient platform for managing wedding event details, guest interactions, and logistical needs. It focuses on creating a functional and visually appealing user experience, catering to both the invited guests and the administrators managing the event. It gives guests a simple way to RSVP, book accommodations, and find all the important details about the day. At the same time, it provides tools for administrators to manage guest lists, track room bookings, and keep everything organized.

The site also allows guests to share photos and updates, creating a space where everyone can connect and celebrate the wedding together. It’s designed to look great and work smoothly, ensuring a positive experience for everyone involved.

---

### Project Phases

#### **Phase 1**

##### **1. Event Information**
- Clear instructions on how to reach the wedding venue.
- Information about nearby accommodation options.
- A detailed wedding day program.
- Guidelines for dress code and gift or donation preferences.
- A comprehensive FAQ section addressing common guest questions.

##### **2. Guest Registration and RSVP**
- Guests can register themselves, their spouse, and their children in a single process.
- Single attendees can add one companion.
- Guests confirm their attendance via RSVP without a deadline.
- Password-protected access for all guests, provided during the invitation process.

##### **3. Accommodation Booking**
- Guests see only the number of rooms available, without additional categorization.
- Guests can book a room only after confirming their attendance.
- Early room reservations for specific guests (e.g., family or distant travelers) for the first 20 days, after which pre-reserved rooms are released.
- A waiting list for room bookings: if a room is released, the next person in the queue is notified via email. The room is reserved for them for 5 days.
- Guests can modify or cancel their room bookings by logging into the site.

##### **4. Admin Features**
- A dashboard to monitor:
    - Disk space usage.
    - Site availability.
    - Key business metrics such as booked rooms and guest attendance statuses.
- Ability to import guest lists via external files with predefined formats.
- View detailed room booking data, including guest information.

##### **5. Notifications**
- Email notifications for:
    - Invitations.
    - Waiting list updates when rooms become available.
    - Admins can send event details to all guests via app notifications or email.

##### **6. Welcome Page**
- Guests see a personalized welcome message upon logging in, with quick links to RSVP, accommodation booking, and event details.
- The page includes an event countdown and direct links to RSVP, bookings, and other features.

#### **Phase 2**

##### **1. Media Sharing and News Feed**
- Guests can upload photos and videos with short captions before, during, and after the wedding.
- A shared news feed displays messages, photos, and videos visible to all logged-in users.
- Posts can receive "likes."
- All uploaded media is public by default, and admins can delete inappropriate content.

---

### Functional Scope
The project ensures that both functionality and design are prioritized to deliver a seamless and visually appealing experience for users. Media and file uploads will be handled by a dedicated service to isolate potential issues and maintain application stability. The entire platform will operate in Bulgarian.

The system will rely on Docker Compose for service orchestration and cloud hosting for scalability and reliability, supporting microservices for specific functionalities like content handling and email notifications.

