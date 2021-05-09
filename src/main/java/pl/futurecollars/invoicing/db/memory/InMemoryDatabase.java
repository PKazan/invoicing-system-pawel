package pl.futurecollars.invoicing.db.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@Repository
@Data
@Primary
public class InMemoryDatabase implements Database {

    private final HashMap<Integer, Invoice> invoiceInMemoryDatabase = new HashMap<>();
    private int index = 1;

    @Override
    public int save(Invoice invoice) {
        invoice.setId(index);
        invoiceInMemoryDatabase.put(index, invoice);

        return index++;
    }

    @Override
    public Optional<Invoice> getById(int id) {
        return Optional.ofNullable(invoiceInMemoryDatabase.get(id));
    }

    @Override
    public List<Invoice> getAll() {
        return new ArrayList<>(invoiceInMemoryDatabase.values());
    }

    @Override
    public Optional<Invoice> update(int id, Invoice updatedInvoice) {

        if (!invoiceInMemoryDatabase.containsKey(id)) {
            throw new IllegalArgumentException("Id " + id + " does not exist");
        }

        updatedInvoice.setId(id);
        return Optional.ofNullable(invoiceInMemoryDatabase.put(id, updatedInvoice));
    }

    @Override
    public Optional<Invoice> delete(int id) {
        return Optional.ofNullable(invoiceInMemoryDatabase.remove(id));
    }
}
