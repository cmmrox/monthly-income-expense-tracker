import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatSidenavModule,
    MatToolbarModule,
    MatIconModule,
    MatListModule,
    MatButtonModule,
  ],
  template: `
    <mat-sidenav-container class="shell">
      <mat-sidenav mode="side" opened class="sidenav">
        <div class="profile">
          <div class="avatar"></div>
          <div class="name">Charith</div>
          <div class="sub">Income & Expense Tracker</div>
        </div>

        <mat-nav-list>
          <a mat-list-item routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }">
            <mat-icon matListItemIcon>home</mat-icon>
            <span matListItemTitle>Home</span>
          </a>
          <a mat-list-item routerLink="/transactions" routerLinkActive="active">
            <mat-icon matListItemIcon>receipt_long</mat-icon>
            <span matListItemTitle>Transactions</span>
          </a>
          <a mat-list-item routerLink="/settings" routerLinkActive="active">
            <mat-icon matListItemIcon>settings</mat-icon>
            <span matListItemTitle>Settings</span>
          </a>
        </mat-nav-list>

        <div class="spacer"></div>
        <div class="footer">
          <small>MIT • MVP</small>
        </div>
      </mat-sidenav>

      <mat-sidenav-content>
        <mat-toolbar color="primary" class="toolbar">
          <span>Monthly Tracker</span>
          <span class="fill"></span>
          <button mat-button routerLink="/transactions">Add</button>
        </mat-toolbar>

        <div class="content">
          <router-outlet />
        </div>
      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
  styles: [
    `
      .shell { height: 100vh; }
      .sidenav { width: 260px; background: #121212; color: #eaeaea; }
      .toolbar { position: sticky; top: 0; z-index: 10; }
      .content { padding: 16px; }
      .fill { flex: 1 1 auto; }
      .profile { padding: 16px; display: grid; gap: 4px; }
      .avatar { width: 48px; height: 48px; border-radius: 50%; background: #2d2d2d; }
      .name { font-weight: 600; }
      .sub { opacity: 0.7; font-size: 12px; }
      .spacer { flex: 1; height: 24px; }
      .footer { padding: 12px 16px; opacity: 0.6; }
      a.active { background: rgba(255,255,255,0.08); border-radius: 8px; margin: 0 8px; }
    `,
  ],
})
export class AppLayoutComponent {}
