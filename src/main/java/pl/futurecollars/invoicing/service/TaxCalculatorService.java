package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

@Service
@AllArgsConstructor
public class TaxCalculatorService {

    InvoiceEntry invoiceEntry;
    private Database database;

    public BigDecimal income(String taxIdentificationNumber) {
        return database.visit(InvoiceEntry::getPrice, sellerPredicate(taxIdentificationNumber));
    }

    public BigDecimal costs(String taxIdentificationNumber) {
        return database.visit(this::getNetPriceIncludingCarExpenses, buyerPredicate(taxIdentificationNumber));
    }

    private BigDecimal getNetPriceIncludingCarExpenses(InvoiceEntry invoiceEntry) {
        if (invoiceEntry.getCarInPrivateUse().isIncludingPrivateExpense()) {
            return invoiceEntry.getPrice().add(invoiceEntry.getVatValue().divide(BigDecimal.valueOf(2, 2), RoundingMode.HALF_UP));
        } else {
            return invoiceEntry.getPrice();
        }
    }

    public BigDecimal incomingVat(String taxIdentificationNumber) {
        return database.visit(InvoiceEntry::getVatValue, sellerPredicate(taxIdentificationNumber));
    }

    public BigDecimal outgoingVat(String taxIdentificationNumber) {
        return database.visit(this::getVatValueIncludingCarExpenses, buyerPredicate(taxIdentificationNumber));
    }

    private BigDecimal getVatValueIncludingCarExpenses(InvoiceEntry invoiceEntry) {
        if (invoiceEntry.getCarInPrivateUse().isIncludingPrivateExpense()) {
            return invoiceEntry.getVatValue().divide(BigDecimal.valueOf(2, 2), RoundingMode.HALF_DOWN);
        } else {
            return invoiceEntry.getVatValue();
        }
    }

    public BigDecimal getEarnings(String taxIdentificationNumber) {
        return income(taxIdentificationNumber).subtract(costs(taxIdentificationNumber));
    }

    public BigDecimal getVatToPay(String taxIdentificationNumber) {
        return incomingVat(taxIdentificationNumber).subtract(outgoingVat(taxIdentificationNumber));
    }

    public BigDecimal getIncomeMinusCostsMinusPensionInsurance(Company company) {
        return getEarnings(company.getTaxIdentificationNumber()).subtract(company.getPensionInsurance());
    }

    private Predicate<Invoice> sellerPredicate(String taxIdentificationNumber) {
        return invoice -> invoice.getSeller().getTaxIdentificationNumber().equals(taxIdentificationNumber);
    }

    private Predicate<Invoice> buyerPredicate(String taxIdentificationNumber) {
        return invoice -> invoice.getBuyer().getTaxIdentificationNumber().equals(taxIdentificationNumber);
    }
}
