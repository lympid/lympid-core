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
package com.lympid.core.behaviorstatemachines.builder;

import com.lympid.core.behaviorstatemachines.impl.MutableRegion;
import com.lympid.core.behaviorstatemachines.impl.MutableState;
import com.lympid.core.behaviorstatemachines.impl.MutableStateMachine;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class VertexSetTest {

  private int counter;

  private String id() {
    return Integer.toString(++counter);
  }

  @Test
  public void leastCommonAncestor_NoAncestor() throws CommonAncestorException {
    /*
     * Build state machine
     */
    MutableStateMachine machine = new MutableStateMachine(id());
    MutableRegion region1 = new MutableRegion(id());
    MutableRegion region2 = new MutableRegion(id());
    MutableState state11 = new MutableState(id());
    MutableState state21 = new MutableState(id());

    machine.addRegion(region1);
    machine.addRegion(region2);
    region1.addVertex(state11);
    region2.addVertex(state21);

    /*
     * Actual tests
     */
    VertexSet vertices = new VertexSet();
    vertices.add(state11);
    vertices.add(state21);

    assertNull(vertices.leastCommonAncestor(state11, state21));
    assertNull(vertices.leastCommonAncestor(state21, state11));
  }

  @Test
  public void leastCommonAncestor_SimpleState() throws CommonAncestorException {
    /*
     * Build state machine
     */
    MutableStateMachine machine = new MutableStateMachine(id());
    MutableRegion region = new MutableRegion(id());
    MutableState state1 = new MutableState(id());
    MutableState state2 = new MutableState(id());

    machine.addRegion(region);
    region.addVertex(state1);
    region.addVertex(state2);

    /*
     * Actual tests
     */
    VertexSet vertices = new VertexSet();
    vertices.add(state1);
    vertices.add(state2);

    assertEquals(region, vertices.leastCommonAncestor(state1, state2));
    assertEquals(region, vertices.leastCommonAncestor(state2, state1));
  }

  @Test
  public void leastCommonAncestor_CompositeState() throws CommonAncestorException {
    /*
     * Build state machine
     */
    MutableStateMachine machine = new MutableStateMachine(id());
    MutableRegion region = new MutableRegion(id());
    MutableState state0 = new MutableState(id());
    MutableState compositeState = new MutableState(id());
    MutableRegion compositeRegion = new MutableRegion(id());
    MutableState state1 = new MutableState(id());
    MutableState state2 = new MutableState(id());

    machine.addRegion(region);
    region.addVertex(state0);
    region.addVertex(compositeState);

    compositeState.setRegions(Arrays.asList(compositeRegion));
    compositeRegion.setState(compositeState);
    compositeRegion.addVertex(state1);
    compositeRegion.addVertex(state2);

    /*
     * Actual tests
     */
    VertexSet vertices = new VertexSet();
    vertices.add(state0);
    vertices.add(compositeState);
    vertices.add(state1);
    vertices.add(state2);

    assertEquals(compositeRegion, vertices.leastCommonAncestor(state1, state2));
    assertEquals(compositeRegion, vertices.leastCommonAncestor(state2, state1));
    assertEquals(region, vertices.leastCommonAncestor(state0, compositeState));
    assertEquals(region, vertices.leastCommonAncestor(compositeState, state0));

    assertEquals(region, vertices.leastCommonAncestor(compositeState, state1));
    assertEquals(region, vertices.leastCommonAncestor(compositeState, state2));
    assertEquals(region, vertices.leastCommonAncestor(state1, compositeState));
    assertEquals(region, vertices.leastCommonAncestor(state2, compositeState));
    assertEquals(region, vertices.leastCommonAncestor(state0, state1));
    assertEquals(region, vertices.leastCommonAncestor(state0, state2));
    assertEquals(region, vertices.leastCommonAncestor(state1, state0));
    assertEquals(region, vertices.leastCommonAncestor(state2, state0));
  }

  @Test
  public void leastCommonAncestor_OrthogonalState() throws CommonAncestorException {
    /*
     * Build state machine
     */
    MutableStateMachine machine = new MutableStateMachine(id());
    MutableRegion mainRegion = new MutableRegion(id());
    MutableState state0 = new MutableState(id());
    MutableState orthogonalState = new MutableState(id());
    MutableRegion region1 = new MutableRegion(id());
    MutableState state11 = new MutableState(id());
    MutableState state12 = new MutableState(id());
    MutableRegion region2 = new MutableRegion(id());
    MutableState state21 = new MutableState(id());
    MutableState state22 = new MutableState(id());

    machine.addRegion(mainRegion);
    mainRegion.addVertex(state0);
    mainRegion.addVertex(orthogonalState);

    orthogonalState.setRegions(Arrays.asList(region1));
    region1.setState(orthogonalState);
    region1.addVertex(state11);
    region1.addVertex(state12);

    orthogonalState.setRegions(Arrays.asList(region2));
    region2.setState(orthogonalState);
    region2.addVertex(state21);
    region2.addVertex(state22);

    /*
     * Actual tests
     */
    VertexSet vertices = new VertexSet();
    vertices.add(state0);
    vertices.add(orthogonalState);
    vertices.add(state11);
    vertices.add(state12);
    vertices.add(state21);
    vertices.add(state22);

    assertEquals(region1, vertices.leastCommonAncestor(state11, state12));
    assertEquals(region1, vertices.leastCommonAncestor(state12, state11));

    assertEquals(region2, vertices.leastCommonAncestor(state21, state22));
    assertEquals(region2, vertices.leastCommonAncestor(state22, state21));

    assertEquals(mainRegion, vertices.leastCommonAncestor(state0, orthogonalState));
    assertEquals(mainRegion, vertices.leastCommonAncestor(orthogonalState, state0));

    assertEquals(mainRegion, vertices.leastCommonAncestor(orthogonalState, state11));
    assertEquals(mainRegion, vertices.leastCommonAncestor(orthogonalState, state12));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state11, orthogonalState));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state12, orthogonalState));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state0, state11));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state0, state12));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state11, state0));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state12, state0));

    assertEquals(mainRegion, vertices.leastCommonAncestor(state11, state21));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state11, state22));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state12, state21));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state12, state22));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state21, state11));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state21, state12));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state22, state11));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state22, state12));
  }

  @Test
  public void leastCommonAncestor_SubStateMachine() throws CommonAncestorException {
    /*
     * Build state machine
     */
    MutableStateMachine mainMachine = new MutableStateMachine(id());
    MutableRegion mainRegion = new MutableRegion(id());
    MutableState state0 = new MutableState(id());
    MutableState stateForSubMachine = new MutableState(id());
    MutableState compositeState = new MutableState(id());
    MutableRegion compositeRegion = new MutableRegion(id());
    MutableState state11 = new MutableState(id());
    MutableState state12 = new MutableState(id());
    MutableStateMachine subMachine = new MutableStateMachine(id());
    MutableRegion subRegion = new MutableRegion(id());
    MutableState subState1 = new MutableState(id());
    MutableState subState2 = new MutableState(id());

    mainMachine.addRegion(mainRegion);
    mainRegion.addVertex(state0);
    mainRegion.addVertex(compositeState);
    mainRegion.addVertex(stateForSubMachine);

    compositeState.setRegions(Arrays.asList(compositeRegion));
    compositeRegion.setState(compositeState);
    compositeRegion.addVertex(state11);
    compositeRegion.addVertex(state12);

    subMachine.addRegion(subRegion);
    subRegion.addVertex(subState1);
    subRegion.addVertex(subState2);

    stateForSubMachine.setSubStateMachine(subMachine);

    /*
     * Actual tests
     */
    VertexSet vertices = new VertexSet();
    vertices.add(state0);
    vertices.add(stateForSubMachine);
    vertices.add(compositeState);
    vertices.add(state11);
    vertices.add(state12);
    vertices.add(subState1);
    vertices.add(subState2);

    assertEquals(subRegion, vertices.leastCommonAncestor(subState1, subState2));
    assertEquals(subRegion, vertices.leastCommonAncestor(subState2, subState1));
    assertEquals(compositeRegion, vertices.leastCommonAncestor(state11, state12));
    assertEquals(compositeRegion, vertices.leastCommonAncestor(state12, state11));
    assertEquals(mainRegion, vertices.leastCommonAncestor(stateForSubMachine, compositeState));
    assertEquals(mainRegion, vertices.leastCommonAncestor(compositeState, stateForSubMachine));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state0, compositeState));
    assertEquals(mainRegion, vertices.leastCommonAncestor(compositeState, state0));
    assertEquals(mainRegion, vertices.leastCommonAncestor(stateForSubMachine, compositeState));
    assertEquals(mainRegion, vertices.leastCommonAncestor(compositeState, stateForSubMachine));

    assertEquals(mainRegion, vertices.leastCommonAncestor(stateForSubMachine, subState1));
    assertEquals(mainRegion, vertices.leastCommonAncestor(stateForSubMachine, subState2));
    assertEquals(mainRegion, vertices.leastCommonAncestor(subState1, stateForSubMachine));
    assertEquals(mainRegion, vertices.leastCommonAncestor(subState2, stateForSubMachine));

    assertEquals(mainRegion, vertices.leastCommonAncestor(compositeState, state11));
    assertEquals(mainRegion, vertices.leastCommonAncestor(compositeState, state12));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state11, compositeState));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state12, compositeState));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state0, state11));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state0, state12));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state11, state0));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state12, state0));
  }

  @Test
  public void leastCommonAncestor_SubStateMachineWithinCompositeState() throws CommonAncestorException {
    /*
     * Build state machine
     */
    MutableStateMachine mainMachine = new MutableStateMachine(id());
    MutableRegion mainRegion = new MutableRegion(id());
    MutableState state0 = new MutableState(id());
    MutableState compositeState = new MutableState(id());
    MutableRegion compositeRegion = new MutableRegion(id());
    MutableState state11 = new MutableState(id());
    MutableState state12 = new MutableState(id());
    MutableState state13 = new MutableState(id());
    MutableStateMachine subMachine = new MutableStateMachine(id());
    MutableRegion subRegion = new MutableRegion(id());
    MutableState subState1 = new MutableState(id());
    MutableState subState2 = new MutableState(id());

    mainMachine.addRegion(mainRegion);
    mainRegion.addVertex(state0);
    mainRegion.addVertex(compositeState);

    compositeState.setRegions(Arrays.asList(compositeRegion));
    compositeRegion.setState(compositeState);
    compositeRegion.addVertex(state11);
    compositeRegion.addVertex(state12);
    compositeRegion.addVertex(state13);

    subMachine.addRegion(subRegion);
    subRegion.addVertex(subState1);
    subRegion.addVertex(subState2);

    state13.setSubStateMachine(subMachine);

    /*
     * Actual tests
     */
    VertexSet vertices = new VertexSet();
    vertices.add(state0);
    vertices.add(compositeState);
    vertices.add(state11);
    vertices.add(state12);
    vertices.add(state13);
    vertices.add(subState1);
    vertices.add(subState2);
    assertEquals(7, vertices.size());

    assertEquals(subRegion, vertices.leastCommonAncestor(subState1, subState2));
    assertEquals(subRegion, vertices.leastCommonAncestor(subState2, subState1));
    assertEquals(compositeRegion, vertices.leastCommonAncestor(state11, state12));
    assertEquals(compositeRegion, vertices.leastCommonAncestor(state12, state11));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state13, compositeState));
    assertEquals(mainRegion, vertices.leastCommonAncestor(compositeState, state13));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state0, compositeState));
    assertEquals(mainRegion, vertices.leastCommonAncestor(compositeState, state0));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state13, compositeState));
    assertEquals(mainRegion, vertices.leastCommonAncestor(compositeState, state13));

    assertEquals(compositeRegion, vertices.leastCommonAncestor(state11, subState1));
    assertEquals(compositeRegion, vertices.leastCommonAncestor(state11, subState2));
    assertEquals(compositeRegion, vertices.leastCommonAncestor(subState1, state11));
    assertEquals(compositeRegion, vertices.leastCommonAncestor(subState2, state11));
    assertEquals(compositeRegion, vertices.leastCommonAncestor(state12, subState1));
    assertEquals(compositeRegion, vertices.leastCommonAncestor(state12, subState2));
    assertEquals(compositeRegion, vertices.leastCommonAncestor(subState1, state12));
    assertEquals(compositeRegion, vertices.leastCommonAncestor(subState2, state12));
    assertEquals(compositeRegion, vertices.leastCommonAncestor(state13, subState1));
    assertEquals(compositeRegion, vertices.leastCommonAncestor(state13, subState2));
    assertEquals(compositeRegion, vertices.leastCommonAncestor(subState1, state13));
    assertEquals(compositeRegion, vertices.leastCommonAncestor(subState2, state13));

    assertEquals(mainRegion, vertices.leastCommonAncestor(compositeState, state11));
    assertEquals(mainRegion, vertices.leastCommonAncestor(compositeState, state12));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state11, compositeState));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state12, compositeState));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state0, state11));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state0, state12));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state11, state0));
    assertEquals(mainRegion, vertices.leastCommonAncestor(state12, state0));
  }

  /**
   * Tests a vertex must have an id.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAdd_fail_1() {
    VertexSet vertices = new VertexSet();
    vertices.add(new MutableState(null));
  }

  /**
   * Tests a vertex must have a unique id.
   */
  @Test(expected = DuplicateVertexNameException.class)
  public void testAdd_fail_2() {
    String id = id();
    MutableState vertex1 = new MutableState(id);
    MutableState vertex2 = new MutableState(id);
    VertexSet vertices = new VertexSet();
    vertices.add(vertex1);
    
    try {
      vertices.add(vertex2);
    } catch (DuplicateVertexNameException ex) {
      assertEquals(vertex1, ex.getExistingVertex());
      throw ex;
    }
  }

  /**
   * Tests a vertex must have a unique name.
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testAdd_fail_3() {
    MutableState vertex1 = new MutableState(id());
    vertex1.setName("hello");
    MutableState vertex2 = new MutableState(id());
    vertex2.setName("hello");
    VertexSet vertices = new VertexSet();
    vertices.add(vertex1);
    vertices.add(vertex2);
  }

  @Test(expected = CommonAncestorException.class)
  public void leastCommonAncestor_fail_1() throws CommonAncestorException {
    VertexSet vertices = new VertexSet();

    try {
      vertices.leastCommonAncestor(null, null);
    } catch (CommonAncestorException ex) {
      assertNull(ex.getFirst());
      assertNull(ex.getSecond());
      throw ex;
    }
  }

  @Test(expected = CommonAncestorException.class)
  public void leastCommonAncestor_fail_2() throws CommonAncestorException {
    MutableState state = new MutableState(id());
    VertexSet vertices = new VertexSet();
    vertices.add(state);

    try {
      vertices.leastCommonAncestor(state, null);
    } catch (CommonAncestorException ex) {
      assertEquals(state, ex.getFirst());
      assertNull(ex.getSecond());
      throw ex;
    }
  }

  @Test(expected = CommonAncestorException.class)
  public void leastCommonAncestor_fail_3() throws CommonAncestorException {
    MutableState state = new MutableState(id());

    MutableRegion region = new MutableRegion(id());
    region.addVertex(state);

    VertexSet vertices = new VertexSet();
    vertices.add(state);

    try {
      vertices.leastCommonAncestor(state, null);
    } catch (CommonAncestorException ex) {
      assertEquals(state, ex.getFirst());
      assertNull(ex.getSecond());
      throw ex;
    }
  }

  @Test(expected = NullPointerException.class)
  public void leastCommonAncestor_fail_4() throws CommonAncestorException {
    MutableState state1 = new MutableState(id());
    MutableState state2 = new MutableState(id());

    MutableRegion region = new MutableRegion(id());
    region.addVertex(state1);

    VertexSet vertices = new VertexSet();
    vertices.add(state1);
    vertices.add(state2);

    try {
      vertices.leastCommonAncestor(state1, state2);
    } catch (CommonAncestorException ex) {
      assertEquals(state1, ex.getFirst());
      assertEquals(state2, ex.getSecond());
      throw ex;
    }
  }
}
