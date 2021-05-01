package pl.futurecollars.invoicing.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@RestController
@RequestMapping("/invoices")

public class InvoiceController {

    InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public int addInvoice(@RequestBody Invoice invoice) {
        return invoiceService.save(invoice);
    }

    @GetMapping
    public List<Invoice> getAll() {
        return invoiceService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getById(@PathVariable int id) {
        return invoiceService.getById(id)
            .map(invoice -> ResponseEntity.ok().body(invoice))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Invoice invoice) {
        try {
            return invoiceService.update(id, invoice)
                .map(name -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        return invoiceService.delete(id)
        .map(name -> ResponseEntity.noContent().build())
        .orElse(ResponseEntity.notFound().build());
    }

}