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
import com.lympid.core.common.TreeNode;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Fabien Renaud
 */
public class LockStateMachineExecutor extends AbstractStateMachineExecutor {

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  public LockStateMachineExecutor(final int id) {
    super(id);
  }

  public LockStateMachineExecutor() {
    super();
  }

  @Override
  protected StateMachineState createMachineState(final StateMachine machine) {
    return AbstractStateMachineState.synchronizedMachineState(super.createMachineState(machine));
  }

  @Override
  protected void start() {
    lock.writeLock().lock();
    try {
      super.start();
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
  public StateMachineSnapshot snapshot() {
    lock.readLock().lock();
    try {
      return super.snapshot();
    } finally {
      lock.readLock().unlock();
    }
  }

}
