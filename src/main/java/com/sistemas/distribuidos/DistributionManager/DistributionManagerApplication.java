package com.sistemas.distribuidos.DistributionManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Lucas Almeida
 * Inicia serviço e mantém online
 */
@SpringBootApplication
public class DistributionManagerApplication {

  public static void main(String[] args) {
    SpringApplication.run(DistributionManagerApplication.class, args);
  }
}