package com.lympid.core.behaviorstatemachines.validation;

import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.impl.MutableConnectionPointReference;
import com.lympid.core.behaviorstatemachines.impl.MutablePseudoState;
import static com.lympid.core.common.TestUtils.randomPseudoStateBut;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/*
 * Copyright 2015 Fabien Renaud.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 * @author Fabien Renaud 
 */
public class ConnectionPointReferenceConstraintsTest {

  /**
   * [1] The entry Pseudostates must be Pseudostates with kind entryPoint.
   */
  @Test
  public void contraint1_success() {
    MutableConnectionPointReference cpr = new MutableConnectionPointReference("123");
    PseudoState ps1 = new MutablePseudoState("10", PseudoStateKind.ENTRY_POINT);
    PseudoState ps2 = new MutablePseudoState("11", PseudoStateKind.ENTRY_POINT);

    cpr.entry().add(ps1);
    cpr.entry().add(ps2);

    StandardValidator.validate(cpr);
  }

  /**
   * [1] The entry Pseudostates must be Pseudostates with kind entryPoint.
   */
  @Test(expected = ConnectionPointReferenceConstraintException.class)
  public void contraint1_fail() {
    MutableConnectionPointReference cpr = new MutableConnectionPointReference("123");
    PseudoState ps1 = new MutablePseudoState("10", PseudoStateKind.ENTRY_POINT);
    PseudoState ps2 = randomPseudoStateBut(PseudoStateKind.ENTRY_POINT);

    cpr.entry().add(ps1);
    cpr.entry().add(ps2);

    try {
      StandardValidator.validate(cpr);
    } catch (ConnectionPointReferenceConstraintException ex) {
      assertEquals(cpr, ex.getConnectionPointReference());
      assertEquals(ps2.kind(), ex.getFaultyPseudoStateKind());
      throw ex;
    }
  }

  /**
   * [2] The exit Pseudostates must be Pseudostates with kind exitPoint.
   */
  @Test
  public void contraint2_success() {
    MutableConnectionPointReference cpr = new MutableConnectionPointReference("123");
    PseudoState ps1 = new MutablePseudoState("10", PseudoStateKind.EXIT_POINT);
    PseudoState ps2 = new MutablePseudoState("11", PseudoStateKind.EXIT_POINT);

    cpr.exit().add(ps1);
    cpr.exit().add(ps2);

    StandardValidator.validate(cpr);
  }

  /**
   * [2] The exit Pseudostates must be Pseudostates with kind exitPoint.
   */
  @Test(expected = ConnectionPointReferenceConstraintException.class)
  public void contraint2_fail() {
    MutableConnectionPointReference cpr = new MutableConnectionPointReference("123");
    PseudoState ps1 = new MutablePseudoState("10", PseudoStateKind.EXIT_POINT);
    PseudoState ps2 = randomPseudoStateBut(PseudoStateKind.EXIT_POINT);

    cpr.exit().add(ps1);
    cpr.exit().add(ps2);

    try {
      StandardValidator.validate(cpr);
    } catch (ConnectionPointReferenceConstraintException ex) {
      assertEquals(cpr, ex.getConnectionPointReference());
      assertEquals(ps2.kind(), ex.getFaultyPseudoStateKind());
      throw ex;
    }
  }
}
