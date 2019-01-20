package com.rad.leadiq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.rad")
public class LeadiqExamApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeadiqExamApplication.class, args);

	}
}
