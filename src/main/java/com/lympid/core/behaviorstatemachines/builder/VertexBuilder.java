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
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.RegionOwner;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexUtils;
import com.lympid.core.behaviorstatemachines.impl.MutableRegion;
import com.lympid.core.behaviorstatemachines.impl.MutableTransition;
import com.lympid.core.behaviorstatemachines.impl.MutableVertex;
import com.lympid.core.behaviorstatemachines.validation.StandardValidator;
import com.lympid.core.common.UmlElement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides common functionality to build states, pseudo states and connect them
 * with transitions.
 *
 * @param <B> Type of the extended pseudo state or state builder class.
 * @param <T> Type of the target {@link MutableVertex} the extended class will
 * build.
 * @param <C> Type of the state machine context.
 *
 * @see Vertex
 * @see StateBuilder
 * @see PseudoStateBuilder
 * 
 * @author Fabien Renaud
 */
public abstract class VertexBuilder<B extends VertexBuilder, T extends MutableVertex, C> implements VertexBuilderReference<C>, Visitable {

  private String name;
  private String id;
  private RegionBuilder container;

  /**
   * Instantiates an abstract named vertex builder.
   *
   * @param name The name of the vertex.
   */
  VertexBuilder(final String name) {
    this.name = name;
  }

  /**
   * Instantiates an unnamed abstract vertex builder.
   */
  VertexBuilder() {
    this(null);
  }

  /**
   * Gets the {@link UmlElement} unique identifier of the vertex.
   *
   * @return The unique identifier of the vertex.
   */
  String getId() {
    return id;
  }

  /**
   * Sets the {@link UmlElement} id of the vertex.
   *
   * @param id A unique id across the whole vertex.
   */
  void setId(final String id) {
    this.id = id;
  }

  /**
   * Gets the {@link com.lympid.core.common.UmlElement} name of the vertex.
   *
   * @return The name of the vertex.
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Sets the {@link UmlElement} name of the vertex.
   *
   * @param name A name for the vertex.
   */
  void setName(final String name) {
    this.name = name;
  }

  /**
   * Sets the region builder this vertex belongs to.
   *
   * @param container The region builder this vertex belongs to.
   *
   * @see Vertex#container()
   */
  void setContainerBuilder(final RegionBuilder container) {
    this.container = container;
  }

  /**
   * Gets the region builder this vertex belongs to.
   *
   * @return The region builder this vertex belongs to.
   *
   * @see Vertex#container()
   */
  RegionBuilder getContainerBuilder() {
    return container;
  }

  /**
   * Builds all outgoing transitions for this vertex.
   *
   * @param vertices Collection of all the vertices in the state machine.
   */
  void connect(final VertexSet vertices) {
    MutableVertex source = vertices.getVertex(this);
    if (source == null) {
      throw new IllegalStateException("Vertex not found. Id: " + getId() + " Name: " + getName()); // TODO: custom exception
    }

    final List<Transition> transitions = new LinkedList<>();
    for (TransitionBuilder t : outgoing()) {
      MutableVertex target;
      Object originalTarget;

      if (t.getTargetAsString() == null) {
        originalTarget = t.getTargetAsVertexBuilder();
        if (t.getTargetAsVertexBuilder() instanceof PseudoStateBuilder) {
          PseudoStateBuilder pseudoStateBuilder = (PseudoStateBuilder) t.getTargetAsVertexBuilder();
          target = vertices.getVertex(pseudoStateBuilder);
        } else {
          target = vertices.getVertex(t.getTargetAsVertexBuilder());
        }
      } else {
        originalTarget = t.getTargetAsString();
        target = vertices.getByName(t.getTargetAsString());
      }

      if (target == null) {
        throw new RuntimeException("Unregistered target. source=" + source + " target=" + originalTarget); // TODO: custom exception
      }

      StandardValidator.validate(t.getKind(), source, target);

      MutableRegion region;
      try {
        region = (MutableRegion) determineRegion(vertices, source, target, t.getKind());
      } catch (CommonAncestorException ex) {
        throw new RuntimeException("No region found. source=" + source + " target=" + originalTarget, ex); // TODO: custom exception
      }

      MutableTransition transition = new MutableTransition(region, source, target, t.getGuard(), t.getEffect(), t.getKind(), t.getId());
      transition.setName(t.getName());
      transition.triggers().addAll(t.getTriggers());
      target.incoming().add(transition);
      region.transition().add(transition);

      transitions.add(transition);
    }
    source.setOutgoing(transitions);
  }

  /**
   * Determines the region the transition described by the {@code source},
   * {@code  target} and {@code kind} parameters belongs to.
   * 
   * <p>
   * TODO: explains what this do because it's not trivial
   * </p>
   * 
   * <p>
   * The specification - UML superstructure 2.4.1 - only provides the following
   * guidelines: "The owner of a transition is not explicitly constrained,
   * though the region must be owned directly or indirectly by the owning state
   * machine context. A suggested owner of a transition is the LCA of the source
   * and target vertices." (Chapter 15.3.14 Transition # Compound transitions)
   * </p>
   *
   * @param vertices Collection of all the vertices in the state machine.
   * @param source The source vertex of the transition.
   * @param target The target vertex of the transition.
   * @param kind The kind of the transition.
   * @return The region the the transition belongs to.
   */
  private Region determineRegion(final VertexSet vertices, final Vertex source, final Vertex target, final TransitionKind kind) throws CommonAncestorException {
    switch (kind) {
      case EXTERNAL:
        /*
         * In case the target is an exit point, it might be an external
         * transition coming from a substate of the composite state. If it is
         * the case, the region of the transition is the first region of the
         * composite state.
         */
        if (VertexUtils.exitPoint(target)) {
          PseudoState pseudoTarget = (PseudoState) target;
          State composite = pseudoTarget.state();
          if (composite == null) {
            /*
             * Case when the exit point is one of a state machine. The source of
             * the transition can not be the state machine itself and the
             * state machine is always the ancestor of the source.
             * There, the transition belongs to the first region of the state
             * machine.
             */
            return pseudoTarget.stateMachine().region().get(0);
          }

          if (source == composite) {
            return composite.container();
          }
          if (VertexUtils.ancestor(composite, source)) {
            return composite.region().get(0);
          }
        }

        Vertex nSource = VertexUtils.exitPoint(source)
          ? ((PseudoState) source).state()
          : source;
        Vertex nTarget = VertexUtils.connectionPoint(target)
          ? ((PseudoState) target).state()
          : target;
        return vertices.leastCommonAncestor(nSource, nTarget);
      case INTERNAL:
        return source.container();
      case LOCAL:
        /*
         * The standard validator -- and UML specfication -- guarantees that
         * either the source is a composite state or an entry point.
         */
        final RegionOwner sourceRegionOwner;
        if (source instanceof State) { // composite state
          sourceRegionOwner = (RegionOwner) source;
        } else {
          PseudoState ps = (PseudoState) source;
          sourceRegionOwner = ps.stateMachine() == null ? ps.state() : ps.stateMachine();
        }
        final List<? extends Region> sourceRegions = sourceRegionOwner.region();

        /*
         * The transition always belongs to the region of the source composite
         * state.
         * When multiple regions, the transition belongs to the region of the
         * target vertex.
         */
        if (sourceRegions.size() == 1) {
          return sourceRegions.get(0);
        }

        /*
         * Orthogonal state case
         */
        final State targetState = target instanceof State
          ? (State) target
          : (State) ((PseudoState) target).state();
        if (sourceRegionOwner == targetState) {
          throw new UnsupportedOperationException("Local transitions which source and target are the same orthogonal state are not supported yet.");
          // TODO: Should the transition belong to the orthogonal state itself (and not one of its regions)?
        }
        State orthogonalSource = (State) sourceRegionOwner;
        Region candidate = targetState.container();
        while (candidate.state() != orthogonalSource) {
          candidate = candidate.state().container();
        }
        return candidate;
      default:
        throw new UnsupportedOperationException("Unknown transition kind: " + kind);
    }
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
    for (TransitionBuilder t : outgoing()) {
      visitor.visit(t);
    }
  }

  /**
   * Builds the vertex and registers it as a vertex of the state machine.
   *
   * @param vertices Collection of all vertices as the state machine.
   * @return The fully built vertex, without its transitions.
   */
  abstract T vertex(VertexSet vertices);

  /**
   * Gets all the outgoing transitions of the vertex.
   *
   * @return A collection of all the outgoing transitions of the vertex.
   */
  abstract Collection<TransitionBuilder<B, C>> outgoing();
}
