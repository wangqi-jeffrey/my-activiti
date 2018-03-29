package com.neo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.neo.mapper")
//屏蔽验证
@EnableAutoConfiguration(exclude = {
     org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
     org.activiti.spring.boot.SecurityAutoConfiguration.class,
     org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration.class
})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

