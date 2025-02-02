import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

interface ScheduleEvent {
  time: string;
  title: string;
  description: string;
  imagePaths: {
    sm: string;
    md: string;
    lg: string;
  };
}

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './schedule.component.html'
})
export class ScheduleComponent implements OnInit, OnDestroy {
  currentIndex = 0;
  isAutoPlaying = true;
  private autoPlayInterval: ReturnType<typeof setInterval> | null = null;
  private readonly transitionDuration = 1200; // 1.2s in milliseconds
  private touchStartX = 0;

  events: ScheduleEvent[] = [
    {
      time: "17:30",
      title: "Коктейл",
      description: "Приветствен коктейл",
      imagePaths: {
        sm: "assets/images/schedule/cocktail-sm.jpg",
        md: "assets/images/schedule/cocktail-md.jpg",
        lg: "assets/images/schedule/cocktail-lg.jpg"
      }
    },
    {
      time: "18:00",
      title: "Церемония",
      description: "Официална церемония в градината",
      imagePaths: {
        sm: "assets/images/schedule/ceremony-sm.jpg",
        md: "assets/images/schedule/ceremony-md.jpg",
        lg: "assets/images/schedule/ceremony-lg.jpg"
      }
    },
    {
      time: "20:00",
      title: "Вечеря",
      description: "Празнична вечеря",
      imagePaths: {
        sm: "assets/images/schedule/dinner-sm.jpg",
        md: "assets/images/schedule/dinner-md.jpg",
        lg: "assets/images/schedule/dinner-lg.jpg"
      }
    }
  ];

  ngOnInit() {
    this.startAutoPlay();
  }

  ngOnDestroy() {
    this.stopAutoPlay();
  }

  startAutoPlay() {
    if (!this.autoPlayInterval) {
      this.autoPlayInterval = setInterval(() => {
        this.nextSlide();
      }, 4000); // 5 seconds interval
    }
  }

  stopAutoPlay() {
    if (this.autoPlayInterval) {
      clearInterval(this.autoPlayInterval);
      this.autoPlayInterval = null;
    }
  }

  onMouseEnter() {
    this.stopAutoPlay();
  }

  onMouseLeave() {
    this.startAutoPlay();
  }

  onTouchStart(event: TouchEvent) {
    this.touchStartX = event.touches[0].clientX;
    this.stopAutoPlay();
  }

  onTouchEnd(event: TouchEvent) {
    const touchEndX = event.changedTouches[0].clientX;
    const difference = this.touchStartX - touchEndX;

    if (Math.abs(difference) > 50) { // Minimum swipe distance
      if (difference > 0) {
        this.nextSlide();
      } else {
        this.previousSlide();
      }
    }

    this.startAutoPlay();
  }

  nextSlide() {
    this.currentIndex = (this.currentIndex + 1) % this.events.length;
  }

  previousSlide() {
    this.currentIndex = this.currentIndex === 0 ? this.events.length - 1 : this.currentIndex - 1;
  }

  goToSlide(index: number) {
    this.currentIndex = index;
  }
}