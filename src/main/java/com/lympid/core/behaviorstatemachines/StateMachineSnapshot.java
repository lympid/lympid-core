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
package com.lympid.core.behaviorstatemachines;

import com.lympid.core.common.Copyable;
import com.lympid.core.common.StringTree;
import java.io.Serializable;
import java.util.Map;

/**
 * Provides a snapshot of the state machine. The snapshot describes the active
 * state configuration of the state machine, its history and context at the
 * moment the snapshot was taken.
 *
 * @param <C> Type of the state machine context.
 *
 * @author Fabien Renaud
 */
public interface StateMachineSnapshot<C> extends Serializable {

  /**
   * The context of the state machine at the moment the snapshot was taken. This
   * method returns a copy of the context when {@code C} implements
   * {@link Copyable}.
   *
   * @return An instance of the context of the state machine. May be a copy.
   */
  C context();

  /**
   * Gets the history each history vertex of the state machine is caching at the
   * moment the snapshot was taken. This method returns a map where each key is
   * the UML identifier of a region and where values are state identifiers of
   * the sub active state configuration of that region when it was exited.
   *
   * @return A map that describes the former sub active state configuration of
   * each region that have an history vertex and were exited.
   */
  Map<String, StringTree> history();

  /**
   * Gets whether the state machine has been started.
   *
   * @return true when the state machine has been started.
   */
  boolean isStarted();

  /**
   * Gets whether the state machine is in a terminate state.
   *
   * @return true when the state machine is in a terminate state.
   */
  boolean isTerminated();

  /**
   * Gets a tree of strings that represents the active state configuration of
   * the state machine. Each node on the tree is the UML identifier of the state
   * that belongs to the active state configuration.
   *
   * @return A tree of state identifiers representing the active state
   * configuration of the state machine.
   */
  StringTree stateConfiguration();

  /**
   * Gets the UML identifier of the state machine.
   *
   * @return The UML identifier of the state machine.
   */
  String stateMachine();

}
