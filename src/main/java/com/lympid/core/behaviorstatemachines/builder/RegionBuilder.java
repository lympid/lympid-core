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

import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.impl.MutableRegion;
import com.lympid.core.behaviorstatemachines.impl.MutableVertex;
import com.lympid.core.common.UmlElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides a builder for a
 * {@link com.lympid.core.behaviorstatemachines.Region}.
 *
 * @param <C> Type of the state machine context.
 * @author Fabien Renaud
 */
public class RegionBuilder<C> implements Visitable {

  private final String name;
  private String id;
  private final Set<VertexBuilder> registry = new HashSet<>();
  private final List<VertexBuilder> orderedRegistry = new LinkedList<>();
  private final Map<String, VertexBuilder> verticesByName = new HashMap<>();
  private boolean pseudoStateBuildersRegistered;

  /**
   * Instantiates a region builder with a name for the region.
   *
   * @param name The name of the region.
   */
  RegionBuilder(final String name) {
    this.name = name;
  }

  /**
   * Instantiates a region builder. The region is unnamed.
   */
  RegionBuilder() {
    this(null);
  }

  /**
   * Gets the {@link UmlElement} unique identifier of the region.
   *
   * @return The unique identifier of the region.
   */
  String getId() {
    return id;
  }

  /**
   * Sets the {@link UmlElement} id of the region.
   *
   * @param id A unique id across the whole region.
   */
  void setId(final String id) {
    this.id = id;
  }

  /**
   * Gets the {@link UmlElement} name of the region.
   *
   * @return The name of the region.
   */
  String getName() {
    return name;
  }

  /**
   * Registers a simple state for the region.
   *
   * @param builder The builder of the simple state.
   * @return The simple state builder given as argument.
   *
   * @see com.lympid.core.behaviorstatemachines.State
   */
  public SimpleStateBuilder<C> state(final SimpleStateBuilder<C> builder) {
    return vertex(builder);
  }

  /**
   * Registers a composite state for the region.
   *
   * @param builder The builder of the composite state.
   * @return The composite state builder given as argument.
   *
   * @see com.lympid.core.behaviorstatemachines.State
   */
  public CompositeStateBuilder<C> state(final CompositeStateBuilder<C> builder) {
    return vertex(builder);
  }

  /**
   * Registers an orthogonal state for the region.
   *
   * @param builder The builder of the orthogonal state.
   * @return The orthogonal state builder given as argument.
   *
   * @see com.lympid.core.behaviorstatemachines.State
   */
  public OrthogonalStateBuilder<C> state(final OrthogonalStateBuilder<C> builder) {
    return vertex(builder);
  }

  /**
   * Creates a submachine state of the given state machine for the region.
   *
   * @param builder The state machine builder to use for the submachine state.
   * @return The created submachine state builder. This is always a new
   * instance.
   *
   * @see com.lympid.core.behaviorstatemachines.State
   */
  public SubMachineStateBuilder<C> state(final StateMachineBuilder<C> builder) {
    return vertex(new SubMachineStateBuilder(builder));
  }

  /**
   * Creates a submachine state of the given state machine for the region and
   * names the submachine state as specified.
   *
   * @param builder The state machine builder to use for the submachine state.
   * @param name The name for the submachine state.
   * @return The created submachine state builder. This is always a new
   * instance.
   *
   * @see com.lympid.core.behaviorstatemachines.State
   */
  public SubMachineStateBuilder<C> state(final StateMachineBuilder<C> builder, final String name) {
    return vertex(new SubMachineStateBuilder(builder, name));
  }

  /**
   * Creates a simple state with the given name for the region.
   *
   * @param name The name of the simple state.
   * @return The created simple state builder. This is always a new instance.
   *
   * @see com.lympid.core.behaviorstatemachines.State
   */
  public SimpleStateBuilder<C> state(final String name) {
    VertexBuilder builder = verticesByName.get(name);
    if (builder == null) {
      return state(new SimpleStateBuilder<>(name));
    }
    if (builder instanceof SimpleStateBuilder) {
      return (SimpleStateBuilder<C>) builder;
    }
    throw new DuplicateVertexBuilderNameException(name, builder);
  }

  /**
   * Registers a vertex builder.
   *
   * @param <T> Type of the state builder.
   * @param builder The vertex builder to register.
   * @return The registered vertex builder.
   */
  private <T extends StateBuilder> T vertex(final T builder) {
    return vertex(builder.getName(), builder);
  }

  /**
   * Registers a vertex builder using a key if it is not registered.
   *
   * @param <T> Type of the vertex builder.
   * @param key The key to register the vertex builder with.
   * @param builder The vertex builder to register.
   * @return The registered vertex builder.
   */
  private <T extends VertexBuilder> T vertex(final String key, final T builder) {
    if (!registry.contains(builder)) {
      builder.setContainerBuilder(this);
      register(key, builder);
    }
    return builder;
  }

  /**
   * Gets the unnamed initial pseudo state builder of the region.
   *
   * @return The singleton instance of the unnamed initial pseudo state builder
   * for that region.
   *
   * @see PseudoStateKind#INITIAL
   */
  public InitialPseudoStateBuilder<C> initial() {
    return initial("__initial", false);
  }

  /**
   * Gets an initial pseudo state builder by name.
   *
   * @param name The name of the pseudo state.
   * @return The singleton instance of the named initial pseudo state builder
   * for that region.
   *
   * @see PseudoStateKind#INITIAL
   */
  public InitialPseudoStateBuilder<C> initial(final String name) {
    return initial(name, true);
  }

  /**
   * Gets an initial pseudo state builder by key.
   *
   * <p>
   * When the initial pseudo state builder does not exist, it is created and
   * registered in order to be reused for future calls. {@code key} is used as
   * name for the initial pseudo state builder when {@code asName} is true.</p>
   *
   * @param key The key for the initial pseudo state builder. May be the name of
   * the pseudo state.
   * @param asName Set to true to use the key as name for the initial pseudo
   * state builder.
   * @return A singleton instance of an initial pseudo state builder.
   *
   * @see PseudoStateKind#INITIAL
   */
  private InitialPseudoStateBuilder<C> initial(final String key, final boolean asName) {
    VertexBuilder builder = verticesByName.get(key);
    if (builder == null) {
      InitialPseudoStateBuilder<C> b = asName ? new InitialPseudoStateBuilder<>(key) : new InitialPseudoStateBuilder<>();
      return vertex(key, b);
    }
    if (builder instanceof InitialPseudoStateBuilder) {
      return (InitialPseudoStateBuilder<C>) builder;
    }
    throw new DuplicateVertexBuilderNameException(key, builder);
  }

  /**
   * Gets the unnamed final state builder of the region.
   *
   * @return The singleton instance of the unnamed final state builder for that
   * region.
   *
   * @see com.lympid.core.behaviorstatemachines.FinalState
   */
  public VertexBuilderReference<C> finalState() {
    return finalState("__finalState", false);
  }

  /**
   * Gets a final state builder by name.
   *
   * @param name The name of the final state.
   * @return The singleton instance of the named final state builder for that
   * region.
   *
   * @see com.lympid.core.behaviorstatemachines.FinalState
   */
  public VertexBuilderReference<C> finalState(final String name) {
    return finalState(name, true);
  }

  /**
   * Gets a final state builder by key.
   *
   * <p>
   * When the final state builder does not exist, it is created and registered
   * in order to be reused for future calls. {@code key} is used as name for the
   * final state builder when {@code asName} is true.</p>
   *
   * @param key The key for final state builder. May be the name of the final
   * state.
   * @param asName Set to true to use the key as name for the final state
   * builder.
   * @return A singleton instance of a final state builder.
   *
   * @see com.lympid.core.behaviorstatemachines.FinalState
   */
  private VertexBuilderReference<C> finalState(final String key, final boolean asName) {
    VertexBuilder builder = verticesByName.get(key);
    if (builder == null) {
      FinalStateBuilder b = asName ? new FinalStateBuilder(key) : new FinalStateBuilder();
      return vertex(key, b);
    }
    if (builder instanceof FinalStateBuilder) {
      return (FinalStateBuilder) builder;
    }
    throw new DuplicateVertexBuilderNameException(key, builder);
  }

  /**
   * Gets a choice pseudo state builder by name.
   *
   * @param name The name of the choice pseudo state.
   * @return The singleton instance of the named choice pseudo state builder
   * for that region.
   *
   * @see PseudoStateKind#CHOICE
   */
  public ChoiceBuilder<C> choice(final String name) {
    VertexBuilder builder = verticesByName.get(name);
    if (builder == null) {
      return vertex(name, new ChoiceBuilder<C>(name));
    }
    if (builder instanceof ChoiceBuilder) {
      return (ChoiceBuilder<C>) builder;
    }
    throw new DuplicateVertexBuilderNameException(name, builder);
  }

  /**
   * Gets a junction pseudo state builder by name.
   *
   * @param name The name of the junction pseudo state.
   * @return The singleton instance of the named junction pseudo state builder
   * for that region.
   *
   * @see PseudoStateKind#JUNCTION
   */
  public JunctionBuilder<C> junction(final String name) {
    VertexBuilder builder = verticesByName.get(name);
    if (builder == null) {
      return vertex(name, new JunctionBuilder<C>(name));
    }
    if (builder instanceof JunctionBuilder) {
      return (JunctionBuilder<C>) builder;
    }
    throw new DuplicateVertexBuilderNameException(name, builder);
  }

  /**
   * Gets a fork pseudo state builder by name.
   *
   * @param name The name of the fork pseudo state.
   * @return The singleton instance of the named fork pseudo state builder for
   * that region.
   *
   * @see PseudoStateKind#FORK
   */
  public ForkBuilder<C> fork(final String name) {
    VertexBuilder builder = verticesByName.get(name);
    if (builder == null) {
      return vertex(name, new ForkBuilder<C>(name));
    }
    if (builder instanceof ForkBuilder) {
      return (ForkBuilder<C>) builder;
    }
    throw new DuplicateVertexBuilderNameException(name, builder);
  }

  /**
   * Gets a join pseudo state builder by name.
   *
   * @param name The name of the join pseudo state.
   * @return The singleton instance of the named join pseudo state builder for
   * that region.
   *
   * @see PseudoStateKind#JOIN
   */
  public JoinBuilder<C> join(final String name) {
    VertexBuilder builder = verticesByName.get(name);
    if (builder == null) {
      return vertex(name, new JoinBuilder<C>(name));
    }
    if (builder instanceof JoinBuilder) {
      return (JoinBuilder<C>) builder;
    }
    throw new DuplicateVertexBuilderNameException(name, builder);
  }

  /**
   * Gets the unnamed terminate pseudo state builder of the region.
   *
   * @return The singleton instance of the unnamed terminate pseudo state
   * builder for that region.
   *
   * @see PseudoStateKind#TERMINATE
   */
  public VertexBuilderReference<C> terminate() {
    return terminate(name + ":__terminate", false);
  }

  /**
   * Gets a terminate pseudo state builder by name.
   *
   * @param name The name of the pseudo state.
   * @return The singleton instance of the named terminate pseudo state builder
   * for that region.
   *
   * @see PseudoStateKind#TERMINATE
   */
  public VertexBuilderReference<C> terminate(final String name) {
    return terminate(name, true);
  }

  /**
   * Gets a terminate pseudo state builder by key.
   *
   * <p>
   * When the terminate pseudo state builder does not exist, it is created and
   * registered in order to be reused for future calls. {@code key} is used as
   * name for the terminate pseudo state builder when {@code asName} is
   * true.</p>
   *
   * @param key The key for the terminate pseudo state builder. May be the name
   * of the pseudo state.
   * @param asName Set to true to use the key as name for the terminate pseudo
   * state builder.
   * @return A singleton instance of a terminate pseudo state builder.
   *
   * @see PseudoStateKind#TERMINATE
   */
  public VertexBuilderReference<C> terminate(final String key, final boolean asName) {
    VertexBuilder builder = verticesByName.get(key);
    if (builder == null) {
      TerminateBuilder<C> b = asName ? new TerminateBuilder<>(key) : new TerminateBuilder<>();
      return vertex(key, b);
    }
    if (builder instanceof TerminateBuilder) {
      return (TerminateBuilder<C>) builder;
    }
    throw new DuplicateVertexBuilderNameException(key, builder);
  }

  /**
   * Gets the unnamed deep history pseudo state builder of the region.
   *
   * @return The singleton instance of the unnamed deep history builder for that
   * region.
   *
   * @see PseudoStateKind#DEEP_HISTORY
   */
  public DeepHistoryBuilder<C> deepHistory() {
    return deepHistory(name + ":__deepHistory", false);
  }

  /**
   * Gets a deep history pseudo state builder by name.
   *
   * @param name The name of the pseudo state.
   * @return The singleton instance of the named deep history builder for that
   * region.
   *
   * @see PseudoStateKind#DEEP_HISTORY
   */
  public DeepHistoryBuilder<C> deepHistory(final String name) {
    return deepHistory(name, true);
  }

  /**
   * Gets a deep history pseudo state builder by key.
   *
   * <p>
   * When the deep history pseudo state builder does not exist, it is created
   * and registered in order to be reused for future calls. {@code key} is used
   * as name for the deep history pseudo state builder when {@code asName} is
   * true.</p>
   *
   * @param key The key for the deep history pseudo state builder. May be the
   * name of the pseudo state.
   * @param asName Set to true to use the key as name for the deep history
   * pseudo state builder.
   * @return A singleton instance of a deep history pseudo state builder.
   *
   * @see PseudoStateKind#DEEP_HISTORY
   */
  public DeepHistoryBuilder<C> deepHistory(final String key, final boolean asName) {
    VertexBuilder builder = verticesByName.get(key);
    if (builder == null) {
      DeepHistoryBuilder<C> b = asName ? new DeepHistoryBuilder<>(key) : new DeepHistoryBuilder<>();
      return vertex(key, b);
    }
    if (builder instanceof DeepHistoryBuilder) {
      return (DeepHistoryBuilder<C>) builder;
    }
    throw new DuplicateVertexBuilderNameException(key, builder);
  }

  /**
   * Gets the unnamed shallow history pseudo state builder of the region.
   *
   * @return The singleton instance of the unnamed shallow history builder for
   * that region.
   *
   * @see PseudoStateKind#SHALLOW_HISTORY
   */
  public ShallowHistoryBuilder<C> shallowHistory() {
    return shallowHistory(name + ":__shallowHistory", false);
  }

  /**
   * Gets a shallow history pseudo state builder by name.
   *
   * @param name The name of the pseudo state.
   * @return The singleton instance of the named shallow history builder for
   * that region.
   *
   * @see PseudoStateKind#SHALLOW_HISTORY
   */
  public ShallowHistoryBuilder<C> shallowHistory(final String name) {
    return shallowHistory(name, true);
  }

  /**
   * Gets a shallow history pseudo state builder by key.
   *
   * <p>
   * When the shallow history pseudo state builder does not exist, it is created
   * and registered in order to be reused for future calls. {@code key} is used
   * as name for the shallow history pseudo state builder when {@code asName} is
   * true.</p>
   *
   * @param key The key for the shallow history pseudo state builder. May be the
   * name of the pseudo state.
   * @param asName Set to true to use the key as name for the shallow history
   * pseudo state builder.
   * @return A singleton instance of a shallow history pseudo state builder.
   *
   * @see PseudoStateKind#SHALLOW_HISTORY
   */
  private ShallowHistoryBuilder<C> shallowHistory(final String key, final boolean asName) {
    VertexBuilder builder = verticesByName.get(key);
    if (builder == null) {
      ShallowHistoryBuilder<C> b = asName ? new ShallowHistoryBuilder<>(key) : new ShallowHistoryBuilder<>();
      return vertex(key, b);
    }
    if (builder instanceof ShallowHistoryBuilder) {
      return (ShallowHistoryBuilder<C>) builder;
    }
    throw new DuplicateVertexBuilderNameException(key, builder);
  }

  /**
   * Registers a vertex builder by key.
   *
   * <p>
   * This method always registers the builder even if it has already been
   * registered.</p>
   *
   * @param key The key to register the vertex builder with.
   * @param builder The vertex builder to register.
   */
  private void register(final String key, final VertexBuilder builder) {
    registry.add(builder);
    orderedRegistry.add(builder);
    if (key != null) {
      verticesByName.put(key, builder);
    }
  }

  /**
   * Builds all vertices regions and vertices this region builder holds. Nested
   * regions are processed.
   *
   * <p>
   * This method does not build transitions.</p>
   *
   * @param vertices Collection of all the vertices in the state machine.
   * @return A fully built region, without its transitions.
   */
  MutableRegion region(final VertexSet vertices) {
    final MutableRegion region = new MutableRegion(id);
    region.setName(name);

    /*
     * Some pseudo states may have been added with target() and needs to be
     * registered now.
     */
    registerPseudoStateBuilders();
    for (VertexBuilder b : orderedRegistry) {
      MutableVertex v = b.vertex(vertices);
      region.addVertex(v);
    }
    return region;
  }

  /**
   * Builds all transitions for the region and its children.
   *
   * @param vertices Collection of all the vertices in the state machine.
   */
  void connect(final VertexSet vertices) {
    /*
     * verticesById only contains the vertices the region owns.
     * It does NOT contain connection points (entry/exit points). Those are
     * handled in the overriden connect method in the composite state builder.
     */
    for (VertexBuilder b : orderedRegistry) {
      b.connect(vertices);
    }
  }

  /**
   * Registers all pseudo state builders if it hasn't been done yet.
   */
  private void registerPseudoStateBuilders() {
    if (!pseudoStateBuildersRegistered) {
      List<VertexBuilder> copy = new ArrayList<>(orderedRegistry);
      for (VertexBuilder b : copy) {
        registerPseudoStateBuilders(b);
      }
      pseudoStateBuildersRegistered = true;
    }
  }

  /**
   * Registers all pseudo states builders that can be found by following
   * combinatorial paths outgoing the vertex b.
   *
   * @param b The vertex of which to follow outgoing transition paths to
   * discover pseudo states.
   */
  private void registerPseudoStateBuilders(final VertexBuilder<?, ?, C> b) {
    for (TransitionBuilder<?, C> t : b.outgoing()) {
      Object target = t.getTargetAsVertexBuilder();

      /*
       * Only targets which are pseudo states interest us.
       */
      if (target instanceof PseudoStateBuilder) {
        PseudoStateBuilder pseudoStateBuilder = (PseudoStateBuilder) target;

        /*
         * Does this vertex belong to a region already? If not, it belongs to
         * the current one.
         */
        boolean ours;
        if (pseudoStateBuilder.getContainerBuilder() == null) {
          pseudoStateBuilder.setContainerBuilder(this);
          ours = true;
        } else {
          ours = pseudoStateBuilder.getContainerBuilder() == this;
        }

        /*
         * Registers the pseudo state if it belongs to the current region.
         *
         */
        if (ours && !registry.contains(pseudoStateBuilder)) {
          registry.add(pseudoStateBuilder);
          orderedRegistry.add(pseudoStateBuilder);
          /*
           * The target of a transition outgoing a pseudo state may be another
           * pseudo state.
           */
          registerPseudoStateBuilders(pseudoStateBuilder);
        }
      }
    }

  }

  @Override
  public void accept(Visitor visitor) {
    registerPseudoStateBuilders();

    visitor.visit(this);
    for (VertexBuilder b : orderedRegistry) { // assumes all vertices have ids when invoked.
      b.accept(visitor);
    }
  }
}
