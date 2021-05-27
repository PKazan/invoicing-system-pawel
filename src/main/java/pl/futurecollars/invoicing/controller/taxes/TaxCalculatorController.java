package pl.futurecollars.invoicing.controller.taxes;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.service.TaxCalculatorService;

@RestController
@AllArgsConstructor
public class TaxCalculatorController implements TaxCalculatorApi {

    private TaxCalculatorService taxCalculatorService;

    @Override
    @ApiOperation(value = "Calculate incomes, costs and vat")
    public TaxCalculatorResponse calculateTaxes(@ApiParam(example = "555-555-55-55") @RequestBody Company company) {
        BigDecimal incomeMinusCosts = taxCalculatorService.getEarnings(company.getTaxIdentificationNumber());
        BigDecimal incomeMinusCostsMinusPensionInsurance = incomeMinusCosts.subtract(company.getPensionInsurance());
        BigDecimal taxCalculationBase = incomeMinusCostsMinusPensionInsurance.setScale(0, RoundingMode.HALF_UP);
        BigDecimal incomeTax = taxCalculationBase.multiply(BigDecimal.valueOf(0.19));
        BigDecimal healthInsuranceToSubtract = company.getHealthInsurance()
            .divide(BigDecimal.valueOf(0.09), RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(0.0775)).setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal incomeTaxMinusHealthInsurance = incomeTax.subtract(healthInsuranceToSubtract);

        return TaxCalculatorResponse.builder()
            .income(taxCalculatorService.income(company.getTaxIdentificationNumber()))
            .costs(taxCalculatorService.costs(company.getTaxIdentificationNumber()))
            .incomeMinusCosts(incomeMinusCosts)
            .pensionInsurance(company.getPensionInsurance())
            .incomeMinusCostsMinusPensionInsurance(incomeMinusCostsMinusPensionInsurance)
            .taxCalculationBase(taxCalculationBase)
            .incomeTax(incomeTax)
            .healthInsurancePaid(company.getHealthInsurance())
            .healthInsuranceToSubtract(healthInsuranceToSubtract)
            .incomeTaxMinusHealthInsurance(incomeTaxMinusHealthInsurance)
            .finalIncomeTaxValue(incomeTaxMinusHealthInsurance.setScale(0, RoundingMode.DOWN))
            .incomingVat(taxCalculatorService.incomingVat(company.getTaxIdentificationNumber()))
            .outgoingVat(taxCalculatorService.outgoingVat(company.getTaxIdentificationNumber()))
            .vatToPay(taxCalculatorService.getVatToPay(company.getTaxIdentificationNumber()))
            .build();
    }
}
