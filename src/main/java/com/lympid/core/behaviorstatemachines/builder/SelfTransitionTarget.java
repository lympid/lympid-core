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
 * Provides an interface to build an internal (aka self) transition outgoing a
 * (pseudo) state. The transition can be defined with a target only.
 *
 * @param <V> {@code StateBuilder} type which is the source of the transition.
 * @author Fabien Renaud
 */
public interface SelfTransitionTarget<V extends StateBuilder> extends TransitionStep<V> {

  /**
   * Sets the target of this transition by name.
   *
   * @return The source of the transition.
   */
  V target();

}
