package com.bookbookclub.bbc_user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class BbcUserServiceApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder(BbcUserServiceApplication.class)
				.properties("spring.config.name=application-user")
				.run(args);
	}
}
