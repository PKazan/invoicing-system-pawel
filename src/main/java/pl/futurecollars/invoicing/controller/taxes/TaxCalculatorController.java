package pl.futurecollars.invoicing.controller.taxes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.service.TaxCalculatorService;

@RequestMapping("/tax")
@RestController
@Api(tags = {"Tax-Controller"})
@AllArgsConstructor
public class TaxCalculatorController {

    private TaxCalculatorService taxCalculatorService;

    @GetMapping(value = "/{taxIdentificationNumber}", produces = {"application/json;charset=UTF-8"})
    @ApiOperation(value = "Calculate incomes, costs and vat")
    public TaxCalculatorResponse calculateTaxes(@ApiParam(example = "555-555-55-55") @PathVariable String taxIdentificationNumber) {
        return TaxCalculatorResponse.builder()
            .income(taxCalculatorService.income(taxIdentificationNumber))
            .costs(taxCalculatorService.costs(taxIdentificationNumber))
            .incomingVat(taxCalculatorService.incomingVat(taxIdentificationNumber))
            .outgoingVat(taxCalculatorService.outgoingVat(taxIdentificationNumber))
            .earnings(taxCalculatorService.getEarnings(taxIdentificationNumber))
            .vatToPay(taxCalculatorService.getVatToPay(taxIdentificationNumber))
            .build();
    }
}
