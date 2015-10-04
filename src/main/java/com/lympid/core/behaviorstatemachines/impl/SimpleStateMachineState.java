/*
 * Copyright 2015 Lympid.
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
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachineMeta;
import com.lympid.core.behaviorstatemachines.Transition;
import java.util.Collections;
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
public class SimpleStateMachineState extends ResumableStateMachineState {

  private final SimpleStateConfiguration activeStates;
  private final Set<State> completed = new HashSet<>(1);
  private StateStatus status;

  public SimpleStateMachineState(final StateMachineMeta metadata) {
    super(metadata);
    this.activeStates = new SimpleStateConfiguration();
  }

  @Override
  public StateConfiguration<?> activeStates() {
    return activeStates;
  }

  @Override
  public StateConfiguration<?> activeStates(final Region region) {
    return activeStates.state() == null ? null : activeStates;
  }

  @Override
  public boolean isActive(final State state) {
    return activeStates.state() == state;
  }

  @Override
  public void activate(final State state) {
    activeStates.setState(state);

    status = new StateStatus(state);
    if (state.doActivity() == null) {
      completed.add(state);
    }
  }

  @Override
  public void deactivate(final State state) {
    assert activeStates.state() == state;

    activeStates.clear();
    completed.clear();
    clearActivity();
    clearEventTimers();
    status = null;
  }

  @Override
  public StateStatus status(final State state) {
    return status;
  }
  
  @Override
  public Lock activityLock(final State state) {
    if (status.getLock() == null) {
      status.setLock(new ReentrantLock());
    }
    return status.getLock();
  }

  @Override
  public void setActivity(final State state, final Future<?> future) {
    assert status.getActivity() == null;
    status.setActivity(future);
  }

  @Override
  public boolean activityCompleted(final State state) {
    assert status.getActivity() != null;
    assert completed.isEmpty();
    status.setActivity(null);
    completed.add(state);
    return true;
  }

  @Override
  public boolean hasCompletedStates() {
    return status.getActivity() == null;
  }

  @Override
  public Set<State> completedStates() {
    return completed;
  }

  @Override
  public void removeCompletedState(final State state) {
    completed.remove(state);
  }

  @Override
  public boolean completedOne(final State state) {
    throw new IllegalStateException("Partial completion of a state is impossible in a simple state machine. Only an activity, if it exists, can complete a state.");
  }

  @Override
  public boolean joinReached(final PseudoState joinVertex, final Transition transition) {
    throw new IllegalStateException("Simple state machines can not have join vertices.");
  }

  @Override
  public void clearJoin(final PseudoState joinVertex) {
    throw new IllegalStateException("Simple state machines can not have join vertices.");
  }

  @Override
  public StateConfiguration<?> restore(final Region r) {
    throw new IllegalStateException("Simple state machines have no history.");
  }

  @Override
  public Map<Region, StateConfiguration<?>> history() {
    return Collections.EMPTY_MAP;
  }

  @Override
  public void saveDeepHistory(final Region region) {
    throw new IllegalStateException("Simple state machines can not have a deep history.");
  }

  @Override
  public void saveShallowHistory(final Region region) {
    throw new IllegalStateException("Simple state machines can not have a shallow history.");
  }

  @Override
  void saveHistory(final Region region, final MutableStateConfiguration history) {
    throw new IllegalStateException("Simple state machines can not have any history.");
  }

  private void clearActivity() {
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

  private void clearEventTimers() {
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
    if (status != null) {
      clearActivity();
      clearEventTimers();
    }
  }
}
