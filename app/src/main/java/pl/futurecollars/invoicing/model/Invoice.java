package pl.futurecollars.invoicing.model;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class Invoice {

    private int id;
    private LocalDate date;
    private Company buyer;
    private Company seller;
    private List<InvoiceEntry> invoiceEntries;

    public Invoice(LocalDate date, Company buyer, Company seller, List<InvoiceEntry> invoiceEntries) {
        this.date = date;
        this.buyer = buyer;
        this.seller = seller;
        this.invoiceEntries = invoiceEntries;
    }

}
