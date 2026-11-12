package com.kingjamesestate.realestate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RealestateApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealestateApplication.class, args);
	}

}
