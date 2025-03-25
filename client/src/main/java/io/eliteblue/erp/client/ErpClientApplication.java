package io.eliteblue.erp.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan( basePackages = "io.eliteblue.erp")
@EnableJpaRepositories( basePackages = "io.eliteblue.erp" )
@EntityScan( basePackages = "io.eliteblue.erp" )
public class ErpClientApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ErpClientApplication.class, args);
    }
}