package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Company {

    @ApiModelProperty(value = "Tax identification number", required = true, example = "555-555-55-55")
    private String taxIdentificationNumber;
    @ApiModelProperty(value = "Company address", required = true, example = "Mazowiecka 134, 32-525, Radzionków")
    private String address;
    @ApiModelProperty(value = "Company name", required = true, example = "Invoice House Ltd.")
    private String name;

}
