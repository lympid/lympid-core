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
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexUtils;
import java.util.Iterator;

/**
 * Constraints due to implementation limitations or specificities.
 *
 * TODO: a transition can not have more than 1 time event TODO: a transition
 * with a time event can only have a state as source
 *
 * @author Fabien Renaud
 */
public final class ImplementationValidator {

  private ImplementationValidator() {
  }

  public static void validate(final ConnectionPointReference connectionPointReference) {
  }

  public static void validate(final FinalState finalState) {
  }

  public static void validate(final PseudoState pseudoState) {
  }

  public static void validate(final Region region) {
  }

  public static void validate(final State state) {
    /*
     * [3] A state is not allowed to have both a submachine and regions.
     *
     * Current implementation turns all submachine states into composite or
     * orthogonal states while still preserving the submachine state data.
     *
     * Therefore, when a state is both composite and submachine, check all the
     * regions of the composite state are the ones of the sub state machine.
     */
    if (state.isComposite() && state.isSubMachineState()) {
      boolean identical = state.region().size() == state.subStateMachine().region().size();
      if (identical) {
        Iterator<? extends Region> stateRegions = state.region().iterator();
        Iterator<? extends Region> machineRegions = state.subStateMachine().region().iterator();
        while (identical && stateRegions.hasNext()) {
          identical = stateRegions.next() == machineRegions.next();
        }
      }

      if (!identical) {
        throw new StateConstraintException(state, "A state is not allowed to have both a submachine and regions.");
      }
    }
  }

  public static void validate(final StateMachine stateMachine) {
  }

  public static void validate(final Transition transition) {
  }

  public static void validate(final TransitionKind kind, final Vertex source, final Vertex target) {
    switch (kind) {
      case LOCAL:
        State nSource = source instanceof State
                ? (State) source
                : ((PseudoState) source).state();
        /*
         * The source is always the ancestor of the target
         */
        if (!VertexUtils.ancestor(nSource, target)) {
          throw new TransitionKindConstraintException(kind, source, target, "The source of a local transition must be an ancestor of the target.");
        }
    }
  }

}
