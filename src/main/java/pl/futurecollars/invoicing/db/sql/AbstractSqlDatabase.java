package pl.futurecollars.invoicing.db.sql;

import java.sql.PreparedStatement;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;
import pl.futurecollars.invoicing.model.Company;

@AllArgsConstructor
public class AbstractSqlDatabase {

    final JdbcTemplate jdbcTemplate;

    @Transactional
    long insertCompany(Company company) {
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

    void updateCompany(Company updatedCompany, Company company) {
        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(
                "update company set name = ?, address = ?, tax_identification_number = ?, health_insurance = ?, pension_insurance = ? "
                    + "where id = ? ");
            ps.setString(1, updatedCompany.getName());
            ps.setString(2, updatedCompany.getAddress());
            ps.setString(3, updatedCompany.getTaxIdentificationNumber());
            ps.setBigDecimal(4, updatedCompany.getHealthInsurance());
            ps.setBigDecimal(5, updatedCompany.getPensionInsurance());
            ps.setLong(6, company.getId());
            return ps;
        });

    }
}
