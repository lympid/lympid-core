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

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.StateMachineSnapshot;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Note: needs to synchronize for activities
 *
 * @author Fabien Renaud
 */
public class PoolStateMachineExecutor<C> extends AbstractStateMachineExecutor<C> {

  private final StateMachineShardPoolExecutor<C> pool;

  private PoolStateMachineExecutor(
    final int id,
    final String name,
    final StateMachine machine,
    final C context,
    final ExecutorConfiguration configuration,
    final StateMachineSnapshot<C> snapshot,
    final StateMachineShardPoolExecutor<C> pool
  ) {
    super(id, name, machine, context, configuration, snapshot);
    this.pool = pool;
  }

  @Override
  public void go() {
    pool.go(this);
  }

  void doGo() {
    super.go();
  }

  @Override
  public void take(Event event) {
    pool.take(this, event);
  }

  void doTake(Event event) {
    super.take(event);
  }

  @Override
  protected void take(final Event event, final State state) {
    pool.take(this, event, state);
  }

  void doTake(final Event event, final State state) {
    super.take(event, state);
  }

  @Override
  protected void takeCompletionEvent() {
    pool.takeCompletionEvent(this);
  }

  void doTakeCompletionEvent() {
    super.takeCompletionEvent();
  }

  @Override
  public void resume() {
    pool.resume(this);
  }

  void doResume() {
    super.resume();
  }

  @Override
  public void pause() {
    pool.pause(this);
  }

  void doPause() {
    super.pause();
  }

  @Override
  public StateMachineSnapshot<C> snapshot() {
    try {
      return asyncSnapshot().get();
    } catch (InterruptedException | ExecutionException ex) {
      ex.printStackTrace(); // FIXME
    }
    return null;
  }

  private Future<StateMachineSnapshot<C>> asyncSnapshot() {
    return pool.snapshot(this);
  }

  StateMachineSnapshot<C> doSnapshot() {
    return super.snapshot();
  }

  public static final class Builder<C> extends AbstractBuilder<C> {

    private final StateMachineShardPoolExecutor<C> pool;
    
    public Builder(final StateMachineShardPoolExecutor<C> pool) {
      this.pool = pool;
    }
    
    @Override
    public StateMachineExecutor<C> build() {
      return new PoolStateMachineExecutor<C>(
        getId(),
        getName(),
        getMachine(),
        getContext(),
        getConfiguration(),
        getSnapshot(),
        pool
      );
    }

  }
}
