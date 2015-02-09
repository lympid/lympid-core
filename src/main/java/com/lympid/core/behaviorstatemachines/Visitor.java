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

  void visitOnEntry(ConnectionPointReference visitable);

  void visitOnExit(ConnectionPointReference visitable);

  void visitOnEntry(State visitable);

  void visitOnExit(State visitable);

  void visitOnEntry(PseudoState visitable);

  void visitOnExit(PseudoState visitable);

  void visitOnEntry(FinalState visitable);

  void visitOnExit(FinalState visitable);

  void visitOnEntry(Region visitable);

  void visitOnExit(Region visitable);

  void visitOnEntry(StateMachine visitable);

  void visitOnExit(StateMachine visitable);

  void visitOnEntry(Transition visitable);

  void visitOnExit(Transition visitable);
}
