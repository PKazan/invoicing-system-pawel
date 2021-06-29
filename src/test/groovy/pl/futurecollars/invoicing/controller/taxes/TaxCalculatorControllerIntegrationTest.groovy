package pl.futurecollars.invoicing.controller.taxes

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoicing.controller.AbstractControllerTest
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Car
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat

import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
class TaxCalculatorControllerIntegrationTest extends AbstractControllerTest {

    def "returned correct values for seller"() {
        given:
        addUniqueInvoices(5)


        when:
        def taxCalculatorResponse = calculateTax((TestHelpers.company(4)))

        then:
        taxCalculatorResponse.income == 10000
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.incomeMinusCosts == 10000
        taxCalculatorResponse.incomingVat == 800
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToPay == 800

        when:
        addInvoiceAndReturnId(jsonService.toJson(Invoice.builder()
                .date(LocalDate.now())
                .number("2020/05/03/123")
                .buyer(TestHelpers.company(50))
                .seller(TestHelpers.company(4))
                .entries((1).collect { TestHelpers.product(1) })
                .build()))

        taxCalculatorResponse = calculateTax(TestHelpers.company(4))

        then:
        taxCalculatorResponse.income == 11000
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.incomeMinusCosts == 11000
        taxCalculatorResponse.incomingVat == 880
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToPay == 880
    }

    def "return correct value for buyer"() {
        given:
        addUniqueInvoices(5)

        when:
        def taxCalculatorResponse = calculateTax(TestHelpers.company(14))

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 10000
        taxCalculatorResponse.incomeMinusCosts == -10000
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 800
        taxCalculatorResponse.vatToPay == -800

        when:
        addInvoiceAndReturnId(jsonService.toJson(Invoice.builder()
                .date(LocalDate.now())
                .number("2020/05/03/124")
                .buyer(TestHelpers.company(14))
                .seller(TestHelpers.company(56))
                .entries((1).collect { TestHelpers.product(1) })
                .build()))
        taxCalculatorResponse = calculateTax(TestHelpers.company(14))

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 11000
        taxCalculatorResponse.incomeMinusCosts == -11000
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 880
        taxCalculatorResponse.vatToPay == -880
    }

    def "returned 0 if no invoice was added"() {

        when:
        def taxCalculatorResponse = calculateTax(TestHelpers.company(0))

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.incomeMinusCosts == 0
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToPay == 0
    }

    def "returned 0 if invoice with given tax identification number was not found"() {
        given:
        addUniqueInvoices(5)

        when:
        def taxCalculatorResponse = calculateTax(TestHelpers.company(20))

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.incomeMinusCosts == 0
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToPay == 0
    }

    def "returned correct value if company is buyer and seller"() {
        given:
        addUniqueInvoices(20)

        when:
        def taxCalculatorResponse = calculateTax(TestHelpers.company(11))

        then:
        taxCalculatorResponse.income == 66000
        taxCalculatorResponse.costs == 1000
        taxCalculatorResponse.incomeMinusCosts == 65000
        taxCalculatorResponse.incomingVat == 5280
        taxCalculatorResponse.outgoingVat == 80
        taxCalculatorResponse.vatToPay == 5200
    }

    def "returned correct value with car in private use"() {
        given:
        def invoice = Invoice.builder()
                .date(LocalDate.now())
                .number("2021/11/12/01")
                .buyer(TestHelpers.company(1))
                .seller(TestHelpers.company(2))
                .entries(List.of(InvoiceEntry.builder()
                        .quantity(BigDecimal.ONE)
                        .price(BigDecimal.valueOf(2000))
                        .vatValue(BigDecimal.valueOf(460))
                        .vatRate(Vat.VAT_23)
                        .carInPrivateUse(Car.builder()
                                .registration("AA-5522")
                                .includingPrivateExpense(true)
                                .build())
                        .build()))
                .build()
        addInvoiceAndReturnId(jsonService.toJson(invoice))

        when:
        def taxCalculatorResponse = calculateTax(invoice.getSeller())

        then:
        taxCalculatorResponse.income == 2000
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.incomeMinusCosts == 2000
        taxCalculatorResponse.incomingVat == 460
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToPay == 460

        when:
        taxCalculatorResponse = calculateTax(invoice.getBuyer())
        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 2230
        taxCalculatorResponse.incomeMinusCosts == -2230
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 230
        taxCalculatorResponse.vatToPay == -230
    }

    def "return correct calculations for all fields"() {
        given:
        Company company = TestHelpers.company(1)

        def invoiceAsBuyer = Invoice.builder()
                .date(LocalDate.now())
                .number("2020/01/01/01")
                .seller(TestHelpers.company(2))
                .buyer(company)
                .entries(List.of(InvoiceEntry.builder()
                        .quantity(BigDecimal.ONE)
                        .price(BigDecimal.valueOf(11329.47))
                        .vatValue(BigDecimal.ZERO)
                        .vatRate(Vat.VAT_0)
                        .carInPrivateUse(Car.builder()
                        .registration("AA-5522")
                                .build())
                        .build()))
                .build()

        addInvoiceAndReturnId(jsonService.toJson(invoiceAsBuyer))

        def invoiceAsSeller = Invoice.builder()
                .date(LocalDate.now())
                .number("2020/01/01/02")
                .seller(company)
                .buyer(TestHelpers.company(3))
                .entries(List.of(InvoiceEntry.builder()
                        .quantity(BigDecimal.ONE)
                        .price(BigDecimal.valueOf(76011.62))
                        .vatValue(BigDecimal.ZERO)
                        .vatRate(Vat.VAT_0)
                        .build()))
                .build()

        addInvoiceAndReturnId(jsonService.toJson(invoiceAsSeller))

        when:
        def taxCalculatorResponse = calculateTax(company)

        then:
        taxCalculatorResponse.income == 76011.62
        taxCalculatorResponse.costs == 11329.47
        taxCalculatorResponse.incomeMinusCosts == 64682.15
        taxCalculatorResponse.incomeMinusCostsMinusPensionInsurance == 64167.58
        taxCalculatorResponse.taxCalculationBase == 64168
        taxCalculatorResponse.incomeTax == 12191.92
        taxCalculatorResponse.healthInsuranceToSubtract == 275.50
        taxCalculatorResponse.incomeTaxMinusHealthInsurance == 11916.42
        taxCalculatorResponse.finalIncomeTaxValue == 11916.00
    }
}
