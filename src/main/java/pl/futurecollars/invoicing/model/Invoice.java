package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.futurecollars.invoicing.db.WithId;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
public class Invoice implements WithId {

    @ApiModelProperty(value = "Invoice id (generated by application", required = true, example = "1")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @ApiModelProperty(value = "Date invoice was created ", required = true, example = "2021-05-14")
    private LocalDate date;

    @ApiModelProperty(value = "Invoice number (assigned by user)", required = true, example = "2020/03/08/00001")
    private String number;

    @JoinColumn(name = "buyer")
    @ApiModelProperty(value = "Company who bought product/service", required = true)
    @OneToOne(cascade = CascadeType.ALL)
    private Company buyer;

    @JoinColumn(name = "seller")
    @ApiModelProperty(value = "Company who sold product/service", required = true)
    @OneToOne(cascade = CascadeType.ALL)
    private Company seller;

    @JoinTable(name = "invoice_invoice_entry", inverseJoinColumns = @JoinColumn(name = "invoice_entry_id"))
    @ApiModelProperty(value = "List of products/services", required = true)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<InvoiceEntry> entries;

}
