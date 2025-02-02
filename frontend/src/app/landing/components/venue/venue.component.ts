import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-venue',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './venue.component.html'
})
export class VenueComponent {
  venueInfo = {
    name: "Вила Юстина",
    address: "Nikola Petkov, 4228 Устина",
    description: `Изключително  живописно място, със страхотни гледки, с за храната сготвена и сервирана с много любов.`
  };
}
