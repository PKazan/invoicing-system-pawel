package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.model.Invoice;

@Service
public class InvoiceService {

    InMemoryDatabase inMemoryDatabase = new InMemoryDatabase();
    private final Database db;

    public InvoiceService(Database database) {
        this.db = database;
    }

    public int save(Invoice invoice) {
        return db.save(invoice);
    }

    public Optional<Invoice> getById(int id) {
        return db.getById(id);
    }

    public List<Invoice> getAll() {
        return db.getAll();
    }

    public Optional<Invoice> update(int id, Invoice updatedInvoice) {
        if (inMemoryDatabase.getInvoiceInMemoryDatabase().containsKey(id)) {
            return Optional.empty();
        } else {
            return db.update(id, updatedInvoice);
        }
    }

    public Optional<Invoice> delete(int id) {
        return db.delete(id);
    }
}
