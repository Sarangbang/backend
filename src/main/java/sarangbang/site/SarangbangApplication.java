package sarangbang.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SarangbangApplication {

	public static void main(String[] args) {
		SpringApplication.run(SarangbangApplication.class, args);
	}

}
