import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContactsComponent } from '../contacts/contacts.component';
import {FooterComponent} from "../../../shared/components/footer/footer.component";

@Component({
  selector: 'app-contact-section',
  standalone: true,
  imports: [CommonModule, ContactsComponent, FooterComponent],
  templateUrl: './contact-section.component.html',
  styleUrls: ['./contact-section.component.css']
})
export class ContactSectionComponent {

}