package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

@Service
@AllArgsConstructor
public class TaxCalculatorService {

    private Database database;

    public BigDecimal income(String taxIdentificationNumber) {
        return database.visit(InvoiceEntry::getPrice, sellerPredicate(taxIdentificationNumber));
    }

    public BigDecimal costs(String taxIdentificationNumber) {
        return database.visit(InvoiceEntry::getPrice, buyerPredicate(taxIdentificationNumber));
    }

    public BigDecimal incomingVat(String taxIdentificationNumber) {
        return database.visit(InvoiceEntry::getVatValue, sellerPredicate(taxIdentificationNumber));
    }

    public BigDecimal outgoingVat(String taxIdentificationNumber) {
        return database.visit(InvoiceEntry::getVatValue, buyerPredicate(taxIdentificationNumber));
    }

    public BigDecimal getEarnings(String taxIdentificationNumber) {
        return income(taxIdentificationNumber).subtract(costs(taxIdentificationNumber));
    }

    public BigDecimal getVatToPay(String taxIdentificationNumber) {
        return incomingVat(taxIdentificationNumber).subtract(outgoingVat(taxIdentificationNumber));
    }

    private Predicate<Invoice> sellerPredicate(String taxIdentificationNumber) {
        return invoice -> invoice.getSeller().getTaxIdentificationNumber().equals(taxIdentificationNumber);
    }

    private Predicate<Invoice> buyerPredicate(String taxIdentificationNumber) {
        return invoice -> invoice.getBuyer().getTaxIdentificationNumber().equals(taxIdentificationNumber);
    }
}
