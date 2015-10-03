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

/**
 * Provides an interface for building external transitions.
 *
 * @param <T> Type of the first step that can be taken in the external
 * transition. This type is used to provide less functionality for a transition
 * when it is appropriate to do so. For example, when a transition must not have
 * triggers but can have guard, T would be set to {@link TransitionGuard}
 * @author Fabien Renaud
 */
public interface OneKindTransitionSource<T extends TransitionStep<?>> {

  /**
   * Adds an external transition to the current (pseudo) vertex builder.
   *
   * @param name The name of the transition. Transition names do NOT have to be
   * unique.
   * @return An interface to build the transition.
   */
  T transition(String name);

  /**
   * Adds an external transition to the current (pseudo) vertex builder.
   *
   * @return An interface to build the transition.
   */
  T transition();
}
