package org.dzhioev.ws.docservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

@SpringBootApplication
public class DocServiceApplication {


    public static void main(String[] args) {
        SpringApplication.run(DocServiceApplication.class, args);
    }

}
