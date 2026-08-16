package com.sistema_de_vacunacion.Delta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeltaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeltaApplication.class, args);
	}

}
