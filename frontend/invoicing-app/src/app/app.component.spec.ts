import { TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { of } from 'rxjs';
import { AppComponent } from './app.component';
import { Company } from './Company';
import { CompanyService } from './company.service';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        AppComponent
      ],
      providers: [
        { provide: CompanyService, useClass: MockCompanyService } 
      ], 
      imports: [
        FormsModule
      ]
    }).compileComponents();
  });

  it('should display a list of companies', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    app.ngOnInit()
    fixture.detectChanges();
    
    expect(fixture.nativeElement.innerText).toContain("111-111-11-11 ul. First 1 First Ltd. 1111.11 111.11")
    expect(fixture.nativeElement.innerText).toContain("222-222-22-22 ul. Second 2 Second Ltd. 2222.22 2222.22")
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have as title 'Invoicing App'`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('Invoicing App');
  });

  it('should render title', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement;
    expect(compiled.querySelector('div.content').textContent).toContain('Hello Invoicing App!');
  });
});

class MockCompanyService {
  
  companies: Company [] = [
    new Company(
      1,
      "111-111-11-11",
      "ul. First 1",
      "First Ltd.",
      1111.11,
      111.11
    ),
    new Company(
      2,
      "222-222-22-22",
      "ul. Second 2",
      "Second Ltd.",
      2222.22,
      222.22
    )
  ];  

  getCompanies() {
    return of(this.companies)
  }
}
