import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-setup-complete',
  standalone: true,
  imports: [CommonModule, RouterModule, MatButtonModule],
  templateUrl: './setup-complete.component.html',
  styleUrls: ['./setup-complete.component.scss'],
})
export class SetupCompleteComponent {
  constructor(private router: Router) {}
  goToHome(): void { this.router.navigate(['/minha-conta']); }
}
