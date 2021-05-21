package pl.futurecollars.invoicing.controller.taxes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.futurecollars.invoicing.model.Company;

@RequestMapping("/tax")
@Api(tags = {"Tax-Controller"})
public interface TaxCalculatorApi {

    @PostMapping(value = "/{taxIdentificationNumber}", produces = {"application/json;charset=UTF-8"})
    @ApiOperation(value = "Calculate incomes, costs and vat")
    TaxCalculatorResponse calculateTaxes(@ApiParam(example = "555-555-55-55") @RequestBody Company company);
}
