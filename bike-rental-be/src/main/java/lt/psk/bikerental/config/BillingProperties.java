package lt.psk.bikerental.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "billing")
public class BillingProperties {
    private double bookingFee;
    private double unlockFee;
    private double pricePerMinute;
}