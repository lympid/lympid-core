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

import com.lympid.core.behaviorstatemachines.TransitionBehavior;

/**
 * Provides an interface to build a transition outgoing a
 * <strong>pseudo state</strong>. The transition can be defined with an effect
 * and a target.
 *
 * @param <V> {@code VertexBuilder} type which is the source of the transition.
 * @param <C> Type of the state machine context.
 *
 * @see TransitionTarget
 * @author Fabien Renaud
 */
public interface PseudoTransitionEffect<V extends VertexBuilder<?, ?, C>, C> extends TransitionTarget<V, C> {

  /**
   * Sets the effect for the transition.
   *
   * The instance of the effect behavior is provided by the
   * {@link BehaviorFactory}.
   *
   * @param effect The behavior to use as an effect for the transition.
   * @return An interface to set the target for the transition.
   */
  TransitionTarget<V, C> effect(Class<? extends TransitionBehavior<C>> effect);

  /**
   * Sets the effect for the transition.
   *
   * @param effect The behavior to use as an effect for the transition.
   * @return An interface to set the target for the transition.
   */
  TransitionTarget<V, C> effect(TransitionBehavior<C> effect);

}
