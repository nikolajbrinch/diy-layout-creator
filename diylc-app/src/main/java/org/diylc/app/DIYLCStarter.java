package org.diylc.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Main class that runs DIYLC.
 * 
 * @author Branislav Stojkovic
 */
@SpringBootApplication
public class DIYLCStarter implements CommandLineRunner {

    @Autowired
    Application application;

    @Override
    public void run(String... args) throws Exception {
        application.run(args);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplicationBuilder(DIYLCStarter.class)
                .headless(false)
                .web(false)
                .build();

        springApplication.run(args);
    }
}