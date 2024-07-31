package com.example.iplan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class IPlanApplication {

	public static void main(String[] args) {
		SpringApplication.run(IPlanApplication.class, args);
	}

}
