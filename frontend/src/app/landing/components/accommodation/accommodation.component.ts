import { Component } from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {
    SectionContainerComponent
} from "../../../shared/components/layout/section-container/section-container.component";
import {GridContainerComponent} from "../../../shared/components/layout/grid-container/grid-container.component";

@Component({
  selector: 'app-accommodation',
  standalone: true,
  imports: [CommonModule, NgOptimizedImage, SectionContainerComponent, GridContainerComponent],
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