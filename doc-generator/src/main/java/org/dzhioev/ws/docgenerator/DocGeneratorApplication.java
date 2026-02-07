package org.dzhioev.ws.docgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DocGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocGeneratorApplication.class, args);
    }

}
