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
import com.lympid.core.behaviorstatemachines.impl.MutableRegion;
import com.lympid.core.behaviorstatemachines.impl.MutableState;
import com.lympid.core.behaviorstatemachines.impl.MutableStateMachine;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class RegionConstraintsTest {

  /**
   * [1] A region can have at most one initial vertex.
   */
  @Test
  public void constraint1_success() {
    MutableRegion r = new MutableRegion();
    StandardValidator.validate(r);

    r.addVertex(new MutablePseudoState(PseudoStateKind.INITIAL));
    StandardValidator.validate(r);
  }

  /**
   * [1] A region can have at most one initial vertex.
   */
  @Test(expected = RegionConstraintException.class)
  public void constraint1_fail() {
    MutableRegion r = new MutableRegion();
    r.addVertex(new MutablePseudoState(PseudoStateKind.INITIAL));
    r.addVertex(new MutablePseudoState(PseudoStateKind.INITIAL));
  }

  /**
   * [1] A region can have at most one initial vertex.
   */
  @Test(expected = RegionConstraintException.class)
  public void constraint1_fail_validator() {
    MutableRegion r = new MutableRegion();
    r.subVertex().add(new MutablePseudoState(PseudoStateKind.INITIAL));
    r.subVertex().add(new MutablePseudoState(PseudoStateKind.INITIAL));
    StandardValidator.validate(r);
  }

  /**
   * [2] A region can have at most one deep history vertex.
   */
  @Test
  public void constraint2_success() {
    MutableRegion r = new MutableRegion();
    StandardValidator.validate(r);

    r.addVertex(new MutablePseudoState(PseudoStateKind.DEEP_HISTORY));
    StandardValidator.validate(r);

    r.addVertex(new MutablePseudoState(PseudoStateKind.SHALLOW_HISTORY));
    StandardValidator.validate(r);
  }

  /**
   * [2] A region can have at most one deep history vertex.
   */
  @Test(expected = RegionConstraintException.class)
  public void constraint2_fail() {
    MutableRegion r = new MutableRegion();
    r.addVertex(new MutablePseudoState(PseudoStateKind.DEEP_HISTORY));

    try {
      r.addVertex(new MutablePseudoState(PseudoStateKind.DEEP_HISTORY));
    } catch (RegionConstraintException ex) {
      assertEquals(r, ex.getRegion());
      throw ex;
    }
  }

  /**
   * [2] A region can have at most one deep history vertex.
   */
  @Test(expected = RegionConstraintException.class)
  public void constraint2_fail_validator() {
    MutableRegion r = new MutableRegion();
    r.subVertex().add(new MutablePseudoState(PseudoStateKind.DEEP_HISTORY));
    r.subVertex().add(new MutablePseudoState(PseudoStateKind.DEEP_HISTORY));
    StandardValidator.validate(r);
  }

  /**
   * [3] A region can have at most one shallow history vertex.
   */
  @Test
  public void constraint3_success() {
    MutableRegion r = new MutableRegion();
    StandardValidator.validate(r);

    r.addVertex(new MutablePseudoState(PseudoStateKind.SHALLOW_HISTORY));
    StandardValidator.validate(r);

    r.addVertex(new MutablePseudoState(PseudoStateKind.DEEP_HISTORY));
    StandardValidator.validate(r);
  }

  /**
   * [3] A region can have at most one shallow history vertex.
   */
  @Test(expected = RegionConstraintException.class)
  public void constraint3_fail() {
    MutableRegion r = new MutableRegion();
    r.addVertex(new MutablePseudoState(PseudoStateKind.SHALLOW_HISTORY));
    r.addVertex(new MutablePseudoState(PseudoStateKind.SHALLOW_HISTORY));
  }

  /**
   * [3] A region can have at most one shallow history vertex.
   */
  @Test(expected = RegionConstraintException.class)
  public void constraint3_fail_validator() {
    MutableRegion r = new MutableRegion();
    r.subVertex().add(new MutablePseudoState(PseudoStateKind.SHALLOW_HISTORY));
    r.subVertex().add(new MutablePseudoState(PseudoStateKind.SHALLOW_HISTORY));
    StandardValidator.validate(r);
  }

  /**
   * [4] If a Region is owned by a StateMachine, then it cannot also be owned by
   * a State and vice versa.
   */
  @Test
  public void constraint4_success() {
    MutableRegion r = new MutableRegion();
    r.setState(new MutableState("12"));
    StandardValidator.validate(r);

    r = new MutableRegion();
    r.setStateMachine(new MutableStateMachine("213"));
    StandardValidator.validate(r);
  }

  /**
   * [4] If a Region is owned by a StateMachine, then it cannot also be owned by
   * a State and vice versa.
   */
  @Test(expected = RegionConstraintException.class)
  public void constraint4_fail() {
    MutableRegion r = new MutableRegion();
    r.setState(new MutableState("12"));
    r.setStateMachine(new MutableStateMachine("213"));
    StandardValidator.validate(r);
  }

  /*
   * TODO
   * [5] The redefinition context of a region is the nearest containing
   * statemachine.
   */
}
