package pl.futurecollars.invoicing.controller

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Unroll
class InvoiceControllerIntegrationTest extends AbstractControllerTest {

    def "empty array is returned when no invoices were added"() {
        expect:
        getAllInvoices() == []
    }

    def "returned correctly id when invoice is added"() {
        given:
        def invoice = TestHelpers.invoice(1)
        def firstInvoiceAsJson = convertToJson(invoice)
        def secondInvoice = TestHelpers.invoice(2)
        def secondInvoiceAsJson = convertToJson(secondInvoice)
        def thirdInvoice = TestHelpers.invoice(3)
        def thirdInvoiceAsJson = convertToJson(thirdInvoice)
        def fourthInvoice = TestHelpers.invoice(4)
        def fourthInvoiceAsJson = convertToJson(fourthInvoice)

        expect:
        def id = addInvoice(firstInvoiceAsJson)
        addInvoice(secondInvoiceAsJson) == id + 1
        addInvoice(thirdInvoiceAsJson) == id + 2
        addInvoice(fourthInvoiceAsJson) == id + 3

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
        def response = mockMvc.perform(get("$ENDPOINT/$id"))
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
                get("$ENDPOINT/$id")
        )
                .andExpect(status().isNotFound())

        where:
        id << [-50, -1, 0, 6, 50, 196]
    }

    def "can delete invoice"() {
        given:
        def invoice = addUniqueInvoices(3)
        def deletedInvoice = invoice.get(2)
        def id = deletedInvoice.getId()

        when:
        mockMvc.perform(delete("$ENDPOINT/$id"))
                .andExpect(status().isNoContent())

        then:
        getAllInvoices().size() == 2

    }

    def "returned status 404 when deleting not existing invoice"() {

        given:
        addUniqueInvoices(5)

        expect:
        mockMvc.perform(delete("$ENDPOINT/$id"))
                .andExpect(status().isNotFound())

        where:
        id << [-50, -1, 0, 6, 50, 196]
    }

    def "invoice can be updated"() {
        given:
        def invoice = addUniqueInvoices(5)
        def updatedInvoice = invoice.get(3)
        updatedInvoice.date = updatedDate
        def id = updatedInvoice.getId()

        when:
        mockMvc.perform(put("$ENDPOINT/$id").content(jsonService.toJson(updatedInvoice)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())

        then:
        def response = mockMvc.perform(get("$ENDPOINT/$id"))
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
        mockMvc.perform(put("$ENDPOINT/$id").content(invoiceAsJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())

        where:
        id << [-50, -1, 0, 6, 50, 196]
    }
}
