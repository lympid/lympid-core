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
 * Provides an interface to build an external or local transition outgoing a
 * (pseudo) state. The transition can be defined with a target only.
 *
 * @param <V> {@code VertexBuilder} type which is the source of the transition.
 * @param <C> Type of the state machine context.
 *
 * @author Fabien Renaud
 */
public interface TransitionTarget<V extends VertexBuilder<?, ?, C>, C> extends TransitionStep<V> {

  /**
   * Sets the target of this transition by name.
   *
   * @param name The name of the (pseudo) state to target.
   * @return The source of the transition.
   */
  V target(String name);

  /**
   * Sets the target of this transition to a {@code VertexBuilderReference}.
   *
   * @param reference A {@code VertexBuilderReference} that represents the
   * (pseudo) state to target.
   * @return The source of the transition.
   */
  V target(VertexBuilderReference<C> reference);

}
