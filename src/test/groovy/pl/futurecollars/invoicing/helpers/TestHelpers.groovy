package pl.futurecollars.invoicing.helpers

import pl.futurecollars.invoicing.model.Car
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat

import java.time.LocalDate

class TestHelpers {

    static company(int id) {
        Company.builder()
                .taxIdentificationNumber("$id")
                .address("ul. Bukowinska 24d/$id 02-703 Warszawa, Polska")
                .name("iCode Trust $id Sp. z o.o")
                .healthInsurance(319.94)
                .pensionInsurance(514.57)
                .build()
    }

    static product(int id) {
        InvoiceEntry.builder()
                .description("Programming course $id")
                .quantity(1)
                .price(BigDecimal.valueOf(id * 1000).setScale(2))
                .vatValue(BigDecimal.valueOf(id * 1000 * 0.08).setScale(2))
                .vatRate(Vat.VAT_8)
                .carInPrivateUse(car())
                .build()
    }

    static invoice(int id) {
        Invoice.builder()
                .date(LocalDate.now())
                .number("2020/05/03/" + id)
                .buyer(company(id + 10))
                .seller(company(id))
                .entries((1..id).collect { product(it) })
                .build()
    }

    static car() {
        Car.builder()
                .registration("WMG-12312")
                .includingPrivateExpense(false)
                .build()
    }
}
