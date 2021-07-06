package pl.futurecollars.invoicing.controller

import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.*
import spock.lang.IgnoreIf
import spock.lang.Requires
import spock.lang.Unroll

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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
        def id = addInvoiceAndReturnId(firstInvoiceAsJson)
        addInvoiceAndReturnId(secondInvoiceAsJson) == id + 1
        addInvoiceAndReturnId(thirdInvoiceAsJson) == id + 2
        addInvoiceAndReturnId(fourthInvoiceAsJson) == id + 3

    }

    def "returned all invoices when getting all invoices"() {
        given:
        def count = 3
        def expectedInvoices = addUniqueInvoices(count)

        when:
        def invoices = getAllInvoices()

        then:
        invoices.size() == count
        invoices.forEach { invoice -> resetIds(invoice) } == expectedInvoices.forEach { expectedInvoice -> resetIds(expectedInvoice) }
    }

    def "returned invoice when getting by id"() {

        given:
        def expectedInvoice = addUniqueInvoices(5)
        def verifiedInvoice = expectedInvoice.get(2)
        def id = verifiedInvoice.getId()

        when:
        def invoice = getInvoiceById(id)

        then:

        resetIds(invoice) == resetIds(verifiedInvoice)
    }

    @Requires({ System.getProperty('spring.profiles.active', 'memory').contains("jpa") })
    def "returned status 404 when getting not existing invoice from file"() {

        given:
        addUniqueInvoices(5)

        expect:
        mockMvc.perform(
                get("$INVOICE_ENDPOINT/$id")
        )
                .andExpect(status().isNotFound())

        where:
        id << [-50, -1, 0, 6, 50, 196]
    }

    @IgnoreIf({ System.getProperty('spring.profiles.active', 'memory').contains("jpa") })
    def "returned status 404 when getting not existing invoice"() {

        given:
        List<InvoiceEntry> entries = new ArrayList<>()
        entries.add(new InvoiceEntry(100, "abc", BigDecimal.valueOf(123), BigDecimal.TEN, BigDecimal.valueOf(123), Vat.VAT_23, Car.builder().registration("xx-111").includingPrivateExpense(false).build()))
        entries.add(new InvoiceEntry(123, "abc", BigDecimal.valueOf(234), BigDecimal.TEN, BigDecimal.valueOf(213), Vat.VAT_23, Car.builder().registration("yy-333").includingPrivateExpense(false).build()))

        (1..5).collect { id ->
            addInvoiceAndReturnId(jsonService.toJson(Invoice.builder()
                    .date(LocalDate.now())
                    .number("2020/05/03/" + id)
                    .buyer(Company.builder()
                            .id(123123)
                            .taxIdentificationNumber("555-555-55-55")
                            .address("Mazowiecka 134, 32-525, Radzionków")
                            .name("Invoice House Ltd.")
                            .healthInsurance(319.94)
                            .pensionInsurance(514.57)
                            .build())
                    .seller(
                            Company.builder()
                                    .id(123123)
                                    .taxIdentificationNumber("555-555-55-55")
                                    .address("Mazowiecka 134, 32-525, Radzionków")
                                    .name("Invoice House Ltd.")
                                    .healthInsurance(319.94)
                                    .pensionInsurance(514.57)
                                    .build())
                    .entries(entries)
                    .build()))
        }

        expect:
        mockMvc.perform(
                get("$INVOICE_ENDPOINT/$id")
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
        mockMvc.perform(delete("$INVOICE_ENDPOINT/$id")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent())

        then:
        getAllInvoices().size() == 2

    }

    @Requires({ System.getProperty('spring.profiles.active', 'memory').contains("jpa") })
    def "returned status 404 when deleting not existing invoice from file"() {

        given:
        addUniqueInvoices(5)

        expect:
        mockMvc.perform(delete("$INVOICE_ENDPOINT/$id")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound())

        where:
        id << [-50, -1, 0, 6, 50, 196]
    }

    @IgnoreIf({ System.getProperty('spring.profiles.active', 'memory').contains("jpa") })
    def "returned status 404 when deleting not existing invoice"() {
        given:

        List<InvoiceEntry> entries = new ArrayList<>()
        entries.add(new InvoiceEntry(100, "abc", BigDecimal.valueOf(123), BigDecimal.TEN, BigDecimal.valueOf(123), Vat.VAT_23, Car.builder().registration("xx-111").includingPrivateExpense(false).build()))
        entries.add(new InvoiceEntry(123, "abc", BigDecimal.valueOf(234), BigDecimal.TEN, BigDecimal.valueOf(213), Vat.VAT_23, Car.builder().registration("yy-333").includingPrivateExpense(false).build()))

        (1..5).collect { id ->
            addInvoiceAndReturnId(jsonService.toJson(Invoice.builder()
                    .date(LocalDate.now())
                    .number("2020/05/03/" + id)
                    .buyer(Company.builder()
                            .id(123123)
                            .taxIdentificationNumber("555-555-55-55")
                            .address("Mazowiecka 134, 32-525, Radzionków")
                            .name("Invoice House Ltd.")
                            .healthInsurance(319.94)
                            .pensionInsurance(514.57)
                            .build())
                    .seller(
                            Company.builder()
                                    .id(123123)
                                    .taxIdentificationNumber("555-555-55-55")
                                    .address("Mazowiecka 134, 32-525, Radzionków")
                                    .name("Invoice House Ltd.")
                                    .healthInsurance(319.94)
                                    .pensionInsurance(514.57)
                                    .build())
                    .entries(entries)
                    .build()))
        }
        expect:
        mockMvc.perform(delete("$INVOICE_ENDPOINT/$id")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
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
        mockMvc.perform(put("$INVOICE_ENDPOINT/$id").content(jsonService.toJson(updatedInvoice)).contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent())

        then:
        def invoiceAfterPut = getInvoiceById(id)

        resetIds(invoiceAfterPut) == resetIds(updatedInvoice)
    }

    def "returned status 404 when updating not existing invoice"() {
        given:
        def invoiceAsJson = jsonService.toJson(invoice)

        expect:
        mockMvc.perform(put("$INVOICE_ENDPOINT/$id").content(invoiceAsJson).contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound())

        where:
        id << [-50, -1, 0, 6, 50, 196]
    }

    private static Invoice resetIds(Invoice invoice) {
        invoice.getBuyer().id = 0
        invoice.getSeller().id = 0
        invoice.entries.forEach { it.carInPrivateUse.id = 0 }
        invoice.entries.forEach { it.id = 0 }
        invoice
    }
}
