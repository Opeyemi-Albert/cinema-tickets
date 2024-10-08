package com.gov.cinematickets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication

public class CinemaTicketsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinemaTicketsApplication.class, args);
		log.info(
				"\n\n ============================ APPLICATION LAUNCHED ======================= \n\n");
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		System.out.println(LocalDateTime.now());
	}

}
