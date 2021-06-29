package pl.futurecollars.invoicing.db.sql;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;

public class CompanySqlDatabase extends AbstractSqlDatabase implements Database<Company> {

    private static final String SELECT_QUERY = "select * from company ";

    public CompanySqlDatabase(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    @Transactional
    public long save(Company company) {

        return insertCompany(company);
    }

    @Override
    public Optional<Company> getById(long id) {
        List<Company> company = jdbcTemplate.query(SELECT_QUERY + "where id = " + id, companyRowMapper());

        return company.isEmpty() ? Optional.empty() : Optional.ofNullable(company.get(0));
    }

    @Override
    public List<Company> getAll() {
        return jdbcTemplate.query(SELECT_QUERY, companyRowMapper());
    }

    @Override
    @Transactional
    public Optional<Company> update(long id, Company updatedCompany) {

        Optional<Company> company = getById(id);
        if (company.isEmpty()) {
            throw new IllegalArgumentException("Id " + id + " does not exist");
        }
        updateCompany(updatedCompany, company.get());

        return company;
    }

    @Override
    @Transactional
    public Optional<Company> delete(long id) {
        Optional<Company> companyToDelete = getById(id);

        if (companyToDelete.isEmpty()) {
            return companyToDelete;
        }

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "delete from company where id = ? ");
            ps.setLong(1, id);
            return ps;
        });
        return companyToDelete;
    }

    private RowMapper<Company> companyRowMapper() {
        return (rs, rowNr) ->
            Company.builder()
                .id(rs.getInt("id"))
                .taxIdentificationNumber(rs.getString("tax_identification_number"))
                .name(rs.getString("name"))
                .address(rs.getString("address"))
                .pensionInsurance(rs.getBigDecimal("pension_insurance"))
                .healthInsurance(rs.getBigDecimal("health_insurance"))
                .build();
    }
}
