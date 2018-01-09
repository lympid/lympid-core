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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Fabien Renaud
 */
public class LockStateMachineExecutor<C> extends AbstractStateMachineExecutor<C> {

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  private LockStateMachineExecutor(
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
  public void go() {
    lock.writeLock().lock();
    try {
      super.go();
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void take(Event event) {
    lock.writeLock().lock();
    try {
      super.take(event);
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  protected synchronized void take(final Event event, final State state) {
    lock.writeLock().lock();
    try {
      super.take(event, state);
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  protected synchronized void takeCompletionEvent() {
    lock.writeLock().lock();
    try {
      super.takeCompletionEvent();
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public StateMachineSnapshot<C> snapshot() {
    lock.readLock().lock();
    try {
      return super.snapshot();
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void pause() {
    lock.writeLock().lock();
    try {
      super.pause();
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void resume() {
    lock.writeLock().lock();
    try {
      super.resume();
    } finally {
      lock.writeLock().unlock();
    }
  }

  public static final class Builder<C> extends AbstractBuilder<C> {

    @Override
    public StateMachineExecutor<C> build() {
      return new LockStateMachineExecutor<>(
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
