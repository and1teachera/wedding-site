import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-grid-container',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './grid-container.component.html',
  styleUrl: './grid-container.component.css'
})
export class GridContainerComponent {
  @Input() columns = 'grid-cols-1 sm:grid-cols-2 lg:grid-cols-3';

}
