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

/**
 * Provides an interface for building internal (aka self) and external
 * transitions.
 *
 * @param <V> {@code StateBuilder} type which is the source of the transition.
 * @param <T> Type of the first step that can taken in a transition. Not useful
 * here.
 * @param <C> Type of the state machine context.
 *
 * @see OneKindTransitionSource
 * @author Fabien Renaud
 */
public interface StateTransitionSource<V extends StateBuilder<?, C>, T extends TransitionStep<?>, C> extends OneKindTransitionSource<TransitionTrigger<V, C, CompletionEvent>> {

  /**
   * Adds an internal (aka self) transition to the state.
   *
   * @param name The name of the transition. Transition names do NOT have to be
   * unique.
   * @return An interface to build the transition.
   */
  SelfTransitionTrigger<V, C, CompletionEvent> selfTransition(String name);

  /**
   * Adds an internal (aka self) transition to the state.
   *
   * @return An interface to build the transition.
   */
  SelfTransitionTrigger<V, C, CompletionEvent> selfTransition();
}
