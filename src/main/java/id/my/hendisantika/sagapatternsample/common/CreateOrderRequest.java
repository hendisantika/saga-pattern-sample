package id.my.hendisantika.sagapatternsample.common;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

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
public record CreateOrderRequest(
        @NotBlank String customerId,
        @NotBlank String productCode,
        @Min(1) int quantity,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotBlank String shippingCity
) {
}
