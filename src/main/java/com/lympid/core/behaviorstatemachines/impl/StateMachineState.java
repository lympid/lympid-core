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
import java.util.Set;
import java.util.concurrent.Future;

/**
 *
 * @author Fabien Renaud
 */
public interface StateMachineState<T> {

  void activate(final State state);

  T activeStates();

  T activeStates(final Region region);

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

  T restore(final Region r);

  void saveDeepHistory(final Region r);

  void saveShallowHistory(final Region r);

  void setActivity(final State state, final Future<?> future);

  void start();

  StateStatus status(final State state);

  void terminate();

}
