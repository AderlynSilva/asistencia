import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface HistorialItem {
  idAsistencia: number;
  idUsuario: number;
  fecha: string;
  horaCheckin: string;
  horaCheckout: string | null;
  estado: string;
  observacion: string | null;
}

export interface PuntualidadItem {
  idUsuario: number;
  nombres: string;
  apellidos: string;
  totalRegistros: number;
  totalPuntual: number;
  totalTardanza: number;
  porcPuntualidad: number;
}

export interface PendienteItem {
  idJustificacion: number;
  idUsuario: number;
  nombres: string;
  apellidos: string;
  tipo: string;
  motivo: string;
  estado: string;
  fecSolicitud: string;
}

@Injectable({ providedIn: 'root' })
export class AsistenciaService {

  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  historial(idUsuario: number, fechaInicio: string, fechaFin: string): Observable<HistorialItem[]> {
    const params = new HttpParams()
      .set('idUsuario', idUsuario)
      .set('fechaInicio', fechaInicio)
      .set('fechaFin', fechaFin);

    return this.http.get<HistorialItem[]>(`${this.baseUrl}/asistencia/historial`, { params });
  }

  puntualidad(fechaInicio: string, fechaFin: string): Observable<PuntualidadItem[]> {
    const params = new HttpParams()
      .set('fechaInicio', fechaInicio)
      .set('fechaFin', fechaFin);

    return this.http.get<PuntualidadItem[]>(`${this.baseUrl}/reportes/puntualidad`, { params });
  }

  pendientes(): Observable<PendienteItem[]> {
    return this.http.get<PendienteItem[]>(`${this.baseUrl}/justificaciones/pendientes`);
  }
}
