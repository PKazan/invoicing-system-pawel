package pl.futurecollars.invoicing.model;

import java.math.BigDecimal;

public enum Vat {

    VAT_0(0),
    VAT_5(5),
    VAT_7(7),
    VAT_8(8),
    VAT_23(23),
    VAT_ZW(0);

    private final BigDecimal vatRate;

    Vat(int vatRate) {
        this.vatRate = BigDecimal.valueOf(vatRate);
    }
}
