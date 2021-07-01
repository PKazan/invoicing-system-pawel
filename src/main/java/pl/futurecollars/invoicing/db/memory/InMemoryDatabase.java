package pl.futurecollars.invoicing.db.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.WithId;

public class InMemoryDatabase<T extends WithId> implements Database<T> {

    private final HashMap<Long, T> inMemoryDatabase = new HashMap<>();
    private long index = 1;

    public HashMap<Long, T> getInMemoryDatabase() {
        return inMemoryDatabase;
    }

    @Override
    public long save(T item) {
        item.setId(index);
        inMemoryDatabase.put(index, item);
        return index++;
    }

    @Override
    public Optional<T> getById(long id) {
        return Optional.ofNullable(inMemoryDatabase.get(id));
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(inMemoryDatabase.values());
    }

    @Override
    public Optional<T> update(long id, T updatedItem) {
        if (getById(id).isEmpty()) {
            return Optional.empty();
        } else {
            updatedItem.setId(id);
            return Optional.ofNullable(inMemoryDatabase.put(id, updatedItem));
        }
    }

    @Override
    public Optional<T> delete(long id) {
        return Optional.ofNullable(inMemoryDatabase.remove(id));
    }
}
