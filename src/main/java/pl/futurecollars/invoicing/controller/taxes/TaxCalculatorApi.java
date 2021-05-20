package pl.futurecollars.invoicing.controller.taxes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/tax")
@Api(tags = {"Tax-Controller"})
public interface TaxCalculatorApi {
    @GetMapping(value = "/{taxIdentificationNumber}", produces = {"application/json;charset=UTF-8"})
    @ApiOperation(value = "Calculate incomes, costs and vat")
    TaxCalculatorResponse calculateTaxes(@ApiParam(example = "555-555-55-55") @PathVariable String taxIdentificationNumber);
}
