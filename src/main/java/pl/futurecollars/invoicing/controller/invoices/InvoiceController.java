package pl.futurecollars.invoicing.controller.invoices;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@RestController
@RequiredArgsConstructor
public class InvoiceController implements InvoiceApi {

    private final InvoiceService invoiceService;

    @Override
    public long addInvoice(@RequestBody Invoice invoice) {
        return invoiceService.save(invoice);
    }

    @Override
    public List<Invoice> getAll() {
        return invoiceService.getAll();
    }

    @Override
    public ResponseEntity<Invoice> getById(@PathVariable long id) {
        return invoiceService.getById(id)
            .map(invoice -> ResponseEntity.ok().body(invoice))
            .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody Invoice invoice) {
        try {
            return invoiceService.update(id, invoice)
                .map(name -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<?> delete(@PathVariable long id) {
        return invoiceService.delete(id)
            .map(name -> ResponseEntity.noContent().build())
            .orElse(ResponseEntity.notFound().build());
    }

}
