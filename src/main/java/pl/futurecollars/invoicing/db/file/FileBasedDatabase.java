package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.util.FilesService;
import pl.futurecollars.invoicing.util.JsonService;

@AllArgsConstructor
public class FileBasedDatabase implements Database {

    private final Path databasePath;
    private final IdService idService;
    private final FilesService filesService;
    private final JsonService jsonService;

    @Override
    public int save(Invoice invoice) {
        try {
            invoice.setId(idService.getNextIdAndIncrement());
            filesService.appendLineToFile(databasePath, jsonService.toJson(invoice));

            return invoice.getId();
        } catch (IOException ex) {
            throw new RuntimeException("Database failed to save invoice", ex);
        }
    }

    @Override
    public Optional<Invoice> getById(int id) {
        try {
            return filesService.readAllLines(databasePath)
                .stream()
                .filter(line -> containsId(line, id))
                .map(line -> jsonService.toObject(line, Invoice.class))
                .findFirst();
        } catch (IOException ex) {
            throw new RuntimeException("Database failed to get invoice with id: " + id, ex);
        }
    }

    @Override
    public List<Invoice> getAll() {
        try {
            return filesService.readAllLines(databasePath)
                .stream()
                .map(line -> jsonService.toObject(line, Invoice.class))
                .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read invoices from file", ex);
        }
    }

    @Override
    public Optional<Invoice> update(int id, Invoice updatedInvoice) {
        try {
            List<String> allInvoices = filesService.readAllLines(databasePath);
            var listWithoutInvoiceWithGivenId = allInvoices
                .stream()
                .filter(line -> !containsId(line, id))
                .collect(Collectors.toList());

            if (allInvoices.size() == listWithoutInvoiceWithGivenId.size()) {
                throw new IllegalArgumentException("Id " + id + " does not exist");
            }

            updatedInvoice.setId(id);
            listWithoutInvoiceWithGivenId.add(jsonService.toJson(updatedInvoice));
            filesService.writeLinesToFile(databasePath, listWithoutInvoiceWithGivenId);
            allInvoices.removeAll(listWithoutInvoiceWithGivenId);
            return Optional.ofNullable(jsonService.toObject(allInvoices.get(0), Invoice.class));

        } catch (IOException e) {
            throw new RuntimeException("Failed to update invoice with id: " + id, e);

        }
    }

    @Override
    public Optional<Invoice> delete(int id) {
        try {
            var allInvoices = filesService.readAllLines(databasePath);
            var listWithoutDeleted = allInvoices
                .stream()
                .filter(line -> !containsId(line, id))
                .collect(Collectors.toList());

            if (allInvoices.size() == listWithoutDeleted.size()) {
                throw new IllegalArgumentException("Id " + id + " does not exist");
            }
            filesService.writeLinesToFile(databasePath, listWithoutDeleted);
            allInvoices.removeAll(listWithoutDeleted);
            return Optional.ofNullable(jsonService.toObject(allInvoices.get(0), Invoice.class));

        } catch (IOException ex) {
            throw new RuntimeException("Failed to delete invoice with id: " + id, ex);
        }
    }

    private boolean containsId(String line, int id) {
        return line.contains("\"id\":" + id + ",");
    }
}
