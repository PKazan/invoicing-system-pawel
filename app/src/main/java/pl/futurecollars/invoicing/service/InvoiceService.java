package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.Optional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

public class InvoiceService {
    private final Database db;

    public InvoiceService(Database database) {
        this.db = database;
    }

    public int create(Invoice invoice) {
        return db.create(invoice);
    }

    public Optional<Invoice> getById(int id) {
        return db.getById(id);
    }

    public List<Invoice> getAll() {
        return db.getAll();
    }

    public void update(int id, Invoice updatedInvoice) {
        db.update(id, updatedInvoice);
    }

    public void delete(int id) {
        db.delete(id);

    }
}
