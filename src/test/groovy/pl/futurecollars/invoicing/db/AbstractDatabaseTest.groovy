package pl.futurecollars.invoicing.db

import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

import static pl.futurecollars.invoicing.helpers.TestHelpers.invoice

abstract class AbstractDatabaseTest extends Specification {

    List<Invoice> invoices = (1..12).collect { invoice(it) }

    abstract Database<Invoice> getDatabaseInstance()

    Database<Invoice> database

    def setup() {
        database = getDatabaseInstance()
        database.reset()

        assert database.getAll().isEmpty()
    }

    def "should save invoices returning sequential id"() {
        when:
        def ids = invoices.collect { it.id = database.save(it) }

        then:
        (1..invoices.size() - 1).forEach { assert ids[it] == ids[0] + it }
    }

    def "invoice should have id set to correct value"() {
        when:
        def ids = invoices.collect { it.id = database.save(it) }

        then:
        ids.forEach { assert database.getById(it).isPresent() }
        ids.forEach { assert database.getById(it).get().getId() == it }

    }

    def "get by id returns expected invoice"() {
        when:
        def ids = invoices.collect { it.id = database.save(it) }

        then:
        ids.forEach {
            def expectedInvoice = resetIds(invoices.get((int) (it - ids[0]))).toString()
            def invoiceFromDb = resetIds(database.getById(it).get()).toString()
            assert invoiceFromDb == expectedInvoice
        }
    }

    def "get by id returns empty optional when there is no invoice with given id"() {
        expect:
        !database.getById(1).isPresent()
    }

    def "get all returns empty collection if there were no invoices"() {
        expect:
        database.getAll().isEmpty()
    }

    def "get all returns all invoices in the database, deleted invoice is not returned"() {
        given:
        invoices.forEach({ it.id = database.save(it) })
        expect:
        database.getAll().size() == invoices.size()
        database.getAll().eachWithIndex { invoice, index ->
            def invoiceFromDbAsString = resetIds(invoice).toString()
            def expectedInvoiceAsString = resetIds(invoices.get(index)).toString()
            assert invoiceFromDbAsString == expectedInvoiceAsString
        }
        when:
        def firstInvoiceId = database.getAll().get(0).getId()
        database.delete(firstInvoiceId)

        then:
        database.getAll().size() == invoices.size() - 1
        database.getAll().eachWithIndex { invoice, index ->
            def invoiceFromDbAsString = resetIds(invoice).toString()
            def expectedInvoiceAsString = resetIds(invoices.get(index + 1)).toString()
            assert invoiceFromDbAsString == expectedInvoiceAsString
        }
        database.getAll().forEach({ it.id != firstInvoiceId })
    }

    def "can delete all invoices"() {
        given:
        invoices.forEach({ it.id = database.save(it) })
        when:
        invoices.forEach({ database.delete(it.getId()) })
        then:
        database.getAll().isEmpty()
    }

    def "deleting not existing invoice returns Optional"() {
        expect:
        database.delete(123) == Optional.empty()
    }

    def "it's possible to update the invoice, original invoice is returned"() {
        given:
        def originalInvoice = invoices.get(0)
        originalInvoice.id = database.save(originalInvoice)

        def expectedInvoice = invoices.get(1)
        expectedInvoice.id = originalInvoice.id

        when:
        def result = database.update(originalInvoice.id, expectedInvoice)

        then:

        def invoiceAfterUpdate = database.getById(originalInvoice.id).get()
        def invoiceAfterUpdateAsString = resetIds(invoiceAfterUpdate).toString()
        def expectedInvoiceAsString = resetIds(expectedInvoice).toString()
        invoiceAfterUpdateAsString == expectedInvoiceAsString
        resetIds(result.get()).toString() == resetIds(originalInvoice).toString()
    }

    def "updating not existing invoice throws exception"() {

        when:
        database.update(213, invoices.get(1))
        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Id 213 does not exist"
    }

    private static Invoice resetIds(Invoice invoice) {
        invoice.getBuyer().id = 0
        invoice.getSeller().id = 0
        invoice.entries.forEach { it.carInPrivateUse.id = 0 }
        invoice.entries.forEach { it.id = 0 }
        invoice
    }
}
