package org.vkomlev.jwt.authority.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthorityServerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AuthorityServerApplication.class, args);
    }
}
