package com.floriano.legato_api;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class LegatoApi {

	public static void main(String[] args) {
		SpringApplication.run(LegatoApi.class, args);
	}

	@PostConstruct
    public void init() {
        // Força a API inteira a usar o horário de Brasília
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }

}
