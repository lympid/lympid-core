/*
 * Copyright 2015 Fabien Renaud.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lympid.core.behaviorstatemachines.impl;

import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.StateMachineMeta;
import com.lympid.core.common.TreeNode;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Fabien Renaud
 */
public class MutableStateMachineMeta implements StateMachineMeta {

  private int completionEvents;
  private int timeEvents;
  private int activities;
  private int historyNodes;
  private TreeNode<Region> tree;
  private final Map<Region, TreeNode<Region>> nodesByRegion = new HashMap<>();
  private final Map<StateMachine, State> ownedStateMachines = new HashMap<>();

  public void incCompletionEvents() {
    completionEvents++;
  }

  @Override
  public boolean hasCompletionEvents() {
    return completionEvents > 0;
  }

  public void incTimeEvents() {
    timeEvents++;
  }

  @Override
  public boolean hasTimeEvents() {
    return timeEvents > 0;
  }

  @Override
  public boolean hasActivities() {
    return activities > 0;
  }

  public void incHistoryNodes() {
    historyNodes++;
  }

  @Override
  public int countOfHistoryNodes() {
    return historyNodes;
  }

  public void register(final State state) {
    if (state.doActivity() != null) {
      activities++;
    }

    if (state.isSubMachineState()) {
      ownedStateMachines.put(state.subStateMachine(), state);
    }
  }

  public void register(final Region region) {
    final State parentState = region.state() == null
      ? ownedStateMachines.get(region.stateMachine())
      : region.state();

    if (parentState == null) { // top level state machine
      tree = new TreeNode<>(region);
      nodesByRegion.put(region, tree);
    } else {
      TreeNode<Region> node = nodesByRegion.get(parentState.container());
      TreeNode<Region> child = new TreeNode<>(region);
      node.add(child);
      nodesByRegion.put(region, child);
    }
  }

  @Override
  public int treeDepth() {
    return treeDepth(tree);
  }

  private int treeDepth(final TreeNode<?> node) {
    if (node == null) {
      return 0;
    }
    if (!node.hasChildren()) {
      return 1;
    }

    int maxDepth = 0;
    for (TreeNode<?> c : node.children()) {
      int d = treeDepth(c);
      if (d > maxDepth) {
        maxDepth = d;
      }
    }
    return maxDepth + 1;

  }

  @Override
  public int countOfLeaves() {
    int leaves = 0;
    for (TreeNode<?> n : nodesByRegion.values()) {
      if (n.isLeaf()) {
        leaves++;
      }
    }
    return leaves;
  }

}
