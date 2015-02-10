/*
 * Copyright 2015 Lympid.
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
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.impl.MutablePseudoState;
import com.lympid.core.behaviorstatemachines.impl.MutableRegion;
import com.lympid.core.behaviorstatemachines.impl.MutableState;
import com.lympid.core.behaviorstatemachines.impl.MutableStateMachine;
import static com.lympid.core.common.TestUtils.randomPseudoState;
import static com.lympid.core.common.TestUtils.randomPseudoStateBut;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class TransitionKindConstraintsTest {

  /**
   * [1] A transition with kind local must have a composite state or an entry
   * point as its source.
   */
  @Test
  public void constraint1_success_composite() {
    MutableState state = new MutableState();
    state.setRegions(Arrays.asList(
      new MutableRegion()
    ));
    assertTrue(state.isComposite());
    assertFalse(state.isOrthogonal());
    StandardValidator.validate(TransitionKind.LOCAL, state, null);
  }

  /**
   * [1] A transition with kind local must have a composite state or an entry
   * point as its source.
   */
  @Test
  public void constraint1_success_orthogonal() {
    MutableState state = new MutableState();
    state.setRegions(Arrays.asList(
      new MutableRegion(),
      new MutableRegion()
    ));
    assertTrue(state.isComposite());
    assertTrue(state.isOrthogonal());
    StandardValidator.validate(TransitionKind.LOCAL, state, null);
  }

  /**
   * [1] A transition with kind local must have a composite state or an entry
   * point as its source.
   */
  @Test
  public void constraint1_success_entryPoint() {
    MutablePseudoState pseudo = new MutablePseudoState(PseudoStateKind.ENTRY_POINT);
    StandardValidator.validate(TransitionKind.LOCAL, pseudo, null);
  }

  /**
   * [1] A transition with kind local must have a composite state or an entry
   * point as its source.
   */
  @Test(expected = TransitionKindConstraintException.class)
  public void constraint1_fail_simple() {
    MutableState state = new MutableState();
    assertTrue(state.isSimple());
    assertFalse(state.isComposite());

    try {
      StandardValidator.validate(TransitionKind.LOCAL, state, null);
    } catch (TransitionKindConstraintException ex) {
      assertEquals(TransitionKind.LOCAL, ex.getKind());
      assertEquals(state, ex.getSource());
      assertNull(ex.getTarget());
      throw ex;
    }
  }

  /**
   * [1] A transition with kind local must have a composite state or an entry
   * point as its source.
   */
  @Test(expected = TransitionKindConstraintException.class)
  public void constraint1_fail_exitPoint() {
    MutablePseudoState pseudo = randomPseudoStateBut(PseudoStateKind.ENTRY_POINT);

    try {
      StandardValidator.validate(TransitionKind.LOCAL, pseudo, null);
    } catch (TransitionKindConstraintException ex) {
      assertEquals(TransitionKind.LOCAL, ex.getKind());
      assertEquals(pseudo, ex.getSource());
      assertNull(ex.getTarget());
      throw ex;
    }
  }

  /**
   * [1] A transition with kind local must have a composite state or an entry
   * point as its source.
   */
  @Test(expected = TransitionKindConstraintException.class)
  public void constraint1_fail_null() {
    try {
      StandardValidator.validate(TransitionKind.LOCAL, null, null);
    } catch (TransitionKindConstraintException ex) {
      assertEquals(TransitionKind.LOCAL, ex.getKind());
      assertNull(ex.getSource());
      assertNull(ex.getTarget());
      throw ex;
    }
  }

  /**
   * [2] A transition with kind external can source any vertex except entry
   * points.
   */
  @Test
  public void constraint2_success() {
    StandardValidator.validate(TransitionKind.EXTERNAL, simpleState(), null);
    StandardValidator.validate(TransitionKind.EXTERNAL, compositeState(), null);
    StandardValidator.validate(TransitionKind.EXTERNAL, orthogonalState(), null);
    StandardValidator.validate(TransitionKind.EXTERNAL, regularSubMachineState(), null);
    StandardValidator.validate(TransitionKind.EXTERNAL, specialSubMachineState(), null);
    StandardValidator.validate(TransitionKind.EXTERNAL, randomPseudoStateBut(PseudoStateKind.ENTRY_POINT), null);
  }

  /**
   * [2] A transition with kind external can source any vertex except entry
   * points.
   */
  @Test(expected = TransitionKindConstraintException.class)
  public void constraint2_fail() {
    MutablePseudoState pseudo = new MutablePseudoState(PseudoStateKind.ENTRY_POINT);
    try {
      StandardValidator.validate(TransitionKind.EXTERNAL, pseudo, null);
    } catch (TransitionKindConstraintException ex) {
      assertEquals(TransitionKind.EXTERNAL, ex.getKind());
      assertEquals(pseudo, ex.getSource());
      assertNull(ex.getTarget());
      throw ex;
    }
  }

  /**
   * [3] A transition with kind internal must have a state as its source, and
   * its source and target must be equal.
   */
  @Test
  public void constraint3_success() {
    State source = simpleState();
    StandardValidator.validate(TransitionKind.INTERNAL, source, source);

    source = compositeState();
    StandardValidator.validate(TransitionKind.INTERNAL, source, source);

    source = orthogonalState();
    StandardValidator.validate(TransitionKind.INTERNAL, source, source);

    source = regularSubMachineState();
    StandardValidator.validate(TransitionKind.INTERNAL, source, source);

    source = specialSubMachineState();
    StandardValidator.validate(TransitionKind.INTERNAL, source, source);
  }
  
  @Test(expected = TransitionKindConstraintException.class)
  public void constraint3_fail_targetNull() {    
    StandardValidator.validate(TransitionKind.INTERNAL, simpleState(), null);
  }
  
  @Test(expected = TransitionKindConstraintException.class)
  public void constraint3_fail_sourceNull() {    
    StandardValidator.validate(TransitionKind.INTERNAL, null, simpleState());
  }
  
  @Test(expected = TransitionKindConstraintException.class)
  public void constraint3_fail_pseudoTarget() {    
    StandardValidator.validate(TransitionKind.INTERNAL, simpleState(), randomPseudoState());
  }
  
  @Test(expected = TransitionKindConstraintException.class)
  public void constraint3_fail_pseudoSource() {    
    StandardValidator.validate(TransitionKind.INTERNAL, randomPseudoState(), simpleState());
  }

  private MutableState simpleState() {
    MutableState state = new MutableState();
    return state;
  }

  private MutableState compositeState() {
    MutableState state = new MutableState();
    state.setRegions(Arrays.asList(new MutableRegion()));
    return state;
  }

  private MutableState orthogonalState() {
    MutableState state = new MutableState();
    state.setRegions(Arrays.asList(new MutableRegion(), new MutableRegion()));
    return state;
  }

  private MutableState regularSubMachineState() {
    MutableStateMachine subMachine = new MutableStateMachine();
    MutableRegion region1 = new MutableRegion();
    MutableRegion region2 = new MutableRegion();
    subMachine.setRegions(Arrays.asList(region1, region2));

    MutableState subMachineState = new MutableState();
    subMachineState.setSubStateMachine(subMachine);

    assertTrue(subMachineState.isSimple());
    assertFalse(subMachineState.isComposite());
    assertFalse(subMachineState.isOrthogonal());
    assertTrue(subMachineState.isSubMachineState());

    return subMachineState;
  }

  private MutableState specialSubMachineState() {
    MutableStateMachine subMachine = new MutableStateMachine();
    MutableRegion region1 = new MutableRegion();
    MutableRegion region2 = new MutableRegion();
    subMachine.setRegions(Arrays.asList(region1, region2));

    MutableState subMachineState = new MutableState();
    subMachineState.setSubStateMachine(subMachine);
    subMachineState.setRegions(Arrays.asList(region1, region2));

    assertFalse(subMachineState.isSimple());
    assertTrue(subMachineState.isComposite());
    assertTrue(subMachineState.isOrthogonal());
    assertTrue(subMachineState.isSubMachineState());

    return subMachineState;
  }
}
