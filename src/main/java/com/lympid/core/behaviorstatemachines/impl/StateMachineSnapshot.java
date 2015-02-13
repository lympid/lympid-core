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

/**
 *
 * @author Fabien Renaud
 */
public final class StateMachineSnapshot<C> {

  private final StateConfiguration activateStates;
  private final boolean started;
  private final boolean terminated;
  private final C context;

  StateMachineSnapshot(final StateMachineState state, final C context) {
    this.activateStates = state.activeStates().copy();
    this.started = state.hasStarted();
    this.terminated = state.isTerminated();
    this.context = context; // TODO: clone
  }

  public StateConfiguration activateStates() {
    return activateStates;
  }

  public C context() {
    return context;
  }

  public boolean isStarted() {
    return started;
  }

  public boolean isTerminated() {
    return terminated;
  }

}
