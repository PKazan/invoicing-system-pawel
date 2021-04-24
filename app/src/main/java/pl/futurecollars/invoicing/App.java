package pl.futurecollars.invoicing;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import pl.futurecollars.invoicing.config.Config;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.file.FileBasedDatabase;
import pl.futurecollars.invoicing.db.file.IdService;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;
import pl.futurecollars.invoicing.service.InvoiceService;
import pl.futurecollars.invoicing.util.FilesService;
import pl.futurecollars.invoicing.util.JsonService;

public class App {

    public static void main(String[] args) {

        FilesService filesService = new FilesService();
        JsonService jsonService = new JsonService();
        IdService idService = new IdService(Path.of(Config.ID_FILE_LOCATION), filesService);
        Database db = new FileBasedDatabase(Path.of(Config.DATABASE_LOCATION), idService, filesService, jsonService);

        InvoiceService service = new InvoiceService(db);

        Company buyer = new Company("111-111-11-11", "Address A", "Company A");
        Company seller = new Company("222-222-22-22", "Address B", "Company B");

        InvoiceEntry invoiceEntry = new InvoiceEntry("services", 1, BigDecimal.valueOf(1000), BigDecimal.valueOf(230), Vat.VAT_23);
        List<InvoiceEntry> invoiceEntries = new ArrayList<>();
        invoiceEntries.add(invoiceEntry);

        Invoice invoice = new Invoice(LocalDate.now(), buyer, seller, invoiceEntries);

        int id = service.save(invoice);

        service.getById(id).ifPresent(System.out::println);

        System.out.println(service.getAll());

        service.delete(id);
    }

}
