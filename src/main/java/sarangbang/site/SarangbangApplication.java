package sarangbang.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
@EnableMongoAuditing
public class SarangbangApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(SarangbangApplication.class, args);
	}

}
