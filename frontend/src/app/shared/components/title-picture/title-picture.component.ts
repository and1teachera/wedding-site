import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-title-picture',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './title-picture.component.html',
  styleUrls: ['./title-picture.component.css'],
})
export class TitlePictureComponent {
  @Input() title = "It's Just Forever, No Big Deal";
  @Input() responsiveClasses = 'w-[60%] md:w-[30%] lg:w-[30%]';
}
