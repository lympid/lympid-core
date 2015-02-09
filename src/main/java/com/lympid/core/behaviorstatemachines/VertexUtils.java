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
package com.lympid.core.behaviorstatemachines;

import com.lympid.core.common.UmlElement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Provides utility methods for {@link State}, {@link Vertex} and
 * {@link UmlElement}.
 *
 * @author Fabien Renaud
 */
public final class VertexUtils {

  /**
   * Private empty constructor for utility-like class.
   */
  private VertexUtils() {
  }

  /**
   * Checks whether s1 is an ancestor of v2.
   * Reference: UML superstructure 2.4.1, chapter 15.3.12 StateMachine, section
   * Additional Operations
   *
   * Note: A vertex is an ancestor of himself such as ancestor(s1, s1) is true.
   *
   * @param s1 The state that is expected to be the ancestor.
   * @param v2 The vertex that is expected to be the child.
   * @return true when s1 is an ancestor of v2..
   */
  public static boolean ancestor(final State s1, final Vertex v2) {
    if (v2 == null) {
      return false;
    }
    if (v2 == s1) {
      return true;
    }
    if (v2.container() == null) {
      return ancestor(s1, ((PseudoState) v2).state());
    }
    return ancestor(s1, v2.container().state());
  }

  /**
   * Checks all the given regions belong to the same orthogonal state and are
   * at the same level.
   *
   * @param regions A collection of regions.
   * @return true when all regions belongs to the same orthogonal state.
   */
  public static boolean allRegionsOfOrthogonalState(final Collection<Region> regions) {
    if (regions == null || regions.isEmpty()) {
      return false;
    }

    State s = null;
    for (Region r : regions) {
      if (s == null) {
        s = r.state();
        if (!orthogonalState(s)) {
          return false;
        }
      } else if (s != r.state()) {
        return false;
      }
    }
    return regions.size() == s.region().size();
  }

  /**
   * Collects all regions to which belong the source vertex of the incoming
   * transitions for the given vertex.
   *
   * @param v The vertex for which to inspect incoming transitions.
   * @return A set of distinct regions.
   */
  public static Set<Region> allSourceRegions(final Vertex v) {
    return allRegions(v.incoming(), Transition::source);
  }

  /**
   * Collects all regions to which belong the target vertex of the outgoing
   * transitions for the given vertex.
   *
   * @param v The vertex for which to inspect outgoing transitions.
   * @return A set of distinct regions.
   */
  public static Set<Region> allTargetRegions(final Vertex v) {
    return allRegions(v.outgoing(), Transition::target);
  }

  private static Set<Region> allRegions(final Collection<? extends Transition> transitions, final Function<Transition, Vertex> function) {
    Set<Region> regions = new HashSet<>();
    for (Transition t : transitions) {
      Vertex v = function.apply(t);
      if (v.container() != null) {
        regions.add(v.container());
      }
    }
    return regions;
  }

  /**
   * Check whether a vertex is state of any kind.
   *
   * @param v Any vertex. Can be null.
   * @return true when the vertex is a simple, composite, orthogonal or
   * submachine state.
   */
  public static boolean state(final Vertex v) {
    return v instanceof State;
  }

  /**
   * Check whether a vertex is simple state.
   *
   * @param v Any vertex. Can be null.
   * @return true when the vertex is a simple state.
   */
  public static boolean simpleState(final Vertex v) {
    return v instanceof State && ((State) v).isSimple();
  }

  /**
   * Check whether a vertex is composite state.
   *
   * @param v Any vertex. Can be null.
   * @return true when the vertex is a composite state.
   */
  public static boolean compositeState(final Vertex v) {
    return v instanceof State && ((State) v).isComposite();
  }

  /**
   * Check whether a vertex is orthogonal state.
   *
   * @param v Any vertex. Can be null.
   * @return true when the vertex is a orthogonal state.
   */
  public static boolean orthogonalState(final Vertex v) {
    return v instanceof State && ((State) v).isOrthogonal();
  }

  /**
   * Check whether a vertex is submachine state.
   *
   * @param v Any vertex. Can be null.
   * @return true when the vertex is a submachine state.
   */
  public static boolean subMachineState(final Vertex v) {
    return v instanceof State && ((State) v).isSubMachineState();
  }

  /**
   * Check whether a vertex is pseudo state of any kind.
   *
   * @param v Any vertex. Can be null.
   * @return true when the vertex is a pseudo state, regardless of its kind.
   */
  public static boolean pseudoState(final Vertex v) {
    return v instanceof PseudoState;
  }

  /**
   * Checks whether a vertex is a pseudo state of the given kind.
   *
   * @param v Any vertex. Can be null.
   * @param kind The expected pseudo state kind
   * @return true whenthe vertex is a pseudo state of the given kind, false
   * otherwise
   */
  public static boolean pseudoState(final Vertex v, final PseudoStateKind kind) {
    return v instanceof PseudoState && ((PseudoState) v).kind() == kind;
  }

  /**
   * Checks whether a vertex is a connection point, that is either an entry
   * point or an exit point.
   *
   * @param v Any vertex. Can be null.
   * @return true whenthe vertex is a connection point.
   */
  public static boolean connectionPoint(final Vertex v) {
    return entryPoint(v) || exitPoint(v);
  }

  /**
   * Checks whether a vertex is an initial pseudo state.
   *
   * @param v Any vertex. Can be null.
   * @return true whenthe vertex is an initial pseudo state.
   */
  public static boolean initial(final Vertex v) {
    return pseudoState(v, PseudoStateKind.INITIAL);
  }

  /**
   * Checks whether a vertex is a deep history pseudo state.
   *
   * @param v Any vertex. Can be null.
   * @return true whenthe vertex is a deep history pseudo state.
   */
  public static boolean deepHistory(final Vertex v) {
    return pseudoState(v, PseudoStateKind.DEEP_HISTORY);
  }

  /**
   * Checks whether a vertex is a shallow history pseudo state.
   *
   * @param v Any vertex. Can be null.
   * @return true whenthe vertex is a shallow history pseudo state, false
   * otherwise
   */
  public static boolean shallowHistory(final Vertex v) {
    return pseudoState(v, PseudoStateKind.SHALLOW_HISTORY);
  }

  /**
   * Checks whether a vertex is a join pseudo state.
   *
   * @param v Any vertex. Can be null.
   * @return true whenthe vertex is a join pseudo state.
   */
  public static boolean join(final Vertex v) {
    return pseudoState(v, PseudoStateKind.JOIN);
  }

  /**
   * Checks whether a vertex is a fork pseudo state.
   *
   * @param v Any vertex. Can be null.
   * @return true whenthe vertex is a fork pseudo state.
   */
  public static boolean fork(final Vertex v) {
    return pseudoState(v, PseudoStateKind.FORK);
  }

  /**
   * Checks whether a vertex is a junction pseudo state.
   *
   * @param v Any vertex. Can be null.
   * @return true whenthe vertex is a junction pseudo state.
   */
  public static boolean junction(final Vertex v) {
    return pseudoState(v, PseudoStateKind.JUNCTION);
  }

  /**
   * Checks whether a vertex is a choice pseudo state.
   *
   * @param v Any vertex. Can be null.
   * @return true whenthe vertex is a choice pseudo state.
   */
  public static boolean choice(final Vertex v) {
    return pseudoState(v, PseudoStateKind.CHOICE);
  }

  /**
   * Checks whether a vertex is an entry point.
   *
   * @param v Any vertex. Can be null.
   * @return true whenthe vertex is an entry point.
   */
  public static boolean entryPoint(final Vertex v) {
    return pseudoState(v, PseudoStateKind.ENTRY_POINT);
  }

  /**
   * Checks whether a vertex is an exit point.
   *
   * @param v Any vertex. Can be null.
   * @return true whenthe vertex is an exit point.
   */
  public static boolean exitPoint(final Vertex v) {
    return pseudoState(v, PseudoStateKind.EXIT_POINT);
  }

  /**
   * Checks whether a vertex is a terminate pseudo state.
   *
   * @param v Any vertex. Can be null.
   * @return true whenthe vertex is a terminate pseudo state.
   */
  public static boolean terminate(final Vertex v) {
    return pseudoState(v, PseudoStateKind.TERMINATE);
  }

  /**
   * Gets the name or the id prepended with dial symbol when the name is null.
   * Examples: #10, node, #1, A
   *
   * @param u The uml element for which to get the name or id.
   * @return The name of the element.
   */
  public static String nameOrId(UmlElement u) {
    return u.getName() == null ? "#" + u.getId() : u.getName();
  }
}
