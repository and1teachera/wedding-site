import { Component } from '@angular/core';
import { HeaderComponent } from './components/header/header.component';
import { NavigationComponent } from './components/navigation/navigation.component';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [HeaderComponent, NavigationComponent],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css'],
})
export class LandingComponent {
  pageTitle = "It's Just Forever, No Big Deal";
  coupleNames = 'Ангел & Мирена';
  weddingDate = new Date('2025-08-09');

  getDaysUntilWedding(): number {
    const today = new Date();
    const timeDiff = this.weddingDate.getTime() - today.getTime();
    return Math.ceil(timeDiff / (1000 * 3600 * 24));
  }
}
