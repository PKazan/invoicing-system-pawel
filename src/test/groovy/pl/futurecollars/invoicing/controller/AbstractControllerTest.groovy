package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import pl.futurecollars.invoicing.controller.taxes.TaxCalculatorResponse
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.util.JsonService
import spock.lang.Specification

import java.time.LocalDate

class AbstractControllerTest extends Specification {

    public final static String TAX_ENDPOINT = "/tax"
    public final static String ENDPOINT = "/invoices"

    @Autowired
    MockMvc mockMvc

    @Autowired
    JsonService jsonService

    Invoice invoice = TestHelpers.invoice(1)

    public LocalDate updatedDate = LocalDate.of(2021, 05, 03)

    def setup() {
        getAllInvoices().each { invoice -> deleteInvoiceById(invoice.id) }
    }


    int addInvoice(String invoiceAsJson) {
        Integer.valueOf(
                mockMvc.perform(MockMvcRequestBuilders.post(ENDPOINT)
                        .content(invoiceAsJson)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn()
                        .response
                        .contentAsString)
    }

    List<Invoice> addUniqueInvoices(int count) {
        (1..count).collect { id ->
            def invoice = TestHelpers.invoice(id)
            invoice.id = addInvoice(jsonService.toJson(invoice))
            return invoice
        }
    }

    List<Invoice> getAllInvoices() {
        def response = mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString

        return jsonService.toObject(response, Invoice[])
    }

    void deleteInvoiceById(int id) {
        mockMvc.perform(MockMvcRequestBuilders.delete("$ENDPOINT/$id"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
    }

    TaxCalculatorResponse calculateTax(Company company) {
        String body = jsonService.toJson(company)
        def response = mockMvc.perform(MockMvcRequestBuilders.post("$TAX_ENDPOINT").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString

        jsonService.toObject(response, TaxCalculatorResponse)
    }

    String convertToJson(Invoice invoice) {
        jsonService.toJson(invoice)
    }
}
