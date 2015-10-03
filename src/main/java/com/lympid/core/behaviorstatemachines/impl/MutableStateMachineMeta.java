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

import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.StateMachineMeta;
import com.lympid.core.common.TreeNode;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Fabien Renaud
 */
public class MutableStateMachineMeta implements StateMachineMeta {

  private boolean simpleStates;
  private boolean compositeStates;
  private boolean orthogonalStates;
  private boolean submachineStates;
  private int completionEvents;
  private int timeEvents;
  private int activities;
  private final Map<PseudoStateKind, Integer> pseudoStateCounts = new EnumMap<>(PseudoStateKind.class);
  private TreeNode<Region> tree;
  private final Map<Region, TreeNode<Region>> nodesByRegion = new HashMap<>();
  private final Map<StateMachine, State> ownedStateMachines = new HashMap<>();
  private final Map<String, State> stateById = new HashMap<>();
  private final Map<String, Region> regionById = new HashMap<>();

  public MutableStateMachineMeta() {
    for (PseudoStateKind kind : PseudoStateKind.values()) {
      pseudoStateCounts.put(kind, 0);
    }
  }

  @Override
  public boolean hasSimpleStates() {
    return simpleStates;
  }

  @Override
  public boolean hasCompositeStates() {
    return compositeStates;
  }

  @Override
  public boolean hasOrthogonalStates() {
    return orthogonalStates;
  }

  @Override
  public boolean hasSubmachineStates() {
    return submachineStates;
  }

  void incCompletionEvents() {
    completionEvents++;
  }

  @Override
  public boolean hasCompletionEvents() {
    return completionEvents > 0;
  }

  void incTimeEvents() {
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

  void register(final PseudoState pseudoState) {
    Integer count = pseudoStateCounts.get(pseudoState.kind());
    pseudoStateCounts.put(pseudoState.kind(), count + 1);
  }

  @Override
  public int countOf(final PseudoStateKind kind) {
    return pseudoStateCounts.get(kind);
  }

  Map<PseudoStateKind, Integer> pseudoStateCounts() {
    return new HashMap<>(pseudoStateCounts);
  }

  void register(final State state) {
    if (state.isSubMachineState()) {
      submachineStates = true;
    } else if (state.isSimple()) {
      simpleStates = true;
    } else if (state.isOrthogonal()) {
      orthogonalStates = true;
    } else {
      assert state.isComposite();
      compositeStates = true;
    }

    if (state.doActivity() != null) {
      activities++;
    }

    if (state.isSubMachineState()) {
      ownedStateMachines.put(state.subStateMachine(), state);
    }
    
    stateById.put(state.getId(), state);
  }

  void register(final Region region) {
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
    
    regionById.put(region.getId(), region);
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

  @Override
  public State state(final String id) {
    return stateById.get(id);
  }
  
  Map<String, State> stateById() {
    return new HashMap<>(stateById);
  }

  @Override
  public Region region(final String id) {
    return regionById.get(id);
  }
  
  Map<String, Region> regionById() {
    return new HashMap<>(regionById);
  }

}
