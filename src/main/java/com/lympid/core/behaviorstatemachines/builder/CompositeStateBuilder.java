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
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.impl.MutablePseudoState;
import com.lympid.core.behaviorstatemachines.impl.MutableRegion;
import com.lympid.core.behaviorstatemachines.impl.MutableState;
import java.util.Arrays;
import java.util.Collection;

/**
 * Builder for a composite state.
 *
 * @param <C> Type of the state machine context.
 *
 * @see State
 *
 * @author Fabien Renaud
 */
public final class CompositeStateBuilder<C> extends StateBuilder<CompositeStateBuilder<C>, C> implements CompositeStateEntry<CompositeStateBuilder<C>, C> {

  private final RegionBuilder<C> regionBuilder;
  private final ConnectionPointBuilder<C> connectionPointBuilder;

  /**
   * Instantiates a named composite state builder.
   *
   * @param name The name of the composite state.
   */
  public CompositeStateBuilder(final String name) {
    super(name);
    this.regionBuilder = new RegionBuilder<>();
    this.connectionPointBuilder = new ConnectionPointBuilder<>();
  }

  /**
   * Instantiates an unnamed composite state builder. The composite state is
   * unnamed.
   */
  public CompositeStateBuilder() {
    super();
    this.regionBuilder = new RegionBuilder<>();
    this.connectionPointBuilder = new ConnectionPointBuilder();
  }

  /**
   * Gets the unique region builder of the composite state.
   *
   * @return The unique region builder of the composite state.
   */
  public RegionBuilder<C> region() {
    return regionBuilder;
  }

  @Override
  public final CompositeStateEntry<CompositeStateBuilder<C>, C> entry(final StateBehavior<C> entry) {
    addEntry(entry);
    return this;
  }

  @Override
  public final CompositeStateEntry<CompositeStateBuilder<C>, C> entry(final Class<? extends StateBehavior<C>> entry) {
    addEntry(entry);
    return this;
  }

  @Override
  public final CompositeStateExit<CompositeStateBuilder<C>, C> exit(final StateBehavior<C> exit) {
    addExit(exit);
    return this;
  }

  @Override
  public final CompositeStateExit<CompositeStateBuilder<C>, C> exit(final Class<? extends StateBehavior<C>> exit) {
    addExit(exit);
    return this;
  }

  @Override
  public final CompositeStateTransitionSource<CompositeStateBuilder<C>, TransitionTrigger<CompositeStateBuilder<C>, C, CompletionEvent>, C, CompletionEvent> activity(final StateBehavior<C> activity) {
    setActivity(activity);
    return this;
  }

  @Override
  public final CompositeStateTransitionSource<CompositeStateBuilder<C>, TransitionTrigger<CompositeStateBuilder<C>, C, CompletionEvent>, C, CompletionEvent> activity(final Class<? extends StateBehavior<C>> activity) {
    setActivity(activity);
    return this;
  }

  /**
   * Adds an outgoing transition of the specified kind to the composite state.
   *
   * @param name The name of the transition.
   * @param kind The kind of the transition.
   * @return An interface to build the transition.
   */
  private TransitionTrigger<CompositeStateBuilder<C>, C, CompletionEvent> transition(final String name, final TransitionKind kind) {
    ErnalTransitionBuilder transition = new ErnalTransitionBuilder(kind, name, this);
    outgoing().add(transition);
    return transition;
  }

  @Override
  public TransitionTrigger<CompositeStateBuilder<C>, C, CompletionEvent> transition(final String name) {
    return transition(name, TransitionKind.EXTERNAL);
  }

  @Override
  public TransitionTrigger<CompositeStateBuilder<C>, C, CompletionEvent> transition() {
    return transition(null);
  }

  @Override
  public TransitionTrigger<CompositeStateBuilder<C>, C, CompletionEvent> localTransition(String name) {
    return transition(name, TransitionKind.LOCAL);
  }

  @Override
  public TransitionTrigger<CompositeStateBuilder<C>, C, CompletionEvent> localTransition() {
    return localTransition(null);
  }

  @Override
  public SelfTransitionTrigger<CompositeStateBuilder<C>, C, CompletionEvent> selfTransition(String name) {
    SelfTransitionBuilder transition = new SelfTransitionBuilder(TransitionKind.INTERNAL, name, this);
    outgoing().add(transition);
    return transition;
  }

  @Override
  public SelfTransitionTrigger<CompositeStateBuilder<C>, C, CompletionEvent> selfTransition() {
    return selfTransition(null);
  }

  /**
   * Gets the unique connection point builder of the composite state.
   * 
   * @return The unique connection point builder of the composite state.
   */
  public ConnectionPointBuilder<C> connectionPoint() {
    return connectionPointBuilder;
  }

  @Override
  MutableState vertex(VertexSet vertices) {
    final MutableState vertex = super.vertex(vertices);

    MutableRegion region = regionBuilder.region(vertices);
    vertex.setRegions(Arrays.asList(region));
    region.setState(vertex);

    Collection<MutablePseudoState> connectionPoints = connectionPointBuilder.vertex(vertices);
    /*
     * Connection points do NOT belong to regions. Only to the composite state.
     */
    for (MutablePseudoState cp : connectionPoints) {
      cp.setState(vertex);
      vertex.connectionPoint().add(cp);
    }

    return vertex;
  }

  @Override
  void connect(final VertexSet vertices) {
    super.connect(vertices);
    regionBuilder.connect(vertices);
    connectionPointBuilder.connect(vertices);
  }

  @Override
  public void accept(Visitor visitor) {
    super.accept(visitor);
    regionBuilder.accept(visitor);
    connectionPointBuilder.accept(visitor);
  }
}
