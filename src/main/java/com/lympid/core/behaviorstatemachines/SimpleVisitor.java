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
 * Abstract implementing the Visitor pattern paradigm. I.e., a class that
 * implements this interface can handle all types of instructions with the
 * properly typed methods just by calling the accept() method.
 *
 * @see Visitor
 *
 * @author Fabien Renaud
 */
public abstract class SimpleVisitor implements Visitor {

  public abstract void visit(ConnectionPointReference visitable);

  public abstract void visit(State visitable);

  public abstract void visit(PseudoState visitable);

  public abstract void visit(FinalState visitable);

  public abstract void visit(Region visitable);

  public abstract void visit(StateMachine visitable);

  public abstract void visit(Transition visitable);

  @Override
  public final void visitOnEntry(ConnectionPointReference visitable) {
    visit(visitable);
  }

  @Override
  public final void visitOnExit(ConnectionPointReference visitable) {
  }

  @Override
  public final void visitOnEntry(State visitable) {
    visit(visitable);
  }

  @Override
  public final void visitOnExit(State visitable) {
  }

  @Override
  public final void visitOnEntry(PseudoState visitable) {
    visit(visitable);
  }

  @Override
  public final void visitOnExit(PseudoState visitable) {
  }

  @Override
  public final void visitOnEntry(FinalState visitable) {
    visit(visitable);
  }

  @Override
  public final void visitOnExit(FinalState visitable) {
  }

  @Override
  public final void visitOnEntry(Region visitable) {
    visit(visitable);
  }

  @Override
  public final void visitOnExit(Region visitable) {
  }

  @Override
  public final void visitOnEntry(StateMachine visitable) {
    visit(visitable);
  }

  @Override
  public final void visitOnExit(StateMachine visitable) {
  }

  @Override
  public final void visitOnEntry(Transition visitable) {
    visit(visitable);
  }

  @Override
  public final void visitOnExit(Transition visitable) {
  }

}
