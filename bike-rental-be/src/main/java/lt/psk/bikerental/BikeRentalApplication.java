package lt.psk.bikerental;

import lt.psk.bikerental.config.BillingProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableConfigurationProperties(BillingProperties.class)
@ImportResource("${lt.psk.bikerental.bean-file-path}")
public class BikeRentalApplication {
	@Value("${lt.psk.bikerental.bean-file-path}")
	private String beanFilePath; // Used to silence "Unresolved configuration property" warning in application.properties.

	public static void main(String[] args) {
		SpringApplication.run(BikeRentalApplication.class, args);
	}

}
