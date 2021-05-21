package pl.futurecollars.invoicing.controller.taxes

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoicing.controller.AbstractControllerTest

@SpringBootTest
@AutoConfigureMockMvc
class TaxCalculatorControllerIntegrationTest extends AbstractControllerTest {

    def "returned correct values for seller"() {
        given:
        addUniqueInvoices(5)

        when:
        def taxCalculatorResponse = calculateTax("4")

        then:
        taxCalculatorResponse.income == 10000
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.incomeMinusCosts == 10000
        taxCalculatorResponse.incomingVat == 800
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToPay == 800

        when:
        addUniqueInvoices(5)
        taxCalculatorResponse = calculateTax("4")

        then:
        taxCalculatorResponse.income == 20000
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.incomeMinusCosts == 20000
        taxCalculatorResponse.incomingVat == 1600
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToPay == 1600

    }

    def "return correct value for buyer"() {
        given:
        addUniqueInvoices(5)

        when:
        def taxCalculatorResponse = calculateTax("14")

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 10000
        taxCalculatorResponse.incomeMinusCosts == -10000
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 800
        taxCalculatorResponse.vatToPay == -800

        when:
        addUniqueInvoices(5)
        taxCalculatorResponse = calculateTax("14")

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 20000
        taxCalculatorResponse.incomeMinusCosts == -20000
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 1600
        taxCalculatorResponse.vatToPay == -1600
    }

    def "returned 0 if no invoice was added"() {

        when:
        def taxCalculatorResponse = calculateTax("0")

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
        def taxCalculatorResponse = calculateTax("20")

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
        def taxCalculatorResponse = calculateTax("11")

        then:
        taxCalculatorResponse.income == 66000
        taxCalculatorResponse.costs == 1000
        taxCalculatorResponse.incomeMinusCosts == 65000
        taxCalculatorResponse.incomingVat == 5280
        taxCalculatorResponse.outgoingVat == 80
        taxCalculatorResponse.vatToPay == 5200
    }
}
