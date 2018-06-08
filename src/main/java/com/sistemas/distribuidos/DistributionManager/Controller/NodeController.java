package com.sistemas.distribuidos.DistributionManager.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * @author Lucas Almeida
 * Controller para receber sinal e enviar dados para os Nós
 * */
@Controller
public class NodeController {

  @MessageMapping("/node")
  @SendTo("/ws-client/node")
  public String sendToNode(String message) throws Exception {
    System.out.println("NodeController - " + message);
    return message;
  }
}