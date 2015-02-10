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

import com.lympid.core.common.Trigger;
import com.lympid.core.common.UmlElement;
import java.util.Collection;

/**
 * A transition is a directed relationship between a source vertex and a target
 * vertex. It may be part of a compound transition, which takes the state
 * machine from one state configuration to another, representing the complete
 * response of the state machine to an occurrence of an event of a particular
 * type.
 *
 * @author Fabien Renaud
 */
public interface Transition extends UmlElement, Visitable {

  TransitionKind kind();

  /**
   * Specifies the triggers that may fire the transition.
   *
   * @return The triggers that may fire the transition.
   */
  Collection<? extends Trigger> triggers();

  /**
   * A guard is a constraint that provides a fine-grained control over the
   * firing of the transition. The guard is evaluated when an event occurrence
   * is dispatched by the state machine. If the guard is true at that time, the
   * transition may be enabled; otherwise, it is disabled. Guards should be pure
   * expressions without side effects. Guard expressions with side effects are
   * ill formed.
   *
   * @return A guard is a constraint that provides a fine-grained control over
   * the firing of the transition.
   */
  BiTransitionConstraint guard();

  /**
   * Specifies an optional behavior to be performed when the transition fires.
   *
   * @return An optional behavior to be performed when the transition fires.
   */
  BiTransitionBehavior effect();

  /**
   * Designates the originating vertex (state or pseudostate) of the transition.
   *
   * @return The source of the transition.
   */
  Vertex source();

  /**
   * Designates the target vertex that is reached when the transition is taken.
   *
   * @return The target of the transition.
   */
  Vertex target();

  /**
   * The transition of which this is a replacement.
   *
   * @return The transition of which this is a replacement.
   */
//  Transition redefinedTransition();
  /**
   * Designates the region that owns this transition.
   *
   * @return The region that owns this transition.
   */
  Region container();
}
