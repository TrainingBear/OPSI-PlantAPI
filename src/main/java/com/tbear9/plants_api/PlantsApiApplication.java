package com.tbear9.plants_api;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class PlantsApiApplication implements CommandLineRunner {
	@Getter @Autowired static JdbcTemplate template;
    Logger logger = LoggerFactory.getLogger(this.getClass());

	public static void main(String[] args) {
		SpringApplication.run(PlantsApiApplication.class, args);
	}

    @Override
	public void run(String... args) throws Exception {
		createTable(template);
	}

	private void createTable(JdbcTemplate template){
        logger.info("Creating tables... ");
        String plantlist = "CREATE TABLE IF NOT EXISTS plantlist(" +
                "index int NOT NULL," +
                " data JSON," +
				" CONSTRAINT UNIK UNIQUE (index)" +
                ")";
        String plantdisseaselist = "CREATE TABLE IF NOT EXISTS plantdiseaselist(" +
                "index int NOT NULL UNIQUE," +
                " data JSON," +
				" CONSTRAINT UNIK UNIQUE (index)" +
				")";
        String plantguidelist = "CREATE TABLE IF NOT EXISTS plantguidelist(" +
                "index int NOT NULL UNIQUE," +
                " data JSON," +
				" CONSTRAINT UNIK UNIQUE (index)" +
				")";
        String plantdetails = "CREATE TABLE IF NOT EXISTS plantdetails(" +
                "index int NOT NULL UNIQUE," +
                " data JSON," +
				" CONSTRAINT UNIK UNIQUE (index)" +
				")";
        String planthardness = "CREATE TABLE IF NOT EXISTS planthardness(" +
                "index int NOT NULL UNIQUE," +
                " data JSON," +
				" CONSTRAINT UNIK UNIQUE (index)" +
				")";
		String plantguide = "CREATE TABLE IF NOT EXISTS plantguide(" +
                "index int NOT NULL UNIQUE," +
                " data JSON," +
				" CONSTRAINT UNIK UNIQUE (index)" +
				")";

		template.execute(plantlist);
		template.execute(plantdisseaselist);
		template.execute(plantguidelist);
		template.execute(plantdetails);
		template.execute(planthardness);
		template.execute(plantguide);
	}
}