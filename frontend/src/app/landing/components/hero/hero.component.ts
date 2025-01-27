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
    {
      src: '/assets/images/hero/couple-1-lg.jpg',
      srcset: '/assets/images/hero/couple-1-sm.jpg 640w, /assets/images/hero/couple-1-md.jpg 1024w, /assets/images/hero/couple-1-lg.jpg 1280w',
      alt: 'Bride and Groom Image 1'
    },
    {
      src: '/assets/images/hero/couple-2-lg.jpg',
      srcset: '/assets/images/hero/couple-2-sm.jpg 640w, /assets/images/hero/couple-2-md.jpg 1024w, /assets/images/hero/couple-2-lg.jpg 1280w',
      alt: 'Bride and Groom Image 2'
    },
    {
      src: '/assets/images/hero/couple-3-lg.jpg',
      srcset: '/assets/images/hero/couple-3-sm.jpg 640w, /assets/images/hero/couple-3-md.jpg 1024w, /assets/images/hero/couple-3-lg.jpg 1280w',
      alt: 'Bride and Groom Image 3'
    }
  ];
}
