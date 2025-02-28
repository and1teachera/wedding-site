import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
import { HeroComponent } from '../hero/hero.component';
import { SectionContainerComponent } from '../../../shared/components/layout/section-container/section-container.component';

@Component({
  selector: 'app-intro-section',
  standalone: true,
  imports: [CommonModule, HeaderComponent, HeroComponent, SectionContainerComponent],
  templateUrl: './intro-section.component.html',
  styleUrls: ['./intro-section.component.css']
})
export class IntroSectionComponent {
  @Input() title = '';
  @Input() weddingDate = '';
  @Input() coupleNames = '';
  @Input() daysUntilWedding = 0;
}