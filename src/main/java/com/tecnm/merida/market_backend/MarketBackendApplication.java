package com.tecnm.merida.market_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class MarketBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarketBackendApplication.class, args);
	}

}
