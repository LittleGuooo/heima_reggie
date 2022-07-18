package com.xu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ServletComponentScan
@Slf4j
@SpringBootApplication
@EnableTransactionManagement
public class HeimaReggieApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeimaReggieApplication.class, args);
    }

}
