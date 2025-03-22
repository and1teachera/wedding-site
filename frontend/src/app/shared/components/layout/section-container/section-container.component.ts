import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-section-container',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './section-container.component.html',
  styleUrl: './section-container.component.css',
})
export class SectionContainerComponent {
  @Input() id?: string;
  @Input() ariaLabel?: string;
  @Input() sectionClass =
    'md:min-h-[100svh] lg:min-h-[100svh] flex flex-col justify-start py-8 sm:py-12 lg:py-16 bg-[#F7F1E1]';
  @Input() maxWidth = 'max-w-6xl';
  @Input() additionalClasses = '';
}
