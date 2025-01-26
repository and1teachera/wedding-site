import { Component } from '@angular/core';
import { ImageSliderComponent } from '../../../shared/components/image-slider/image-slider.component';

@Component({
  selector: 'app-hero',
  standalone: true,
  imports: [ImageSliderComponent],
  templateUrl: './hero.component.html',
  styleUrls: ['./hero.component.css']
})
export class HeroComponent {
  heroImages = [
    { src: '/assets/images/hero/couple-1.jpg', alt: 'Bride and Groom - Image 1' },
    { src: '/assets/images/hero/couple-2.jpg', alt: 'Bride and Groom - Image 2' },
    { src: '/assets/images/hero/couple-3.jpg', alt: 'Bride and Groom - Image 3' },
  ];
}
