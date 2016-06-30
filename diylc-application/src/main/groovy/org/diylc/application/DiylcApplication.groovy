package org.diylc.application

import groovy.transform.CompileStatic

import java.awt.EventQueue

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@CompileStatic
@SpringBootApplication
class DiylcApplication implements CommandLineRunner {

    @Autowired
    WindowManager windowManager

    @Override
    public void run(String... args) throws Exception {
        DiylcFrame frame = windowManager.newWindow()
        
        EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        frame.setVisible(true)
                    }
                })
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplicationBuilder(DiylcApplication.class)
                .headless(false)
                .web(false)
                .build()

        application.run(args)
    }
}