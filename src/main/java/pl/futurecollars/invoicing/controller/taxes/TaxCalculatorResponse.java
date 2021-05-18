package pl.futurecollars.invoicing.controller.taxes;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class TaxCalculatorResponse {

    private BigDecimal income;
    private BigDecimal costs;
    private BigDecimal incomingVat;
    private BigDecimal outgoingVat;
    private BigDecimal earnings;
    private BigDecimal vatToPay;

    private TaxCalculatorResponse(BigDecimal income, BigDecimal costs, BigDecimal incomingVat, BigDecimal outgoingVat, BigDecimal earnings,
                                  BigDecimal vatToPay) {
        this.income = income;
        this.costs = costs;
        this.incomingVat = incomingVat;
        this.outgoingVat = outgoingVat;
        this.earnings = earnings;
        this.vatToPay = vatToPay;
    }

    public static TaxCalculatorResponseBuilder builder() {
        return new TaxCalculatorResponseBuilder();
    }

    static class TaxCalculatorResponseBuilder {
        private BigDecimal income;
        private BigDecimal costs;
        private BigDecimal incomingVat;
        private BigDecimal outgoingVat;
        private BigDecimal earnings;
        private BigDecimal vatToPay;

        public TaxCalculatorResponseBuilder income(BigDecimal income) {
            this.income = income;
            return this;
        }

        public TaxCalculatorResponseBuilder costs(BigDecimal costs) {
            this.costs = costs;
            return this;
        }

        public TaxCalculatorResponseBuilder incomingVat(BigDecimal incomingVat) {
            this.incomingVat = incomingVat;
            return this;
        }

        public TaxCalculatorResponseBuilder outgoingVat(BigDecimal outgoingVat) {
            this.outgoingVat = outgoingVat;
            return this;
        }

        public TaxCalculatorResponseBuilder earnings(BigDecimal earnings) {
            this.earnings = earnings;
            return this;
        }

        public TaxCalculatorResponseBuilder vatToPay(BigDecimal vatToPay) {
            this.vatToPay = vatToPay;
            return this;
        }

        public TaxCalculatorResponse build() {
            TaxCalculatorResponse taxCalculatorResponse =
                new TaxCalculatorResponse(income, costs, incomingVat, outgoingVat, earnings, vatToPay);
            taxCalculatorResponse.income = income;
            taxCalculatorResponse.costs = costs;
            taxCalculatorResponse.incomingVat = incomingVat;
            taxCalculatorResponse.outgoingVat = outgoingVat;
            taxCalculatorResponse.earnings = earnings;
            taxCalculatorResponse.vatToPay = vatToPay;
            return taxCalculatorResponse;
        }
    }
}
