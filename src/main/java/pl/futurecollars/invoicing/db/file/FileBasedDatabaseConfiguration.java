package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.util.FilesService;
import pl.futurecollars.invoicing.util.JsonService;

@Configuration
@Slf4j
@ConditionalOnProperty(name = "invoices.database", havingValue = "file")
public class FileBasedDatabaseConfiguration {

    @Bean
    public Database<Invoice> invoiceFileBasedDatabase(
        IdService idService,
        FilesService filesService,
        JsonService jsonService,
        @Value("${invoices.database.directory}") String databaseDirectory,
        @Value("${invoices.database.invoices.file}") String invoicesFile
    ) throws IOException {
        log.debug("Invoice file db selected");
        Path filePath = Files.createTempFile(databaseDirectory, invoicesFile);

        return new FileBasedDatabase<Invoice>(filePath, idService, filesService, jsonService, Invoice.class);
    }

    @Bean
    public Database<Company> companyFileBasedDatabase(
        IdService idService,
        FilesService filesService,
        JsonService jsonService,
        @Value("${invoices.database.directory}") String databaseDirectory,
        @Value("${invoices.database.companies.file}") String invoicesFile
    ) throws IOException {
        log.debug("Company file db selected");
        Path filePath = Files.createTempFile(databaseDirectory, invoicesFile);

        return new FileBasedDatabase<Company>(filePath, idService, filesService, jsonService, Company.class);
    }

    @Bean
    public IdService idService(
        FilesService filesService,
        @Value("${invoices.database.directory}") String databaseDirectory,
        @Value("${invoices.database.id.file}") String idFile
    ) throws IOException {
        Path filePath = Files.createTempFile(databaseDirectory, idFile);
        return new IdService(filePath, filesService);
    }
}
