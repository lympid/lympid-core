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
import com.lympid.core.behaviorstatemachines.BiTransitionBehavior;

/**
 * Provides an interface to build an internal (ask self) transition outgoing a
 * <strong>state</strong>. The transition can be defined with an effect and a
 * target.
 *
 * @param <V> {@code StateBuilder} type which is the source of the transition.
 * @param <E> {@code Event} type which lead to this step in the transition.
 *
 * @see SelfTransitionTarget
 * @author Fabien Renaud
 */
public interface SelfTransitionEffect<V extends StateBuilder, C, E extends Event> extends SelfTransitionTarget<V> {

  /**
   * Sets the effect for the transition.
   *
   * The instance of the effect behavior is provided by the
   * {@link BehaviorFactory}.
   *
   * @param effect The behavior to use as an effect for the transition.
   * @return An interface to set the target for the transition.
   */
  SelfTransitionTarget<V> effect(Class<? extends BiTransitionBehavior<E, C>> effect);

  /**
   * Sets the effect for the transition.
   *
   * @param effect The behavior to use as an effect for the transition.
   * @return An interface to set the target for the transition.
   */
  SelfTransitionTarget<V> effect(BiTransitionBehavior<E, C> effect);

}
