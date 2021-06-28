package pl.futurecollars.invoicing.db.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

@Configuration
@Slf4j
@ConditionalOnProperty(name = "invoices.database", havingValue = "memory")
public class InMemoryDatabaseConfiguration {

    @Bean
    public Database<Invoice> inMemoryInvoiceDatabase() {
        log.debug("InMemory invoice db selected");
        return new InMemoryDatabase<Invoice>();
    }

    @Bean
    public Database<Company> inMemoryCompanyDatabase() {
        log.debug("InMemory company db selected");
        return new InMemoryDatabase<Company>();
    }
}
