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
import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.impl.MutableConnectionPointReference;
import com.lympid.core.behaviorstatemachines.impl.MutablePseudoState;
import com.lympid.core.behaviorstatemachines.impl.MutableRegion;
import com.lympid.core.behaviorstatemachines.impl.MutableState;
import com.lympid.core.behaviorstatemachines.impl.MutableStateMachine;
import com.lympid.core.behaviorstatemachines.validation.StateMachineConstraintException;

/**
 *
 * @param <C> Type of the state machine context.
 *
 * @see State
 * @see StateMachine
 *
 * @author Fabien Renaud
 */
public final class SubMachineStateBuilder<C> extends StateBuilder<SubMachineStateBuilder<C>, C> implements StateEntry<SubMachineStateBuilder<C>, C> {

  private final StateMachineBuilder<C> stateMachineBuilder;
  private final ConnectionPointReferenceBuilder<C> connectionPointReferenceBuilder;
  private IdMakerVisitor idMakerVisitor;

  SubMachineStateBuilder(final StateMachineBuilder stateMachineBuilder, final String name) {
    super(name);
    this.stateMachineBuilder = stateMachineBuilder;
    this.connectionPointReferenceBuilder = new ConnectionPointReferenceBuilder<>(name);
  }

  SubMachineStateBuilder(final StateMachineBuilder stateMachineBuilder) {
    this(stateMachineBuilder, stateMachineBuilder.getName());
  }

  @Override
  public final StateEntry<SubMachineStateBuilder<C>, C> entry(final StateBehavior<C> entry) {
    addEntry(entry);
    return this;
  }

  @Override
  public final StateEntry<SubMachineStateBuilder<C>, C> entry(final Class<? extends StateBehavior<C>> entry) {
    addEntry(entry);
    return this;
  }

  @Override
  public final StateExit<SubMachineStateBuilder<C>, C> exit(final StateBehavior<C> exit) {
    addExit(exit);
    return this;
  }

  @Override
  public final StateExit<SubMachineStateBuilder<C>, C> exit(final Class<? extends StateBehavior<C>> exit) {
    addExit(exit);
    return this;
  }

  @Override
  public final StateTransitionSource<SubMachineStateBuilder<C>, TransitionTrigger<SubMachineStateBuilder<C>, C, CompletionEvent>, C> activity(final StateBehavior<C> activity) {
    setActivity(activity);
    return this;
  }

  @Override
  public final StateTransitionSource<SubMachineStateBuilder<C>, TransitionTrigger<SubMachineStateBuilder<C>, C, CompletionEvent>, C> activity(final Class<? extends StateBehavior<C>> activity) {
    setActivity(activity);
    return this;
  }

  private TransitionTrigger<SubMachineStateBuilder<C>, C, CompletionEvent> transition(final String name, final TransitionKind kind) {
    ErnalTransitionBuilder transition = new ErnalTransitionBuilder(kind, name, this);
    outgoing().add(transition);
    return transition;
  }

  @Override
  public TransitionTrigger<SubMachineStateBuilder<C>, C, CompletionEvent> transition(final String name) {
    return transition(name, TransitionKind.EXTERNAL);
  }

  @Override
  public TransitionTrigger<SubMachineStateBuilder<C>, C, CompletionEvent> transition() {
    return transition(null);
  }

  @Override
  public SelfTransitionTrigger<SubMachineStateBuilder<C>, C, CompletionEvent> selfTransition(String name) {
    SelfTransitionBuilder transition = new SelfTransitionBuilder(TransitionKind.INTERNAL, name, this);
    outgoing().add(transition);
    return transition;
  }

  @Override
  public SelfTransitionTrigger<SubMachineStateBuilder<C>, C, CompletionEvent> selfTransition() {
    return selfTransition(null);
  }

  public ConnectionPointReferenceBuilder<C> connectionPoint() {
    return connectionPointReferenceBuilder;
  }

  @Override
  MutableState vertex(VertexSet vertices) {
    final MutableState vertex = super.vertex(vertices);

    /*
     * From UML superstructure 2.4.1, chapter 15.3.11 State # Submachine state
     *
     * A submachine state is semantically equivalent to a composite state. [...]
     * Submachine state is a decomposition mechanism that allows factoring of
     * common behaviors and their reuse.
     *
     * The code below turns the sub state machine into a composite state.
     */
    stateMachineBuilder.accept(idMakerVisitor);
    connectionPointReferenceBuilder.accept(idMakerVisitor);

    MutableStateMachine subMachine = stateMachineBuilder.build();

    for (Region r : subMachine.region()) {
      MutableRegion mr = (MutableRegion) r;
      mr.setState(vertex);
      /*
       * [4] If a Region is owned by a StateMachine, then it cannot also be
       * owned by a State and vice versa.
       */
      mr.setStateMachine(null);
    }
    vertex.setRegions(subMachine.region());
    vertex.setSubStateMachine(subMachine);


    /*
     * Creates all the connection point references from the connection points
     * defined in the sub state machine and register those in the VertexSet.
     *
     * In addition, the current submachine state is defined as owner of the
     * connection points of the sub state machine and all exit points defined
     * via the ConnectionPointReferenceBuilder have their ids replaced with the
     * matching exit points of the sub state machine.
     */
    if (!subMachine.connectionPoint().isEmpty()) {
      MutableConnectionPointReference ref = new MutableConnectionPointReference(connectionPointReferenceBuilder.getId());
      ref.setState(vertex);

      final String namePrefix = getName() + "::";
      for (PseudoState cp : subMachine.connectionPoint()) {
        vertices.add(namePrefix + cp.getName(), cp);
        switch (cp.kind()) {
          case ENTRY_POINT:
            MutablePseudoState entryPoint = (MutablePseudoState) cp;
            ref.entry().add(entryPoint);
            entryPoint.setState(vertex);
            entryPoint.setName(namePrefix + entryPoint.getName());
            vertex.connectionPoint().add(entryPoint);
            break;
          case EXIT_POINT:
            MutablePseudoState exitPoint = (MutablePseudoState) cp;
            ref.exit().add(exitPoint);
            exitPoint.setState(vertex);
            exitPoint.setName(namePrefix + exitPoint.getName());
            vertex.connectionPoint().add(exitPoint);
            connectionPointReferenceBuilder.mergeExitPoint(exitPoint);
            break;
          default:
            throw new StateMachineConstraintException(subMachine, "The connection points of a state machine must be pseudostates of kind entry point or exit point.");
        }
      }
      vertex.connection(ref);
    }

    return vertex;
  }

  @Override
  void connect(final VertexSet vertices) {
    super.connect(vertices);
    connectionPointReferenceBuilder.connect(vertices);
  }

  @Override
  public void accept(Visitor visitor) {
    super.accept(visitor);
    if (visitor instanceof IdMakerVisitor) {
      /*
       * postpone the operation when the submachine is being copied as a
       * composite state.
       */
      idMakerVisitor = (IdMakerVisitor) visitor;
    } else {
      stateMachineBuilder.accept(visitor);
      connectionPointReferenceBuilder.accept(visitor);
    }
  }
}
