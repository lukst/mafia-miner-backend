package com.services.mafia.miner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MafiaMinerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MafiaMinerApplication.class, args);
	}

}
