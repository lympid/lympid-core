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

import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.impl.MutablePseudoState;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Provides common functionality to build pseudo states.
 *
 * @param <B> Type of the extended {@link PseudoStateBuilder}.
 * @param <C> Type of the state machine context.
 *
 * @see PseudoState
 * @see InitialPseudoStateBuilder
 * @see ChoiceBuilder
 * @see JunctionBuilder
 * @see EntryPointBuilder
 * @see ExitPointBuilder
 * @see DeepHistoryBuilder
 * @see ShallowHistoryBuilder
 * @see ForkBuilder
 * @see JoinBuilder
 * @see TerminateBuilder
 * 
 * @author Fabien Renaud
 */
abstract class PseudoStateBuilder<B extends PseudoStateBuilder<?, C>, C> extends VertexBuilder<B, MutablePseudoState, C> {

  private final PseudoStateKind kind;
  private final Collection<TransitionBuilder<B, C>> outgoing = new LinkedList<>();

  /**
   * Instantiates a named pseudo state builder with the given pseudo state kind.
   *
   * @param kind The kind of the pseudo state.
   * @param name The name of the pseudo state.
   */
  PseudoStateBuilder(final PseudoStateKind kind, final String name) {
    super(name);
    this.kind = kind;
  }

  /**
   * Instantiates an unnamed pseudo state builder with the given pseudo state
   * kind.
   *
   * @param kind The kind of the pseudo state.
   */
  PseudoStateBuilder(final PseudoStateKind kind) {
    super();
    this.kind = kind;
  }

  @Override
  final Collection<TransitionBuilder<B, C>> outgoing() {
    return outgoing;
  }

  @Override
  MutablePseudoState vertex(final VertexSet vertices) {
    MutablePseudoState vertex = new MutablePseudoState(getId(), kind);
    vertex.setName(getName());
    return vertices.put(this, vertex);
  }
}
