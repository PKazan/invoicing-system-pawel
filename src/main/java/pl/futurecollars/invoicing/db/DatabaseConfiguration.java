package pl.futurecollars.invoicing.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.file.FileBasedDatabase;
import pl.futurecollars.invoicing.db.file.IdService;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.util.FilesService;
import pl.futurecollars.invoicing.util.JsonService;

@Slf4j
@Configuration
public class DatabaseConfiguration {

    @Bean
    @ConditionalOnProperty(name = "invoices.database", havingValue = "file")
    public Database fileBasedDatabase(
        IdService idService,
        FilesService filesService,
        JsonService jsonService,
        @Value("${invoices.database.directory}") String databaseDirectory,
        @Value("${invoices.database.invoices.file}") String invoicesFile
    ) throws IOException {
        log.debug("File db selected");
        Path filePath = Files.createTempFile(databaseDirectory, invoicesFile);
        return new FileBasedDatabase(filePath, idService, filesService, jsonService);
    }

    @Bean
    @ConditionalOnProperty(name = "invoices.database", havingValue = "memory")
    public Database inMemoryDatabase() {
        log.debug("In mem db selected");
        return new InMemoryDatabase();
    }

    @Bean
    @ConditionalOnProperty(name = "invoices.database", havingValue = "file")
    public IdService idService(
        FilesService filesService,
        @Value("${invoices.database.directory}") String databaseDirectory,
        @Value("${invoices.database.id.file}") String idFile
    ) throws IOException {
        Path filePath = Files.createTempFile(databaseDirectory, idFile);
        return new IdService(filePath, filesService);
    }
}
