package id.my.hendisantika.sagapatternsample.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by IntelliJ IDEA.
 * Project : saga-pattern-sample
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Link : s.id/hendisantika
 * Date: 25/10/26
 * Time: 13.21
 */
@Entity
@Table(name = "products", schema = "inventory_service")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    private String code;

    @Column(nullable = false)
    private String name;

    private int availableStock;

    // optimistic locking prevents two concurrent sagas from overselling the same stock
    @Version
    private Long version;

    public Product(String code, String name, int availableStock) {
        this.code = code;
        this.name = name;
        this.availableStock = availableStock;
    }
}
