package pl.futurecollars.invoicing.controller.taxes;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaxCalculatorResponse {

    private BigDecimal income;
    private BigDecimal costs;
    private BigDecimal incomingVat;
    private BigDecimal outgoingVat;
    private BigDecimal earnings;
    private BigDecimal vatToPay;
}
