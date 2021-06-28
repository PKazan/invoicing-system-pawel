package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.model.Company;

@Service
public class CompanyService {

    InMemoryDatabase<Company> inMemoryDatabase = new InMemoryDatabase<>();
    private final Database<Company> db;

    public CompanyService(Database<Company> database) {
        this.db = database;
    }

    public long save(Company company) {
        return db.save(company);
    }

    public Optional<Company> getById(long id) {
        return db.getById(id);
    }

    public List<Company> getAll() {
        return db.getAll();
    }

    public Optional<Company> update(long id, Company updatedCompany) {
        if (inMemoryDatabase.getInMemoryDatabase().containsKey(id)) {
            return Optional.empty();
        } else {
            updatedCompany.setId(id);
            return db.update(id, updatedCompany);
        }
    }

    public Optional<Company> delete(long id) {
        return db.delete(id);
    }
}
