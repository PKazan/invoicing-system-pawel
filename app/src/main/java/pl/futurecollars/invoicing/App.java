package pl.futurecollars.invoicing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;
import pl.futurecollars.invoicing.service.InvoiceService;

public class App {

    public static void main(String[] args) {

        Database db = new InMemoryDatabase();
        InvoiceService invoiceService = new InvoiceService(db);

        Company buyer = new Company("111-111-11-11", "Address A", "Company A");
        Company seller = new Company("222-222-22-22", "Address B", "Company B");

        InvoiceEntry invoiceEntry = new InvoiceEntry("services", BigDecimal.valueOf(1000), BigDecimal.valueOf(230), Vat.VAT_23);
        List<InvoiceEntry> invoiceEntries = new ArrayList<>();
        invoiceEntries.add(invoiceEntry);

        Invoice invoice = new Invoice(LocalDate.now(), buyer, seller, invoiceEntries);
        int id = invoiceService.create(invoice);

        invoiceService.getById(id).ifPresent(System.out::println);
        System.out.println(invoiceService.getAll());
        invoiceService.delete(id);
    }
}
