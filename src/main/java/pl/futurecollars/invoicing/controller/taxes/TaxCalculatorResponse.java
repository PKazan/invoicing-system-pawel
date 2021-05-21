package pl.futurecollars.invoicing.controller.taxes;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaxCalculatorResponse {

    private BigDecimal income;
    private BigDecimal costs;
    private BigDecimal incomeMinusCosts;
    private BigDecimal pensionInsurance;
    private BigDecimal incomeMinusCostsMinusPensionInsurance;
    private BigDecimal taxCalculationBase;
    private BigDecimal incomeTax;
    private BigDecimal healthInsurancePaid;
    private BigDecimal healthInsuranceToSubtract;
    private BigDecimal incomeTaxMinusHealthInsurance;
    private BigDecimal finalIncomeTaxValue;
    private BigDecimal incomingVat;
    private BigDecimal outgoingVat;
    private BigDecimal vatToPay;
}
