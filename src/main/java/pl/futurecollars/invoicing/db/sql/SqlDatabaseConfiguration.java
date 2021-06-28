package pl.futurecollars.invoicing.db.sql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

@Configuration
@Slf4j
@ConditionalOnProperty(name = "invoices.database", havingValue = "sql")
public class SqlDatabaseConfiguration {
    @Bean
    public Database<Invoice> sqlInvoiceDatabase(JdbcTemplate jdbcTemplate) {
        log.debug("Sql invoice db selected");
        return new InvoiceSqlDatabase(jdbcTemplate);
    }

    @Bean
    public Database<Company> sqlCompanyDatabase(JdbcTemplate jdbcTemplate) {
        log.debug("Sql company db selected");
        return new CompanySqlDatabase(jdbcTemplate);
    }
}
