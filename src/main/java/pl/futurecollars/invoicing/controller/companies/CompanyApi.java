package pl.futurecollars.invoicing.controller.companies;

import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.futurecollars.invoicing.model.Company;

@RequestMapping("/companies")
public interface CompanyApi {

    @PostMapping
    @ApiOperation(value = "Add new company to system")
    long addInvoice(@RequestBody Company company);

    @GetMapping(produces = {"application/json;charset=UTF-8"})
    @ApiOperation(value = "Get list of all companies")
    List<Company> getAll();

    @GetMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"})
    @ApiOperation(value = "Get company by id")
    ResponseEntity<Company> getById(@PathVariable long id);

    @PutMapping("/{id}")
    @ApiOperation(value = "Update company with given id")
    ResponseEntity<?> update(@PathVariable long id, @RequestBody Company company);

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete company with given id")
    ResponseEntity<?> delete(@PathVariable long id);
}
