package com.fnmusic.user.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication
public class FNMusicUserManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(FNMusicUserManagementApplication.class, args);
	}

}

