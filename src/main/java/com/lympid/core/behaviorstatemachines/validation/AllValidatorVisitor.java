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
import com.lympid.core.behaviorstatemachines.SimpleVisitor;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.Transition;

/**
 *
 * @author Fabien Renaud
 */
public class AllValidatorVisitor extends SimpleVisitor {

  private final IdValidator idValidator = new IdValidator();

  @Override
  public void visit(final ConnectionPointReference visitable) {
    idValidator.validate(visitable);
    StandardValidator.validate(visitable);
    ImplementationValidator.validate(visitable);
  }

  @Override
  public void visit(final State visitable) {
    idValidator.validate(visitable);
    StandardValidator.validate(visitable);
    ImplementationValidator.validate(visitable);
  }

  @Override
  public void visit(final PseudoState visitable) {
    idValidator.validate(visitable);
    StandardValidator.validate(visitable);
    ImplementationValidator.validate(visitable);
  }

  @Override
  public void visit(final FinalState visitable) {
    idValidator.validate(visitable);
    StandardValidator.validate(visitable);
    ImplementationValidator.validate(visitable);
  }

  @Override
  public void visit(final Region visitable) {
    idValidator.validate(visitable);
    StandardValidator.validate(visitable);
    ImplementationValidator.validate(visitable);
  }

  @Override
  public void visit(final StateMachine visitable) {
    idValidator.validate(visitable);
    StandardValidator.validate(visitable);
    ImplementationValidator.validate(visitable);
  }

  @Override
  public void visit(final Transition visitable) {
    idValidator.validate(visitable);
    StandardValidator.validate(visitable);
    StandardValidator.validate(visitable.kind(), visitable.source(), visitable.target());

    ImplementationValidator.validate(visitable);
    ImplementationValidator.validate(visitable.kind(), visitable.source(), visitable.target());
  }

}
