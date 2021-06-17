package pl.futurecollars.invoicing.controller.taxes;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaxCalculatorResponse {

    private final BigDecimal income;
    private final BigDecimal costs;
    private final BigDecimal incomeMinusCosts;
    private final BigDecimal pensionInsurance;
    private final BigDecimal incomeMinusCostsMinusPensionInsurance;
    private final BigDecimal taxCalculationBase;
    private final BigDecimal incomeTax;
    private final BigDecimal healthInsurancePaid;
    private final BigDecimal healthInsuranceToSubtract;
    private final BigDecimal incomeTaxMinusHealthInsurance;
    private final BigDecimal finalIncomeTaxValue;
    private final BigDecimal incomingVat;
    private final BigDecimal outgoingVat;
    private final BigDecimal vatToPay;
}
