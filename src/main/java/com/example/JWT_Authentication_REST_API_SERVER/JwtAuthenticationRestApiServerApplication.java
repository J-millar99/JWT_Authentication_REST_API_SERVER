package com.example.JWT_Authentication_REST_API_SERVER;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class JwtAuthenticationRestApiServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtAuthenticationRestApiServerApplication.class, args);
	}

}
