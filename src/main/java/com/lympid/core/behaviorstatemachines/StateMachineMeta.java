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

/**
 * Provide information about the state machine that typically requires a full
 * traversal to collect.
 *
 * @author Fabien Renaud
 */
public interface StateMachineMeta {

  /**
   * Gets whether the state machine or any of its sub machines has simple
   * states.
   *
   * <p>
   * This may actually return false for very specific, and questionable, state
   * machines.</p>
   *
   * @return true when the state machine has simple states.
   */
  boolean hasSimpleStates();

  /**
   * Gets whether the state machine or any its sub machines has non-orthogonal
   * composite states.
   *
   * @return true when the state machine has non-orthogonal composite states.
   */
  boolean hasCompositeStates();

  /**
   * Gets whether the state machine or any of its sub machines has orthogonal
   * states.
   *
   * @return true when the state machine has orthogonal states.
   */
  boolean hasOrthogonalStates();

  /**
   * Gets whether the state machine has sub machine states.
   *
   * @return true when the state machine has sub machine states.
   */
  boolean hasSubmachineStates();

  /**
   * Gets whether the state machine has transitions with no triggers.
   *
   * @return true when the state machine has transitions with no triggers.
   */
  boolean hasCompletionEvents();

  /**
   * Gets whether the state machine has transitions with time events.
   *
   * @return true when the state machine has transitions with time events.
   */
  boolean hasTimeEvents();

  /**
   * Gets whether the state machine has states that define activities.
   *
   * @return true when the state machine as at least one state with an activity
   */
  boolean hasActivities();

  /**
   * Gets the number of the specified pseudo state kind the state machine or any
   * of its sub machines has.
   *
   * @param kind The pseudo state kind for which to get the count
   * @return The number pseudo states of the given kind the state machine has.
   */
  int countOf(PseudoStateKind kind);

  /**
   * Get the maximum number of nodes it takes to go from the root element to the
   * most-child element in the tree.
   *
   * @return The maximum number of nodes for the state machine
   */
  int treeDepth();

  /**
   * Gets the maximum number of leaves that state machine could have.
   *
   * @return The maximum number of leaves in the state machine.
   */
  int countOfLeaves();

}
