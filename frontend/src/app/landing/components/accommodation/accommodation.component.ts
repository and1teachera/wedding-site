import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-accommodation',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './accommodation.component.html'
})
export class AccommodationComponent {
  accommodationInfo = {
    name: "Хотел Юстина",
    address: "Nikola Petkov, 4228 Устина",
    description: `Настаняването е осигурено в прекрасния хотел към комплекса. 
                 Разполагаме с луксозни стаи с изглед към лозята. Всички стаи 
                 са оборудвани с климатик, телевизор и самостоятелна баня.`
  };
}