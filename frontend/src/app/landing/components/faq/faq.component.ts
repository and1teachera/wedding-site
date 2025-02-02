import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface FAQ {
  question: string;
  answer: string;
  isOpen: boolean;
}

@Component({
  selector: 'app-faq',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './faq.component.html'
})
export class FaqComponent {
  faqs: FAQ[] = [
    { question: "Какво време да очакваме?", answer: "Очаквайте топло време, но вземете леко яке за вечерта.", isOpen: false },
    { question: "Мога ли да доведа гост?", answer: "Моля, уведомете ни предварително, ако искате да доведете гост.", isOpen: false },
    { question: "Има ли паркинг?", answer: "Да, има наличен паркинг за всички гости.", isOpen: false }
  ];

  toggleFAQ(index: number) {
    this.faqs[index].isOpen = !this.faqs[index].isOpen;
  }
}
