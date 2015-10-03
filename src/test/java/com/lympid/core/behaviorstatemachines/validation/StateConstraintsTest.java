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
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.impl.MutableConnectionPointReference;
import com.lympid.core.behaviorstatemachines.impl.MutablePseudoState;
import com.lympid.core.behaviorstatemachines.impl.MutableRegion;
import com.lympid.core.behaviorstatemachines.impl.MutableState;
import com.lympid.core.behaviorstatemachines.impl.MutableStateMachine;
import static com.lympid.core.common.TestUtils.randomPseudoStateBut;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class StateConstraintsTest {

  /**
   * [1] Only submachine states can have connection point references.
   */
  @Test
  public void constraint1_success() {
    MutableStateMachine subMachine = new MutableStateMachine("aa");
    MutableConnectionPointReference cpr = connectionPointReference(subMachine);

    MutableState state = new MutableState("a");
    state.setSubStateMachine(subMachine);
    state.connection(cpr);

    assertTrue(state.isSubMachineState());
    StandardValidator.validate(state);
  }

  /**
   * [1] Only submachine states can have connection point references.
   * Tests when the state is simple.
   */
  @Test(expected = StateConstraintException.class)
  public void constraint1_fail_simple() {
    MutableStateMachine subMachine = new MutableStateMachine("aa");
    MutableConnectionPointReference cpr = connectionPointReference(subMachine);

    MutableState state = new MutableState("a");
    state.connection(cpr);

    assertTrue(state.isSimple());
    StandardValidator.validate(state);
  }

  /**
   * [1] Only submachine states can have connection point references.
   * Tests when the state is simple.
   */
  @Test(expected = StateConstraintException.class)
  public void constraint1_fail_composite() {
    MutableStateMachine subMachine = new MutableStateMachine("aa");
    MutableConnectionPointReference cpr = connectionPointReference(subMachine);

    MutableState state = new MutableState("a");
    state.connection(cpr);
    state.setRegions(Arrays.asList(new MutableRegion("r1")));

    assertTrue(state.isComposite());
    StandardValidator.validate(state);
  }

  /**
   * [1] Only submachine states can have connection point references.
   * Tests when the state is simple.
   */
  @Test(expected = StateConstraintException.class)
  public void constraint1_fail_orthogonal() {
    MutableStateMachine subMachine = new MutableStateMachine("aa");
    MutableConnectionPointReference cpr = connectionPointReference(subMachine);

    MutableState state = new MutableState("a");
    state.connection(cpr);
    state.setRegions(Arrays.asList(new MutableRegion("r1"), new MutableRegion()));

    assertTrue(state.isOrthogonal());
    StandardValidator.validate(state);
  }

  /**
   * [2] The connection point references used as destinations/sources of
   * transitions associated with a submachine state must be defined as
   * entry/exit points in the submachine state machine.
   *
   * Tests with an entry point.
   */
  @Test(expected = StateConstraintException.class)
  public void constraint2_fail_entryPoint() {
    MutableStateMachine subMachine = new MutableStateMachine("aa");
    MutableConnectionPointReference cpr = connectionPointReference(subMachine);
    cpr.entry().add(new MutablePseudoState("11", PseudoStateKind.ENTRY_POINT));

    MutableState state = new MutableState("a");
    state.setSubStateMachine(subMachine);
    state.connection(cpr);

    assertTrue(state.isSubMachineState());
    try {
      StandardValidator.validate(state);
    } catch (StateConstraintException ex) {
      assertEquals(state, ex.getState());
      throw ex;
    }
  }

  /**
   * [2] The connection point references used as destinations/sources of
   * transitions associated with a submachine state must be defined as
   * entry/exit points in the submachine state machine.
   *
   * Tests with an exit point.
   */
  @Test(expected = StateConstraintException.class)
  public void constraint2_fail_exitPoint() {
    MutableStateMachine subMachine = new MutableStateMachine("aa");
    MutableConnectionPointReference cpr = connectionPointReference(subMachine);
    cpr.exit().add(new MutablePseudoState("11", PseudoStateKind.EXIT_POINT));

    MutableState state = new MutableState("a");
    state.setSubStateMachine(subMachine);
    state.connection(cpr);

    assertTrue(state.isSubMachineState());
    StandardValidator.validate(state);
  }

  /*
   * [3] A state is not allowed to have both a submachine and regions.
   * isComposite implies not isSubmachineState
   *
   * This rule is not strictly verified in the way the specification describes
   * it. See ImplementationValidator for more information.
   */
  /**
   * [4] A simple state is a state without any regions.
   * [5] A composite state is a state with at least one region.
   * [6] An orthogonal state is a composite state with at least 2 regions.
   */
  @Test
  public void constraint4and5and6() {
    MutableState state = new MutableState();
    assertEquals(0, state.region().size());
    assertTrue(state.isSimple());
    assertFalse(state.isComposite());
    assertFalse(state.isOrthogonal());
    assertFalse(state.isSubMachineState());
    StandardValidator.validate(state);

    List<Region> regions = new ArrayList<>();

    regions.add(new MutableRegion());
    state.setRegions(regions);
    assertEquals(1, state.region().size());
    assertFalse(state.isSimple());
    assertTrue(state.isComposite());
    assertFalse(state.isOrthogonal());
    assertFalse(state.isSubMachineState());
    StandardValidator.validate(state);

    for (int i = 0; i < 4; i++) {
      regions.add(new MutableRegion());
      state.setRegions(regions);
      assertEquals(2 + i, state.region().size());
      assertFalse(state.isSimple());
      assertTrue(state.isComposite());
      assertTrue(state.isOrthogonal());
      assertFalse(state.isSubMachineState());
      StandardValidator.validate(state);
    }
  }

  /**
   * [4] A simple state is a state without any regions.
   */
  @Test(expected = StateConstraintException.class)
  public void constraint4_fail() {
    FreeMutableState state = new FreeMutableState(true, false, false, false);
    state.setRegions(Arrays.asList(new MutableRegion()));
    StandardValidator.validate(state);
  }

  /**
   * [5] A composite state is a state with at least one region.
   */
  @Test(expected = StateConstraintException.class)
  public void constraint5_fail() {
    FreeMutableState state = new FreeMutableState(false, true, false, false);
    StandardValidator.validate(state);
  }

  /**
   * [6] An orthogonal state is a composite state with at least 2 regions.
   */
  @Test(expected = StateConstraintException.class)
  public void constraint6_fail() {
    FreeMutableState state = new FreeMutableState(false, true, true, false);
    state.setRegions(Arrays.asList(new MutableRegion()));
    StandardValidator.validate(state);
  }

  /**
   * [7] Only submachine states can have a reference statemachine.
   */
  @Test
  public void constraint7() {
    MutableStateMachine subMachine = new MutableStateMachine("aa");

    MutableState state = new MutableState("a");
    state.setSubStateMachine(subMachine);

    assertNotNull(state.subStateMachine());
    assertTrue(state.isSimple());
    assertFalse(state.isComposite());
    assertFalse(state.isOrthogonal());
    assertTrue(state.isSubMachineState());
    StandardValidator.validate(state);
  }

  /**
   * [7] Only submachine states can have a reference statemachine.
   */
  @Test(expected = StateConstraintException.class)
  public void constraint7_fail() {
    FreeMutableState state = new FreeMutableState(true, false, false, false);
    state.setSubStateMachine(new MutableStateMachine());
    StandardValidator.validate(state);
  }
  
  /*
   * [8] The redefinition context of a state is the nearest containing
   * statemachine.
   */

  /**
   * [9] Only composite states can have entry or exit pseudostates defined.
   */
  @Test
  public void constraint9_success() {
    MutableState state = new MutableState();
    MutablePseudoState entryPoint = new MutablePseudoState(PseudoStateKind.ENTRY_POINT);
    entryPoint.setName("dd");
    state.connectionPoint().add(entryPoint);

    List<Region> regions = new ArrayList<>();

    regions.add(new MutableRegion());
    state.setRegions(regions);
    StandardValidator.validate(state);

    for (int i = 0; i < 4; i++) {
      regions.add(new MutableRegion());
      state.setRegions(regions);
      StandardValidator.validate(state);
    }
  }

  /**
   * [9] Only composite states can have entry or exit pseudostates defined.
   *
   * Test with a simple state.
   */
  @Test(expected = StateConstraintException.class)
  public void constraint9_fail_simple() {
    MutableState state = new MutableState();
    MutablePseudoState entryPoint = new MutablePseudoState(PseudoStateKind.ENTRY_POINT);
    entryPoint.setName("dd");
    state.connectionPoint().add(entryPoint);
    StandardValidator.validate(state);
  }

  /**
   * [10] Only entry or exit pseudostates can serve as connection points.
   */
  @Test(expected = StateConstraintException.class)
  public void constraint10_fail() {
    MutableState state = new MutableState();
    MutablePseudoState entryPoint = randomPseudoStateBut(PseudoStateKind.ENTRY_POINT, PseudoStateKind.EXIT_POINT);
    entryPoint.setName("dd");
    state.setRegions(Arrays.asList(new MutableRegion()));
    state.connectionPoint().add(entryPoint);
    StandardValidator.validate(state);
  }

  @Test(expected = StateConstraintException.class)
  public void connectionPoints_nullName() {
    MutableState state = new MutableState();
    MutablePseudoState entryPoint = new MutablePseudoState(PseudoStateKind.ENTRY_POINT);
    state.setRegions(Arrays.asList(new MutableRegion()));
    state.connectionPoint().add(entryPoint);
    StandardValidator.validate(state);
  }

  @Test(expected = StateConstraintException.class)
  public void connectionPoints_duplicateName() {
    MutableState state = new MutableState();

    MutablePseudoState entryPoint = new MutablePseudoState(PseudoStateKind.ENTRY_POINT);
    entryPoint.setName("EP");
    MutablePseudoState exitPoint = new MutablePseudoState(PseudoStateKind.ENTRY_POINT);
    exitPoint.setName("EP");

    state.setRegions(Arrays.asList(new MutableRegion()));
    state.connectionPoint().add(entryPoint);
    state.connectionPoint().add(exitPoint);

    StandardValidator.validate(state);
  }

  private static MutableConnectionPointReference connectionPointReference(final MutableStateMachine machine) {
    Random rnd = new Random();
    int n = rnd.nextInt(10) + 1;
    int m = rnd.nextInt(10) + 1;

    MutableConnectionPointReference cpr = new MutableConnectionPointReference("123");
    for (int i = 0; i < n; i++) {
      MutablePseudoState ps = new MutablePseudoState("10" + i, PseudoStateKind.ENTRY_POINT);
      cpr.entry().add(ps);
      machine.connectionPoint().add(ps);
      ps.setStateMachine(machine);
    }
    for (int i = 0; i < m; i++) {
      MutablePseudoState ps = new MutablePseudoState("11" + i, PseudoStateKind.EXIT_POINT);
      cpr.exit().add(ps);
      machine.connectionPoint().add(ps);
      ps.setStateMachine(machine);
    }
    return cpr;
  }

  private static class FreeMutableState extends MutableState {

    private final boolean simple;
    private final boolean composite;
    private final boolean orthogonal;
    private final boolean subMachineState;

    public FreeMutableState(boolean simple, boolean composite, boolean orthogonal, boolean subMachineState) {
      this.simple = simple;
      this.composite = composite;
      this.orthogonal = orthogonal;
      this.subMachineState = subMachineState;
    }

    @Override
    public boolean isComposite() {
      return composite;
    }

    @Override
    public boolean isOrthogonal() {
      return orthogonal;
    }

    @Override
    public boolean isSimple() {
      return simple;
    }

    @Override
    public boolean isSubMachineState() {
      return subMachineState;
    }

  }
}
