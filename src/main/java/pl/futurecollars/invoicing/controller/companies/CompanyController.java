package pl.futurecollars.invoicing.controller.companies;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.service.CompanyService;

@RestController
@RequiredArgsConstructor
public class CompanyController implements CompanyApi {

    private final CompanyService companyService;

    @Override
    public long addInvoice(@RequestBody Company company) {
        return companyService.save(company);
    }

    @Override
    public List<Company> getAll() {
        return companyService.getAll();
    }

    @Override
    public ResponseEntity<Company> getById(@PathVariable long id) {
        return companyService.getById(id)
            .map(company -> ResponseEntity.ok().body(company))
            .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody Company company) {
        try {
            return companyService.update(id, company)
                .map(com -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<?> delete(@PathVariable long id) {
        return companyService.delete(id)
            .map(company -> ResponseEntity.noContent().build())
            .orElse(ResponseEntity.notFound().build());
    }
}
