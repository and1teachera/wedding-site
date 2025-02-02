import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FaqComponent } from '../faq/faq.component';
import { DressCodeComponent } from '../dress-code/dress-code.component';

@Component({
  selector: 'app-info-section',
  standalone: true,
  imports: [CommonModule, FaqComponent, DressCodeComponent],
  templateUrl: './info-section.component.html'
})
export class InfoSectionComponent {}
