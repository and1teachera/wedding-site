import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-content-container',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './content-container.component.html',
  styleUrl: './content-container.component.css'
})
export class ContentContainerComponent {
  @Input() maxWidth = 'max-w-6xl';
  @Input() padding = 'px-4 sm:px-6 lg:px-8';
  @Input() additionalClasses = '';
}
