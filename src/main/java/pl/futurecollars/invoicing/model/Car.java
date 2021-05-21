package pl.futurecollars.invoicing.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Car {
    private String registration;
    private boolean includingPrivateExpense;
}
