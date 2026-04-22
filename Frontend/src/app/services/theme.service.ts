import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private isDarkMode = signal(this.getStoredTheme());
  isDark = this.isDarkMode.asReadonly();

  constructor() {
    this.applyTheme();
  }

  toggleTheme(): void {
    this.isDarkMode.update((v) => !v);
    localStorage.setItem('theme', this.isDarkMode() ? 'dark' : 'light');
    this.applyTheme();
  }

  private getStoredTheme(): boolean {
    const stored = localStorage.getItem('theme');
    if (stored) return stored === 'dark';
    return window.matchMedia('(prefers-color-scheme: dark)').matches;
  }

  private applyTheme(): void {
    document.body.classList.toggle('dark-mode', this.isDarkMode());
    document.body.classList.toggle('light-mode', !this.isDarkMode());
  }
}
