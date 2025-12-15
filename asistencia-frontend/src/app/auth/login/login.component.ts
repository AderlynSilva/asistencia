import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {

  username = '';
  password = '';
  error = '';

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  onLogin() {
  console.log('CLICK LOGIN', this.username, this.password); // 👈 AQUÍ

  this.error = '';

  this.auth.login(this.username, this.password).subscribe({
    next: (res) => {
      if (res.rol === 'ADMIN') {
        this.router.navigate(['/admin']);
      } else {
        this.router.navigate(['/empleado']);
      }
    },
    error: () => {
      this.error = 'Credenciales inválidas';
    }
  });
}
}

