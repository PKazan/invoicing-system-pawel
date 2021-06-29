package pl.futurecollars.invoicing.controller

import org.springframework.http.MediaType
import pl.futurecollars.invoicing.helpers.TestHelpers
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Unroll
class CompanyControllerIntegrationTest extends AbstractControllerTest {
    def "empty array is returned when no companies were added"() {
        expect:
        getAllCompanies() == []
    }

    def "returned correctly id when company is added"() {
        given:
        def company = TestHelpers.company(1)
        def firstCompanyAsJson = convertToJson(company)
        def secondCompany = TestHelpers.company(2)
        def secondCompanyAsJson = convertToJson(secondCompany)
        def thirdCompany = TestHelpers.company(3)
        def thirdCompanyAsJson = convertToJson(thirdCompany)
        def fourthCompany = TestHelpers.company(4)
        def fourthCompanyAsJson = convertToJson(fourthCompany)

        expect:
        def id = addCompanyAndReturnId(firstCompanyAsJson)
        addCompanyAndReturnId(secondCompanyAsJson) == id + 1
        addCompanyAndReturnId(thirdCompanyAsJson) == id + 2
        addCompanyAndReturnId(fourthCompanyAsJson) == id + 3

    }

    def "returned all companies when getting all companies"() {
        given:
        def count = 3
        def expectedCompany = addUniqueCompany(count)

        expect:
        getAllCompanies().size() == count
        getAllCompanies() == expectedCompany
    }

    def "returned company when getting by id"() {

        given:
        def expectedCompany = addUniqueCompany(5)
        def verifiedCompany = expectedCompany.get(2)
        def id = verifiedCompany.getId()

        when:
        def company = getCompanyById(id)

        then:
        company == verifiedCompany
    }

    def "returned status 404 when getting not existing company"() {

        given:
        addUniqueCompany(5)

        expect:
        mockMvc.perform(
                get("$COMPANY_ENDPOINT/$id")
        )
                .andExpect(status().isNotFound())

        where:
        id << [-50, -1, 0, 6, 50, 196]
    }

    def "can delete company"() {
        given:
        def company = addUniqueCompany(3)
        def deletedCompany = company.get(2)
        def id = deletedCompany.getId()

        when:
        mockMvc.perform(delete("$COMPANY_ENDPOINT/$id"))
                .andExpect(status().isNoContent())

        then:
        getAllCompanies().size() == 2

    }

    def "returned status 404 when deleting not existing company"() {

        given:
        addUniqueCompany(5)

        expect:
        mockMvc.perform(delete("$COMPANY_ENDPOINT/$id"))
                .andExpect(status().isNotFound())

        where:
        id << [-50, -1, 0, 6, 50, 196]
    }

    def "company can be updated"() {
        given:
        def companies = addUniqueCompany(5)
        def updatedCompany = companies.get(3)
        updatedCompany.taxIdentificationNumber = "123-123-12-31"
        def id = updatedCompany.getId()

        when:
        mockMvc.perform(put("$COMPANY_ENDPOINT/$id").content(jsonService.toJson(updatedCompany)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())

        then:
        def companyAfterPut = getCompanyById(id)
        companyAfterPut == updatedCompany
    }

    def "returned status 404 when updating not existing company"() {
        given:
        def companyAsJson = jsonService.toJson(company)

        expect:
        mockMvc.perform(put("$COMPANY_ENDPOINT/$id").content(companyAsJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())

        where:
        id << [-50, -1, 0, 6, 50, 196]
    }

}

