import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { interval, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import {NgForOf, NgOptimizedImage} from "@angular/common";

interface SlideImage {
  src: string;
  alt: string;
}

@Component({
  selector: 'app-image-slider',
  standalone: true,
  templateUrl: './image-slider.component.html',
  imports: [
    NgForOf,
    NgOptimizedImage
  ],
  styleUrls: ['./image-slider.component.css']
})
export class ImageSliderComponent implements OnInit, OnDestroy {
  @Input() images: SlideImage[] = [];
  @Input() intervalTime = 5000;

  currentIndex = 0;
  private destroy$ = new Subject<void>();

  ngOnInit() {
    this.startAutoSlide();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private startAutoSlide() {
    interval(this.intervalTime)
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => {
          this.currentIndex = (this.currentIndex + 1) % this.images.length;
        });
  }
}
