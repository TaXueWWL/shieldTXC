package com.snowalker.txcdemo;

import com.shield.txc.configuration.EnableShieldEventTxc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableShieldEventTxc
@SpringBootApplication
public class TxcDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TxcDemoApplication.class, args);
    }

}
