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
package com.lympid.core.behaviorstatemachines;

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration;
import com.lympid.core.behaviorstatemachines.impl.ExecutorListener;
import com.lympid.core.behaviorstatemachines.impl.LockStateMachineExecutor;
import com.lympid.core.behaviorstatemachines.impl.PoolStateMachineExecutor;
import com.lympid.core.behaviorstatemachines.impl.SyncStateMachineExecutor;

/**
 * Interface for processing a state machine.
 *
 * @param <C> Type of the state machine context.
 *
 * @see SyncStateMachineExecutor
 * @see LockStateMachineExecutor
 * @see PoolStateMachineExecutor
 * @author Fabien Renaud
 */
public interface StateMachineExecutor<C> {

  /**
   * Gets the unique id of this executor. The id is unique per JVM process only.
   *
   * @return The unique id of the executor.
   */
  int getId();
  
  /**
   * Gets the name of this executor.
   * 
   * @return The name of the executor. 
   */
  String getName();

  /**
   * Sets listeners for the state machine executor.
   *
   * @param listeners The listeners that executor must call back.
   */
  void setListeners(final ExecutorListener listeners);

  /**
   * Gets the listeners the executor has been subscribed to.
   *
   * @return The listeners the executor has been subscribed to.
   */
  ExecutorListener listeners();

  /**
   * Sets the state machine the executor will process.
   *
   * @param machine The state machine to use for processing.
   */
  void setStateMachine(StateMachine machine);

  /**
   * Gets the state machine of the executor.
   *
   * @return The state machine the executor is utilizing.
   */
  StateMachine stateMachine();

  ExecutorConfiguration configuration();

  /**
   * Sets the context of the state machine.
   *
   * @param context The context of the state machine.
   */
  void setContext(C context);

  /**
   * Starts the state machine. The state machine, its context, listeners and
   * configuration must have been set prior to calling this method.
   */
  void go();

  /**
   * Takes an event and attempts to process it. The executor only takes events
   * if the state machine has been started with {@link #go}.
   *
   * @param event The event to process.
   */
  void take(final Event event);

  StateMachineSnapshot<C> snapshot();

  /**
   * Pauses the state machine.
   *
   * <p>
   * Any new incoming events will be dropped and processing will only resume
   * after {@link #resume} has been invoked.
   * </p>
   *
   * @return The snapshot of the state machine when it was paused. This snapshot
   * can be used to resume the state machine.
   */
  StateMachineSnapshot pause();

  /**
   * Resumes a state machine with the specified active state configuration and
   * context.
   *
   * @param snapshot The state of the state machine where to resume from.
   */
  void resume(StateMachineSnapshot snapshot);
}
