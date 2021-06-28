package pl.futurecollars.invoicing.db.jpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

@Configuration
@Slf4j
@ConditionalOnProperty(name = "invoices.database", havingValue = "jpa")
public class JpaDatabaseConfiguration {

    @Bean
    public Database<Invoice> jpaInvoiceDatabase(InvoiceRepository invoiceRepository) {
        log.debug("JPA invoice db selected");
        return new JpaDatabase<>(invoiceRepository);
    }

    @Bean
    public Database<Company> jpaCompanyDatabase(CompanyRepository companyRepository) {
        log.debug("JPA company db selected");
        return new JpaDatabase<>(companyRepository);
    }
}
