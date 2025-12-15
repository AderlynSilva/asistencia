import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminPendientesComponent } from './admin-pendientes.component';

describe('AdminPendientesComponent', () => {
  let component: AdminPendientesComponent;
  let fixture: ComponentFixture<AdminPendientesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminPendientesComponent]
    });
    fixture = TestBed.createComponent(AdminPendientesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
