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

import com.lympid.core.behaviorstatemachines.impl.MutablePseudoState;
import com.lympid.core.behaviorstatemachines.impl.MutableRegion;
import com.lympid.core.behaviorstatemachines.impl.MutableState;
import com.lympid.core.behaviorstatemachines.impl.MutableStateMachine;
import com.lympid.core.common.TestUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class VertexUtilsTest {
  
  @Test
  public void coverPrivateConstructor() throws Exception {
    TestUtils.callPrivateConstructor(VertexUtils.class);
  }

  /**
   * Test of ancestor method, of class VertexUtils.
   */
  @Test
  public void testAncestor() {
    assertFalse(VertexUtils.ancestor(null, null));

    MutableRegion region0 = new MutableRegion();
    MutableState state1 = new MutableState();
    MutableState state2 = new MutableState();
    region0.addVertex(state1);
    region0.addVertex(state2);
    assertFalse(VertexUtils.ancestor(state1, null));
    assertFalse(VertexUtils.ancestor(state2, null));
    assertFalse(VertexUtils.ancestor(null, state1));
    assertFalse(VertexUtils.ancestor(null, state2));
    assertFalse(VertexUtils.ancestor(state1, state2));
    assertFalse(VertexUtils.ancestor(state2, state1));
    assertTrue(VertexUtils.ancestor(state1, state1));

    MutableRegion region1 = new MutableRegion();
    state1.setRegions(Collections.singletonList(region1));
    region1.setState(state1);
    MutableState state11 = new MutableState();
    MutableState state12 = new MutableState();
    region1.addVertex(state11);
    region1.addVertex(state12);
    assertFalse(VertexUtils.ancestor(state11, state12));
    assertFalse(VertexUtils.ancestor(state11, state1));
    assertFalse(VertexUtils.ancestor(state12, state1));
    assertFalse(VertexUtils.ancestor(state11, state2));
    assertFalse(VertexUtils.ancestor(state12, state2));
    assertTrue(VertexUtils.ancestor(state1, state11));
    assertTrue(VertexUtils.ancestor(state1, state12));
    assertFalse(VertexUtils.ancestor(state2, state11));
    assertFalse(VertexUtils.ancestor(state2, state12));
  }

  /**
   * Test of allRegionsOfOrthogonalState method, of class VertexUtils.
   */
  @Test
  public void testAllRegionsOfOrthogonalState() {
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(null));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(new LinkedList()));

    MutableState state1 = new MutableState();
    MutableState state2 = new MutableState();
    MutableRegion r1 = new MutableRegion();
    MutableRegion r2 = new MutableRegion();
    MutableRegion r3 = new MutableRegion();
    MutableRegion r4 = new MutableRegion();
    MutableRegion r5 = new MutableRegion();

    state1.setRegions(Collections.singletonList(r1));
    r1.setState(state1);
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(state1.region()));

    state1.setRegions(Arrays.asList(r1, r2));
    r1.setState(state1);
    r2.setState(state1);
    assertTrue(VertexUtils.allRegionsOfOrthogonalState(state1.region()));
    state1.setRegions(Arrays.asList(r1, r2, r3));
    r1.setState(state1);
    r2.setState(state1);
    r3.setState(state1);
    assertTrue(VertexUtils.allRegionsOfOrthogonalState(state1.region()));

    state1.setRegions(Arrays.asList(r1, r2));
    state2.setRegions(Arrays.asList(r3, r4));
    r1.setState(state1);
    r2.setState(state1);
    r3.setState(state2);
    r4.setState(state2);
    assertTrue(VertexUtils.allRegionsOfOrthogonalState(Collections.singletonList(r1)));
    assertTrue(VertexUtils.allRegionsOfOrthogonalState(Collections.singletonList(r2)));
    assertTrue(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r1, r2)));
    assertTrue(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r2, r1)));
    assertTrue(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r3, r4)));
    assertTrue(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r4, r3)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r1, r2, r5)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r3, r5, r4)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r1, r3)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r1, r4)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r2, r3)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r2, r4)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r3, r1)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r3, r2)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r4, r1)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r4, r2)));
    
    
    r1.addVertex(state2);
    assertTrue(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r1, r2)));
    assertTrue(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r3, r4)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r1, r3)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r1, r4)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r2, r3)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r2, r4)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r3, r1)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r3, r2)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r4, r1)));
    assertFalse(VertexUtils.allRegionsOfOrthogonalState(Arrays.asList(r4, r2)));
  }

  /**
   * Test of state method, of class VertexUtils.
   */
  @Test
  public void testState() {
    assertFalse(VertexUtils.simpleState(null));
    assertFalse(VertexUtils.compositeState(null));
    assertFalse(VertexUtils.orthogonalState(null));
    assertFalse(VertexUtils.subMachineState(null));
    
    MutableState state = new MutableState();
    List<Region> regions = new ArrayList<>();
    assertTrue(VertexUtils.simpleState(state));
    assertFalse(VertexUtils.compositeState(state));
    assertFalse(VertexUtils.orthogonalState(state));
    assertFalse(VertexUtils.subMachineState(state));

    regions.add(new MutableRegion());
    state.setRegions(regions);
    assertFalse(VertexUtils.simpleState(state));
    assertTrue(VertexUtils.compositeState(state));
    assertFalse(VertexUtils.orthogonalState(state));
    assertFalse(VertexUtils.subMachineState(state));

    regions.add(new MutableRegion());
    state.setRegions(regions);
    assertFalse(VertexUtils.simpleState(state));
    assertTrue(VertexUtils.compositeState(state));
    assertTrue(VertexUtils.orthogonalState(state));
    assertFalse(VertexUtils.subMachineState(state));
  }

  /**
   * Test of subMachineState method, of class VertexUtils.
   */
  @Test
  public void testSubMachineState() {
    MutableState state = new MutableState();
    state.setSubStateMachine(new MutableStateMachine());
    assertTrue(VertexUtils.simpleState(state));
    assertFalse(VertexUtils.compositeState(state));
    assertFalse(VertexUtils.orthogonalState(state));
    assertTrue(VertexUtils.subMachineState(state));
  }

  /**
   * Test of pseudoState method, of class VertexUtils.
   */
  @Test
  public void testPseudoStates() {
    Map<PseudoStateKind, Predicate<Vertex>> map = new EnumMap<>(PseudoStateKind.class);
    map.put(PseudoStateKind.CHOICE, VertexUtils::choice);
    map.put(PseudoStateKind.DEEP_HISTORY, VertexUtils::deepHistory);
    map.put(PseudoStateKind.ENTRY_POINT, VertexUtils::entryPoint);
    map.put(PseudoStateKind.EXIT_POINT, VertexUtils::exitPoint);
    map.put(PseudoStateKind.FORK, VertexUtils::fork);
    map.put(PseudoStateKind.INITIAL, VertexUtils::initial);
    map.put(PseudoStateKind.JOIN, VertexUtils::join);
    map.put(PseudoStateKind.JUNCTION, VertexUtils::junction);
    map.put(PseudoStateKind.SHALLOW_HISTORY, VertexUtils::shallowHistory);
    map.put(PseudoStateKind.TERMINATE, VertexUtils::terminate);

    for (PseudoStateKind k : PseudoStateKind.values()) {
      MutablePseudoState ps = new MutablePseudoState(k);

      assertTrue(VertexUtils.pseudoState(ps));
      assertFalse(VertexUtils.state(ps));
      for (Entry<PseudoStateKind, Predicate<Vertex>> e : map.entrySet()) {
        if (e.getKey() == k) {
          assertTrue(e.getValue().test(ps));
        } else {
          assertFalse(e.getValue().test(ps));
        }
      }
    }

    MutableState state = new MutableState();
    List<Region> regions = new ArrayList<>();
    assertFalse(VertexUtils.pseudoState(state));
    for (Predicate<Vertex> p : map.values()) {
      assertFalse(p.test(null));
      assertFalse(p.test(state));
    }

    regions.add(new MutableRegion());
    state.setRegions(regions);
    assertFalse(VertexUtils.pseudoState(state));
    for (Predicate<Vertex> p : map.values()) {
      assertFalse(p.test(state));
    }

    regions.add(new MutableRegion());
    state.setRegions(regions);
    assertFalse(VertexUtils.pseudoState(state));
    for (Predicate<Vertex> p : map.values()) {
      assertFalse(p.test(state));
    }
  }

}
