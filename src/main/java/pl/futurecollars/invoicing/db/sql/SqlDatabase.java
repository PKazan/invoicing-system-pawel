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
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

@RequiredArgsConstructor
public class SqlDatabase implements Database {

    private final JdbcTemplate jdbcTemplate;
    private final Map<Vat, Integer> vatToId = new HashMap<>();

    @Override
    public int save(Invoice invoice) {

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                connection.prepareStatement(
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
            PreparedStatement ps =
                connection.prepareStatement(
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
                    .prepareStatement("insert into invoice_entry (description, quantity, price, vat_value, vat_rate) values (?, ?, ?, ?, ?);",
                        new String[] {"id"});
                ps.setString(1, entry.getDescription());
                ps.setInt(2, entry.getQuantity());
                ps.setBigDecimal(3, entry.getPrice());
                ps.setBigDecimal(4, entry.getVatValue());
                ps.setInt(5, vatToId.get(entry.getVatRate()));
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
    private void initVaTRatesMap() {
        jdbcTemplate.query("select * from vat",
            rs -> {
                vatToId.put(Vat.valueOf("VAT_" + rs.getString("name")), rs.getInt("id"));
            });
    }

    @Override
    public Optional<Invoice> getById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Invoice> getAll() {
        return jdbcTemplate.query("select i.id, date, number, c.name as seller_name, cb.name as buyer_name " +
            "from invoice i " +
            "inner join company c on i.seller = c.id " +
            "inner join company cb on i.buyer = cb.id", (rs, rowNr) -> {
            int invoiceId = rs.getInt("id");

            List<InvoiceEntry> invoiceEntries = jdbcTemplate.query(
                "select * from invoice_invoice_entry iie "
                    + "inner join invoice_entry e on iie.invoice_entry_id = e.id"
                    + "where invoice_id = " + invoiceId,
                (response, ignored) -> InvoiceEntry.builder()
                    .id(response.getInt("id"))
                    .description(response.getString("description"))
                    .quantity(response.getInt("quantity"))
                    .price(response.getBigDecimal("price"))
                    .vatValue(response.getBigDecimal("price"))
                    .vatRate(vatToId.get(response.getInt("vat_rate")))
                    .build());

            return Invoice.builder()
                .date(rs.getDate("date").toLocalDate())
                .number(rs.getString("number"))
                .buyer(Company.builder().name(rs.getString("buyer_name")).build())
                .seller(Company.builder().name(rs.getString("seller_name")).build())
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
