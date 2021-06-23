package pl.futurecollars.invoicing.db;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.file.FileBasedDatabase;
import pl.futurecollars.invoicing.db.file.IdService;
import pl.futurecollars.invoicing.db.jpa.InvoiceRepository;
import pl.futurecollars.invoicing.db.jpa.JpaDatabase;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.db.mongo.MongoBasedDatabase;
import pl.futurecollars.invoicing.db.mongo.MongoIdProvider;
import pl.futurecollars.invoicing.db.sql.SqlDatabase;
import pl.futurecollars.invoicing.model.Invoice;
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
    @ConditionalOnProperty(name = "invoices.database", havingValue = "sql")
    public Database sqlDatabase(JdbcTemplate jdbcTemplate) {
        log.debug("Sql db selected");
        return new SqlDatabase(jdbcTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "invoices.database", havingValue = "jpa")
    public Database jpaDatabase(InvoiceRepository invoiceRepository) {
        log.debug("JPA db selected");
        return new JpaDatabase(invoiceRepository);
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

    @Bean
    @ConditionalOnProperty(name = "invoices.database", havingValue = "mongo")
    public MongoDatabase mongoDb(@Value("${invoices.database.name}") String databaseName
    ) {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClientSettings settings = MongoClientSettings.builder()
            .codecRegistry(pojoCodecRegistry)
            .build();
        MongoClient client = MongoClients.create(settings);
        return client.getDatabase(databaseName);
    }

    @Bean
    @ConditionalOnProperty(name = "invoices.database", havingValue = "mongo")
    public MongoIdProvider mongoIdProvider(
        @Value("${invoices.database.counter.collection}") String collectionName, MongoDatabase mongoDb
    ) {
        MongoCollection<Document> collection = mongoDb.getCollection(collectionName);
        return new MongoIdProvider(collection);
    }

    @Bean
    @ConditionalOnProperty(name = "invoices.database", havingValue = "mongo")
    public Database mongoDatabase(
        @Value("${invoices.database.collection}") String collectionName,
        MongoDatabase mongoDb,
        MongoIdProvider mongoIdProvider
    ) {
        MongoCollection<Invoice> collection = mongoDb.getCollection(collectionName, Invoice.class);
        return new MongoBasedDatabase(collection, mongoIdProvider);
    }
}
