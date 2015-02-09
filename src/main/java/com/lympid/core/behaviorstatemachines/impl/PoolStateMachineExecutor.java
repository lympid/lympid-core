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

/**
 * Note: needs to synchronize for activities
 *
 * @author Fabien Renaud
 */
public class PoolStateMachineExecutor extends AbstractStateMachineExecutor {

  private final StateMachineShardPoolExecutor pool;

  public PoolStateMachineExecutor(final StateMachineShardPoolExecutor pool, final int id) {
    super(id);
    this.pool = pool;
  }

  public PoolStateMachineExecutor(final StateMachineShardPoolExecutor pool) {
    super();
    this.pool = pool;
  }
  
  @Override
  public void start() {
    pool.start(this);
  }
  
  public void doStart() {
    super.start();
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
}
