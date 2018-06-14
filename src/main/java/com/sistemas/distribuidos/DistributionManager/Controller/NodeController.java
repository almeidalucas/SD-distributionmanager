package com.sistemas.distribuidos.DistributionManager.Controller;

import com.sistemas.distribuidos.DistributionManager.Entity.NodeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
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
  @Autowired
  private SimpMessageSendingOperations messagingTemplate;

  /*Emissão de logs*/
  private static final Logger logger = LoggerFactory.getLogger(NodeController.class);

  private List<NodeItem> nodeList = new ArrayList<>();

  private List numberList = Arrays.asList(1, 2, 4, 9, 8, 23);

  @MessageMapping("/addNode")
  @SendTo("/ws-client/add-node")
  public NodeItem addNode(@Payload NodeItem item, SimpMessageHeaderAccessor headerAccessor) throws Exception {
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

    return item;
  }

  @MessageMapping("/node")
  public void sendToNode(int qtNode) {

    if (nodeList.size() > 0) {
      for (NodeItem nodeItem :
          nodeList) {
        if (nodeItem.getNodeId().isEmpty()) nodeList.remove(nodeItem);
        else nodeItem.setList(new ArrayList<>());
      }

      int nodeDelimiter = 1000 / nodeList.size();

      for (Integer number :
          (List<Integer>) numberList) {
        int x = number / nodeDelimiter;
        nodeList.get(x).getList().add(number);
      }

      for (NodeItem nodeItem :
          nodeList) {
        messagingTemplate.convertAndSend("/ws-client/node", nodeItem);
      }
    }
  }

  @MessageMapping("/request-nodes")
  @SendTo("/ws-client/preview-manager")
  public List<NodeItem> sendToPrevManager(String node) {
    return nodeList;
  }

  @MessageMapping("/ordered-list")
  @SendTo("/ws-client/ordered-node")
  public NodeItem orderedList(NodeItem item) {
    logger.info("NodeController - " + item.toString());
    for (NodeItem nodeItem :
        nodeList) {
      if (nodeItem.getNodeId().equals(item.getNodeId())) {
        nodeItem.setList(item.getList());
        nodeItem.setOrdered(true);
        return nodeItem;
      }
    }
    return null;
  }

  /*Evento para detectar novo cliente conectado*/
  @EventListener
  public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    logger.info("Nova conexão!");
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
        messagingTemplate.convertAndSend("/ws-client/remove-node", nodeId);
        break;
      }
    }
  }
}