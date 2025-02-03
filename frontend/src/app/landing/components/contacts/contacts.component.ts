import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-contacts',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './contacts.component.html',
  styleUrls: ['./contacts.component.css']
})
export class ContactsComponent {
  contactInfo = {
    phone: '+359 888 123 456',
    email: 'angel.mirena@wedding.com',
    venue: {
      name: 'Вила Юстина',
      address: 'Nikola Petkov, 4228 Устина',
      coordinates: {
        lat: '42.136564',
        lng: '24.622790'
      }
    }
  };
}