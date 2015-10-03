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
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.impl.MutablePseudoState;
import com.lympid.core.behaviorstatemachines.impl.MutableRegion;
import com.lympid.core.behaviorstatemachines.impl.MutableState;
import com.lympid.core.behaviorstatemachines.impl.MutableStateMachine;
import com.lympid.core.common.TestUtils;
import java.util.Arrays;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class ImplementationValidatorTest {

  @Test
  public void coverPrivateConstructor() throws Exception {
    TestUtils.callPrivateConstructor(ImplementationValidator.class);
  }

  /**
   * [3] A state is not allowed to have both a submachine and regions.
   *
   * Current implementation turns all submachine states into composite or
   * orthogonal states while still preserving the submachine state data.
   */
  @Test
  public void testSpecialSubMachineState_success_1() {
    MutableStateMachine subMachine = new MutableStateMachine();
    MutableRegion region1 = new MutableRegion();
    MutableRegion region2 = new MutableRegion();
    subMachine.setRegions(Arrays.asList(region1, region2));
    
    MutableState subMachineState = new MutableState();
    subMachineState.setSubStateMachine(subMachine);
    subMachineState.setRegions(Arrays.asList(region1, region2));
    
    ImplementationValidator.validate(subMachineState);
  }

  /**
   * [3] A state is not allowed to have both a submachine and regions.
   *
   * Current implementation turns all submachine states into composite or
   * orthogonal states while still preserving the submachine state data.
   * 
   * Checks the composite state has just the right amount of regions.
   */
  @Test(expected = StateConstraintException.class)
  public void testSpecialSubMachineState_fail_1() {    
    MutableStateMachine subMachine = new MutableStateMachine();
    MutableRegion region1 = new MutableRegion();
    MutableRegion region2 = new MutableRegion();
    subMachine.setRegions(Arrays.asList(region1, region2));
    
    MutableState subMachineState = new MutableState();
    subMachineState.setSubStateMachine(subMachine);
    subMachineState.setRegions(Arrays.asList(region1, region2, region2));
    
    ImplementationValidator.validate(subMachineState);
  }

  /**
   * [3] A state is not allowed to have both a submachine and regions.
   *
   * Current implementation turns all submachine states into composite or
   * orthogonal states while still preserving the submachine state data.
   * 
   * Checks the regions must be of the same instance.
   */
  @Test(expected = StateConstraintException.class)
  public void testSpecialSubMachineState_fail_2() {    
    MutableStateMachine subMachine = new MutableStateMachine();
    MutableRegion region1 = new MutableRegion();
    MutableRegion region2 = new MutableRegion("123");
    subMachine.setRegions(Arrays.asList(region1, region2));
    
    MutableState subMachineState = new MutableState();
    MutableRegion region2bis = new MutableRegion("123");
    subMachineState.setSubStateMachine(subMachine);
    subMachineState.setRegions(Arrays.asList(region1, region2bis));
    
    ImplementationValidator.validate(subMachineState);
  }
  
  @Test
  public void testLocalTransition_sourceState_success() {
    MutableStateMachine machine = new MutableStateMachine();
    MutableRegion machineRegion = new MutableRegion();
    machine.setRegions(Arrays.asList(machineRegion));
    machineRegion.setStateMachine(machine);
    
    MutableState composite = new MutableState();
    machineRegion.addVertex(composite);
    MutableRegion compositeRegion = new MutableRegion();
    composite.setRegions(Arrays.asList(compositeRegion));
    compositeRegion.setState(composite);
    
    MutableState child = new MutableState();
    compositeRegion.addVertex(child);
    
    ImplementationValidator.validate(TransitionKind.LOCAL, composite, child);
  }
  
  @Test(expected = TransitionKindConstraintException.class)
  public void testLocalTransition_sourceState_fail() {
    MutableStateMachine machine = new MutableStateMachine();
    MutableRegion machineRegion = new MutableRegion();
    machine.setRegions(Arrays.asList(machineRegion));
    machineRegion.setStateMachine(machine);
    
    MutableState composite = new MutableState();
    machineRegion.addVertex(composite);
    MutableRegion compositeRegion = new MutableRegion();
    composite.setRegions(Arrays.asList(compositeRegion));
    compositeRegion.setState(composite);
    
    MutableState state = new MutableState();
    machineRegion.addVertex(state);
    
    ImplementationValidator.validate(TransitionKind.LOCAL, composite, state);
  }
  
  @Test
  public void testLocalTransition_sourceEntryPoint_success() {
    MutableStateMachine machine = new MutableStateMachine();
    MutableRegion machineRegion = new MutableRegion();
    machine.setRegions(Arrays.asList(machineRegion));
    machineRegion.setStateMachine(machine);
    
    MutableState composite = new MutableState();
    machineRegion.addVertex(composite);
    MutableRegion compositeRegion = new MutableRegion();
    composite.setRegions(Arrays.asList(compositeRegion));
    compositeRegion.setState(composite);
    
    MutablePseudoState entryPoint = new MutablePseudoState(PseudoStateKind.ENTRY_POINT);
    composite.connectionPoint().add(entryPoint);
    entryPoint.setState(composite);
    
    MutableState child = new MutableState();
    compositeRegion.addVertex(child);
    
    ImplementationValidator.validate(TransitionKind.LOCAL, entryPoint, child);
  }
  
  @Test(expected = TransitionKindConstraintException.class)
  public void testLocalTransition_sourceEntryPoint_fail() {
    MutableStateMachine machine = new MutableStateMachine();
    MutableRegion machineRegion = new MutableRegion();
    machine.setRegions(Arrays.asList(machineRegion));
    machineRegion.setStateMachine(machine);
    
    MutableState composite = new MutableState();
    machineRegion.addVertex(composite);
    MutableRegion compositeRegion = new MutableRegion();
    composite.setRegions(Arrays.asList(compositeRegion));
    compositeRegion.setState(composite);
    
    MutablePseudoState entryPoint = new MutablePseudoState(PseudoStateKind.ENTRY_POINT);
    composite.connectionPoint().add(entryPoint);
    entryPoint.setState(composite);
    
    MutableState state = new MutableState();
    machineRegion.addVertex(state);
    
    ImplementationValidator.validate(TransitionKind.LOCAL, entryPoint, state);
  }
}
