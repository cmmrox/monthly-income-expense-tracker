import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {
  NbLayoutModule,
  NbSidebarModule,
  NbMenuModule,
  NbActionsModule,
  NbSelectModule,
  NbIconModule,
  NbUserModule,
  NbButtonModule,
} from '@nebular/theme';
import { NbThemeService } from '@nebular/theme';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    NbLayoutModule,
    NbSidebarModule,
    NbMenuModule,
    NbActionsModule,
    NbSelectModule,
    NbIconModule,
    NbUserModule,
    NbButtonModule,
  ],
  template: `
    <nb-layout>
      <nb-layout-header fixed>
        <div class="header">
          <button nbButton ghost status="basic" (click)="toggleSidebar()">
            <nb-icon icon="menu-2-outline"></nb-icon>
          </button>
          <div class="brand">Monthly Tracker</div>

          <div class="spacer"></div>

          <nb-select size="small" [selected]="theme" (selectedChange)="setTheme($event)">
            <nb-option value="material-dark">Dark</nb-option>
            <nb-option value="material-light">Light</nb-option>
          </nb-select>

          <nb-actions size="small">
            <nb-action icon="bell-outline"></nb-action>
            <nb-action icon="email-outline"></nb-action>
            <nb-action>
              <nb-user size="small" name="Charith"></nb-user>
            </nb-action>
          </nb-actions>
        </div>
      </nb-layout-header>

      <nb-sidebar tag="menu-sidebar" class="menu-sidebar" responsive>
        <nb-menu [items]="menu"></nb-menu>
      </nb-sidebar>

      <nb-layout-column>
        <router-outlet />
      </nb-layout-column>
    </nb-layout>
  `,
  styles: [
    `
      .header {
        display: flex;
        align-items: center;
        gap: 8px;
        width: 100%;
      }
      .brand {
        font-weight: 600;
        letter-spacing: 0.2px;
      }
      .spacer {
        flex: 1 1 auto;
      }
      nb-layout-column {
        padding: 1.25rem;
      }
    `,
  ],
})
export class AppLayoutComponent {
  private themeService = inject(NbThemeService);

  theme: 'material-dark' | 'material-light' = (localStorage.getItem('theme') as any) || 'material-dark';

  menu = [
    {
      title: 'Home',
      icon: 'home-outline',
      link: '/',
      home: true,
    },
    {
      title: 'Transactions',
      icon: 'repeat-outline',
      link: '/transactions',
    },
    {
      title: 'Settings',
      icon: 'settings-2-outline',
      link: '/settings',
    },
  ];

  constructor() {
    // Ensure body has the theme class on first load.
    const body = document.body;
    const classes = Array.from(body.classList);
    if (!classes.some((c) => c === `nb-theme-${this.theme}`)) {
      classes
        .filter((c) => c.startsWith('nb-theme-'))
        .forEach((c) => body.classList.remove(c));
      body.classList.add(`nb-theme-${this.theme}`);
    }

    this.themeService.changeTheme(this.theme);
  }

  setTheme(theme: 'material-dark' | 'material-light') {
    this.theme = theme;
    localStorage.setItem('theme', theme);

    // Force the theme class on body to avoid any cases where it doesn't get applied.
    const body = document.body;
    Array.from(body.classList)
      .filter((c) => c.startsWith('nb-theme-'))
      .forEach((c) => body.classList.remove(c));
    body.classList.add(`nb-theme-${theme}`);

    this.themeService.changeTheme(theme);
  }

  toggleSidebar() {
    // Nebular provides sidebar service, but simplest: dispatch a click on sidebar toggle.
    // We'll use the service in next refinement pass.
    const event = new CustomEvent('toggle-sidebar', { bubbles: true });
    window.dispatchEvent(event);
  }
}
