package com.sistemas.distribuidos.DistributionManager.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

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

  /*Emissão de logs*/
  private static final Logger logger = LoggerFactory.getLogger(NodeController.class);

  /*Evento para detectar novo cliente conectado*/
  @EventListener
  public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    logger.info("Nova conexão!");
  }

  /*Evento para detectar cliente desconectado*/
  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    System.out.println("Usuário desconectado!");
  }
}