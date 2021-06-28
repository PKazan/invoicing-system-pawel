package pl.futurecollars.invoicing.db.sql;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;

@RequiredArgsConstructor
public class CompanySqlDatabase implements Database<Company> {

    private static final String SELECT_QUERY = "select * from company ";
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public long save(Company company) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "insert into company (name, address, tax_identification_number, health_insurance, pension_insurance) values (?, ?, ?, ?, ?);",
                new String[] {"id"});
            ps.setString(1, company.getName());
            ps.setString(2, company.getAddress());
            ps.setString(3, company.getTaxIdentificationNumber());
            ps.setBigDecimal(4, company.getHealthInsurance());
            ps.setBigDecimal(5, company.getPensionInsurance());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
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

    @Override
    @Transactional
    public Optional<Company> update(long id, Company updatedCompany) {

        Optional<Company> company = getById(id);
        if (company.isEmpty()) {
            throw new IllegalArgumentException("Id " + id + " does not exist");
        }

        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(
                "update company set name = ?, address = ?, tax_identification_number = ?, health_insurance = ?, pension_insurance = ? "
                    + "where id = ? ");
            ps.setString(1, updatedCompany.getName());
            ps.setString(2, updatedCompany.getAddress());
            ps.setString(3, updatedCompany.getTaxIdentificationNumber());
            ps.setBigDecimal(4, updatedCompany.getHealthInsurance());
            ps.setBigDecimal(5, updatedCompany.getPensionInsurance());
            ps.setLong(6, company.get().getId());
            return ps;
        });
        return company;
    }

    @Override
    @Transactional
    public Optional<Company> delete(long id) {
        Optional<Company> companyToDelete = getById(id);

        if (companyToDelete.isEmpty()) {
            return companyToDelete;
        }

        //        deleteEntriesAndCarsRelatedToInvoice(id);
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "delete from company where id = ? ");
            ps.setLong(1, id);
            return ps;
        });
        return companyToDelete;
    }
}
