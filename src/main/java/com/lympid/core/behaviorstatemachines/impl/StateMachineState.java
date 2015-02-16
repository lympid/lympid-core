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
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.Transition;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 *
 * @author Fabien Renaud
 */
public interface StateMachineState {

  public static StateMachineState synchronizedMachineState(StateMachineState inst) {
    return new SynchronizedStateMachineState(inst);
  }

  void activate(final State state);

  StateConfiguration<?> activeStates();

  StateConfiguration<?> activeStates(final Region region);

  boolean activityCompleted(final State state);

  boolean completedOne(final State state);

  Set<State> completedStates();

  void removeCompletedState(final State state);

  void deactivate(final State state);

  boolean hasCompletedStates();

  boolean hasStarted();

  boolean joinReached(PseudoState joinVertex, Transition transition);

  void clearJoin(PseudoState joinVertex);

  boolean isActive(final State s);

  boolean isTerminated();

  StateConfiguration<?> restore(final Region r);

  Map<Region, StateConfiguration<?>> history();

  void saveDeepHistory(final Region r);

  void saveShallowHistory(final Region r);

  void setActivity(final State state, final Future<?> future);

  void start();

  StateStatus status(final State state);

  void terminate();

  public static final class SynchronizedStateMachineState implements StateMachineState {

    private final StateMachineState inst;
    private final Object mutex = new Object();

    private SynchronizedStateMachineState(final StateMachineState inst) {
      this.inst = inst;
    }

    @Override
    public void activate(State state) {
      synchronized (mutex) {
        inst.activate(state);
      }
    }

    @Override
    public StateConfiguration activeStates() {
      StateConfiguration out;
      synchronized (mutex) {
        out = inst.activeStates();
      }
      return out;
    }

    @Override
    public StateConfiguration activeStates(Region region) {
      StateConfiguration out;
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
    public StateConfiguration restore(Region r) {
      StateConfiguration out;
      synchronized (mutex) {
        out = inst.restore(r);
      }
      return out;
    }

    @Override
    public Map<Region, StateConfiguration<?>> history() {
      Map<Region, StateConfiguration<?>> out;
      synchronized (mutex) {
        out = inst.history();
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
