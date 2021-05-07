package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.util.JsonService
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class InvoiceControllerIntegrationTest extends Specification {

    @Autowired
    MockMvc mockMvc
    @Autowired
    JsonService jsonService
    Invoice invoice = TestHelpers.invoice(1)
    private LocalDate updatedDate = LocalDate.of(2021, 05, 03)

    def setup() {
        getAllInvoices().each { invoice -> deleteInvoice(invoice.id)}
    }

    def "empty array is returned when no invoices were added"() {
        expect:
        getAllInvoices() == []
    }

    def "returned correctly id when invoice is added"() {
        given:
        def invoiceAsJson = convertToJson()

        expect:
//        addInvoice(invoiceAsJson) == 1
//        addInvoice(invoiceAsJson) == 2
//        addInvoice(invoiceAsJson) == 3
        def id = addInvoice(invoiceAsJson)
        addInvoice(invoiceAsJson) == id + 1
        addInvoice(invoiceAsJson) == id + 2
        addInvoice(invoiceAsJson) == id + 3

    }

    def "returned all invoices when getting all invoices"() {
        given:
        def count = 3
        def expectedInvoices = addUniqueInvoices(count)

        expect:
        getAllInvoices().size() == count
        getAllInvoices() == expectedInvoices
    }

    def "returned invoice when getting by id"() {

        given:
        def expectedInvoice = addUniqueInvoices(5)
        def verifiedInvoice = expectedInvoice.get(2)
        def id = verifiedInvoice.getId()

        when:
        def response = mockMvc.perform(get("/invoices/$id"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoice = jsonService.toObject(response, Invoice)

        then:
        invoice == verifiedInvoice
    }

    def "returned status 404 when getting not existing invoice"() {

        given:
        addUniqueInvoices(5)

        expect:
        mockMvc.perform(
                get("/invoices/6")
        )
                .andExpect(status().isNotFound())
    }

    def "can delete invoice"() {
        given:
        def invoice = addUniqueInvoices(3)
        def deletedInvoice = invoice.get(2)
        def id = deletedInvoice.getId()

        when:
        mockMvc.perform(delete("/invoices/$id"))
                .andExpect(status().isNoContent())

        then:
        getAllInvoices().size() == 2

    }

    def "returned status 404 when deleting not existing invoice"() {
        expect:
        mockMvc.perform(delete("/invoices/2"))
                .andExpect(status().isNotFound())
    }

    def "invoice can be updated"() {
        given:
        def invoice = addUniqueInvoices(5)
        def updatedInvoice = invoice.get(3)
        updatedInvoice.date = updatedDate
        def id = updatedInvoice.getId()

        when:
        mockMvc.perform(put("/invoices/$id").content(jsonService.toJson(updatedInvoice)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())

        then:
        def response = mockMvc.perform(get("/invoices/$id"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoiceAfterPut = jsonService.toObject(response, Invoice)

        invoiceAfterPut == updatedInvoice
    }

    def "returned status 404 when updating not existing invoice"() {
        given:
        def invoiceAsJson = jsonService.toJson(invoice)

        expect:
        mockMvc.perform(put("/invoices/1").content(invoiceAsJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
    }

    private List<Invoice> getAllInvoices() {
       def response =  mockMvc.perform(get("/invoices"))
        .andExpect(status().isOk())
        .andReturn()
        .response
        .contentAsString

        return jsonService.toObject(response, Invoice[])

    }


    private List<Invoice> addUniqueInvoices(int count) {
        (1..count).collect { id ->
            def invoice = TestHelpers.invoice(id)
            invoice.id = addInvoice(jsonService.toJson(invoice))
            return invoice
        }
    }

    private int addInvoice(String invoiceAsJson) {
        Integer.valueOf(mockMvc.perform(post("/invoices").content(invoiceAsJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString
        )
    }

    private String convertToJson() {
        jsonService.toJson(invoice)
    }

    private ResultActions deleteInvoice(int id) {
        mockMvc.perform(delete("/invoices/$id"))
                .andExpect(status().isNoContent())
    }
}
