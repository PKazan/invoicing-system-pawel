package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceEntry {

    @ApiModelProperty(value = "Product/service description", required = true, example = "Dell x12 v3")
    private String description;
    @ApiModelProperty(value = "Number of items", required = true, example = "85")
    private int quantity;
    @ApiModelProperty(value = "Product/service net price", required = true, example = "1857.15")
    private BigDecimal price;
    @ApiModelProperty(value = "Product/service vat value", required = true, example = "0")
    private BigDecimal vatValue;
    @ApiModelProperty(value = "Tax rate", required = true, example = "VAT_0")
    private Vat vatRate;
}
