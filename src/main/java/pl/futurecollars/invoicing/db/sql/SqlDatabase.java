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

    private final JdbcTemplate jdbcTemplate;
    private final Map<Vat, Integer> vatToId = new HashMap<>();
    private final Map<Integer, Vat> idToVat = new HashMap<>();

    @Override
    @Transactional
    public int save(Invoice invoice) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "insert into company (name, address, tax_identification_number, health_insurance, pension_insurance) values (?, ?, ?, ?, ?);",
                new String[] {"id"});
            ps.setString(1, invoice.getBuyer().getName());
            ps.setString(2, invoice.getBuyer().getAddress());
            ps.setString(3, invoice.getBuyer().getTaxIdentificationNumber());
            ps.setBigDecimal(4, invoice.getBuyer().getHealthInsurance());
            ps.setBigDecimal(5, invoice.getBuyer().getPensionInsurance());
            return ps;
        }, keyHolder);

        long buyerId = keyHolder.getKey().longValue();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "insert into company (name, address, tax_identification_number,  health_insurance, pension_insurance) values (?, ?, ?, ?, ?);",
                new String[] {"id"});
            ps.setString(1, invoice.getSeller().getName());
            ps.setString(2, invoice.getSeller().getAddress());
            ps.setString(3, invoice.getSeller().getTaxIdentificationNumber());
            ps.setBigDecimal(4, invoice.getSeller().getHealthInsurance());
            ps.setBigDecimal(5, invoice.getSeller().getPensionInsurance());
            return ps;
        }, keyHolder);

        long sellerId = keyHolder.getKey().longValue();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                connection.prepareStatement("insert into invoice (date, number, buyer, seller) values (?, ?, ?, ?);", new String[] {"id"});
            ps.setDate(1, Date.valueOf(invoice.getDate()));
            ps.setString(2, invoice.getNumber());
            ps.setLong(3, buyerId);
            ps.setLong(4, sellerId);
            return ps;
        }, keyHolder);

        int invoiceId = keyHolder.getKey().intValue();

        invoice.getEntries().forEach(entry -> {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                    .prepareStatement("insert into invoice_entry (description, quantity, price, vat_value, vat_rate, car_in_private_use) values (?, ?, ?, ?, ?, ?);",
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
        return invoiceId;
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
        return Optional.empty();
    }

    @Override
    public List<Invoice> getAll() {
        return jdbcTemplate.query("select i.id, date, number, "
            + "c.name as seller_name, c.tax_identification_number as seller_tax_id, c.address as seller_address, "
            + "c.pension_insurance as seller_pension_insurance, c.health_insurance as seller_health_insurance, "
            + "cb.name as buyer_name, cb.tax_identification_number as buyer_tax_id, cb.address as buyer_address, "
            + "cb.pension_insurance as buyer_pension_insurance, cb.health_insurance as buyer_health_insurance "
            + "from invoice i "
            + "inner join company c on i.seller = c.id "
            + "inner join company cb on i.buyer = cb.id", (rs, rowNr) -> {
            int invoiceId = rs.getInt("id");

            List<InvoiceEntry> invoiceEntries = jdbcTemplate.query(
                "select * from invoice_invoice_entry iie "
                    + "inner join invoice_entry e on iie.invoice_entry_id = e.id "
                    + "left outer join car c on e.car_in_private_use = c.id "
                    + "where invoice_id = " + invoiceId,
                (response, ignored) -> InvoiceEntry.builder()
                    .id(response.getInt("id"))
                    .description(response.getString("description"))
                    .quantity(response.getInt("quantity"))
                    .price(response.getBigDecimal("price"))
                    .vatValue(response.getBigDecimal("price"))
                    .vatRate(idToVat.get(response.getInt("vat_rate")))
                    .carInPrivateUse(response.getObject("registration") != null
                    ? Car.builder()
                    .registration(response.getString("registration"))
                    .includingPrivateExpense(response.getBoolean("including_private_expense"))
                    .build()
                    :null)
                    .build());

            return Invoice.builder()
                .id(rs.getInt("id"))
                .date(rs.getDate("date").toLocalDate())
                .number(rs.getString("number"))
                .buyer(Company.builder()
                    .taxIdentificationNumber(rs.getString("buyer_tax_id"))
                    .name(rs.getString("buyer_name"))
                    .address(rs.getString("buyer_address"))
                    .pensionInsurance(rs.getBigDecimal("buyer_pension_insurance"))
                    .healthInsurance(rs.getBigDecimal("buyer_health_insurance"))
                    .build()
                )

                .seller(Company.builder()
                    .taxIdentificationNumber(rs.getString("seller_tax_id"))
                    .name(rs.getString("seller_name"))
                    .address(rs.getString("seller_address"))
                    .pensionInsurance(rs.getBigDecimal("seller_pension_insurance"))
                    .healthInsurance(rs.getBigDecimal("seller_health_insurance"))
                    .build())
                .entries(invoiceEntries)
                .build();
        });
    }

    @Override
    public Optional<Invoice> update(int id, Invoice updatedInvoice) {
        return Optional.empty();
    }

    @Override
    public Optional<Invoice> delete(int id) {
        return Optional.empty();
    }
}
