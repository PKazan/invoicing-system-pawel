package pl.futurecollars.invoicing.db.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.util.Streamable;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

public class JpaDatabase implements Database {

    private final InvoiceRepository invoiceRepository;

    public JpaDatabase(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public long save(Invoice invoice) {
        return invoiceRepository.save(invoice).getId();
    }

    @Override
    public Optional<Invoice> getById(long id) {
        return invoiceRepository.findById(id);
    }

    @Override
    public List<Invoice> getAll() {
        return Streamable.of(invoiceRepository.findAll()).toList();
    }

    @Override
    public Optional<Invoice> update(long id, Invoice updatedInvoice) {
        Optional<Invoice> invoiceOptional = getById(id);

        if (invoiceOptional.isEmpty()) {
            throw new IllegalArgumentException("Id " + id + " does not exist");
        }

        Invoice invoice = invoiceOptional.get();

        updatedInvoice.setId(id);
        updatedInvoice.getBuyer().setId(invoice.getBuyer().getId());
        updatedInvoice.getSeller().setId(invoice.getSeller().getId());

        invoiceRepository.save(updatedInvoice);

        return invoiceOptional;
    }

    @Override
    public Optional<Invoice> delete(long id) {
        Optional<Invoice> invoice = getById(id);
        invoice.ifPresent(invoiceRepository::delete);
        return invoice;
    }
}
