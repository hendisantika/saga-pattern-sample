package id.my.hendisantika.sagapatternsample.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

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
@Component
@RequiredArgsConstructor
public class InventoryDataInitializer implements ApplicationRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (productRepository.count() > 0) {
            return;
        }
        productRepository.saveAll(List.of(
                new Product("LAPTOP-001", "Laptop", 10),
                new Product("PHONE-001", "Smartphone", 20),
                new Product("MOUSE-001", "Wireless Mouse", 50),
                new Product("CONSOLE-001", "Game Console", 0)
        ));
    }
}
