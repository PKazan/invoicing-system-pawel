package pl.futurecollars.invoicing.controller.taxes

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.util.JsonService
import spock.lang.Specification


@SpringBootTest
@AutoConfigureMockMvc
class TaxCalculatorControllerIntegrationTest extends Specification {

    private static String TAX_ENDPOINT = "/tax"
    private static String ENDPOINT = "/invoices"

    @Autowired
    MockMvc mockMvc

    @Autowired
    JsonService jsonService

    Invoice invoice = TestHelpers.invoice(1)

    def setup() {
        getAllInvoices().each {invoice -> deleteInvoiceById(invoice.id) }
    }

    def "returned correct values for seller"() {
        given:
        addUniqueInvoices(5)

        when:
        def taxCalculatorResponse = calculateTax("4")

        then:
        taxCalculatorResponse.income == 10000
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 10000
        taxCalculatorResponse.incomingVat == 800
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToPay == 800

        when:
        addUniqueInvoices(5)
        taxCalculatorResponse = calculateTax("4")

        then:
        taxCalculatorResponse.income == 20000
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 20000
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
        taxCalculatorResponse.earnings == -10000
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 800
        taxCalculatorResponse.vatToPay == -800

        when:
        addUniqueInvoices(5)
        taxCalculatorResponse = calculateTax("14")

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 20000
        taxCalculatorResponse.earnings == -20000
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 1600
        taxCalculatorResponse.vatToPay == -1600
    }

    def "returned 0 if no invoices was added"() {

        when:
        def taxCalculatorResponse = calculateTax("0")

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 0
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
        taxCalculatorResponse.earnings == 0
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToPay == 0
    }

    private int addInvoice(String invoiceAsJson) {
        Integer.valueOf(
                mockMvc.perform(MockMvcRequestBuilders.post(ENDPOINT)
                        .content(invoiceAsJson)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn()
                        .response
                        .contentAsString)
    }

    private List<Invoice> addUniqueInvoices(int count) {
        (1..count).collect { id ->
            def invoice = TestHelpers.invoice(id)
            invoice.id = addInvoice(jsonService.toJson(invoice))
            return invoice
        }
    }

    private List<Invoice> getAllInvoices() {
        def response = mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString

        return jsonService.toObject(response, Invoice[])
    }

    private void deleteInvoiceById(int id) {
        mockMvc.perform(MockMvcRequestBuilders.delete("$ENDPOINT/$id"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
    }

    private TaxCalculatorResponse calculateTax(String taxIdentificationNumber) {
        def response = mockMvc.perform(MockMvcRequestBuilders.get("$TAX_ENDPOINT/$taxIdentificationNumber"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString

        jsonService.toObject(response, TaxCalculatorResponse)

    }
}
