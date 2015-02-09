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

/**
 * PseudostateKind is an enumeration of the following literal values: initial,
 * deepHistory, shallowHistory, join, fork, junction, choice, entryPoint,
 * exitPoint, terminate
 *
 * @author Fabien Renaud
 */
public enum PseudoStateKind {

  /**
   * An initial pseudostate represents a default vertex that is the source for a
   * single transition to the default state of a 542 UML Superstructure
   * Specification, v2.2 composite state. There can be at most one initial
   * vertex in a region. The outgoing transition from the initial vertex may
   * have a behavior, but not a trigger or guard.
   */
  INITIAL,
  /**
   * deepHistory represents the most recent active configuration of the
   * composite state that directly contains this pseudostate (e.g., the state
   * configuration that was active when the composite state was last exited). A
   * composite state can have at most one deep history vertex. At most one
   * transition may originate from the history connector to the default deep
   * history state. This transition is taken in case the composite state had
   * never been active before. Entry actions of states entered on the path to
   * the state represented by a deep history are performed.
   */
  DEEP_HISTORY,
  /**
   * shallowHistory represents the most recent active substate of its containing
   * state (but not the substates of that substate). A composite state can have
   * at most one shallow history vertex. A transition coming into the shallow
   * history vertex is equivalent to a transition coming into the most recent
   * active substate of a state. At most one transition may originate from the
   * history connector to the default shallow history state. This transition is
   * taken in case the composite state had never been active before. Entry
   * actions of states entered on the path to the state represented by a shallow
   * history are performed.
   */
  SHALLOW_HISTORY,
  /**
   * join vertices serve to merge several transitions emanating from source
   * vertices in different orthogonal regions. The transitions entering a join
   * vertex cannot have guards or triggers.
   */
  JOIN,
  /**
   * fork vertices serve to split an incoming transition into two or more
   * transitions terminating on orthogonal target vertices (i.e., vertices in
   * different regions of a composite state). The segments outgoing from a fork
   * vertex must not have guards or triggers.
   */
  FORK,
  /**
   * junction vertices are semantic-free vertices that are used to chain
   * together multiple transitions. They are used to construct compound
   * transition paths between states. For example, a junction can be used to
   * converge multiple incoming transitions into a single outgoing transition
   * representing a shared transition path (this is known as a merge).
   * Conversely, they can be used to split an incoming transition into multiple
   * outgoing transition segments with different guard conditions. This realizes
   * a static conditional branch. (In the latter case, outgoing transitions
   * whose guard conditions evaluate to false are disabled. A predefined guard
   * denoted “else” may be defined for at most one outgoing transition. This
   * transition is enabled if all the guards labeling the other transitions are
   * false.) Static conditional branches are distinct from dynamic conditional
   * branches that are realized by choice vertices (described below).
   */
  JUNCTION,
  /**
   * choice vertices which, when reached, result in the dynamic evaluation of
   * the guards of the triggers of its outgoing transitions. This realizes a
   * dynamic conditional branch. It allows splitting of transitions into
   * multiple outgoing paths such that the decision on which path to take may be
   * a function of the results of prior actions performed in the same runto-
   * completion step. If more than one of the guards evaluates to true, an
   * arbitrary one is selected. If none of the guards evaluates to true, then
   * the model is considered ill-formed. (To avoid this, it is recommended to
   * define one outgoing transition with the predefined “else” guard for every
   * choice vertex.) Choice vertices should be distinguished from static branch
   * points that are based on junction points (described above).
   */
  CHOICE,
  /**
   * An entry point pseudostate is an entry point of a state machine or
   * composite state. In each region of the state machine or composite state it
   * has a single transition to a vertex within the same region.
   */
  ENTRY_POINT,
  /**
   * An exit point pseudostate is an exit point of a state machine or composite
   * state. Entering an exit point within any region of the composite state or
   * state machine referenced by a submachine state implies the exit of this
   * composite state or submachine state and the triggering
   */
  EXIT_POINT,
  /**
   * Entering a terminate pseudostate implies that the execution of this state
   * machine by means of its context object is terminated. The state machine
   * does not exit any states nor does it perform any exit actions other than
   * those associated with the transition leading to the terminate pseudostate.
   * Entering a terminate pseudostate is equivalent to invoking a
   * DestroyObjectAction.
   */
  TERMINATE
}
