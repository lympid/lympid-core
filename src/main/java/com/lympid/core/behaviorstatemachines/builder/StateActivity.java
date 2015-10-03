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

import com.lympid.core.basicbehaviors.CompletionEvent;
import com.lympid.core.behaviorstatemachines.StateBehavior;

/**
 * Provides an interface for building:
 * <ul>
 * <li>an activity behavior</li>
 * <li>local transitions</li>
 * <li>internal transitions</li>
 * <li>external transitions</li>
 * </ul>
 * for a simple or submachine state.
 *
 * @param <V> A {@link CompositeStateBuilder} or {@link OrthogonalStateBuilder}.
 * @param <C> Type of the state machine context.
 *
 * @see StateTransitionSource
 *
 * @author Fabien Renaud
 */
public interface StateActivity<V extends StateBuilder<?, C>, C> extends StateTransitionSource<V, TransitionTrigger<V, C, CompletionEvent>, C> {

  /**
   * Set the state activity.
   *
   * @param activity The state activity.
   * @return An interface to build local, internal and external transitions to
   * the simple/submachine state.
   */
  StateTransitionSource<V, TransitionTrigger<V, C, CompletionEvent>, C> activity(StateBehavior<C> activity);

  /**
   * Set the state activity.
   *
   * The instance of the state activity behavior is provided by the
   * {@link BehaviorFactory}.
   *
   * @param activity The state activity.
   * @return An interface to build local, internal and external transitions to
   * the simple/submachine state.
   */
  StateTransitionSource<V, TransitionTrigger<V, C, CompletionEvent>, C> activity(Class<? extends StateBehavior<C>> activity);
}
