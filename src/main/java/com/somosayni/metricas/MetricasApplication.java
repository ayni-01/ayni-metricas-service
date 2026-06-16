package com.somosayni.metricas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.somosayni.metricas", "com.somosayni.shared"})
public class MetricasApplication {
    public static void main(String[] args) {
        SpringApplication.run(MetricasApplication.class, args);
    }
}
