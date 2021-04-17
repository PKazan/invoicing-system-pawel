package pl.futurecollars.invoicing.db;

import java.util.List;
import java.util.Optional;
import pl.futurecollars.invoicing.model.Invoice;

public interface Database {

    int create(Invoice invoice);

    Optional<Invoice> getById(int id);

    List<Invoice> getAll();

    void update(int id, Invoice updateInvoice);

    void delete(int id);

}