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

import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.impl.MutablePseudoState;
import com.lympid.core.behaviorstatemachines.impl.MutableStateMachine;
import java.util.Collection;

/**
 *
 * @param <C> Type of the state machine context.
 *
 * @see State
 * @see StateMachine
 *
 * @author Fabien Renaud
 */
public class SubStateMachineBuilder<C> extends StateMachineBuilder<C> {

  private final ConnectionPointBuilder<C> connectionPointBuilder;

  public SubStateMachineBuilder(final String name) {
    super(name);
    this.connectionPointBuilder = new ConnectionPointBuilder<>();
  }

  public ConnectionPointBuilder<C> connectionPoint() {
    return connectionPointBuilder;
  }

  @Override
  protected void buildConnectionPoints(final MutableStateMachine machine, final VertexSet vertices) {
    Collection<MutablePseudoState> connectionPoints = connectionPointBuilder.vertex(vertices);
    /*
     * Connection points do NOT belong to regions. Only to the composite state.
     */
    for (MutablePseudoState cp : connectionPoints) {
      cp.setStateMachine(machine);
      machine.connectionPoint().add(cp);
    }

    connectionPointBuilder.connect(vertices);
  }

  @Override
  public void accept(final Visitor visitor) {
    connectionPointBuilder.accept(visitor);
    super.accept(visitor);
  }

}
