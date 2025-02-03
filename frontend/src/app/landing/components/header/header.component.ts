import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import {TitlePictureComponent} from "../../../shared/components/title-picture/title-picture.component";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, TitlePictureComponent],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent {
  @Input() title = '';
  @Input() weddingDate = '';
  @Input() coupleNames = '';
  @Input() daysUntilWedding = 0;
}
