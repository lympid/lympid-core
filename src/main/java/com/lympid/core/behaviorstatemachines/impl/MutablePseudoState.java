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
package com.lympid.core.behaviorstatemachines.impl;

import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.Visitor;

/**
 *
 * @author Fabien Renaud
 */
public final class MutablePseudoState extends MutableVertex implements PseudoState {

  private final PseudoStateKind kind;
  private StateMachine stateMachine;
  private State state;

  public MutablePseudoState(final String id, PseudoStateKind kind) {
    super(id);
    this.kind = kind;
  }

  public MutablePseudoState(PseudoStateKind kind) {
    super();
    this.kind = kind;
  }

  @Override
  public PseudoStateKind kind() {
    return kind;
  }

  @Override
  public StateMachine stateMachine() {
    return stateMachine;
  }

  public void setStateMachine(StateMachine stateMachine) {
    this.stateMachine = stateMachine;
  }

  @Override
  public State state() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visitOnEntry(this);
    visitor.visitOnExit(this);
  }
}
