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
package com.lympid.core.behaviorstatemachines.validation;

import com.lympid.core.behaviorstatemachines.ConnectionPointReference;
import com.lympid.core.behaviorstatemachines.FinalState;
import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexUtils;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Fabien Renaud
 */
public final class StandardValidator {

  private StandardValidator() {
  }

  public static void validate(final ConnectionPointReference connectionPointReference) {
    /*
     * [1] The entry Pseudostates must be Pseudostates with kind entryPoint.
     */
    for (PseudoState s : connectionPointReference.entry()) {
      if (s.kind() != PseudoStateKind.ENTRY_POINT) {
        throw new ConnectionPointReferenceConstraintException("The entry Pseudostates must be Pseudostates with kind entryPoint.", connectionPointReference, s.kind());
      }
    }

    /*
     * [2] The exit Pseudostates must be Pseudostates with kind exitPoint.
     */
    for (PseudoState s : connectionPointReference.exit()) {
      if (s.kind() != PseudoStateKind.EXIT_POINT) {
        throw new ConnectionPointReferenceConstraintException("The exit Pseudostates must be Pseudostates with kind exitPoint.", connectionPointReference, s.kind());
      }
    }
  }

  public static void validate(final FinalState finalState) {
    /*
     * [1] A final state cannot have any outgoing transitions.
     */
    if (!finalState.outgoing().isEmpty()) {
      throw new FinalStateConstraintException(finalState, "A final state cannot have any outgoing transitions.");
    }

    /*
     * [2] A final state cannot have regions.
     */
    if (!finalState.region().isEmpty()) {
      throw new FinalStateConstraintException(finalState, "A final state cannot have regions.");
    }

    /*
     * [3] A final state cannot reference a submachine.
     */
    if (finalState.subStateMachine() != null) {
      throw new FinalStateConstraintException(finalState, "A final state cannot reference a submachine.");
    }

    /*
     * [4] A final state has no entry behavior.
     */
    if (!finalState.entry().isEmpty()) {
      throw new FinalStateConstraintException(finalState, "A final state cannot have an entry behavior.");
    }

    /*
     * [5] A final state has no exit behavior.
     */
    if (!finalState.exit().isEmpty()) {
      throw new FinalStateConstraintException(finalState, "A final state cannot have an exit behavior.");
    }

    /*
     * [6] A final state has no state (doActivity) behavior.
     */
    if (finalState.doActivity() != null) {
      throw new FinalStateConstraintException(finalState, "A final state cannot have a state (doActivity) behavior.");
    }
  }

  public static void validate(final PseudoState pseudoState) {
    switch (pseudoState.kind()) {
      case INITIAL:
        /*
         * [1] An initial vertex can have at most one outgoing transition.
         */
        if (pseudoState.outgoing().size() > 1) {
          throw new PseudoStateConstraintException(pseudoState, "An initial vertex can have at most one outgoing transition.");
        }

        /*
         * [9] The outgoing transition from an initial vertex may have a
         * behavior, but not a trigger or guard.
         */
        for (Transition o : pseudoState.outgoing()) {
          if (!hasNoGuard(o) || !o.triggers().isEmpty()) {
            throw new PseudoStateConstraintException(pseudoState, "The outgoing transition from an initial vertex may have a behavior, but not a trigger or guard.");
          }
        }
        break;
      case DEEP_HISTORY:
      case SHALLOW_HISTORY:
        /*
         * [2] History vertices can have at most one outgoing transition.
         */
        if (pseudoState.outgoing().size() > 1) {
          throw new PseudoStateConstraintException(pseudoState, "History vertices can have at most one outgoing transition.");
        }
        break;
      case JOIN:
        /*
         * [3] In a complete statemachine, a join vertex must have at least two
         * incoming transitions and exactly one outgoing transition.
         */
        if (pseudoState.outgoing().size() != 1 || pseudoState.incoming().size() < 2) {
          throw new PseudoStateConstraintException(pseudoState, "A join vertex must have at least two incoming transitions and exactly one outgoing transition.");
        }

        /*
         * [4] All transitions incoming a join vertex must originate in
         * different regions of an orthogonal state.
         */
        Set<Region> sourceRegions = VertexUtils.allSourceRegions(pseudoState);
        if (sourceRegions.size() != pseudoState.incoming().size() || !VertexUtils.allRegionsOfOrthogonalState(sourceRegions)) {
          throw new PseudoStateConstraintException(pseudoState, "All transitions incoming a join vertex must originate in different regions of an orthogonal state.");
        }
        break;
      case FORK:
        /*
         * [5] In a complete statemachine, a fork vertex must have at least two
         * outgoing transitions and exactly one incoming transition.
         */
        if (pseudoState.outgoing().size() < 2 || pseudoState.incoming().size() != 1) {
          throw new PseudoStateConstraintException(pseudoState, "A fork vertex must have at least two outgoing transitions and exactly one incoming transition.");
        }

        /*
         * [6] All transitions outgoing a fork vertex must target states in
         * different regions of an orthogonal state.
         */
        Set<Region> targetRegions = VertexUtils.allTargetRegions(pseudoState);
        if (targetRegions.size() != pseudoState.outgoing().size() || !VertexUtils.allRegionsOfOrthogonalState(targetRegions)) {
          throw new PseudoStateConstraintException(pseudoState, "All transitions outgoing a fork vertex must target states in different regions of an orthogonal state.");
        }
        break;
      case JUNCTION:
        /*
         * [7] In a complete statemachine, a junction vertex must have at least
         * one incoming and one outgoing transition.
         */
        if (pseudoState.outgoing().size() < 1 || pseudoState.incoming().size() < 1) {
          throw new PseudoStateConstraintException(pseudoState, "A junction vertex must have at least one incoming and one outgoing transition.");
        }
        break;
      case CHOICE:
        /*
         * [8] In a complete statemachine, a choice vertex must have at least
         * one incoming and one outgoing transition.
         */
        if (pseudoState.outgoing().size() < 1 || pseudoState.incoming().size() < 1) {
          throw new PseudoStateConstraintException(pseudoState, "A choice vertex must have at least one incoming and one outgoing transition.");
        }
        break;
      case ENTRY_POINT:
        /*
         * From 15.3.8 Pseudostate
         *
         * An entry point pseudostate is an entry point of a state machine or
         * composite state. In each region of the state machine or composite
         * state it has at most a single transition to a vertex within the same
         * region.
         */
        // TODO
        break;
      case EXIT_POINT:
        /*
         * From 15.3.8 Pseudostate
         *
         * An exit point pseudostate is an exit point of a state machine or
         * composite state. Entering an exit point within any region of the
         * composite state or state machine referenced by a submachine state
         * implies the exit of this composite state or submachine state and the
         * triggering of the transition that has this exit point as source in
         * the state machine enclosing the submachine or composite state.
         *
         * Therefore, an exit point always has at least one outgoing transition.
         */
        // TODO
        break;
    }
  }

  public static void validate(final Region region) {
    /*
     * [1] A region can have at most one initial vertex.
     * [2] A region can have at most one deep history vertex.
     * [3] A region can have at most one shallow history vertex.
     */
    int countOfInitialVertex = 0;
    int countOfDeepHistoryVertex = 0;
    int countOfShallowHistoryVertex = 0;
    for (Vertex v : region.subVertex()) {
      if (v instanceof PseudoState) {
        PseudoState pseudoState = (PseudoState) v;
        switch (pseudoState.kind()) {
          case INITIAL:
            countOfInitialVertex++;
            break;
          case DEEP_HISTORY:
            countOfDeepHistoryVertex++;
            break;
          case SHALLOW_HISTORY:
            countOfShallowHistoryVertex++;
            break;
        }
      }
    }
    if (countOfInitialVertex > 1) {
      throw new RegionConstraintException(region, "A region can have at most one initial vertex.");
    }
    if (countOfDeepHistoryVertex > 1) {
      throw new RegionConstraintException(region, "A region can have at most one deep history vertex.");
    }
    if (countOfShallowHistoryVertex > 1) {
      throw new RegionConstraintException(region, "A region can have at most one shallow history vertex.");
    }

    /*
     * [4] If a Region is owned by a StateMachine, then it cannot also be owned
     * by a State and vice versa.
     */
    if (region.stateMachine() != null && region.state() != null) {
      throw new RegionConstraintException(region, "A region cannot be owned by both a State and a StateMachine.");
    }

    /*
     * [5] The redefinition context of a region is the nearest containing
     * statemachine.
     */
  }

  public static void validate(final State state) {
    /*
     * [1] Only submachine states can have connection point references.
     */
    if (state.connection() != null && !state.isSubMachineState()) {
      throw new StateConstraintException(state, "Only submachine states can have connection point references.");
    }

    /*
     * [2] The connection point references used as destinations/sources of
     * transitions associated with a submachine state must be defined as
     * entry/exit points in the submachine state machine.
     */
    if (state.isSubMachineState() && state.connection() != null) {
      for (PseudoState p : state.connection().entry()) {
        if (p.stateMachine() != state.subStateMachine()) {
          throw new StateConstraintException(state, "The connection point references used as destinations/sources of transitions associated with a submachine state must be defined as entry/exit points in the submachine state machine.");
        }
      }
      for (PseudoState p : state.connection().exit()) {
        if (p.stateMachine() != state.subStateMachine()) {
          throw new StateConstraintException(state, "The connection point references used as destinations/sources of transitions associated with a submachine state must be defined as entry/exit points in the submachine state machine.");
        }
      }
    }

    /*
     * [3] A state is not allowed to have both a submachine and regions.
     * This is verified in ImplementationValidator.
     */

    /*
     * [4] A simple state is a state without any regions.
     */
    if (state.isSimple() && !state.region().isEmpty()) {
      throw new StateConstraintException(state, "A simple state is a state without any regions.");
    }

    /*
     * [5] A composite state is a state with at least one region.
     */
    if (state.isComposite() && state.region().isEmpty()) {
      throw new StateConstraintException(state, "A composite state is a state with at least one region.");
    }

    /*
     * [6] An orthogonal state is a composite state with at least 2 regions.
     */
    if (state.isOrthogonal() && state.region().size() < 2) {
      throw new StateConstraintException(state, "An orthogonal state is a composite state with at least 2 regions.");
    }

    /*
     * [7] Only submachine states can have a reference statemachine.
     */
    if (state.subStateMachine() != null && !state.isSubMachineState()) {
      throw new StateConstraintException(state, "Only submachine states can have a reference statemachine.");
    }

    /*
     * [8] The redefinition context of a state is the nearest containing
     * statemachine.
     */
    // TODO
    /*
     * [9] Only composite states can have entry or exit pseudostates defined.
     */
    if (!state.connectionPoint().isEmpty() && !state.isComposite()) {
      throw new StateConstraintException(state, "Only composite states can have entry or exit pseudostates defined.");
    }

    /*
     * 15.3.11 State: [E]ntry or exit Pseudostates [...] must have different
     * names.
     */
    if (!state.connectionPoint().isEmpty()) {
      Set<String> connectionPointNames = new HashSet<>();
      for (PseudoState cp : state.connectionPoint()) {
        if (cp.getName() == null) {
          throw new StateConstraintException(state, "Connection points must have a name defined. Null is not accepted.");
        }
        if (!connectionPointNames.add(cp.getName())) {
          throw new StateConstraintException(state, "There is already a connecption point defined for name: " + cp.getName());
        }
      }
    }

    /*
     * [10] Only entry or exit pseudostates can serve as connection points.
     */
    for (PseudoState cp : state.connectionPoint()) {
      if (cp.kind() != PseudoStateKind.ENTRY_POINT && cp.kind() != PseudoStateKind.EXIT_POINT) {
        throw new StateConstraintException(state, "Only entry or exit pseudostates can serve as connection points. Found: " + cp.kind());
      }
    }
  }

  public static void validate(final StateMachine stateMachine) {
    /*
     * [1] The classifier context of a state machine cannot be an interface.
     */
    // TODO

    /*
     * [2] The context classifier of the method state machine of a behavioral
     * feature must be the classifier that owns the behavioral feature.
     */
    // TODO
    /*
     * [3] The connection points of a state machine are pseudostates of kind
     * entry point or exit point.
     */
    for (PseudoState cp : stateMachine.connectionPoint()) {
      if (cp.kind() != PseudoStateKind.ENTRY_POINT && cp.kind() != PseudoStateKind.EXIT_POINT) {
        throw new StateMachineConstraintException(stateMachine, "The connection points of a state machine must be pseudostates of kind entry point or exit point.");
      }
    }

    /*
     * [4] A state machine as the method for a behavioral feature cannot have
     * entry/exit connection points.
     */
    // TODO
  }

  /**
   * Reference: UML superstructure 2.4.1, chapter 15.3.14 Transition, section
   * Constraints.
   *
   * @param transition The transition to validate
   */
  public static void validate(final Transition transition) {
    if (transition.source() instanceof PseudoState) {
      PseudoState pseudoState = (PseudoState) transition.source();
      if (pseudoState.kind() == PseudoStateKind.FORK) {
        /*
         * [1] A fork segment must not have guards or triggers.
         */
        if (!hasNoGuard(transition) || !transition.triggers().isEmpty()) {
          throw new TransitionConstraintException(transition, "A fork segment must not have guards or triggers.");
        }

        /*
         * [3] A fork segment must always target a state.
         */
        if (!(transition.target() instanceof State)) {
          throw new TransitionConstraintException(transition, "A fork segment must always target a state.");
        }
      }

      /*
       * [5] Transitions outgoing pseudostates may not have a trigger (except
       * for those coming out of the initial pseudostate).
       * [6] An initial transition at the topmost level (region of a
       * statemachine) either has no trigger or it has a trigger with the
       * stereotype “create.” Note: Stereotype "create" is not supported.
       * Therefore, initial transitions have no trigger as well.
       */
      if (!transition.triggers().isEmpty()) {
        throw new TransitionConstraintException(transition, "A transition outgoing a pseudo state must not have triggers.");
      }
    }

    if (transition.target() instanceof PseudoState) {
      PseudoState pseudoState = (PseudoState) transition.target();
      if (pseudoState.kind() == PseudoStateKind.JOIN) {
        /*
         * [2] A join segment must not have guards or triggers.
         */
        if (!hasNoGuard(transition) || !transition.triggers().isEmpty()) {
          throw new TransitionConstraintException(transition, "A join segment must not have guards or triggers.");
        }

        /*
         * [4] A join segment must always originate from a state.
         */
        if (!(transition.source() instanceof State)) {
          throw new TransitionConstraintException(transition, "A join segment must always originate from a state.");
        }
      }
    }

    /*
     * [7] In case of more than one trigger, the signatures of these must be
     * compatible in case the parameters of the signal are assigned to local
     * variables/attributes.
     */
     // TODO
    /*
     * [8] The redefinition context of a transition is the nearest containing
     * statemachine.
     */
    // TODO
  }

  /**
   * Reference: UML superstructure 2.4.1, chapter 15.3.15 TransitionKind,
   * section Constraints.
   *
   * @param kind The kind of transition to validate
   * @param source The source vertex for the transition
   * @param target The target vertex for the transition
   */
  public static void validate(final TransitionKind kind, final Vertex source, final Vertex target) {
    switch (kind) {
      case LOCAL:
        /*
         * [1] A transition with kind local must have a composite state or an
         * entry point as its source.
         */
        if (source instanceof State) {
          if (!((State) source).isComposite()) {
            throw new TransitionKindConstraintException(kind, source, target, "A transition with kind local must have a composite state or an entry point as its source.");
          }
        } else if (source instanceof PseudoState) {
          if (((PseudoState) source).kind() != PseudoStateKind.ENTRY_POINT) {
            throw new TransitionKindConstraintException(kind, source, target, "A transition with kind local must have a composite state or an entry point as its source.");
          }
        } else {
          throw new TransitionKindConstraintException(kind, source, target, "A transition with kind local must have a composite state or an entry point as its source.");
        }

        break;
      case EXTERNAL:
        /*
         * [2] A transition with kind external can source any vertex except
         * entry points.
         */
        if (source instanceof PseudoState && ((PseudoState) source).kind() == PseudoStateKind.ENTRY_POINT) {
          throw new TransitionKindConstraintException(kind, source, target, "A transition with kind external can source any vertex except entry points.");
        }
        break;
      case INTERNAL:
        /*
         * [3] A transition with kind internal must have a state as its source,
         * and its source and target must be equal.
         */
        if (!(source instanceof State)) {
          throw new TransitionKindConstraintException(kind, source, target, "The source of an internal transition must be a state.");
        }
        if (source != target) {
          throw new TransitionKindConstraintException(kind, source, target, "The source and target of an internal transition must be the same.");
        }
        break;
    }
  }

  private static boolean hasNoGuard(final Transition t) {
    return t.guard() == null;
  }
}
