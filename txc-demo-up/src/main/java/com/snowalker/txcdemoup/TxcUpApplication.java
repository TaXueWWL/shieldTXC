package com.snowalker.txcdemoup;

import com.shield.txc.configuration.EnableShieldEventTxc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableShieldEventTxc
@SpringBootApplication
public class TxcUpApplication {

    public static void main(String[] args) {
        SpringApplication.run(TxcUpApplication.class, args);
    }

}
