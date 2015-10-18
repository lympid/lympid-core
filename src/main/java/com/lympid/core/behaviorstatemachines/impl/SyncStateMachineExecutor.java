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

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.StateMachineSnapshot;

/**
 *
 * @author Fabien Renaud
 */
public class SyncStateMachineExecutor<C> extends AbstractStateMachineExecutor<C> {

  private SyncStateMachineExecutor(
    final int id,
    final String name,
    final StateMachine machine,
    final C context,
    final ExecutorConfiguration configuration,
    final StateMachineSnapshot<C> snapshot
  ) {
    super(id, name, machine, context, configuration, snapshot);
  }

  @Override
  protected StateMachineState createMachineState(final StateMachine machine) {
    return StateMachineState.synchronizedMachineState(super.createMachineState(machine));
  }

  @Override
  protected synchronized void start() {
    super.start();
  }

  @Override
  public synchronized void take(Event event) {
    super.take(event);
  }

  @Override
  protected synchronized void take(final Event event, final State state) {
    super.take(event, state);
  }

  @Override
  protected synchronized void takeCompletionEvent() {
    super.takeCompletionEvent();
  }

  @Override
  public synchronized StateMachineSnapshot<C> snapshot() {
    return super.snapshot();
  }

  @Override
  public synchronized void pause() {
    super.pause();
  }

  @Override
  public synchronized void resume() {
    super.resume();
  }

  public static final class Builder<C> extends AbstractBuilder<C> {

    @Override
    public StateMachineExecutor<C> build() {
      return new SyncStateMachineExecutor<>(
        getId(),
        getName(),
        getMachine(),
        getContext(),
        getConfiguration(),
        getSnapshot()
      );
    }

  }

}
