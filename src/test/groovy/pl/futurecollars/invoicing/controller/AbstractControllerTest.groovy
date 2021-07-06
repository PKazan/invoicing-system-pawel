package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.controller.taxes.TaxCalculatorResponse
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.util.JsonService
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WithMockUser
@SpringBootTest
@AutoConfigureMockMvc
class AbstractControllerTest extends Specification {

    public static final String TAX_ENDPOINT = "/tax"
    public static final String INVOICE_ENDPOINT = "/invoices"
    public static final String COMPANY_ENDPOINT = "/companies"

    @Autowired
    MockMvc mockMvc

    @Autowired
    JsonService jsonService

    Invoice invoice = TestHelpers.invoice(1)
    Company company = TestHelpers.company(1)

    public LocalDate updatedDate = LocalDate.of(2021, 05, 03)

    def setup() {
        getAllInvoices().each { invoice -> deleteInvoiceById(invoice.id) }
        getAllCompanies().each { company -> deleteCompanyById(company.id) }

    }

    int addInvoiceAndReturnId(String invoiceAsJson) {
        addItemAndReturnId(invoiceAsJson, INVOICE_ENDPOINT)
    }

    int addCompanyAndReturnId(String companyAsJson) {
        addItemAndReturnId(companyAsJson, COMPANY_ENDPOINT)
    }

    private int addItemAndReturnId(String companyAsJson, String endpoint) {
        Integer.valueOf(
                mockMvc.perform(post(endpoint)
                        .content(companyAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                        .andExpect(status().isOk())
                        .andReturn()
                        .response
                        .contentAsString)
    }

    List<Invoice> addUniqueInvoices(long count) {
        (1..count).collect { id ->
            def invoice = TestHelpers.invoice(id)
            invoice.id = addInvoiceAndReturnId(jsonService.toJson(invoice))
            return invoice
        }
    }

    List<Company> addUniqueCompany(long count) {
        (1..count).collect { id ->
            def company = TestHelpers.company(id)
            company.id = addCompanyAndReturnId(jsonService.toJson(company))
            return company
        }
    }

    List<Invoice> getAllInvoices() {
        return getAllItems(Invoice[], INVOICE_ENDPOINT)
    }

    List<Company> getAllCompanies() {
        return getAllItems(Company[], COMPANY_ENDPOINT)
    }

    private <T> T getAllItems(Class<T> clazz, String endpoint) {
        def response = mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        return jsonService.toObject(response, clazz)
    }

    void deleteInvoiceById(long id) {
        mockMvc.perform(delete("$INVOICE_ENDPOINT/$id")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent())
    }

    void deleteCompanyById(long id) {
        mockMvc.perform(delete("$COMPANY_ENDPOINT/$id")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent())
    }

    TaxCalculatorResponse calculateTax(Company company) {
        String body = jsonService.toJson(company)
        def response = mockMvc.perform(post("$TAX_ENDPOINT").content(body).contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        jsonService.toObject(response, TaxCalculatorResponse)
    }

    String convertToJson(Invoice invoice) {
        jsonService.toJson(invoice)
    }

    String convertToJson(Company company) {
        jsonService.toJson(company)
    }

    Invoice getInvoiceById(long id) {
        getItemById(id, INVOICE_ENDPOINT, Invoice)
    }

    Company getCompanyById(long id) {
        getItemById(id, COMPANY_ENDPOINT, Company)
    }

    private <T> T getItemById(long id, String endpoint, Class<T> clazz) {
        def itemAsString = mockMvc.perform(get("$endpoint/$id"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        return jsonService.toObject(itemAsString, clazz)
    }
}
