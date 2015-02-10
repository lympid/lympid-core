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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @param <C> Type of the state machine context.
 *
 * @see PseudoState
 * @see PseudoStateKind#ENTRY_POINT
 * @see PseudoStateKind#EXIT_POINT
 *
 * @author Fabien Renaud
 */
public class ConnectionPointBuilder<C> implements EntryPoint<C>, Visitable {

  private final List<EntryPointReference<?, C>> entry = new LinkedList<>();
  private final List<ExitPointBuilder<C>> exit = new LinkedList<>();

  @Override
  public EntryPoint<C> entryPoint(final EntryPointReference<?, C> entryPoint) {
    this.entry.add(entryPoint);
    return this;
  }

  @Override
  public ExitPoint<C> exitPoint(final ExitPointBuilder<C> exitPoint) {
    this.exit.add(exitPoint);
    return this;
  }

  @Override
  public ExitPoint<C> exitPoint(final String name) {
    this.exit.add(new ExitPointBuilder<>(name));
    return this;
  }

  Collection<MutablePseudoState> vertex(final VertexSet vertices) {
    Set<MutablePseudoState> connectionPoints = new HashSet<>();
    for (EntryPointReference e : entry) {
      connectionPoints.add(e.vertex(vertices));
    }
    for (ExitPointBuilder e : exit) {
      connectionPoints.add(e.vertex(vertices));
    }
    return connectionPoints;
  }

  /**
   * Builds all transitions outgoing entry and exit points.
   *
   * @param vertices Vertices of the world.
   */
  void connect(final VertexSet vertices) {
    for (EntryPointReference e : entry) {
      e.connect(vertices);
    }
    for (ExitPointBuilder e : exit) {
      e.connect(vertices);
    }
  }

  @Override
  public void accept(Visitor visitor) {
    for (EntryPointReference e : entry) {
      e.accept(visitor);
      visitor.visit(e);
    }
    for (ExitPointBuilder e : exit) {
      e.accept(visitor);
      visitor.visit(e);
    }
  }

}
