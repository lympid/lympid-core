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
 * Interface implementing the visitor pattern paradigm.
 *
 * @author Fabien Renaud
 */
public interface Visitor {

  /**
   * Visits a connection point reference. This method is invoked at the very
   * beginning of visiting a connection point reference.
   *
   * @param visitable The {@code ConnectionPointReference} to visit.
   */
  void visitOnEntry(ConnectionPointReference visitable);

  /**
   * Visits a connection point reference. This method is invoked at the very end
   * of visiting a connection point reference.
   *
   * @param visitable The {@code ConnectionPointReference} to visit.
   */
  void visitOnExit(ConnectionPointReference visitable);

  /**
   * Visits a state. This method is invoked at the very beginning of visiting a
   * state.
   *
   * @param visitable The {@code State} to visit.
   */
  void visitOnEntry(State visitable);

  /**
   * Visits a state. This method is invoked at the very end of visiting a state.
   *
   * @param visitable The {@code State} to visit.
   */
  void visitOnExit(State visitable);

  /**
   * Visits a pseudo state. This method is invoked at the very beginning of
   * visiting a pseudo state.
   *
   * @param visitable The {@code PseudoState} to visit.
   */
  void visitOnEntry(PseudoState visitable);

  /**
   * Visits a pseudo state. This method is invoked at the very end of visiting a
   * pseudo state.
   *
   * @param visitable The {@code PseudoState} to visit.
   */
  void visitOnExit(PseudoState visitable);

  /**
   * Visits a final state. This method is invoked at the very beginning of
   * visiting a final state.
   *
   * @param visitable The {@code FinalState} to visit.
   */
  void visitOnEntry(FinalState visitable);

  /**
   * Visits a final state. This method is invoked at the very end of visiting a
   * final state.
   *
   * @param visitable The {@code FinalState} to visit.
   */
  void visitOnExit(FinalState visitable);

  /**
   * Visits a region. This method is invoked at the very beginning of visiting a
   * region.
   *
   * @param visitable The {@code Region} to visit.
   */
  void visitOnEntry(Region visitable);

  /**
   * Visits a region. This method is invoked at the very end of visiting a
   * region.
   *
   * @param visitable The {@code Region} to visit.
   */
  void visitOnExit(Region visitable);

  /**
   * Visits a state machine. This method is invoked at the very beginning of
   * visiting a state machine.
   *
   * @param visitable The {@code StateMachine} to visit.
   */
  void visitOnEntry(StateMachine visitable);

  /**
   * Visits a state machine. This method is invoked at the very end of visiting
   * a state machine.
   *
   * @param visitable The {@code StateMachine} to visit.
   */
  void visitOnExit(StateMachine visitable);

  /**
   * Visits a transition. This method is invoked at the very beginning of
   * visiting a transition.
   *
   * @param visitable The {@code Transition} to visit.
   */
  void visitOnEntry(Transition visitable);

  /**
   * Visits a transition. This method is invoked at the very end of visiting a
   * transition.
   *
   * @param visitable The {@code Transition} to visit.
   */
  void visitOnExit(Transition visitable);
}
