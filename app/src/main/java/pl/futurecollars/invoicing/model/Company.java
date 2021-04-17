package pl.futurecollars.invoicing.model;

import lombok.Data;

@Data
public class Company {

    private String identificationNumber;
    private String address;
    private String name;

    public Company(String identificationNumber, String address, String name) {
        this.identificationNumber = identificationNumber;
        this.address = address;
        this.name = name;
    }

}

