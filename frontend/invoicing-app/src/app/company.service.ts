import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";
import { Company } from "./Company";
import { Injectable } from "@angular/core";

const PATH = 'companies'; 

@Injectable()
export class CompanyService {

private contentType = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
};

constructor(private http: HttpClient) {
}

getCompanies(): Observable<Company[]> {
    return this.http.get<Company[]>(this.apiUrl(PATH));
}

addCompany(company: Company): Observable<any> {
    return this.http.post<any>(this.apiUrl(PATH), this.toCompanyRequest(company), this.contentType);
}

deleteCompany(id: number): Observable<any> {
    return this.http.delete<any>(this.apiUrl(PATH, id));
}

editCompany(company: Company): Observable<any> {
    return this.http.put<Company>(this.apiUrl(PATH, company.id), this.toCompanyRequest(company), this.contentType);
}

private apiUrl(service: string, id: number = null): string {
    const idInUrl = (id !==null ? '/' + id : '');

    return environment.apiUrl + '/' + service +idInUrl;
}

private toCompanyRequest(company: Company) {
    return {
        taxIdentificationNumber: company.taxIdentificationNumber, 
        name: company.name,
        address: company.address,
        pensionInsurance: company.pensionInsurance,
        healthInsurance: company.healthInsurance,
    };
}

}