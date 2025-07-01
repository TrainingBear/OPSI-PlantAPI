package com.tbear9.plants_api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class PlantsApiApplication implements CommandLineRunner {
	@Autowired DBManager db;
	@Autowired JdbcTemplate template;

    Logger logger = LoggerFactory.getLogger(this.getClass());

	public static void main(String[] args) {
		SpringApplication.run(PlantsApiApplication.class, args);
	}

    @Override
	public void run(String... args) throws Exception {
		API.setTemplate(template);
		db.init();
	}
}