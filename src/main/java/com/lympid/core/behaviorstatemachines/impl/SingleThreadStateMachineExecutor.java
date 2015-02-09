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
import java.util.Objects;

/**
 *
 * @author Fabien Renaud
 */
public class SingleThreadStateMachineExecutor extends AbstractStateMachineExecutor {

  private Thread goThread;

  public SingleThreadStateMachineExecutor(final int id) {
    super(id);
  }

  public SingleThreadStateMachineExecutor() {
    super();
  }

  @Override
  public void setStateMachine(final StateMachine machine) {
    if (machine.metadata().hasTimeEvents()) {
      throw new RuntimeException(getClass().getSimpleName() + " does not support state machines with time events yet.");
    }
    if (machine.metadata().hasActivities()) {
      throw new RuntimeException(getClass().getSimpleName() + " does not support state machines with activities yet.");
    }
    super.setStateMachine(machine);
  }

  @Override
  public void go() {
    goThread = Thread.currentThread();
    super.go();
  }

  @Override
  protected void start() {
    checkThread();
    super.start();
  }

  @Override
  public void take(final Event event) {
    checkThread();
    super.take(event);
  }

  @Override
  public void take(final Event event, final State state) {
    checkThread();
    super.take(event, state);
  }

  @Override
  protected void takeCompletionEvent() {
    checkThread();
    super.takeCompletionEvent();
  }

  @Override
  public StateMachineSnapshot snapshot() {
    checkThread();
    return super.snapshot();
  }

  private void checkThread() {
    if (!Objects.equals(goThread, Thread.currentThread())) {
      throw new IllegalThreadStateException("This state machine executor allows only the 'go' thread to run the state machine. Go thread: " + goThread.getName() + ". Current thread: " + Thread.currentThread().getName());
    }
  }
}
