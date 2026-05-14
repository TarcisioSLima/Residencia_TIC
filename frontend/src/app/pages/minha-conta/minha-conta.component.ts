import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-minha-conta',
  standalone: true,
  imports: [CommonModule, RouterModule, MatButtonModule, MatSnackBarModule],
  templateUrl: './minha-conta.component.html',
  styleUrls: ['./minha-conta.component.scss'],
})
export class MinhaContaComponent implements OnInit {
  userName = '';
  userEmail = '';

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.userName = localStorage.getItem('user_nome') ?? 'Usuário';
    this.userEmail = localStorage.getItem('user_email') ?? '';

    this.authService.getMe().subscribe({
      next: (user) => {
        if (user.nome)  { this.userName  = user.nome;  localStorage.setItem('user_nome',  user.nome);  }
        if (user.email) { this.userEmail = user.email; localStorage.setItem('user_email', user.email); }
      }
    });

    this.route.queryParams.subscribe(params => {
      if (params['saved']) {
        this.snackBar.open('Preferências salvas com sucesso', 'Fechar', { duration: 4000 });
      }
    });
  }

  logout(): void { this.authService.logout(); }
}
