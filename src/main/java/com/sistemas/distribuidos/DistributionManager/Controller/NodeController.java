package com.sistemas.distribuidos.DistributionManager.Controller;

import com.sistemas.distribuidos.DistributionManager.Entity.NodeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lucas Almeida
 * Controller para receber sinal e enviar dados para os Nós
 */
@Controller
public class NodeController {

  /*Emissão de logs*/
  private static final Logger logger = LoggerFactory.getLogger(NodeController.class);

  private List<NodeItem> nodeList = new ArrayList<>();

  @MessageMapping("/addNode")
  public void addNode(@Payload NodeItem item, SimpMessageHeaderAccessor headerAccessor) throws Exception {
    System.out.println("addNode - " + item.toString());
    headerAccessor.getSessionAttributes().put("nodeId", item.getNodeId());

    boolean existsNodeItem = false;

    for (NodeItem nodeItem :
        nodeList) {
      if (nodeItem.getNodeId().isEmpty()) {
        nodeItem.setNodeId(item.getNodeId());
        existsNodeItem = true;
        break;
      }
    }
    if (!existsNodeItem) nodeList.add(item);
  }

  @MessageMapping("/node")
  @SendTo("/ws-client/node")
  public NodeItem sendToNode(String node) throws Exception {
    NodeItem nodeItem = new NodeItem();

    //TODO ler arquivo com numeros e separar por nos ativos
    for (NodeItem item:
         nodeList) {
      if (item.getList().size() == 0) {
        nodeItem.setNodeId(item.getNodeId());
        nodeItem.setList(Arrays.asList(1, 3, 4, 2, 6, 5, 3, 9));
        break;
      }
    }

    System.out.println("Node - " + nodeItem.toString());
    return nodeItem;
  }

  @MessageMapping("/ordered-list")
  public void orderedList(NodeItem item) {
    logger.info("NodeController - " + item.toString());
    for (NodeItem nodeItem :
        nodeList) {
      if (nodeItem.getNodeId().equals(item.getNodeId())) {
        nodeItem.setList(item.getList());
        break;
      }
    }
  }

  /*Evento para detectar novo cliente ListaNumerosconectado*/
  @EventListener
  public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    logger.info("Nova conexão!");
    //TODO enviar nó conectado para front
  }

  /*Evento para detectar cliente desconectado*/
  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    logger.info("Usuário desconectado!");
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

    String nodeId = (String) headerAccessor.getSessionAttributes().get("nodeId");
    logger.info("nodeId: " + nodeId + " removed.");
    for (NodeItem nodeItem :
        nodeList) {
      if (nodeId != null && nodeId.equals(nodeItem.getNodeId())) {
        nodeItem.setNodeId("");
      }
    }
    //TODO enviar nó desconectado para front
  }
}