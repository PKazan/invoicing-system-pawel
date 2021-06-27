package pl.futurecollars.invoicing.db.mongo;

import com.mongodb.client.MongoCollection;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.data.util.Streamable;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@AllArgsConstructor
public class MongoBasedDatabase implements Database {

    private final MongoCollection<Invoice> invoices;

    private final MongoIdProvider idProvider;

    @Override
    public long save(Invoice invoice) {
        long id = idProvider.getNextIdAndIncrement();
        invoice.setId(id);
        invoices.insertOne(invoice);

        return id;
    }

    @Override
    public Optional<Invoice> getById(long id) {
        return Optional.ofNullable(invoices.find(idFilter(id)).first());
    }

    @Override
    public List<Invoice> getAll() {
        return Streamable.of(invoices.find()).toList();
    }

    @Override
    public Optional<Invoice> update(long id, Invoice updatedInvoice) {

        if (getById(id).isEmpty()) {
            throw new IllegalArgumentException("Id " + id + " does not exist");
        }
        updatedInvoice.setId(id);
        return Optional.ofNullable(invoices.findOneAndReplace(idFilter(id), updatedInvoice));
    }

    @Override
    public Optional<Invoice> delete(long id) {
        return Optional.ofNullable(invoices.findOneAndDelete(idFilter(id)));
    }

    private Document idFilter(long id) {
        return new Document("_id", id);
    }
}
