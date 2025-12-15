import { Component } from '@angular/core';
import { AsistenciaService, HistorialItem } from 'src/app/core/services/asistencia.service';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-empleado',
  templateUrl: './empleado.component.html'
})
export class EmpleadoComponent {
  data: HistorialItem[] = [];
  error = '';
  fechaInicio = '2025-12-10';
  fechaFin = '2025-12-12';

  constructor(private api: AsistenciaService, private auth: AuthService) {}

  cargar() {
    this.error = '';
    const idUsuario = Number(localStorage.getItem('idUsuario'));

    this.api.historial(idUsuario, this.fechaInicio, this.fechaFin).subscribe({
      next: (res) => this.data = res,
      error: () => this.error = 'No se pudo cargar historial (revisa token/fechas).'
    });
  }
}
