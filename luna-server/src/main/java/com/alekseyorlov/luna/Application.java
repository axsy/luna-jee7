package com.alekseyorlov.luna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@PropertySource("classpath:/orm-configuration.properties")
@EntityScan(
        basePackageClasses = { Application.class, Jsr310JpaConverters.class }
)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}