package pl.futurecollars.invoicing.controller;

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
import pl.futurecollars.invoicing.model.Invoice;

@RequestMapping("/invoices")
public interface InvoiceApi {

    @PostMapping
    @ApiOperation(value = "Add new invoice to system")
    int addInvoice(@RequestBody Invoice invoice);

    @GetMapping(produces = {"application/json;charset=UTF-8"})
    @ApiOperation(value = "Get list of all invoices")
    List<Invoice> getAll();

    @GetMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"})
    @ApiOperation(value = "Get invoice by id")
    ResponseEntity<Invoice> getById(@PathVariable int id);

    @PutMapping("/{id}")
    @ApiOperation(value = "Update invoice with given id")
    ResponseEntity<?> update(@PathVariable int id, @RequestBody Invoice invoice);

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete invoice with given id")
    ResponseEntity<?> delete(@PathVariable int id);
}
