package pl.futurecollars.invoicing.db.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.WithId;

public class JpaDatabase<T extends WithId> implements Database<T> {

    private final CrudRepository<T, Long> crudRepository;

    public JpaDatabase(CrudRepository<T, Long> crudRepository) {
        this.crudRepository = crudRepository;
    }

    @Override
    public long save(T item) {
        return crudRepository.save(item).getId();
    }

    @Override
    public Optional<T> getById(long id) {
        return crudRepository.findById(id);
    }

    @Override
    public List<T> getAll() {
        return Streamable.of(crudRepository.findAll()).toList();
    }

    @Override
    public Optional<T> update(long id, T updatedItem) {
        Optional<T> itemOptional = getById(id);

        if (itemOptional.isEmpty()) {
            return Optional.empty();
        }

        crudRepository.save(updatedItem);

        return itemOptional;
    }

    @Override
    public Optional<T> delete(long id) {
        Optional<T> item = getById(id);
        item.ifPresent(crudRepository::delete);
        return item;
    }
}
