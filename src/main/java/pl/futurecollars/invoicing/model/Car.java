package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Car {
    @ApiModelProperty(value = "Car registration number", required = true, example = "WGM-5MN8")
    private String registration;
    @ApiModelProperty(value = "Information whether car is also used for privat purposes", required = true, example = "true")
    private boolean includingPrivateExpense;
}
