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
        getAllInvoices().each { deleteInvoiceById(invoice.id) }
    }

    def "returned correct values when tax identification number was found"() {
        given:
        addUniqueInvoices(5)

        when:
        def taxCalculatorResponse = calculateTax("5")

        then:
        taxCalculatorResponse.income == 15000
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 15000
        taxCalculatorResponse.incomingVat == 1200
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToPay == 1200


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

    private String convertToJson(int id) {
        jsonService.toJson(TestHelpers.invoice(id))

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
