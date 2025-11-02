package org.tech.finalprojectadb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "org.tech.finalprojectadb.repository")
public class FinalProjectAdbApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinalProjectAdbApplication.class, args);
	}

}
