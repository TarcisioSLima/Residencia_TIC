import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../services/auth.service';

export interface SidebarItem { label: string; icon: string; route: string; }

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIconModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent {
  navItems: SidebarItem[] = [
    { label: 'Avisos',  icon: 'campaign',    route: '/admin/admin-panel' },
    { label: 'Envios',  icon: 'send',        route: '/admin/envios' },
    { label: 'Logs',    icon: 'terminal',    route: '/admin/logs' },
  ];

  constructor(private authService: AuthService, private router: Router) {}

  isActive(route: string): boolean { return this.router.url.startsWith(route); }

  logout(): void { this.authService.logout(); }
}
