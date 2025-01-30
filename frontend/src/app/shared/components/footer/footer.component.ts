import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent {
  weddingDate = 'AUGUST 9, 2025';
  coupleInitials = 'A&M';
  shortDate = '9.8.2025';
}
