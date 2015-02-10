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

import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.impl.MutablePseudoState;
import com.lympid.core.behaviorstatemachines.impl.MutableStateMachine;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class StateMachineConstraintsTest {

  /*
   * TODO
   * [1] The classifier context of a state machine cannot be an interface.
   */
  /*
   * TODO
   * [2] The context classifier of the method state machine of a behavioral
   * feature must be the classifier that owns the behavioral feature.
   */
  /**
   * [3] The connection points of a state machine are pseudostates of kind entry
   * point or exit point.
   */
  @Test
  public void constraint3_success() {
    MutableStateMachine machine = new MutableStateMachine();
    machine.connectionPoint().add(new MutablePseudoState(PseudoStateKind.ENTRY_POINT));
    StandardValidator.validate(machine);
    machine.connectionPoint().add(new MutablePseudoState(PseudoStateKind.ENTRY_POINT));
    StandardValidator.validate(machine);
    machine.connectionPoint().add(new MutablePseudoState(PseudoStateKind.EXIT_POINT));
    StandardValidator.validate(machine);

    machine = new MutableStateMachine();
    machine.connectionPoint().add(new MutablePseudoState(PseudoStateKind.EXIT_POINT));
    StandardValidator.validate(machine);
    machine.connectionPoint().add(new MutablePseudoState(PseudoStateKind.EXIT_POINT));
    StandardValidator.validate(machine);
    machine.connectionPoint().add(new MutablePseudoState(PseudoStateKind.ENTRY_POINT));
    StandardValidator.validate(machine);
  }

  /**
   * [3] The connection points of a state machine are pseudostates of kind entry
   * point or exit point.
   *
   * Fail case with an entry point.
   */
  @Test(expected = StateMachineConstraintException.class)
  public void constraint3_fail_1() {
    MutableStateMachine machine = new MutableStateMachine();
    machine.connectionPoint().add(new MutablePseudoState(PseudoStateKind.ENTRY_POINT));
    StandardValidator.validate(machine);
    machine.connectionPoint().add(new MutablePseudoState(PseudoStateKind.INITIAL));

    try {
      StandardValidator.validate(machine);
    } catch (StateMachineConstraintException ex) {
      assertEquals(machine, ex.getStateMachine());
      throw ex;
    }
  }

  /**
   * [3] The connection points of a state machine are pseudostates of kind entry
   * point or exit point.
   *
   * Fail case with an exit point.
   */
  @Test(expected = StateMachineConstraintException.class)
  public void constraint3_fail_2() {
    MutableStateMachine machine = new MutableStateMachine();
    machine.connectionPoint().add(new MutablePseudoState(PseudoStateKind.EXIT_POINT));
    StandardValidator.validate(machine);
    machine.connectionPoint().add(new MutablePseudoState(PseudoStateKind.INITIAL));
    StandardValidator.validate(machine);
  }

  /**
   * [4] A state machine as the method for a behavioral feature cannot have
   * entry/exit connection points.
   */
}
