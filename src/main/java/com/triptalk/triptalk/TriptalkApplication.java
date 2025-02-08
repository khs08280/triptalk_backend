package com.triptalk.triptalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TriptalkApplication {

	public static void main(String[] args) {
		SpringApplication.run(TriptalkApplication.class, args);
	}

}
