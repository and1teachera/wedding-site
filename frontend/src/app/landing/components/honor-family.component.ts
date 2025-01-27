import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-honor-family',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './honor-family.component.html',
  styleUrls: ['./honor-family.component.css']
})
export class HonorFamilyComponent {
  honorFamilyInfo = {
    imageSrc: {
      small: '/assets/images/honor-family/honor-family-sm.jpg',
      medium: '/assets/images/honor-family/honor-family-md.jpg',
      large: '/assets/images/honor-family/honor-family-lg.jpg'
    },
    imageAlt: 'Best Man and Best Woman with their family',
    description: `As the Best Man and Best Woman, Lazar and Maria bring not just their individual roles 
                 but their whole family's love and support to our celebration. Their unwavering friendship 
                 and guidance have been invaluable throughout our journey.`
  };
}
