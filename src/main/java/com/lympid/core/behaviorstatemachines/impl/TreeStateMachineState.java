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

import com.lympid.core.behaviorstatemachines.FinalState;
import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachineMeta;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.common.TreeNode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * TODO: optimize max size of the maps
 *
 * @author Fabien Renaud
 */
public class TreeStateMachineState implements StateMachineState<TreeNode<State>> {

  private final TreeNode<State> activeStates;
  private final Map<Region, TreeNode<State>> nodesByRegion;
  private final Map<Region, TreeNode<State>> histories;
  private final Map<State, StateStatus> activeStateStatutes;
  private final Set<State> completedStates;
  private final Map<PseudoState, Set<Transition>> joins;
  private boolean started;
  private boolean terminated;

  public TreeStateMachineState(final StateMachineMeta metadata) {
    this.activeStates = new TreeNode<>();
    this.nodesByRegion = new HashMap<>();
    this.histories = new HashMap<>(metadata.countOfHistoryNodes());
    this.activeStateStatutes = new HashMap<>();
    this.completedStates = new HashSet<>();
    this.joins = new HashMap<>();
  }

  public static StateMachineState<TreeNode<State>> synchronizedMachineState(StateMachineState<TreeNode<State>> inst) {
    return new SynchronizedStateMachineState(inst);
  }

  @Override
  public TreeNode<State> activeStates() {
    return activeStates;
  }

  @Override
  public TreeNode<State> activeStates(final Region region) {
    return nodesByRegion.get(region);
  }

  @Override
  public boolean isActive(final State state) {
    assert state != null;
    TreeNode<State> node = nodesByRegion.get(state.container());
    if (node == null) {
      return false;
    }
    return state == node.content();
  }

  @Override
  public void activate(final State state) {
    TreeNode<State> node = nodesByRegion.get(state.container());
    assert node == null || node.content() != state;

    if (state.container().state() == null) { // top level state machine case
      activeStates.setContent(state);
      nodesByRegion.put(state.container(), activeStates);
    } else {
      Region stateRegion = state.container();
      node = nodesByRegion.get(stateRegion.state().container()); // get the parent node
      assert node != null;

      TreeNode<State> tn = new TreeNode<>(state);
      node.add(tn);
      nodesByRegion.put(stateRegion, tn);
    }

    activeStateStatutes.put(state, new StateStatus(state));
    if (state.region().isEmpty() && state.doActivity() == null) {
      completedStates.add(state);
    }
  }

  @Override
  public void deactivate(final State state) {
    TreeNode<State> node = nodesByRegion.get(state.container());
    assert node != null && node.content() == state;

    /*
     * Can only deactivate leaf.
     */
    assert node.isLeaf();

    if (state.container().state() == null) { // top level state machine case
      activeStates.setContent(null);
      nodesByRegion.clear();
    } else {
      node.parent().remove(node);
      nodesByRegion.remove(state.container());
    }

//    completedStates.remove(state);
    StateStatus status = activeStateStatutes.remove(state);
    assert status != null;

    clearActivity(status);
    clearEventTimers(status);
  }

  @Override
  public StateStatus status(final State state) {
    return activeStateStatutes.get(state);
  }

  @Override
  public void start() {
    this.started = true;
  }

  @Override
  public boolean hasStarted() {
    return started;
  }

  @Override
  public void terminate() {
    this.terminated = true;
  }

  @Override
  public boolean isTerminated() {
    return terminated;
  }

  @Override
  public void setActivity(final State state, final Future<?> future) {
    StateStatus status = activeStateStatutes.get(state);
    assert status != null : "Status is null for state: " + state;

    status.setActivity(future);
  }

  @Override
  public boolean activityCompleted(final State state) {
    StateStatus status = activeStateStatutes.get(state);
    assert status != null : "Status is null for state: " + state;

    status.setActivity(null);
    return completedOne(status);
  }

  @Override
  public boolean hasCompletedStates() {
    return !completedStates.isEmpty();
  }

  @Override
  public Set<State> completedStates() {
    return completedStates;
  }

  @Override
  public void removeCompletedState(final State state) {
    completedStates.remove(state);
  }

  @Override
  public boolean completedOne(final State state) {
    StateStatus status = activeStateStatutes.get(state);
    assert status != null : "Status is null for state: " + state;

    return completedOne(status);
  }

  private boolean completedOne(final StateStatus status) {
    if (status.getActivity() != null) {
      return false;
    }

    State state = status.getState();
    if (!state.region().isEmpty()) {
      for (Region r : state.region()) {
        TreeNode<State> node = nodesByRegion.get(r);
        if (node != null && !(node.content() instanceof FinalState)) {
          return false;
        }
      }
    }

    completedStates.add(state);
    return true;
  }

  @Override
  public boolean joinReached(final PseudoState joinVertex, final Transition transition) {
    Set<Transition> registered = joins.get(joinVertex);
    if (registered == null) {
      registered = new HashSet(joinVertex.incoming().size());
      joins.put(joinVertex, registered);
    }
    registered.add(transition);

    return registered.size() == joinVertex.incoming().size();
  }

  @Override
  public void clearJoin(final PseudoState joinVertex) {
    joins.remove(joinVertex);
  }

  @Override
  public TreeNode<State> restore(final Region r) {
    return histories.get(r);
  }

  @Override
  public void saveDeepHistory(final Region r) {
    TreeNode<State> node = nodesByRegion.get(r);
    if (node == null || node.content() instanceof FinalState) {
      histories.put(r, null);
    } else {
      histories.put(r, node.copy());
    }
  }

  @Override
  public void saveShallowHistory(final Region r) {
    TreeNode<State> node = nodesByRegion.get(r);
    if (node == null || node.content() instanceof FinalState) {
      histories.put(r, null);
    } else {
      histories.put(r, new TreeNode<>(node.content()));
    }
  }

  private void clearActivity(final StateStatus status) {
    Future f = status.getActivity();
    if (f != null && !f.isDone()) {
      if (f.cancel(true)) {
        // TODO
      } else {
        // TODO
      }
    }
  }

  private void clearEventTimers(final StateStatus status) {
    if (status.hasEventTimers()) {
      for (Future f : status.getEventTimers()) {
        if (!f.isDone()) {
          if (f.cancel(false)) {
            // TODO
          } else {
            // TODO
          }
        }
      }
    }
  }

  public static final class SynchronizedStateMachineState implements StateMachineState<TreeNode<State>> {

    private final StateMachineState<TreeNode<State>> inst;
    private final Object mutex = new Object();

    private SynchronizedStateMachineState(final StateMachineState<TreeNode<State>> inst) {
      this.inst = inst;
    }

    @Override
    public void activate(State state) {
      synchronized (mutex) {
        inst.activate(state);
      }
    }

    @Override
    public TreeNode<State> activeStates() {
      TreeNode<State> out;
      synchronized (mutex) {
        out = inst.activeStates();
      }
      return out;
    }

    @Override
    public TreeNode<State> activeStates(Region region) {
      TreeNode<State> out;
      synchronized (mutex) {
        out = inst.activeStates(region);
      }
      return out;
    }

    @Override
    public boolean activityCompleted(State state) {
      boolean out;
      synchronized (mutex) {
        out = inst.activityCompleted(state);
      }
      return out;
    }

    @Override
    public boolean completedOne(State state) {
      boolean out;
      synchronized (mutex) {
        out = inst.completedOne(state);
      }
      return out;
    }

    @Override
    public Set<State> completedStates() {
      Set<State> out;
      synchronized (mutex) {
        out = inst.completedStates();
      }
      return out;
    }

    @Override
    public void removeCompletedState(final State state) {
      synchronized (mutex) {
        inst.removeCompletedState(state);
      }
    }

    @Override
    public void deactivate(State state) {
      synchronized (mutex) {
        inst.deactivate(state);
      }
    }

    @Override
    public boolean hasCompletedStates() {
      boolean out;
      synchronized (mutex) {
        out = inst.hasCompletedStates();
      }
      return out;
    }

    @Override
    public boolean hasStarted() {
      boolean out;
      synchronized (mutex) {
        out = inst.hasStarted();
      }
      return out;
    }

    @Override
    public boolean isActive(State s) {
      boolean out;
      synchronized (mutex) {
        out = inst.isActive(s);
      }
      return out;
    }

    @Override
    public boolean isTerminated() {
      boolean out;
      synchronized (mutex) {
        out = inst.isTerminated();
      }
      return out;
    }

    @Override
    public boolean joinReached(final PseudoState joinVertex, final Transition transition) {
      boolean out;
      synchronized (mutex) {
        out = inst.joinReached(joinVertex, transition);
      }
      return out;
    }

    @Override
    public void clearJoin(final PseudoState joinVertex) {
      synchronized (mutex) {
        inst.clearJoin(joinVertex);
      }
    }

    @Override
    public TreeNode<State> restore(Region r) {
      TreeNode<State> out;
      synchronized (mutex) {
        out = inst.restore(r);
      }
      return out;
    }

    @Override
    public void saveDeepHistory(Region r) {
      synchronized (mutex) {
        inst.saveDeepHistory(r);
      }
    }

    @Override
    public void saveShallowHistory(Region r) {
      synchronized (mutex) {
        inst.saveShallowHistory(r);
      }
    }

    @Override
    public void setActivity(State state, Future<?> future) {
      synchronized (mutex) {
        inst.setActivity(state, future);
      }
    }

    @Override
    public void start() {
      synchronized (mutex) {
        inst.start();
      }
    }

    @Override
    public StateStatus status(State state) {
      StateStatus out;
      synchronized (mutex) {
        out = inst.status(state);
      }
      return out;
    }

    @Override
    public void terminate() {
      synchronized (mutex) {
        inst.terminate();
      }
    }

  }
}
