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
package com.lympid.core.behaviorstatemachines.builder;

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.behaviorstatemachines.BiTransitionConstraint;

/**
 * Provides an interface to build an external or local transition outgoing a
 * <strong>state</strong>. The transition can be defined with a guard, an effect
 * and a target.
 *
 * @param <V> {@code VertexBuilder} type which is the source of the transition.
 * @param <C> Type of the state machine context.
 * @param <E> {@code Event} type which lead to this step in the transition.
 *
 * @see TransitionEffect
 * @author Fabien Renaud
 */
public interface TransitionGuard<V extends VertexBuilder<?, ?, C>, C, E extends Event> extends TransitionEffect<V, C, E> {

  /**
   * Sets a guard for the transition.
   *
   * The instance of the guard is provided by the {@link ConstraintFactory}.
   *
   * @param guard A class implementing a {@link BiTransitionConstraint}.
   * @return An interface to set the effect and target of the transition.
   */
  TransitionEffect<V, C, E> guard(Class<? extends BiTransitionConstraint<E, C>> guard);

  /**
   * Sets a guard that is the binary opposite of the given class.
   *
   * Anytime guardClass returns true, the guard of this transition will return
   * false and vice-versa.
   *
   * The instance of the guard is provided by the {@link ConstraintFactory}.
   *
   * @param guard A class implementing a {@link BiTransitionConstraint}.
   * @return An interface to set the effect and target of the transition.
   */
  TransitionEffect<V, C, E> guardElse(Class<? extends BiTransitionConstraint<E, C>> guard);

  /**
   * Sets a guard for the transition.
   *
   * @param guard The constraint for the guard.
   * @return An interface to set the effect and target of the transition.
   */
  TransitionEffect<V, C, E> guard(BiTransitionConstraint<E, C> guard);

}
