import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmpleadoHistorialComponent } from './empleado-historial.component';

describe('EmpleadoHistorialComponent', () => {
  let component: EmpleadoHistorialComponent;
  let fixture: ComponentFixture<EmpleadoHistorialComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EmpleadoHistorialComponent]
    });
    fixture = TestBed.createComponent(EmpleadoHistorialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
