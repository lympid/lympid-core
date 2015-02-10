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

import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An implementation for registering vertices and vertex builders and querying
 * vertices by name or associated vertex builder.
 *
 * <p>
 * The purpose of this class is to register all vertices and vertex builders
 * owned by a state machine in order to use those later for building transitions
 * by connecting vertex builders -- possibly belonging to different regions --
 * between them.</p>
 *
 * @author Fabien Renaud
 */
final class VertexSet {

  private final Map<Vertex, Vertex> byVertex = new HashMap<>();
  private final Map<VertexBuilderReference, Vertex> byBuilder = new HashMap<>();
  private final Map<String, Vertex> byName = new HashMap<>();
  private final List<State> subMachineStates = new ArrayList<>();

  /**
   * Associates the specified vertex builder with the specified vertex instance
   * in this collection.
   *
   * <p>
   * Typical utilization of this method assumes {@code vertex} is built from
   * {@code builder} but such assertion is never verified.</p>
   *
   * @param <T> Type of the {@link Vertex}.
   * @param builder The vertex builder.
   * @param vertex The vertex instance.
   * @return The given vertex.
   */
  <T extends Vertex> T put(final VertexBuilderReference builder, final T vertex) {
    add(vertex);
    byBuilder.put(builder, vertex);
    return vertex;
  }

  /**
   * Adds a vertex to this collection.
   *
   * @param <T> Type of the {@link Vertex}.
   * @param vertex The vertex instance.
   * @return The given vertex.
   */
  <T extends Vertex> T add(final T vertex) {
    return add(vertex.getName(), vertex);
  }

  /**
   * Associates the specified name with the specified vertex instance in this
   * collection.
   *
   * @param <T> Type of the {@link Vertex}.
   * @param vertex The vertex instance.
   * @return The given vertex.
   */
  <T extends Vertex> T add(final String name, final T vertex) {
    if (vertex.getId() == null) {
      throw new IllegalArgumentException("A vertex must have an id/");
    }

    Vertex v = byVertex.get(vertex);
    if (v == null) {
      byVertex.put(vertex, vertex);
      if (VertexUtils.subMachineState(vertex)) {
        subMachineStates.add((State) vertex);
      }
      if (name != null && byName.put(name, vertex) != null) {
        throw new UnsupportedOperationException("Two vertices have the same name. Although that should allowed, it is unsupported at the moment. Name: " + name);
      }
    } else if (vertex != v) {
      throw new DuplicateVertexNameException(name, v);
    }
    return vertex;
  }

  /**
   * Gets the {@link Vertex} associated with the given vertex builder.
   *
   * @param <T> Type of the {@code Vertex} implementation expected.
   * @param builder The vertex builder.
   * @return The one vertex associated with the given vertex builder or null if
   * such mapping has never been registered.
   */
  <T extends Vertex> T getVertex(final VertexBuilderReference builder) {
    T v = (T) byBuilder.get(builder);
    return v == null ? getByName(builder.getName()) : v;
  }

  /**
   * Gets the {@link Vertex} matching the given name.
   *
   * @param <T> Type of the {@code Vertex} implementation expected.
   * @param name The name of the vertex.
   * @return The one vertex that has a name matching the given one or null if
   * such vertex has never been registered.
   */
  <T extends Vertex> T getByName(final String name) {
    return (T) byName.get(name);
  }

  /**
   * Gets the deepest region which is the ancestor of two vertices.
   *
   * @param v1 A vertex.
   * @param v2 A vertex.
   * @return The deepest region which is the ancestor of both given vertices.
   * @throws CommonAncestorException If no common ancestor exists.
   */
  Region leastCommonAncestor(final Vertex v1, final Vertex v2) throws CommonAncestorException {
    if (v1 == null) {
      throw new CommonAncestorException(v1, v2);
    }
    if (v1.container() == null) {
      throw new CommonAncestorException(v1, v2);
    }
    if (v2 == null) {
      throw new CommonAncestorException(v1, v2);
    }
    if (v1.container().equals(v2.container())) {
      return v1.container();
    }

    Deque<Region> r1 = parentRegions(v1);
    Deque<Region> r2 = parentRegions(v2);
    Region leastCommonRegion = null;
    while (!r1.isEmpty() && !r2.isEmpty() && r1.peek().equals(r2.peek())) {
      leastCommonRegion = r1.pop();
      r2.pop();
    }
    return leastCommonRegion;
  }

  /**
   * Builds the stack of regions that is between the top level state machine and
   * the specified vertex. The top region in the stack is always the region of
   * the top level state machine; the last is always the region directly parent
   * to the given vertex.
   *
   * <p>
   * For instance, let a vertex V belong to a region R1, which is defined as the
   * region of the composite state S, which belongs to a region R0, which is the
   * region of the top level state machine. Then, the stack of regions this
   * method returns for V is [R0, R1], with R0 being the region at the top of
   * the stack.
   * </p>
   *
   * @param vertex The vertex for which to find its region path from the top
   * level state machine.
   * @return An ordered stack of regions. The top region in the stack is always
   * the region of the top level state machine; the last is always the region
   * directly parent to the given vertex.
   */
  private Deque<Region> parentRegions(final Vertex vertex) {
    Deque<Region> parents = new ArrayDeque<>();

    Region r = vertex.container();
    do {
      parents.push(r);
    } while ((r = parentRegion(r)) != null);

    return parents;
  }

  /**
   * Finds the direct parent region of the specified region. There are three
   * possibles cases:
   * <ul>
   * <li>the region belongs to a composite state; in which case this method
   * returns the region owning the composite state.</li>
   * <li>the region belongs to a submachine state; in which case this method
   * returns the region owning the submachine state.</li>
   * <li>the region belongs to the top level state machine; in which case this
   * method returns null.</li>
   * </ul>
   *
   * @param region A region.
   * @return The parent region of the given region or null if there is none.
   */
  private Region parentRegion(final Region region) {
    if (region.stateMachine() == null) {
      return region.state().container();
    }

    for (State s : subMachineStates) {
      if (s.subStateMachine().equals(region.stateMachine())) {
        return s.container();
      }
    }

    return null;
  }

  /**
   * Returns the number of registered vertices.
   *
   * @return The number of registered vertices.
   */
  int size() {
    return byVertex.size();
  }
}
