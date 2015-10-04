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
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachineMeta;
import com.lympid.core.behaviorstatemachines.Transition;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Fabien Renaud
 */
abstract class AbstractStateMachineState extends ResumableStateMachineState {

  private final MutableStateConfiguration activeStates;
  private final Map<Region, MutableStateConfiguration> nodesByRegion;
  private final Map<Region, MutableStateConfiguration> histories;
  private final Map<State, StateStatus> activeStateStatutes;
  private final Set<State> completedStates;
  private final Map<PseudoState, Set<Transition>> joins;

  protected AbstractStateMachineState(final MutableStateConfiguration activeStates, final StateMachineMeta metadata) {
    super(metadata);
    this.activeStates = activeStates;
    this.nodesByRegion = new HashMap<>();
    this.histories = hashMap(metadata.countOf(PseudoStateKind.SHALLOW_HISTORY) + metadata.countOf(PseudoStateKind.DEEP_HISTORY));
    this.activeStateStatutes = new HashMap<>();
    this.completedStates = new HashSet<>();
    this.joins = hashMap(metadata.countOf(PseudoStateKind.JOIN));
  }

  private Map hashMap(final int size) {
    return size == 0 ? Collections.EMPTY_MAP : new HashMap(size);
  }

  @Override
  public StateConfiguration<?> activeStates() {
    return activeStates;
  }

  @Override
  public StateConfiguration<?> activeStates(final Region region) {
    return nodesByRegion.get(region);
  }

  @Override
  public boolean isActive(final State state) {
    assert state != null;
    StateConfiguration stateConfig = nodesByRegion.get(state.container());
    if (stateConfig == null) {
      return false;
    }
    return state == stateConfig.state();
  }

  @Override
  public void activate(final State state) {
    MutableStateConfiguration stateConfig = nodesByRegion.get(state.container());
    assert stateConfig == null || stateConfig.state() != state;

    if (state.container().state() == null) { // top level state machine case
      activeStates.setState(state);
      nodesByRegion.put(state.container(), activeStates);
    } else {
      Region stateRegion = state.container();
      stateConfig = nodesByRegion.get(stateRegion.state().container()); // get the parent node
      assert stateConfig != null;

      MutableStateConfiguration newCollection = stateConfig.addChild(state);
      nodesByRegion.put(stateRegion, newCollection);
    }

    activeStateStatutes.put(state, new StateStatus(state));
    if (state.region().isEmpty() && state.doActivity() == null) {
      completedStates.add(state);
    }
  }

  @Override
  public void deactivate(final State state) {
    MutableStateConfiguration stateConfig = nodesByRegion.get(state.container());
    assert stateConfig != null && stateConfig.state() == state;

    /*
     * Can only deactivate leaf.
     */
    assert stateConfig.isEmpty();

    if (state.container().state() == null) { // top level state machine case
      activeStates.clear();
      nodesByRegion.clear();
    } else {
      stateConfig.parent().removeChild(stateConfig);
      nodesByRegion.remove(state.container());
    }

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
  public Lock activityLock(final State state) {
    StateStatus status = activeStateStatutes.get(state);
    if (status.getLock() == null) {
      status.setLock(new ReentrantLock());
    }
    return status.getLock();
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
        StateConfiguration stateConfig = nodesByRegion.get(r);
        if (stateConfig != null && !(stateConfig.state() instanceof FinalState)) {
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
  public StateConfiguration<?> restore(final Region region) {
    return histories.get(region);
  }

  @Override
  public Map<Region, StateConfiguration<?>> history() {
    return (Map) histories;
  }

  @Override
  public void saveDeepHistory(final Region region) {
    MutableStateConfiguration stateConfig = nodesByRegion.get(region);
    if (stateConfig == null || stateConfig.state() instanceof FinalState) {
      histories.remove(region);
    } else {
      saveHistory(region, stateConfig.copy());
    }
  }

  @Override
  public void saveShallowHistory(final Region region) {
    StateConfiguration stateConfig = nodesByRegion.get(region);
    if (stateConfig == null || stateConfig.state() instanceof FinalState) {
      histories.remove(region);
    } else {
      saveHistory(region, new SimpleStateConfiguration(stateConfig.state()));
    }
  }

  @Override
  void saveHistory(final Region region, final MutableStateConfiguration history) {
    histories.put(region, history);
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
    status.setActivity(null);
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
      status.setEventTimers(null);
    }
  }

  @Override
  public void pause() {
    super.pause();
    for (StateStatus status : activeStateStatutes.values()) {
      clearActivity(status);
      clearEventTimers(status);
    }
  }
}
