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

import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.impl.MutableState;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Provides common functionality to build states.
 *
 * @param <B> Type of the extended class.
 * @param <C> Type of the state machine context.
 *
 * @see State
 * @see SimpleStateBuilder
 * @see CompositeStateBuilder
 * @see OrthogonalStateBuilder
 * @see SubStateMachineBuilder
 *
 * @author Fabien Renaud
 */
abstract class StateBuilder<B extends StateBuilder, C> extends VertexBuilder<B, MutableState, C> {

  private final Collection<TransitionBuilder<B, C>> outgoing = new LinkedList<>();
  private final List<Object> entry = new LinkedList<>();
  private final List<Object> exit = new LinkedList<>();
  private Object activity;

  /**
   * Instantiates an abstract named state builder.
   *
   * @param name The name of the state.
   */
  StateBuilder(final String name) {
    super(name);
  }

  /**
   * Instantiates an abstract unnamed state builder.
   */
  StateBuilder() {
    super();
  }

  /**
   * Adds an entry behavior to the state.
   *
   * @param entry The entry behavior to execute every time the state is entered.
   */
  void addEntry(final StateBehavior<C> entry) {
    this.entry.add(entry);
  }

  /**
   * Adds an entry behavior class to the state.
   *
   * @param entry The entry behavior class to execute every time the state is
   * entered.
   */
  void addEntry(final Class<? extends StateBehavior<C>> entry) {
    this.entry.add(entry);
  }

  /**
   * Adds an entry behavior to the state when the given condition is satisfied.
   *
   * @param entry The entry behavior to execute every time the state is entered.
   * @param condition The predicate that must be satisfied for adding the entry
   * behavior. The predicate evaluates a collection of objects that are a mixed
   * values of {@code StateBehavior} instances and types.
   */
  void addEntry(final StateBehavior<C> entry, final Predicate<Collection<Object>> condition) {
    if (condition.test(this.entry)) {
      addEntry(entry);
    }
  }

  /**
   * Adds an exit behavior to the state.
   *
   * @param exit The exit behavior to execute every time the state is exited.
   */
  void addExit(final StateBehavior<C> exit) {
    this.exit.add(exit);
  }

  /**
   * Adds an exit behavior to the state.
   *
   * @param exit The exit behavior to execute every time the state is exited.
   */
  void addExit(final Class<? extends StateBehavior<C>> exit) {
    this.exit.add(exit);
  }

  /**
   * Adds an exit behavior to the state when the given condition is satisfied.
   *
   * @param entry The exit behavior to execute every time the state is exited.
   * @param condition The predicate that must be satisfied for adding the exit
   * behavior. The predicate evaluates a collection of objects that are a mixed
   * values of {@code StateBehavior} instances and types.
   */
  void addExit(final StateBehavior<C> exit, final Predicate<Collection<Object>> condition) {
    if (condition.test(this.exit)) {
      addExit(exit);
    }
  }

  /**
   * Sets an activity for the state.
   *
   * @param activity The behavior to execute while this state is active.
   */
  void setActivity(final StateBehavior<C> activity) {
    this.activity = activity;
  }

  /**
   * Sets an activity for the state.
   *
   * @param activity The behavior to execute while this state is active.
   */
  void setActivity(final Class<? extends StateBehavior<C>> activity) {
    this.activity = activity;
  }

  @Override
  MutableState vertex(final VertexSet vertices) {
    MutableState vertex = new MutableState(getId());
    vertex.setName(getName());
    vertex.setEntry(BehaviorFactory.toBehaviorList(entry));
    vertex.setExit(BehaviorFactory.toBehaviorList(exit));
    vertex.setDoActivity(BehaviorFactory.toBehavior(activity));
    return vertices.put(this, vertex);
  }

  @Override
  final Collection<TransitionBuilder<B, C>> outgoing() {
    return outgoing;
  }
}
