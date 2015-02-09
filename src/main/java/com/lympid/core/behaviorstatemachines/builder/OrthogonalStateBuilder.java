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
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.impl.MutablePseudoState;
import com.lympid.core.behaviorstatemachines.impl.MutableRegion;
import com.lympid.core.behaviorstatemachines.impl.MutableState;
import com.lympid.core.behaviorstatemachines.validation.StateConstraintException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @param <C> Type of the state machine context.
 * 
 * @see State
 * 
 * @author Fabien Renaud
 */
public final class OrthogonalStateBuilder<C> extends StateBuilder<OrthogonalStateBuilder<C>, C> implements CompositeStateEntry<OrthogonalStateBuilder<C>, C> {

  private final Map<String, RegionBuilder<C>> regionBuilders = new HashMap<>();
  private final ConnectionPointBuilder<C> connectionPointBuilder;

  public OrthogonalStateBuilder(final String name) {
    super(name);
    this.connectionPointBuilder = new ConnectionPointBuilder<>();
  }

  public OrthogonalStateBuilder() {
    super();
    this.connectionPointBuilder = new ConnectionPointBuilder<>();
  }

  public RegionBuilder<C> region(String name) {
    RegionBuilder<C> builder = regionBuilders.get(name);
    if (builder == null) {
      builder = new RegionBuilder<>(name);
      regionBuilders.put(name, builder);
    }
    return builder;
  }

  @Override
  public final CompositeStateEntry<OrthogonalStateBuilder<C>, C> entry(final StateBehavior<C> entry) {
    addEntry(entry);
    return this;
  }

  @Override
  public final CompositeStateEntry<OrthogonalStateBuilder<C>, C> entry(final Class<? extends StateBehavior<C>> entry) {
    addEntry(entry);
    return this;
  }

  @Override
  public final CompositeStateExit<OrthogonalStateBuilder<C>, C> exit(final StateBehavior<C> exit) {
    addExit(exit);
    return this;
  }

  @Override
  public final CompositeStateExit<OrthogonalStateBuilder<C>, C> exit(final Class<? extends StateBehavior<C>> exit) {
    addExit(exit);
    return this;
  }

  @Override
  public final CompositeStateTransitionSource<OrthogonalStateBuilder<C>, TransitionTrigger<OrthogonalStateBuilder<C>, C, CompletionEvent>, C, CompletionEvent> activity(final StateBehavior<C> activity) {
    setActivity(activity);
    return this;
  }

  @Override
  public final CompositeStateTransitionSource<OrthogonalStateBuilder<C>, TransitionTrigger<OrthogonalStateBuilder<C>, C, CompletionEvent>, C, CompletionEvent> activity(final Class<? extends StateBehavior<C>> activity) {
    setActivity(activity);
    return this;
  }

  private TransitionTrigger<OrthogonalStateBuilder<C>, C, CompletionEvent> transition(final String name, final TransitionKind kind) {
    ErnalTransitionBuilder transition = new ErnalTransitionBuilder(kind, name, this);
    outgoing().add(transition);
    return transition;
  }

  @Override
  public TransitionTrigger<OrthogonalStateBuilder<C>, C, CompletionEvent> transition(final String name) {
    return transition(name, TransitionKind.EXTERNAL);
  }

  @Override
  public TransitionTrigger<OrthogonalStateBuilder<C>, C, CompletionEvent> transition() {
    return transition(null);
  }

  @Override
  public TransitionTrigger<OrthogonalStateBuilder<C>, C, CompletionEvent> localTransition(String name) {
    return transition(name, TransitionKind.LOCAL);
  }

  @Override
  public TransitionTrigger<OrthogonalStateBuilder<C>, C, CompletionEvent> localTransition() {
    return localTransition(null);
  }

  @Override
  public SelfTransitionTrigger<OrthogonalStateBuilder<C>, C, CompletionEvent> selfTransition(String name) {
    SelfTransitionBuilder transition = new SelfTransitionBuilder(TransitionKind.INTERNAL, name, this);
    outgoing().add(transition);
    return transition;
  }

  @Override
  public SelfTransitionTrigger<OrthogonalStateBuilder<C>, C, CompletionEvent> selfTransition() {
    return selfTransition(null);
  }

  public ConnectionPointBuilder<C> connectionPoint() {
    return connectionPointBuilder;
  }

  @Override
  MutableState vertex(final VertexSet vertices) {
    final MutableState vertex = super.vertex(vertices);
    
    if (regionBuilders.size() < 2) {
      throw new StateConstraintException(vertex, "An orthogonal state is a composite state with at least 2 regions.");
    }

    List<Region> regions = new ArrayList<>(regionBuilders.size());
    for (RegionBuilder b : regionBuilders.values()) {
      MutableRegion region = b.region(vertices);
      regions.add(region);
      region.setState(vertex);
    }
    vertex.setRegions(regions);

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
    for (RegionBuilder b : regionBuilders.values()) {
      b.connect(vertices);
    }
    connectionPointBuilder.connect(vertices);
  }

  @Override
  public void accept(Visitor visitor) {
    super.accept(visitor);
    for (RegionBuilder b : regionBuilders.values()) {
      b.accept(visitor);
    }
    connectionPointBuilder.accept(visitor);
  }
}
