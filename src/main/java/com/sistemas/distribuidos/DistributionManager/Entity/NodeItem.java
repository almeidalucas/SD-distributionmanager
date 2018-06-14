package com.sistemas.distribuidos.DistributionManager.Entity;

import java.util.ArrayList;
import java.util.List;

public class NodeItem {

  private String nodeId = "";
  private boolean isOrdered = false;
  private List<Integer> list = new ArrayList<>();

  public List<Integer> getList() {
    return list;
  }

  public void setList(List<Integer> list) {
    this.list = list;
  }

  public String getNodeId() {
    return nodeId;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  public boolean isOrdered() {
    return isOrdered;
  }

  public void setOrdered(boolean ordered) {
    isOrdered = ordered;
  }
}