package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.WithId;
import pl.futurecollars.invoicing.util.FilesService;
import pl.futurecollars.invoicing.util.JsonService;

@AllArgsConstructor
public class FileBasedDatabase<T extends WithId> implements Database<T> {

    private final Path databasePath;
    private final IdService idService;
    private final FilesService filesService;
    private final JsonService jsonService;
    private final Class<T> clazz;

    @Override
    public long save(T item) {
        try {
            item.setId(idService.getNextIdAndIncrement());
            filesService.appendLineToFile(databasePath, jsonService.toJson(item));

            return item.getId();
        } catch (IOException ex) {
            throw new RuntimeException("Database failed to save item", ex);
        }
    }

    @Override
    public Optional<T> getById(long id) {
        try {
            return filesService.readAllLines(databasePath)
                .stream()
                .filter(line -> containsId(line, id))
                .map(line -> jsonService.toObject(line, clazz))
                .findFirst();
        } catch (IOException ex) {
            throw new RuntimeException("Database failed to get item with id: " + id, ex);
        }
    }

    @Override
    public List<T> getAll() {
        try {
            return filesService.readAllLines(databasePath)
                .stream()
                .map(line -> jsonService.toObject(line, clazz))
                .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read item from file", ex);
        }
    }

    @Override
    public Optional<T> update(long id, T updatedItem) {
        try {
            List<String> allItems = filesService.readAllLines(databasePath);
            var listWithoutItemWithGivenId = allItems
                .stream()
                .filter(line -> !containsId(line, id))
                .collect(Collectors.toList());

            if (allItems.size() == listWithoutItemWithGivenId.size()) {
                throw new IllegalArgumentException("Id " + id + " does not exist");
            }

            updatedItem.setId(id);
            listWithoutItemWithGivenId.add(jsonService.toJson(updatedItem));
            filesService.writeLinesToFile(databasePath, listWithoutItemWithGivenId);
            allItems.removeAll(listWithoutItemWithGivenId);
            return Optional.ofNullable(jsonService.toObject(allItems.get(0), clazz));

        } catch (IOException e) {
            throw new RuntimeException("Failed to update item with id: " + id, e);

        }
    }

    @Override
    public Optional<T> delete(long id) {
        try {
            var allItems = filesService.readAllLines(databasePath);
            var listWithoutDeleted = allItems
                .stream()
                .filter(line -> !containsId(line, id))
                .collect(Collectors.toList());

            if (allItems.size() == listWithoutDeleted.size()) {
                return Optional.empty();
            }

            filesService.writeLinesToFile(databasePath, listWithoutDeleted);
            allItems.removeAll(listWithoutDeleted);
            return Optional.ofNullable(jsonService.toObject(allItems.get(0), clazz));

        } catch (IOException ex) {
            throw new RuntimeException("Failed to delete item with id: " + id, ex);
        }
    }

    private boolean containsId(String line, long id) {
        return line.contains("\"id\":" + id + ",");
    }
}
