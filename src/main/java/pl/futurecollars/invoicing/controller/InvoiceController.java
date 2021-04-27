package pl.futurecollars.invoicing.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@RestController
@RequestMapping("/invoices")

public class InvoiceController {

    private InvoiceService service = new InvoiceService(new InMemoryDatabase());

    @PostMapping
    public int addInvoice(@RequestBody Invoice invoice) {
        return service.save(invoice);
    }

    @GetMapping
    public List<Invoice> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Invoice>> getById(@PathVariable int id) {
        return Optional.ofNullable(service.getById(id))
            .map(invoice -> ResponseEntity.ok().body(invoice))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Invoice invoice) {
        service.update(id, invoice);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

}
