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

import com.lympid.core.behaviorstatemachines.TransitionConstraint;

/**
 * Provides an interface to build a transition outgoing a
 * <strong>pseudo state</strong>. The transition can be defined with a guard, an
 * effect and a target.
 *
 * @param <V> {@code VertexBuilder} type which is the source of the transition.
 * @param <C> Type of the state machine context.
 *
 * @see PseudoTransitionEffect
 * @author Fabien Renaud
 */
public interface PseudoTransitionGuard<V extends VertexBuilder<?, ?, C>, C> extends PseudoTransitionEffect<V, C> {

  /**
   * Sets a guard for the transition.
   *
   * The instance of the guard is provided by the {@link ConstraintFactory}.
   *
   * @param guard A class implementing a {@link TransitionConstraint}.
   * @return An interface to set the effect and target of the transition.
   */
  PseudoTransitionEffect<V, C> guard(Class<? extends TransitionConstraint<C>> guard);

  /**
   * Sets a guard that is the binary opposite of the given class.
   *
   * Anytime guardClass returns true, the guard of this transition will return
   * false and vice-versa.
   *
   * The instance of the guard is provided by the {@link ConstraintFactory}.
   *
   * @param guard A class implementing a {@link TransitionConstraint}.
   * @return An interface to set the effect and target of the transition.
   */
  PseudoTransitionEffect<V, C> guardElse(Class<? extends TransitionConstraint<C>> guard);

  /**
   * Sets a guard for the transition.
   *
   * @param guard The constraint for the guard.
   * @return An interface to set the effect and target of the transition.
   */
  PseudoTransitionEffect<V, C> guard(TransitionConstraint<C> guard);

}
