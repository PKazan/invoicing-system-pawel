package pl.futurecollars.invoicing.db.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

public class InMemoryDatabase implements Database {

    private final HashMap<Long, Invoice> invoiceInMemoryDatabase = new HashMap<>();
    private long index = 1;

    public HashMap<Long, Invoice> getInvoiceInMemoryDatabase() {
        return invoiceInMemoryDatabase;
    }

    @Override
    public long save(Invoice invoice) {
        invoice.setId(index);
        invoiceInMemoryDatabase.put(index, invoice);
        return index++;
    }

    @Override
    public Optional<Invoice> getById(long id) {
        return Optional.ofNullable(invoiceInMemoryDatabase.get(id));
    }

    @Override
    public List<Invoice> getAll() {
        return new ArrayList<>(invoiceInMemoryDatabase.values());
    }

    @Override
    public Optional<Invoice> update(long id, Invoice updatedInvoice) {
        if (!invoiceInMemoryDatabase.containsKey(id)) {
            throw new IllegalArgumentException("Id " + id + " does not exist");
        }
        updatedInvoice.setId(id);
        return Optional.ofNullable(invoiceInMemoryDatabase.put(id, updatedInvoice));
    }

    @Override
    public Optional<Invoice> delete(long id) {
        return Optional.ofNullable(invoiceInMemoryDatabase.remove(id));
    }
}
