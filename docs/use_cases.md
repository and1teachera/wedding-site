### Use Case 1: Register Guests

**Use Case Description**: Allow guests to register for the event using a secure link with a unique parameter and default password.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Guest
- **Stakeholders and Interests**:
  - **Guests**: Want an easy, secure, and intuitive way to register for the event and manage their family’s participation.
  - **Admins**: Need a system that validates and secures guest registrations without requiring additional manual interventions.
- **Preconditions**:
  - Guest has received the secure link and default password from the admin.
  - The link parameter is valid.
- **Postconditions**:
  - Guest account is created with optional family details.
  - Guest can RSVP and optionally book accommodations.
- **Main Success Scenario Step by Step**:
  1. Guest accesses the secure link (`weddings.site/asfhse`).
  2. Sees the "Register Me" button and clicks it.
  3. Enters the default password (`mywedding`).
  4. If the password matches, the system displays a form:
    - Guest fills out their name.
    - If the name exists in the system, the guest provides their email (required) and optionally a phone number.
    - Guest optionally sets a custom password; otherwise, the default password remains active.
  5. System confirms the registration and displays the RSVP page.
  6. Guest RSVPs for the event:
    - Options are "Tentative" (default), "Yes," or "No."
    - Guest can RSVP on behalf of family members, entering optional email and phone details for each.
  7. If RSVP is positive, the system displays an option to book accommodation for the whole family.
  8. Guest completes the process, and the system saves all data.
- **Extensions (Other Scenarios and Alternatives)**:
  - If the password is incorrect, the system displays an error message.
  - If the secure link has expired, the system prompts the guest to contact support.
- **Special Requirements**:
  - Ensure secure validation of the link and parameter.
  - Data input fields must comply with localization standards for Bulgaria.
- **Data Variation List**:
  - Family member data: Name, Email (optional), Phone (optional), RSVP (optional).
  - Password type: Default, Custom.
- **Frequency Occurrence**:
  - One-time action per guest or family.
- **Trigger**:
  - Guest accesses the secure link.
- **Actors**:
  - Guest, System
- **Goals**:
  - Create a secure and streamlined registration process for guests and their families.
- **Failed Endings**:
  - Registration fails due to an invalid link, incorrect password, or missing required fields.
- **Miscellaneous**:
  - Consider adding validation messages for invalid entries.


#### **Flowchart:**

``` mermaid
flowchart TD
    A[Access Secure Link] --> B[Click Register Me]
    B --> C[Enter Default Password]
    C --|Password Matches|--> D[Display Registration Form]
    C --|Password Incorrect|--> E[Show Error Message]
    D --> F[Fill Out Name]
    F --|Name Exists|--> G[Provide Email and Phone]
    F --|Name Does Not Exist|--> H[Submit Form]
    G --> I[Set Custom Password]
    G --|No Custom Password|--> J[Use Default Password]
    I --> J
    J --> H
    H --> K[System Confirms Registration]
    K --> L[Display RSVP Page]
    L --> M[RSVP for Event]
    M --|Positive RSVP|--> N[Option to Book Accommodation]
    M --|Negative RSVP|--> O[Complete Registration]
    N --> O

```


#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Guest
    participant System

    Guest->>System: Access Secure Link
    System-->>Guest: Display "Register Me" Button
    Guest->>System: Click "Register Me"
    Guest->>System: Enter Default Password
    alt Password Valid
        System-->>Guest: Display Registration Form
        Guest->>System: Fill Out Name
        alt Name Exists
            Guest->>System: Provide Email (Required) & Phone (Optional)
        end
        Guest->>System: Submit Form
        System-->>Guest: Display RSVP Page
        Guest->>System: RSVP for Event
        alt Positive RSVP
            System-->>Guest: Display Option to Book Accommodation
        end
        System-->>Guest: Save Data
    else Password Invalid
        System-->>Guest: Show Error Message
    end
```
---

### Use Case 2: Admin Data Import

**Use Case Description**: Allow admins to import guest data using a predefined format.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Admin
- **Stakeholders and Interests**:
  - **Admins**: Want an efficient way to upload guest data in bulk, ensuring accuracy and consistency.
  - **Guests**: Indirectly benefit from preloaded information, simplifying their registration process.
- **Preconditions**:
  - Admin is logged into the system.
  - Guest data file is formatted correctly.
- **Postconditions**:
  - Guest data is successfully imported into the system, including:
    - Name, status (single, couple, couple with children).
    - Family member details.
    - Pre-booked room details (with duration).
- **Main Success Scenario Step by Step**:
  1. Admin logs into the platform and navigates to the Data Import section.
  2. Uploads the guest data file.
  3. System validates the file format and content.
  4. If valid, the system imports guest data and confirms success.
  5. Admin views the imported data summary for verification.
- **Extensions (Other Scenarios and Alternatives)**:
  - If the file format is invalid, the system rejects the upload and displays error details.
  - If some records fail validation, the system highlights problematic rows for correction.
- **Special Requirements**:
  - Supported file format: CSV.
  - File must include predefined fields:
    - Name, Status, Family Data, Pre-booked Room, Booking Duration (optional, defaults to 10 days).
  - Provide error logs for invalid imports.
- **Data Variation List**:
  - Guest status: Single, Couple, Couple with Children.
  - Pre-booked room duration: Default (10 days), Custom.
- **Frequency Occurrence**:
  - Occurs during initial setup or when updating guest lists.
- **Trigger**:
  - Admin navigates to the Data Import section.
- **Actors**:
  - Admin, System
- **Goals**:
  - Simplify guest data management for admins.
- **Failed Endings**:
  - Import fails due to invalid file format, corrupted file, or missing required fields.
- **Miscellaneous**:
  - Consider implementing a preview mode for admins to verify data before finalizing the import.
- **Suggested Additional Use Cases**:
  - Export Guest Data: Allow admins to download the current guest list for external use.

#### **Flowchart:**

``` mermaid
flowchart TD
    A[Admin Logs In] --> B[Navigates to Data Import Section]
    B --> C[Uploads Guest Data File]
    C --> D[System Validates File Format and Content]
    D -->|Valid File| E[Import Data to System]
    D -->|Invalid File| F[Reject Upload with Error Details]
    E --> G[Show Import Success Message]
    E --> H[Admin Reviews Imported Data]
    F --> I[Highlight Problematic Rows for Correction]

```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Admin
    participant System

    Admin->>System: Log In
    Admin->>System: Navigate to Data Import Section
    Admin->>System: Upload Guest Data File
    System-->>Admin: Validate File Format and Content
    alt File Valid
        System-->>Admin: Import Data Successfully
        Admin->>System: Review Imported Data
    else File Invalid
        System-->>Admin: Show Error Details
        System-->>Admin: Highlight Problematic Rows
    end
```


---
### Use Case 3: Confirm RSVP

**Use Case Description**: Enable guests to confirm their attendance at the wedding.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Guest
- **Stakeholders and Interests**:
  - Guests: Want a simple way to confirm their attendance.
  - Admins: Need an accurate headcount for planning purposes.
- **Preconditions**:
  - Guest is registered and logged into the system.
- **Postconditions**:
  - RSVP status is saved in the system.
- **Main Success Scenario**:
  1. Guest logs into the platform.
  2. Navigates to the RSVP page.
  3. Confirms attendance and provides details of attendees (e.g., spouse, children, or companion).
  4. System updates the RSVP status and displays a confirmation message.
- **Extensions (Other Scenarios and Alternatives)**:
  - Guest decides to cancel their RSVP and updates it later.
- **Special Requirements**:
  - Ensure clear messaging about RSVP deadlines, if any.
- **Data Variation List**:
  - Number of attendees: single, couple, or family.
- **Frequency Occurrence**:
  - One-time action but can be updated if needed.
- **Trigger**:
  - Guest logs in to confirm RSVP.
- **Actors**:
  - Guest, System
- **Goals**:
  - Provide admins with an accurate attendance count.
- **Failed Endings**:
  - RSVP fails to save due to a system error.

#### **Flowchart:**

``` mermaid
flowchart TD
    A[Guest Logs In] --> B[Navigates to RSVP Page]
    B --> C[Selects RSVP Status]
    C --|Positive RSVP|--> D[Enter Attendee Details Optional]
    C --|Negative RSVP|--> E[Submit RSVP]
    D --> F[Submit RSVP with Details]
    F --> G[Save RSVP Status and Details]
    E --> G
    G --> H[Display Confirmation Message]

```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Guest
    participant System

    Guest->>System: Log In
    Guest->>System: Navigate to RSVP Page
    Guest->>System: Select RSVP Status
    alt Positive RSVP
        Guest->>System: Enter Attendee Details
    end
    Guest->>System: Submit RSVP
    System-->>Guest: Save RSVP Status
    System-->>Guest: Display Confirmation Message
```
---

### Use Case 4: Book Accommodation

**Use Case Description**: Allow guests to book a room or join a waiting list, ensuring the process is robust and concurrent actions are handled gracefully.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Guest
- **Stakeholders and Interests**:
  - **Guests**: Want a smooth booking process and assurance that their booking is secure.
  - **Admins**: Require accurate and reliable room allocation data.
- **Preconditions**:
  - Guest has confirmed their RSVP.
  - Guest is logged into the system.
- **Postconditions**:
  - Room booking is saved, or the guest is added to the waiting list.
- **Main Success Scenario Step by Step**:
  1. Guest navigates to the accommodation booking page.
  2. Views the list of available rooms.
  3. Selects a room, which is immediately locked for 15 minutes.
  4. Confirms the booking within the 15-minute window.
  5. RabbitMQ processes the booking data to ensure reliability.
  6. System saves the booking and updates room availability.
  7. If all rooms are taken, the system notifies the guest and offers the option to join the waiting list.
- **Extensions (Other Scenarios and Alternatives)**:
  - If the guest does not confirm within 15 minutes, the room is unlocked and becomes available to others.
  - If the guest attempts to select a room that is locked by another guest, the system displays a notification explaining the unavailability.
- **Special Requirements**:
  - Integrate RabbitMQ for processing booking data to prevent data loss.
  - Implement a locking mechanism for rooms with a 15-minute timer.
- **Data Variation List**:
  - Room status: Available, Locked, Booked.
  - Guest status: On Waiting List, Booking Confirmed.
- **Frequency Occurrence**:
  - Regularly during the RSVP period.
- **Trigger**:
  - Guest navigates to the accommodation booking page.
- **Actors**:
  - Guest, System, RabbitMQ.
- **Goals**:
  - Ensure a reliable and fair booking process for all guests.
- **Failed Endings**:
  - Booking fails due to system errors or RabbitMQ unavailability.
- **Miscellaneous**:
  - Consider adding a countdown timer for the 15-minute lock visible to guests.
- **Suggested Additional Use Cases**:
  - Notify Admin of Booking Errors: Alert admins if RabbitMQ fails to process bookings.
#### **Flowchart:**

``` mermaid
flowchart TD
    A[Guest Logs In] --> B[Navigates to Accommodation Booking Page]
    B --> C[Views List of Available Rooms]
    C --> D[Selects a Room]
    D --> E[System Locks Room for 15 Minutes]
    E -->|Confirms Booking| F[RabbitMQ Processes Booking]
    F --> G[Saves Booking and Updates Availability]
    E -->|Does Not Confirm in 15 Minutes| H[Room Unlocked]
    C -->|All Rooms Taken| I[Offers Option to Join Waiting List]
    I --> J[Guest Joins Waiting List]
    J --> G
```


#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Guest
    participant System
    participant RabbitMQ

    Guest->>System: Log In
    Guest->>System: Navigate to Booking Page
    System-->>Guest: Display List of Available Rooms
    Guest->>System: Select a Room
    System-->>Guest: Lock Room for 15 Minutes
    alt Confirms Booking
        Guest->>System: Confirm Booking
        System->>RabbitMQ: Process Booking Data
        RabbitMQ-->>System: Confirm Booking Success
        System-->>Guest: Display Confirmation
    else Does Not Confirm
        System-->>System: Unlock Room After 15 Minutes
    end
    alt No Rooms Available
        System-->>Guest: Notify All Rooms Taken
        Guest->>System: Join Waiting List
        System-->>Guest: Confirmation of Waitlist Status
    end
```


---

### Use Case 5: Manage Waiting List

**Use Case Description**: Notify guests on the waiting list when a room becomes available and allow them to book it.

- **Scope**: Wedding Site
- **Level**: Subfunction
- **Primary Actor**: System
- **Stakeholders and Interests**:
  - Guests: Want timely notifications when a room becomes available.
  - Admins: Require automated handling of the waiting list to reduce manual work.
- **Preconditions**:
  - At least one guest is on the waiting list.
  - A room has been released.
- **Postconditions**:
  - Guest on the waiting list is notified, and the room is reserved for 5 days.
- **Main Success Scenario**:
  1. System detects a released room.
  2. Notifies the first guest on the waiting list via email.
  3. Reserves the room for the notified guest for 5 days.
  4. Guest logs in and confirms the booking within the reservation period.
  5. System saves the booking and updates the waiting list.
- **Extensions (Other Scenarios and Alternatives)**:
  - If the notified guest does not confirm within 5 days, the room is offered to the next person on the waiting list.
  - If no guests on the waiting list confirm, the room becomes available for general booking.
- **Special Requirements**:
  - Ensure email notifications include clear instructions and deadlines.
- **Data Variation List**:
  - Reservation hold period: Default 5 days.
- **Frequency Occurrence**:
  - As needed, whenever rooms are released.
- **Trigger**:
  - Room cancellation or expiration of a reservation hold.
- **Actors**:
  - Guest, System
- **Goals**:
  - Streamline room allocation and minimize delays in booking.
- **Failed Endings**:
  - Notification email fails to send, or no guests confirm the room within the hold period.
#### **Flowchart:**

``` mermaid
flowchart TD
    A[Room Released] --> B[System Detects Availability]
    B --> C[Notifies First Guest on Waiting List]
    C --> D[Reserves Room for 5 Days]
    D -->|Guest Confirms| E[Saves Booking and Updates Waiting List]
    D -->|No Confirmation in 5 Days| F[Room Offered to Next Guest]
    F -->|No Guests Confirm| G[Room Becomes Available for General Booking]
```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant System
    participant Guest

    System->>System: Detect Room Release
    System->>Guest: Send Notification
    System-->>Guest: Reserve Room for 5 Days
    alt Guest Confirms Booking
        Guest->>System: Confirm Booking
        System-->>Guest: Save Booking
        System-->>System: Update Waiting List
    else No Confirmation
        System-->>System: Offer Room to Next Guest
        alt No Guests Confirm
            System-->>System: Mark Room as Available
        end
    end
```


---

### Use Case 6: Modify Bookings

**Use Case Description**: Allow guests to update or cancel their room bookings after confirmation.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Guest
- **Stakeholders and Interests**:
  - Guests: Want flexibility to change their accommodation plans as needed.
  - Admins: Need updated booking information for accurate planning.
- **Preconditions**:
  - Guest has a confirmed booking.
  - Guest is logged into the system.
- **Postconditions**:
  - Updated booking information is saved, or the room is released and added to the available pool or waiting list.
- **Main Success Scenario**:
  1. Guest logs into the platform.
  2. Navigates to the booking management.
  3. Updates their booking details or cancels the booking.
  4. System saves the changes and displays a confirmation message.
  5. If the booking is canceled, the system triggers the waiting list process or makes the room available for general booking.
- **Extensions (Other Scenarios and Alternatives)**:
  - If a technical issue occurs, the system logs the error and instructs the guest to contact support.
- **Special Requirements**:
  - Ensure changes are reflected immediately in the system.
- **Data Variation List**:
  - Booking status: Updated, Canceled.
- **Frequency Occurrence**:
  - As needed.
- **Trigger**:
  - Guest navigates to the booking management page.
- **Actors**:
  - Guest, System
- **Goals**:
  - Provide flexibility for guests while keeping room availability data accurate.
- **Failed Endings**:
  - Update fails due to system error or invalid input.

#### **Flowchart:**

``` mermaid
flowchart TD
    A[Guest Logs In] --> B[Navigates to Booking Management]
    B --> C[Selects Booking to Modify or Cancel]
    C -->|Modify Booking| D[Updates Booking Details]
    C -->|Cancel Booking| E[Releases Room]
    D --> F[Saves Updated Booking]
    E --> G[Triggers Waiting List Process or Makes Room Available]
    F --> H[Display Confirmation Message]
    G --> H
```


#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Guest
    participant System

    Guest->>System: Log In
    Guest->>System: Navigate to Booking Management
    Guest->>System: Select Booking
    alt Modify Booking
        Guest->>System: Update Booking Details
        System-->>Guest: Save Updated Booking
    else Cancel Booking
        Guest->>System: Confirm Cancellation
        System-->>System: Release Room
        System-->>System: Trigger Waiting List or Mark Room Available
    end
    System-->>Guest: Display Confirmation Message
```

---

### Use Case 7: Send Notifications

**Use Case Description**: Allow admins to send notifications to all guests with important event updates or reminders.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Admin
- **Stakeholders and Interests**:
  - Guests: Want to stay informed about any updates or changes related to the event.
  - Admins: Need an easy way to communicate with all guests simultaneously.
- **Preconditions**:
  - Admin is logged into the system.
- **Postconditions**:
  - Notifications are sent to all selected guests via app or email.
- **Main Success Scenario**:
  1. Admin logs into the platform.
  2. Navigates to the notifications management page.
  3. Creates a new notification, specifying recipients and content.
  4. System sends the notification to all selected guests.
  5. Admin receives a confirmation of successful delivery.
- **Extensions (Other Scenarios and Alternatives)**:
  - If some notifications fail to send, the system retries or logs the error for admin review.
- **Special Requirements**:
  - Support bulk notification delivery with minimal delay.
- **Data Variation List**:
  - Notification type: App, Email.
- **Frequency Occurrence**:
  - As needed.
- **Trigger**:
  - Admin decides to send an update or reminder.
- **Actors**:
  - Admin, System
- **Goals**:
  - Ensure effective communication with all guests.
- **Failed Endings**:
  - Notifications fail due to connectivity issues or incorrect recipient data.

#### **Flowchart:**

``` mermaid
flowchart TD
    A[Admin Logs In] --> B[Navigates to Notifications Management]
    B --> C[Creates New Notification]
    C --> D[Specifies Recipients and Content]
    D --> E[System Sends Notification]
    E -->|Success| F[Confirm Delivery to Admin]
    E -->|Failure| G[Log Errors and Retry Sending]
    G --> F
```


#### **Sequence Diagram:**

``` mermaid 
sequenceDiagram
    participant Admin
    participant System

    Admin->>System: Log In
    Admin->>System: Navigate to Notifications Management
    Admin->>System: Create New Notification
    Admin->>System: Specify Recipients and Content
    System->>System: Process Notification Delivery
    alt Delivery Success
        System-->>Admin: Confirm Delivery Success
    else Delivery Failure
        System-->>Admin: Log Errors and Retry Sending
    end
```

---

### Use Case 8: View Event Information

**Use Case Description**: Allow guests to access important event details such as venue location, schedule, dress code, and FAQ.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Guest
- **Stakeholders and Interests**:
  - Guests: Want easy access to all the details they need to prepare for the wedding.
  - Admins: Need to ensure guests are well-informed without frequent direct inquiries.
- **Preconditions**:
  - Guest is logged into the system.
- **Postconditions**:
  - Event information is displayed to the guest.
- **Main Success Scenario**:
  1. Guest logs into the platform.
  2. Navigates to the event information page.
  3. Views details including venue address, wedding schedule, dress code, and FAQ.
  4. System ensures the information is displayed clearly and correctly.
- **Extensions (Other Scenarios and Alternatives)**:
  - If the guest is logged out during access, the system redirects them to the login page.
- **Special Requirements**:
  - Use responsive design for mobile-friendly access.
- **Data Variation List**:
  - Information type: Venue, Schedule, Dress Code, FAQ.
- **Frequency Occurrence**:
  - As needed, typically leading up to the wedding date.
- **Trigger**:
  - Guest accesses the event information page.
- **Actors**:
  - Guest, System
- **Goals**:
  - Provide all necessary details to guests in a single place.
- **Failed Endings**:
  - Information fails to load due to system error.


#### **Flowchart:**

``` mermaid
flowchart TD
    A[Guest Logs In] --> B[Navigates to Event Information Page]
    B --> C[Displays Venue Details]
    B --> D[Displays Wedding Schedule]
    B --> E[Displays Dress Code]
    B --> F[Displays FAQ]
    C --> G[Information Displayed Clearly]
    D --> G
    E --> G
    F --> G
    G --> H[Guest Reads Information]
```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Guest
    participant System

    Guest->>System: Log In
    Guest->>System: Navigate to Event Information Page
    System-->>Guest: Load Venue Details
    System-->>Guest: Load Wedding Schedule
    System-->>Guest: Load Dress Code
    System-->>Guest: Load FAQ
    Guest->>System: View Details
```
---

### Use Case 9: Handle Downtime

**Use Case Description**: Ensure critical functionalities are handled gracefully during system outages.

- **Scope**: Wedding Site
- **Level**: Subfunction
- **Primary Actor**: System
- **Stakeholders and Interests**:
  - Guests: Need assurances that their actions (e.g., booking) will not be lost during downtime.
  - Admins: Want minimal disruption to guest experience and event planning.
- **Preconditions**:
  - System outage is detected.
- **Postconditions**:
  - Critical actions are queued, and guests are informed of the downtime.
- **Main Success Scenario**:
  1. System detects downtime or a critical error.
  2. Guests attempting to perform actions like booking or RSVP are informed of the downtime via an error message.
  3. Actions such as room booking are queued for processing once the system is restored.
  4. Guests are provided a contact option to email admins for urgent assistance.
  5. Admins receive notifications about the downtime and queued actions.
- **Extensions (Other Scenarios and Alternatives)**:
  - If the system remains down for an extended period, admins can manually communicate updates to guests.
- **Special Requirements**:
  - Queue mechanism to save guest actions during downtime.
  - Error messages should include clear instructions and contact options.
- **Data Variation List**:
  - Action type: Room Booking, RSVP.
- **Frequency Occurrence**:
  - Rare, but critical when it happens.
- **Trigger**:
  - System detects an outage or critical failure.
- **Actors**:
  - Guest, Admin, System
- **Goals**:
  - Minimize disruption to guest experience during downtime.
- **Failed Endings**:
  - Actions are lost due to queuing system failure.


#### **Flowchart:**

``` mermaid
flowchart TD
    A[System Detects Downtime] --> B[Display Error Message to Guests]
    B --> C[Queue Critical Actions for Later Processing]
    C --> D[Provide Guests with Admin Contact Information]
    D --> E[Notify Admin of Downtime and Queued Actions]
    C --> F[System Restores Availability]
    F --> G[Process Queued Actions]
    G --> H[Inform Guests of Completed Actions]
```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Guest
    participant System
    participant Admin

    System->>System: Detect Downtime
    Guest->>System: Attempt Action (e.g., Room Booking)
    System-->>Guest: Display Error Message
    System->>System: Queue Action for Processing
    System-->>Guest: Provide Admin Contact Info
    System->>Admin: Notify of Downtime and Queued Actions
    alt System Restored
        System->>System: Process Queued Actions
        System-->>Guest: Inform of Completed Actions
    end
```

---

### Use Case 10: Monitor Event Metrics

**Use Case Description**: Allow admins to view key metrics such as room bookings, RSVP statuses, and disk space usage.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Admin
- **Stakeholders and Interests**:
  - Admins: Need an overview of key metrics to manage the event effectively.
  - Guests: Indirectly benefit from the admin's ability to address any issues promptly.
- **Preconditions**:
  - Admin is logged into the system.
- **Postconditions**:
  - Metrics are displayed on the admin dashboard.
- **Main Success Scenario**:
  1. Admin logs into the platform and navigates to the admin dashboard.
  2. Dashboard displays real-time metrics, including:
    - Number of booked rooms and available rooms.
    - RSVP status counts (confirmed, not coming).
    - Disk space usage for media uploads.
    - System uptime or downtime statistics.
  3. Admin can view details or trends for specific metrics as needed.
- **Extensions (Other Scenarios and Alternatives)**:
  - If a metric fails to load, the system displays an error message and retries.
  - If the admin notices an issue, they can initiate troubleshooting or contact support.
- **Special Requirements**:
  - Ensure metrics are updated in real-time or at regular intervals.
- **Data Variation List**:
  - Metrics type: Bookings, RSVP, Disk Space, System Uptime.
- **Frequency Occurrence**:
  - Regularly, especially during peak event planning times.
- **Trigger**:
  - Admin navigates to the dashboard.
- **Actors**:
  - Admin, System
- **Goals**:
  - Provide admins with actionable insights to manage the event effectively.
- **Failed Endings**:
  - Metrics fail to load due to system errors or connectivity issues.

#### **Flowchart:**

``` mermaid
flowchart TD
    A[Admin Logs In] --> B[Navigates to Admin Dashboard]
    B --> C[Display Real-Time Metrics]
    C --> D[Bookings Data]
    C --> E[RSVP Status Data]
    C --> F[Disk Space Usage]
    C --> G[System Uptime/Downtime Statistics]
    D --> H[Admin Views Booking Trends]
    E --> H
    F --> H
    G --> H
    H --> I[Admin Identifies Issues or Trends]
```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Admin
    participant System

    Admin->>System: Log In
    Admin->>System: Navigate to Admin Dashboard
    System-->>Admin: Load Real-Time Metrics
    alt Metrics Load Successfully
        System-->>Admin: Display Metrics (Bookings, RSVP, Disk Space, Uptime)
        Admin->>System: View Trends or Details
    else Metrics Fail to Load
        System-->>Admin: Show Error and Retry
    end
```

---

### Use Case 11: Send Notifications

**Use Case Description**: Allow admins to send notifications to guests with important updates or reminders.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Admin
- **Stakeholders and Interests**:
  - Guests: Want to stay informed about any updates related to the event.
  - Admins: Need an efficient way to communicate updates to all guests.
- **Preconditions**:
  - Admin is logged into the system.
- **Postconditions**:
  - Notifications are sent to all selected guests via email or app notifications.
- **Main Success Scenario**:
  1. Admin logs into the platform and navigates to the notifications section.
  2. Creates a new notification by specifying:
    - Recipient group (all guests, RSVP-confirmed guests, etc.).
    - Notification content.
    - Delivery method (email, app notification, or both).
  3. System sends the notification to the specified recipients.
  4. Admin receives a confirmation of successful delivery.
- **Extensions (Other Scenarios and Alternatives)**:
  - If some notifications fail to send, the system retries or logs the errors for admin review.
- **Special Requirements**:
  - Allow admins to preview the notification before sending.
  - Support bulk delivery with minimal delay.
- **Data Variation List**:
  - Recipient type: All guests, RSVP-confirmed only.
  - Delivery method: Email, App Notification, Both.
- **Frequency Occurrence**:
  - As needed, especially leading up to the wedding day.
- **Trigger**:
  - Admin decides to send an update or reminder.
- **Actors**:
  - Admin, System
- **Goals**:
  - Ensure effective communication with guests.
- **Failed Endings**:
  - Notifications fail due to system errors or incorrect recipient data.

#### **Flowchart:**

``` mermaid
flowchart TD
    A[Admin Logs In] --> B[Navigates to Notifications Section]
    B --> C[Creates New Notification]
    C --> D[Specifies Recipients and Content]
    D --> E[Chooses Delivery Method]
    E --> F[System Sends Notification]
    F -->|Success| G[Confirmation Sent to Admin]
    F -->|Failure| H[Log Errors and Retry Sending]
    H --> G
```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Admin
    participant System

    Admin->>System: Log In
    Admin->>System: Navigate to Notifications Section
    Admin->>System: Create New Notification
    Admin->>System: Specify Recipients and Content
    Admin->>System: Choose Delivery Method (Email/App/Both)
    System->>System: Process Notification Delivery
    alt Delivery Success
        System-->>Admin: Confirm Successful Delivery
    else Delivery Failure
        System-->>Admin: Log Errors and Retry Sending
    end
```

---

### Use Case 12: Guest Login and Password Recovery

**Use Case Description**: Allow guests to log in to the platform or recover access if their password is lost.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Guest
- **Stakeholders and Interests**:
  - **Guests**: Want secure, reliable access to manage their RSVPs, bookings, and other details.
  - **Admins**: Need to ensure guests can access their accounts without compromising security.
- **Preconditions**:
  - Guest has a registered account.
  - Login credentials (email) are available for login flow.
- **Postconditions**:
  - User session is established for logged-in users.
  - Password recovery emails are sent for forgotten credentials.
- **Main Success Scenario Step by Step**:
  1. Guest navigates to the login page.
  2. If the identifier (e.g., `weddings.site/asfhse`) is provided, the page shows both a **Login** button and a **Register Me** button.
  3. Guest enters their credentials (username/password) and clicks Login.
  4. System validates credentials using Spring Security and establishes a session.
  5. If successful, guest is redirected to their dashboard.
  6. If credentials are invalid, the system displays an error message.
  7. If the guest has forgotten their password, they click “Forgot Password,” enter their email, and receive a recovery email with instructions.
- **Extensions (Other Scenarios and Alternatives)**:
  - Guest session expires, requiring re-login.
  - Guest uses default password and decides to change it after logging in.
  - If email for recovery is invalid, system provides an error message.
- **Special Requirements**:
  - Implement session management using Spring Security.
  - Ensure secure password reset flows (e.g., one-time tokens).
- **Data Variation List**:
  - Login source: Identifier-based, Default page.
- **Frequency Occurrence**:
  - Regularly for login, occasionally for password recovery.
- **Trigger**:
  - Guest navigates to the login page or requests password recovery.
- **Actors**:
  - Guest, System
- **Goals**:
  - Ensure guests can securely log in or recover access if needed.
- **Failed Endings**:
  - Login or password recovery fails due to invalid inputs or system errors.

#### **Flowchart:**

``` mermaid
flowchart TD
    A[Guest Navigates to Login Page] --> B[Enters Credentials]
    B -->|Valid Credentials| C[System Establishes Session]
    C --> D[Guest Redirected to Dashboard]
    B -->|Invalid Credentials| E[Display Error Message]
    B -->|Forgot Password| F[Click Forgot Password]
    F --> G[Enter Email for Recovery]
    G -->|Valid Email| H[System Sends Recovery Email]
    G -->|Invalid Email| I[Display Error Message]
    H --> J[Guest Resets Password via Email Link]

```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Guest
    participant System

    Guest->>System: Navigate to Login Page
    Guest->>System: Enter Username and Password
    alt Valid Credentials
        System-->>Guest: Establish Session
        System-->>Guest: Redirect to Dashboard
    else Invalid Credentials
        System-->>Guest: Display Error Message
    end
    alt Forgot Password
        Guest->>System: Click "Forgot Password"
        Guest->>System: Enter Email Address
        alt Valid Email
            System-->>Guest: Send Recovery Email
            Guest->>System: Reset Password via Link
            System-->>Guest: Confirm Password Reset
        else Invalid Email
            System-->>Guest: Display Error Message
        end
    end
```
---

### Use Case 13: Admin Login

**Use Case Description**: Allow the admin to securely log in to the system.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Admin
- **Stakeholders and Interests**:
  - **Admins**: Need secure access to manage guest data, events, and system metrics.
  - **Guests**: Indirectly benefit from an admin’s ability to ensure smooth operations.
- **Preconditions**:
  - Admin has valid login credentials.
- **Postconditions**:
  - Admin session is established.
- **Main Success Scenario Step by Step**:
  1. Admin navigates to the admin login page (separate from the guest login page).
  2. Enters their credentials (username/password) and clicks Login.
  3. System validates credentials using Spring Security and establishes a session.
  4. If successful, admin is redirected to the admin dashboard.
  5. If credentials are invalid, the system displays an error message.
- **Extensions (Other Scenarios and Alternatives)**:
  - Admin session expires, requiring re-login.
  - If an admin forgets their credentials, they request a recovery email, which includes a reset link.
- **Special Requirements**:
  - Ensure admin credentials are stored securely (e.g., hashed passwords).
  - Implement stricter session timeouts for admins.
- **Data Variation List**:
  - Admin access level: Admin.
- **Frequency Occurrence**:
  - Daily or as needed.
- **Trigger**:
  - Admin navigates to the admin login page.
- **Actors**:
  - Admin, System
- **Goals**:
  - Securely log the admin into the system.
- **Failed Endings**:
  - Login fails due to invalid credentials or system errors.

#### **Flowchart:**

``` mermaid
flowchart TD
    A[Admin Navigates to Login Page] --> B[Enters Credentials]
    B -->|Valid Credentials| C[System Establishes Session]
    C --> D[Admin Redirected to Dashboard]
    B -->|Invalid Credentials| E[Display Error Message]
    B -->|Forgot Password| F[Request Recovery Email]
    F --> G[Enter Email for Recovery]
    G -->|Valid Email| H[System Sends Recovery Email]
    G -->|Invalid Email| I[Display Error Message]
    H --> J[Admin Resets Password via Email Link]
```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Admin
    participant System

    Admin->>System: Navigate to Admin Login Page
    Admin->>System: Enter Username and Password
    alt Valid Credentials
        System-->>Admin: Establish Session
        System-->>Admin: Redirect to Dashboard
    else Invalid Credentials
        System-->>Admin: Display Error Message
    end
    alt Forgot Password
        Admin->>System: Request Recovery Email
        Admin->>System: Enter Email Address
        alt Valid Email
            System-->>Admin: Send Recovery Email
            Admin->>System: Reset Password via Link
            System-->>Admin: Confirm Password Reset
        else Invalid Email
            System-->>Admin: Display Error Message
        end
    end
```

---
### Use Case 14: Upload Media

**Use Case Description**: Allow guests to upload photos and videos to the shared news feed.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Guest
- **Stakeholders and Interests**:
  - Guests: Want to share moments from the wedding before, during, and after the event.
  - Admins: Need to ensure uploaded content aligns with the event’s tone and appropriateness.
- **Preconditions**:
  - Guest is logged into the system.
  - Media file meets format and size requirements.
- **Postconditions**:
  - Uploaded media appears on the news feed.
- **Main Success Scenario**:
  1. Guest logs into the platform.
  2. Navigates to the news feed page.
  3. Selects a photo or video file and enters a caption.
  4. System verifies the file format and size.
  5. Media is uploaded, processed, and displayed in the news feed.
- **Extensions (Other Scenarios and Alternatives)**:
  - If the file exceeds size limits or is in an unsupported format, the system prompts the guest to try again.
  - If upload fails due to connectivity issues, the system retries or informs the guest to try later.
- **Special Requirements**:
  - Support for basic media formats (e.g., JPG, PNG, MP4).
  - Display a progress bar during uploads.
- **Data Variation List**:
  - Media type: Photo, Video.
  - File size: Up to predefined limits.
- **Frequency Occurrence**:
  - As desired, before, during, and after the wedding.
- **Trigger**:
  - Guest navigates to the upload page and selects a media file.
- **Actors**:
  - Guest, System
- **Goals**:
  - Allow guests to share memorable moments via the platform.
- **Failed Endings**:
  - Upload fails due to file errors or system issues.

#### **Flowchart:**

``` mermaid
flowchart TD
    A[Guest Logs In] --> B[Navigates to News Feed Page]
    B --> C[Selects Photo or Video File]
    C --> D[Enters Caption]
    D --> E[System Verifies File Format and Size]
    E -->|Valid File| F[Uploads and Processes Media]
    F --> G[Displays Media in News Feed]
    E -->|Invalid File| H[Displays Error Message and Retry Option]
    F -->|Upload Fails| I[Retry Upload or Inform Guest]
```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Guest
    participant System

    Guest->>System: Log In
    Guest->>System: Navigate to News Feed Page
    Guest->>System: Select Media File and Enter Caption
    System->>System: Verify File Format and Size
    alt Valid File
        System->>System: Process Media Upload
        System-->>Guest: Display Media in News Feed
    else Invalid File
        System-->>Guest: Display Error Message and Retry Option
    end
    alt Upload Fails
        System-->>Guest: Retry or Inform of Failure
    end
```


---

### Use Case 15: View News Feed

**Use Case Description**: Allow guests to view the shared news feed with photos, videos, and messages.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Guest
- **Stakeholders and Interests**:
  - Guests: Want to see shared moments from other attendees.
  - Admins: Need to ensure all content displayed is appropriate and within guidelines.
- **Preconditions**:
  - Guest is logged into the system.
  - Media exists in the feed.
- **Postconditions**:
  - Guest views the latest posts and interacts with them by scrolling through or liking them.
- **Main Success Scenario**:
  1. Guest logs into the platform.
  2. Navigates to the news feed page.
  3. Views the latest shared posts.
  4. Scrolls down to load older posts using lazy loading.
- **Extensions (Other Scenarios and Alternatives)**:
  - If no media is available, the system displays a placeholder message like “No posts yet.”
  - If lazy loading fails, the system displays an error message with a retry option.
- **Special Requirements**:
  - Use lazy loading to minimize server load and improve user experience.
- **Data Variation List**:
  - Post type: Photo, Video, Text.
- **Frequency Occurrence**:
  - Regularly, before, during, and after the wedding.
- **Trigger**:
  - Guest navigates to the news feed page.
- **Actors**:
  - Guest, System
- **Goals**:
  - Provide guests with a dynamic and interactive feed of shared wedding moments.
- **Failed Endings**:
  - News feed fails to load due to system issues.

#### **Flowchart:**

``` mermaid
flowchart TD
    A[Guest Logs In] --> B[Navigates to News Feed Page]
    B --> C[Displays Latest Posts]
    C --> D[Guest Scrolls to View Older Posts]
    D -->|Lazy Loading Successful| E[Displays Older Posts]
    D -->|Lazy Loading Fails| F[Displays Error Message and Retry Option]
    C -->|No Posts Available| G[Displays Placeholder Message]
```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Guest
    participant System

    Guest->>System: Log In
    Guest->>System: Navigate to News Feed Page
    System-->>Guest: Display Latest Posts
    alt Lazy Loading Successful
        Guest->>System: Scroll to View Older Posts
        System-->>Guest: Load and Display Older Posts
    else Lazy Loading Fails
        System-->>Guest: Display Error Message with Retry Option
    end
    alt No Posts Available
        System-->>Guest: Display Placeholder Message
    end
```

---

### Use Case 16: Delete Inappropriate Media

**Use Case Description**: Allow admins to delete posts directly from the news feed, with deleted posts stored on a separate page for further action.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Admin
- **Stakeholders and Interests**:
  - **Admins**: Want to remove inappropriate posts efficiently while retaining the option to review and hard delete them later.
  - **Guests**: Want the news feed to remain appropriate and focused.
- **Preconditions**:
  - Admin is logged into the system.
  - Posts exist in the news feed.
- **Postconditions**:
  - Post is removed from the public news feed and stored in a deleted posts page.
- **Main Success Scenario Step by Step**:
  1. Admin logs into the platform and navigates to the news feed.
  2. Identifies an inappropriate post.
  3. Clicks the delete icon next to the post.
  4. System removes the post from the public feed and stores it in the deleted posts page.
  5. Admin navigates to the deleted posts page to review all removed posts.
  6. Admin optionally performs a hard delete on any post from the deleted posts page.
- **Extensions (Other Scenarios and Alternatives)**:
  - If the admin mistakenly deletes a post, it remains recoverable from the deleted posts page until a hard delete is performed.
- **Special Requirements**:
  - Add a delete icon visible only to admins on each post.
  - Deleted posts page should display post details and deletion timestamps.
- **Data Variation List**:
  - Post type: Photo, Video, Text.
- **Frequency Occurrence**:
  - As needed.
- **Trigger**:
  - Admin identifies a post to delete.
- **Actors**:
  - Admin, System
- **Goals**:
  - Ensure inappropriate posts are quickly removed from public view but remain accessible for admin review.
- **Failed Endings**:
  - Post fails to move to the deleted posts page due to system error.
#### **Flowchart:**

``` mermaid
flowchart TD
    A[Admin Logs In] --> B[Navigates to News Feed]
    B --> C[Identifies Inappropriate Post]
    C --> D[Clicks Delete Icon]
    D --> E[System Removes Post from News Feed]
    E --> F[Stores Post in Deleted Posts Page]
    F --> G[Admin Reviews Deleted Posts]
    G --> H[Optionally Performs Hard Delete]
    F -->|Post Mistakenly Deleted| I[Post Remains Recoverable]

```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Admin
    participant System

    Admin->>System: Log In
    Admin->>System: Navigate to News Feed
    Admin->>System: Identify Inappropriate Post
    Admin->>System: Click Delete Icon
    System-->>Admin: Confirm Post Removal
    System->>System: Remove Post from News Feed
    System->>System: Store Post in Deleted Posts Page
    Admin->>System: Navigate to Deleted Posts Page
    Admin->>System: Review Deleted Posts
    alt Perform Hard Delete
        Admin->>System: Hard Delete Post
        System-->>Admin: Confirm Permanent Removal
    else Do Not Perform Hard Delete
        System-->>Admin: Post Remains in Deleted Posts
    end
```


---

### Use Case 17: Like Posts

**Use Case Description**: Allow guests to like posts on the news feed.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Guest
- **Stakeholders and Interests**:
  - Guests: Want to interact with posts and express appreciation for shared content.
  - Admins: Indirectly benefit by seeing higher guest engagement.
- **Preconditions**:
  - Guest is logged into the system.
  - Media exists in the feed.
- **Postconditions**:
  - The like count is incremented for the selected post.
- **Main Success Scenario**:
  1. Guest logs into the platform.
  2. Navigates to the news feed.
  3. Selects a post to like.
  4. Clicks the like button.
  5. System updates the like count for the post and displays the updated count.
- **Extensions (Other Scenarios and Alternatives)**:
  - If the guest clicks the like button again, the system removes their like and decrements the count.
- **Special Requirements**:
  - Ensure real-time updates to the like count.
- **Data Variation List**:
  - Post type: Photo, Video, Text.
- **Frequency Occurrence**:
  - Regularly, as guests interact with the news feed.
- **Trigger**:
  - Guest clicks the like button on a post.
- **Actors**:
  - Guest, System
- **Goals**:
  - Enable guests to engage with the news feed interactively.
- **Failed Endings**:
  - Like fails to register due to system issues.

#### **Flowchart:**

``` mermaid
flowchart TD
    A[Guest Logs In] --> B[Navigates to News Feed]
    B --> C[Selects a Post]
    C --> D[Clicks Like Button]
    D --> E[System Updates Like Count]
    E --> F[Displays Updated Like Count]
    D -->|Click Again| G[System Removes Like and Updates Count]
    G --> F
```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Guest
    participant System

    Guest->>System: Log In
    Guest->>System: Navigate to News Feed
    Guest->>System: Select Post
    Guest->>System: Click Like Button
    alt Like Successful
        System->>System: Increment Like Count
        System-->>Guest: Display Updated Like Count
    else Unlike Post
        Guest->>System: Click Like Button Again
        System->>System: Decrement Like Count
        System-->>Guest: Display Updated Like Count
    end
```

---
### Use Case 18: Notify Admin of Critical Metrics

**Use Case Description**: Alert admins when critical system metrics exceed thresholds using monitoring tools.

- **Scope**: Wedding Site
- **Level**: Subfunction
- **Primary Actor**: System
- **Stakeholders and Interests**:
  - **Admins**: Need timely alerts about critical system health issues to maintain the platform’s reliability.
  - **Guests**: Indirectly benefit from system stability and uptime.
- **Preconditions**:
  - Monitoring tools (Prometheus, Spring Actuator, etc.) are configured.
  - Alert thresholds are defined (e.g., low disk space, high memory usage).
- **Postconditions**:
  - Admins receive alerts for critical issues via email or dashboard notifications.
- **Main Success Scenario Step by Step**:
  1. Prometheus collects system metrics (e.g., disk usage, memory, CPU load) via Node Exporter and Spring Actuator.
  2. Alert Manager evaluates metrics against predefined thresholds.
  3. If a threshold is breached, Alert Manager triggers an alert.
  4. Admins receive the alert via configured channels (email).
  5. Admin logs into Grafana to view detailed metrics and investigate the issue.
  6. Admin resolves the issue (e.g., increases disk space, restarts services).
- **Extensions (Other Scenarios and Alternatives)**:
  - If alerts fail to send, the system logs the issue for manual review.
  - Include redundancy checks to verify the accuracy of triggered alerts.
- **Special Requirements**:
  - Configure alerts for metrics like disk space, uptime, memory usage, and CPU load.
  - Integrate Prometheus with Spring Actuator for detailed application metrics.
  - Use Grafana to visualize data and support troubleshooting.
- **Data Variation List**:
  - Metrics: Disk usage, CPU load, Memory usage, System uptime.
  - Alert types: Warning, Critical.
- **Frequency Occurrence**:
  - Rare but critical when triggered.
- **Trigger**:
  - Metrics breach predefined thresholds.
- **Actors**:
  - Admin, System
- **Goals**:
  - Ensure the system operates reliably by providing proactive alerts to admins.
- **Failed Endings**:
  - Alerts fail to send, or admins are unable to resolve the issue in time.

#### **Flowchart:**

``` mermaid
flowchart TD
    A[Prometheus Collects Metrics] --> B[Alert Manager Evaluates Metrics]
    B -->|Threshold Breached| C[Trigger Alert]
    C --> D[Send Alert to Admin via Email or Dashboard]
    D --> E[Admin Logs into Grafana]
    E --> F[Investigates Metrics and Identifies Issue]
    F --> G[Resolves Issue, e.g., Increase Disk Space]
    B -->|No Threshold Breach| H[No Action Needed]
    D -->|Alert Sending Fails| I[Log Issue for Manual Review]

```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Prometheus
    participant Alert Manager
    participant Admin
    participant Grafana

    Prometheus->>Alert Manager: Send Collected Metrics
    Alert Manager->>Alert Manager: Evaluate Metrics
    alt Threshold Breached
        Alert Manager->>Admin: Send Alert via Email/Dashboard
        Admin->>Grafana: Log In and View Metrics
        Admin->>Grafana: Investigate and Identify Issue
        Admin->>Admin: Resolve Issue (e.g., Increase Disk Space)
    else Threshold Not Breached
        Alert Manager-->>Prometheus: No Action Needed
    end
    alt Alert Sending Fails
        Alert Manager->>System Logs: Log Issue for Manual Review
    end
```

---

### Use Case 19: Update Event Information

**Use Case Description**: Allow admins to modify and publish updates for event information, such as venue details, schedule, dress code, and FAQs.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Admin
- **Stakeholders and Interests**:
  - **Admins**: Need the ability to update event details as plans change.
  - **Guests**: Want accurate and up-to-date event information to plan effectively.
- **Preconditions**:
  - Admin is logged into the system.
- **Postconditions**:
  - Updated event information is saved and visible to all guests.
- **Main Success Scenario Step by Step**:
  1. Admin logs into the platform and navigates to the Event Information section.
  2. Admin selects the category of information to edit (e.g., venue, schedule, FAQs).
  3. Updates the content fields with new information.
  4. Submits the changes for publication.
  5. System saves the updates and immediately displays them to guests in the Event Information section.
- **Extensions (Other Scenarios and Alternatives)**:
  - If an admin edits information but does not publish, the system saves it as a draft.
  - If the updated information fails to save due to a system error, the admin receives an error message with retry options.
- **Special Requirements**:
  - Provide a preview option for admins before publishing updates.
  - Ensure changes are immediately visible to all logged-in users.
- **Data Variation List**:
  - Editable fields: Venue, Schedule, FAQs, Dress Code.
- **Frequency Occurrence**:
  - As needed, depending on event updates.
- **Trigger**:
  - Admin navigates to the Event Information section to make edits.
- **Actors**:
  - Admin, System
- **Goals**:
  - Keep all guests informed with accurate and current event details.
- **Failed Endings**:
  - Update fails to save due to system issues, leaving outdated information visible.
#### **Flowchart:**

``` mermaid
flowchart TD
    A[Admin Logs In] --> B[Navigates to Event Information Section]
    B --> C[Selects Category to Edit]
    C --> D[Updates Content Fields]
    D --> E[Submits Changes for Publication]
    E -->|Changes Saved| F[Updates Visible to Guests]
    E -->|Save Fails| G[Displays Error Message with Retry Option]
    E -->|Save as Draft| H[Saves Changes Without Publishing]
```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Admin
    participant System
    participant Guests

    Admin->>System: Log In
    Admin->>System: Navigate to Event Information Section
    Admin->>System: Select Category to Edit
    Admin->>System: Update Content Fields
    Admin->>System: Submit Changes for Publication
    alt Changes Saved
        System->>Guests: Display Updated Information
    else Save Fails
        System-->>Admin: Display Error Message with Retry Option
    end
    alt Save as Draft
        System->>System: Save Changes Without Publishing
    end
```


---

### Use Case 20: Offer an Optional Note Field for Guests

**Use Case Description**: Allow guests to add optional notes during RSVP and view these notes later. Admins can access all notes along with the guest details and timestamps.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Guest
- **Stakeholders and Interests**:
  - **Guests**: Want to share additional details or special requests, such as travel plans or accessibility needs.
  - **Admins**: Need to review guest notes to address special requests or manage accommodations effectively.
- **Preconditions**:
  - Guest is logged into the system.
  - Guest is completing or has completed RSVP.
- **Postconditions**:
  - Notes are saved with the RSVP and visible to the admin and guest.
- **Main Success Scenario Step by Step**:
  1. Guest logs into the platform and navigates to the RSVP page.
  2. Fills out the required RSVP details and adds an optional note in the designated field.
  3. Submits the RSVP with the note.
  4. System saves the RSVP and associates the note with the guest’s profile.
  5. Admin accesses the Notes section in the dashboard to view all submitted notes, along with the guest’s name and submission timestamp.
  6. Guest can log in later to view their RSVP response, including the note.
- **Extensions (Other Scenarios and Alternatives)**:
  - If the guest does not include a note, the system saves the RSVP without the optional field.
- **Special Requirements**:
  - Notes should have a character limit to prevent overly long submissions.
  - Provide rich text formatting options if necessary (e.g., for URLs or lists).
- **Data Variation List**:
  - Note types: Dietary needs, travel plans, other requests.
- **Frequency Occurrence**:
  - One-time action per RSVP, though guests may update their notes if they edit their RSVP later.
- **Trigger**:
  - Guest adds a note during RSVP, or admin reviews the Notes section in the dashboard.
- **Actors**:
  - Guest, Admin, System
- **Goals**:
  - Ensure guests can communicate special requests, and admins can access these details efficiently.
- **Failed Endings**:
  - Notes fail to save due to system errors, leaving the RSVP incomplete.

#### **Flowchart:**

``` mermaid
flowchart TD
    A[Guest Logs In] --> B[Navigates to RSVP Page]
    B --> C[Fills Required RSVP Details]
    C --> D[Adds Note in Optional Field]
    D --> E[Submits RSVP]
    E --> F[System Saves RSVP and Note]
    F --> G[Admin Views Notes in Dashboard]
    F --> H[Guest Views Note Later with RSVP Details]
    D -->|No Note Added| I[Saves RSVP Without Note]
```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Guest
    participant System
    participant Admin

    Guest->>System: Log In
    Guest->>System: Navigate to RSVP Page
    Guest->>System: Enter RSVP Details and Optional Note
    Guest->>System: Submit RSVP
    System->>System: Save RSVP and Note
    System-->>Guest: Confirm RSVP and Save Note
    Admin->>System: Access Notes Section
    System-->>Admin: Display Guest Notes with Timestamps
    Guest->>System: View RSVP Details with Note
```

---
### Use Case 21: Allow Admins to Highlight or Feature Specific Posts

**Use Case Description**: Allow admins to highlight or feature specific posts on the news feed, ensuring important or memorable content gains visibility.

- **Scope**: Wedding Site
- **Level**: User Goal
- **Primary Actor**: Admin
- **Stakeholders and Interests**:
  - **Admins**: Want to promote important or special posts to maintain engagement and showcase memorable moments.
  - **Guests**: Want easy access to featured posts that hold significance for the event.
- **Preconditions**:
  - Admin is logged into the system.
  - Posts exist in the news feed.
- **Postconditions**:
  - Selected posts are marked as featured and displayed prominently in the feed.
- **Main Success Scenario Step by Step**:
  1. Admin logs into the platform and navigates to the news feed moderation page.
  2. Selects a post to highlight or feature.
  3. Marks the post as featured via a toggle or button.
  4. System updates the post’s metadata to include the “Featured” status.
  5. Featured posts are displayed at the top of the news feed or in a dedicated section.
- **Extensions (Other Scenarios and Alternatives)**:
  - If an admin mistakenly features a post, they can unmark it using the same toggle or button.
  - If multiple posts are featured, they are displayed in order of their feature timestamp.
- **Special Requirements**:
  - Add a visual marker (e.g., a star or ribbon icon) to indicate featured posts.
  - Allow filtering the feed to display only featured posts.
- **Data Variation List**:
  - Post type: Photo, Video, Text.
  - Feature status: Featured, Not Featured.
- **Frequency Occurrence**:
  - As needed, depending on the importance of posts.
- **Trigger**:
  - Admin decides to feature a post for prominence.
- **Actors**:
  - Admin, System
- **Goals**:
  - Ensure special posts receive more visibility and engagement.
- **Failed Endings**:
  - System fails to update the post’s feature status.
- **Miscellaneous**:
  - Consider allowing guests to filter or sort the news feed by featured posts.
- **Suggested Additional Use Cases**:
  - Notify Guests of Featured Posts: Inform guests about newly featured posts.
#### **Flowchart:**

``` mermaid
flowchart TD
    A[Admin Logs In] --> B[Navigates to News Feed Moderation Page]
    B --> C[Selects Post to Highlight]
    C --> D[Clicks Feature Toggle/Button]
    D --> E[System Updates Post Metadata as Featured]
    E --> F[Displays Featured Post at Top of Feed or in Dedicated Section]
    D -->|Feature Removed| G[Post is Unmarked as Featured]
```

#### **Sequence Diagram:**

``` mermaid
sequenceDiagram
    participant Admin
    participant System
    participant Guests

    Admin->>System: Log In
    Admin->>System: Navigate to News Feed Moderation Page
    Admin->>System: Select Post to Highlight
    Admin->>System: Mark Post as Featured
    System->>System: Update Post Metadata
    System-->>Admin: Confirm Post is Featured
    Guests->>System: View News Feed
    System-->>Guests: Display Featured Post Prominently
    alt Remove Featured Status
        Admin->>System: Unmark Post as Featured
        System->>System: Remove Feature Metadata
        System-->>Admin: Confirm Feature Removed
    end
```

---




