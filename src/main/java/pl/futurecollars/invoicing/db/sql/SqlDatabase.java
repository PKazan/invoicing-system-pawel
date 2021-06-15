package pl.futurecollars.invoicing.db.sql;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Car;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

@RequiredArgsConstructor
public class SqlDatabase implements Database {

    private static final String SELECT_QUERY = "select i.id, date, number, "
        + "c.id as seller_id, c.name as seller_name, c.tax_identification_number as seller_tax_id, c.address as seller_address, "
        + "c.health_insurance as seller_health_insurance, c.pension_insurance as seller_pension_insurance, "
        + "cb.id as buyer_id, cb.name as buyer_name, cb.tax_identification_number as buyer_tax_id, cb.address as buyer_address, "
        + "cb.health_insurance as buyer_health_insurance, cb.pension_insurance as buyer_pension_insurance "
        + "from invoice i "
        + "inner join company c on i.seller = c.id "
        + "inner join company cb on i.buyer = cb.id ";

    private final JdbcTemplate jdbcTemplate;
    private final Map<Vat, Integer> vatToId = new HashMap<>();
    private final Map<Integer, Vat> idToVat = new HashMap<>();

    @Override
    @Transactional
    public int save(Invoice invoice) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        long buyerId = insertCompany(invoice.getBuyer());
        long sellerId = insertCompany(invoice.getSeller());
        int invoiceId = insertInvoice(buyerId, sellerId, invoice);
        addEntriesRelatedToInvoice(invoiceId, invoice);

        return invoiceId;
    }

    private int insertInvoice(long buyerId, long sellerId, Invoice invoice) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                connection.prepareStatement("insert into invoice (date, number, buyer, seller) values (?, ?, ?, ?);", new String[] {"id"});
            ps.setDate(1, Date.valueOf(invoice.getDate()));
            ps.setString(2, invoice.getNumber());
            ps.setLong(3, buyerId);
            ps.setLong(4, sellerId);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    private long insertCompany(Company company) {
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

    @PostConstruct
    void initVatRatesMap() {
        jdbcTemplate.query("select * from vat",
            rs -> {
                vatToId.put(Vat.valueOf("VAT_" + rs.getString("name")), rs.getInt("id"));
                idToVat.put(rs.getInt("id"), Vat.valueOf("VAT_" + rs.getString("name")));
            });
    }

    private Integer insertCarAndGetItId(Car car) {
        if (car == null) {
            return null;
        }

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                connection.prepareStatement("insert into car (registration, including_private_expense) values (?, ?);", new String[] {"id"});
            ps.setString(1, car.getRegistration());
            ps.setBoolean(2, car.isIncludingPrivateExpense());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    @Override
    public Optional<Invoice> getById(int id) {
        List<Invoice> invoice = jdbcTemplate.query(SELECT_QUERY + "where i.id = " + id, invoiceRowMapper());

        return invoice.isEmpty() ? Optional.empty() : Optional.ofNullable(invoice.get(0));
    }

    @Override
    public List<Invoice> getAll() {
        return jdbcTemplate.query(SELECT_QUERY, invoiceRowMapper());
    }

    private RowMapper<Invoice> invoiceRowMapper() {
        return (rs, rowNr) -> {
            int invoiceId = rs.getInt("id");

            List<InvoiceEntry> invoiceEntries = jdbcTemplate.query(
                "select * from invoice_invoice_entry iie "
                    + "inner join invoice_entry e on iie.invoice_entry_id = e.id "
                    + "left outer join car c on e.car_in_private_use = c.id "
                    + "where invoice_id = " + invoiceId,
                (response, ignored) -> InvoiceEntry.builder()
                    .description(response.getString("description"))
                    .quantity(response.getInt("quantity"))
                    .price(response.getBigDecimal("price"))
                    .vatValue(response.getBigDecimal("vat_value"))
                    .vatRate(idToVat.get(response.getInt("vat_rate")))
                    .carInPrivateUse(response.getObject("registration") != null
                        ? Car.builder()
                        .registration(response.getString("registration"))
                        .includingPrivateExpense(response.getBoolean("including_private_expense"))
                        .build()
                        : null)
                    .build());

            return Invoice.builder()
                .id(rs.getInt("id"))
                .date(rs.getDate("date").toLocalDate())
                .number(rs.getString("number"))
                .buyer(Company.builder()
                    .id(rs.getInt("buyer_id"))
                    .taxIdentificationNumber(rs.getString("buyer_tax_id"))
                    .name(rs.getString("buyer_name"))
                    .address(rs.getString("buyer_address"))
                    .pensionInsurance(rs.getBigDecimal("buyer_pension_insurance"))
                    .healthInsurance(rs.getBigDecimal("buyer_health_insurance"))
                    .build()
                )

                .seller(Company.builder()
                    .id(rs.getInt("seller_id"))
                    .taxIdentificationNumber(rs.getString("seller_tax_id"))
                    .name(rs.getString("seller_name"))
                    .address(rs.getString("seller_address"))
                    .pensionInsurance(rs.getBigDecimal("seller_pension_insurance"))
                    .healthInsurance(rs.getBigDecimal("seller_health_insurance"))
                    .build())
                .entries(invoiceEntries)
                .build();
        };
    }

    @Override
    @Transactional
    public Optional<Invoice> update(int id, Invoice updatedInvoice) {

        Optional<Invoice> invoice = getById(id);
        if (invoice.isEmpty()) {
            throw new IllegalArgumentException("Id " + id + " does not exist");
        }

        updateCompany(updatedInvoice.getBuyer(), invoice.get().getBuyer());
        updateCompany(updatedInvoice.getSeller(), invoice.get().getSeller());

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "update invoice set date = ?, number = ? "
                    + "where invoice.id = " + id);
            ps.setDate(1, Date.valueOf(updatedInvoice.getDate()));
            ps.setString(2, updatedInvoice.getNumber());
            return ps;
        });

        deleteEntriesAndCarsRelatedToInvoice(id);
        addEntriesRelatedToInvoice(id, updatedInvoice);

        return invoice;

    }

    private void updateCompany(Company updatedCompany, Company company) {
        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(
                "update company set name = ?, address = ?, tax_identification_number = ?, health_insurance = ?, pension_insurance = ? "
                    + "where id = ? ");
            ps.setString(1, updatedCompany.getName());
            ps.setString(2, updatedCompany.getAddress());
            ps.setString(3, updatedCompany.getTaxIdentificationNumber());
            ps.setBigDecimal(4, updatedCompany.getHealthInsurance());
            ps.setBigDecimal(5, updatedCompany.getPensionInsurance());
            ps.setInt(6, company.getId());
            return ps;
        });
    }

    @Override
    @Transactional
    public Optional<Invoice> delete(int id) {
        Optional<Invoice> invoiceToDelete = getById(id);
        if (invoiceToDelete.isEmpty()) {
            return invoiceToDelete;
        }

        deleteEntriesAndCarsRelatedToInvoice(id);
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "delete from company where id = ? ");
            ps.setInt(1, id);
            return ps;
        });
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "delete from invoice "
                    + "where id = ? ");
            ps.setInt(1, id);
            return ps;
        });
        return invoiceToDelete;
    }

    private void deleteEntriesAndCarsRelatedToInvoice(int id) {

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "delete from car where id in"
                    +
                    "(select car_in_private_use from invoice_entry where id in"
                    + "(select invoice_entry_id from invoice_invoice_entry where invoice_id = ?))");
            ps.setInt(1, id);
            return ps;
        });

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "delete from invoice_entry where id in"
                    + "(select invoice_entry_id from invoice_invoice_entry where invoice_id  = ?)");
            ps.setInt(1, id);
            return ps;
        });

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "delete from invoice_invoice_entry "
                    + "where invoice_id = ? ");
            ps.setInt(1, id);
            return ps;
        });

    }

    private void addEntriesRelatedToInvoice(int invoiceId, Invoice invoice) {

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        invoice.getEntries().forEach(entry -> {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                    .prepareStatement(
                        "insert into invoice_entry (description, quantity, price, vat_value, vat_rate, car_in_private_use) "
                            + "values (?, ?, ?, ?, ?, ?);",
                        new String[] {"id"});
                ps.setString(1, entry.getDescription());
                ps.setInt(2, entry.getQuantity());
                ps.setBigDecimal(3, entry.getPrice());
                ps.setBigDecimal(4, entry.getVatValue());
                ps.setInt(5, vatToId.get(entry.getVatRate()));
                ps.setObject(6, insertCarAndGetItId(entry.getCarInPrivateUse()));
                return ps;
            }, keyHolder);

            int invoiceEntryId = keyHolder.getKey().intValue();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "insert into invoice_invoice_entry(invoice_id, invoice_entry_id) values (?, ?);");
                ps.setInt(1, invoiceId);
                ps.setInt(2, invoiceEntryId);
                return ps;
            });
        });
    }
}
