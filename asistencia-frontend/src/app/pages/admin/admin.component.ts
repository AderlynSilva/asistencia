import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';

interface PuntualidadEmpleadoDto {
  idUsuario: number;
  nombres: string;
  apellidos: string;
  totalRegistros: number;
  totalPuntual: number;
  totalTardanza: number;
  porcPuntualidad: number;
}

interface JustificacionPendienteDto {
  idJustificacion: number;
  idUsuario: number;
  nombres: string;
  apellidos: string;
  tipo: string;
  motivo: string;
  estado: string;
  fecSolicitud: string;
}

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html'
})
export class AdminComponent {

  error = '';
  fechaInicio = '2025-12-10';
  fechaFin = '2025-12-10';

  puntualidad: PuntualidadEmpleadoDto[] = [];
  pendientes: JustificacionPendienteDto[] = [];

  private apiReportes = 'http://localhost:8080/api/reportes';
  private apiJustificaciones = 'http://localhost:8080/api/justificaciones';

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private router: Router
  ) {}

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

  cargarReporte() {
    this.error = '';
    this.http.get<PuntualidadEmpleadoDto[]>(
      `${this.apiReportes}/puntualidad?fechaInicio=${this.fechaInicio}&fechaFin=${this.fechaFin}`
    ).subscribe({
      next: (res) => this.puntualidad = res,
      error: () => this.error = 'No se pudo cargar el reporte (¿token o rol?)'
    });
  }

  cargarPendientes() {
    this.error = '';
    this.http.get<JustificacionPendienteDto[]>(
      `${this.apiJustificaciones}/pendientes`
    ).subscribe({
      next: (res) => this.pendientes = res,
      error: () => this.error = 'No se pudo cargar pendientes (¿token o rol?)'
    });
  }

  cambiarEstado(id: number, estado: string) {
    const usuarioRevisor = localStorage.getItem('username') || 'admin';
    this.http.put(
      `http://localhost:8080/api/justificaciones/${id}/estado?estado=${estado}&usuarioRevisor=${usuarioRevisor}`,
      {}
    ).subscribe({
      next: () => this.cargarPendientes(),
      error: () => this.error = 'No se pudo cambiar estado'
    });
  }
}
