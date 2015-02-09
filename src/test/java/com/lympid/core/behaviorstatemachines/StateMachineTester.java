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

import com.lympid.core.behaviorstatemachines.impl.TextVisitor;
import com.lympid.core.common.UmlElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author Fabien Renaud
 */
public final class StateMachineTester {
  
  private static final Pattern ANY_NEW_LINE_PATTERN = Pattern.compile("\r?\n?");

  public static void assertTextVisitor(final String expectedOutput, final StateMachine machine) {
    TextVisitor v = new TextVisitor();
    machine.accept(v);

    assertTextVisitor(expectedOutput, v);
  }

  public static void assertTextVisitor(final String expectedOutput, final TextVisitor actualOutput) {
    String out = actualOutput.toString();
    
    String expected = ANY_NEW_LINE_PATTERN.matcher(expectedOutput).replaceAll("");
    String out1 = ANY_NEW_LINE_PATTERN.matcher(out).replaceAll("");
    assertEquals(expected, out1);    
  }

  public static Region assertTopLevelStateMachine(StateMachine topLevelStateMachine) {
    assertNotNull(topLevelStateMachine);
    assertNotNull(topLevelStateMachine.region());
    assertEquals(1, topLevelStateMachine.region().size());

    Iterator<? extends Region> itRegion = topLevelStateMachine.region().iterator();
    assertTrue(itRegion.hasNext());
    Region region = itRegion.next();
    assertFalse(itRegion.hasNext());

    return region;
  }

  public static State assertSimple(Vertex vertex) {
    assertTrue(vertex instanceof State);
    State state = (State) vertex;
    assertTrue(state.isSimple());
    assertFalse(state.isComposite());
    assertFalse(state.isOrthogonal());
    assertFalse(state.isSubMachineState());
    assertNull(state.subStateMachine());
    assertEquals(0, state.region().size());
    return state;
  }

  public static State assertComposite(Vertex vertex) {
    assertTrue(vertex instanceof State);
    State state = (State) vertex;
    assertFalse(state.isSimple());
    assertTrue(state.isComposite());
    assertFalse(state.isOrthogonal());
    assertFalse(state.isSubMachineState());
    assertNull(state.subStateMachine());
    assertTrue(state.region().size() > 0);
    return state;
  }

  public static State assertOrthogonal(Vertex vertex) {
    assertTrue(vertex instanceof State);
    State state = (State) vertex;
    assertFalse(state.isSimple());
    assertTrue(state.isComposite());
    assertTrue(state.isOrthogonal());
    assertFalse(state.isSubMachineState());
    assertNull(state.subStateMachine());
    assertTrue(state.region().size() > 1);
    return state;
  }

  public static State assertSubMachine(Vertex vertex) {
    assertTrue(vertex instanceof State);
    State state = (State) vertex;
    assertFalse(state.isSimple());
    assertFalse(state.isComposite());
    assertFalse(state.isOrthogonal());
    assertTrue(state.isSubMachineState());
    assertNotNull(state.subStateMachine());
    return state;
  }

  public static State assertFinal(Vertex vertex) {
    State state = assertSimple(vertex);
    assertEquals(0, state.entry().size());
    assertEquals(0, state.exit().size());
    assertEquals(0, state.region().size());
    return state;
  }

  public static void assertRegions(Collection<? extends Region> regions, int expectedNumberOfRegions, RegionTest... regionTests) {
    assertEquals(expectedNumberOfRegions, regions.size());

    Map<String, RegionTest> byName = new HashMap<>(regionTests.length);
    Map<String, RegionTest> byId = new HashMap<>(regionTests.length);
    for (RegionTest t : regionTests) {
      if (t.getId() == null) {
        byName.put(t.getName(), t);
      } else {
        byId.put(t.getId(), t);
      }
    }
    for (Region r : regions) {
      if (r.getName() == null) {
        RegionTest t = byId.get(r.getId());
        assertNotNull("No test region for id: " + r.getId(), t);
        assertRegion(r, t);
      } else {
        RegionTest t = byName.get(r.getName());
        assertNotNull("No test region for name: " + r.getName(), t);
        assertRegion(r, t);
      }
    }
  }

  public static void assertRegion(Region region, int expectedNumberOfVertices, int expectedNumberOfTransitions) {
    assertRegion(region, expectedNumberOfVertices, expectedNumberOfTransitions, null, null);
  }

  public static void assertRegion(Region region, int expectedNumberOfVertices, int expectedNumberOfTransitions, TransitionTest... transitionTests) {
    assertRegion(region, expectedNumberOfVertices, expectedNumberOfTransitions, null, transitionTests);
  }

  public static void assertRegion(Region region, int expectedNumberOfVertices, int expectedNumberOfTransitions, VertexTest... vertexTests) {
    assertRegion(region, expectedNumberOfVertices, expectedNumberOfTransitions, vertexTests, null);
  }

  public static void assertRegion(Region region, int expectedNumberOfVertices, int expectedNumberOfTransitions, VertexTest[] vertexTests, TransitionTest[] transitionTests) {
    assertRegion(region, new RegionTest(null, null, expectedNumberOfVertices, expectedNumberOfTransitions, vertexTests, transitionTests));
  }

  public static void assertRegion(Region region, RegionTest regionTest) {
    assertEquals(regionTest.getExpectedNumberOfVertices(), region.subVertex().size());
    assertEquals(regionTest.getExpectedNumberOfTransitions(), region.transition().size());

    if (regionTest.getVertexTests() != null) {
      Map<String, VertexTest> map = new HashMap<>(regionTest.getVertexTests().length);
      for (VertexTest t : regionTest.getVertexTests()) {
        map.put(t.getName(), t);
      }

      for (Vertex v : region.subVertex()) {
        VertexTest vt = null;
        if (v.getName() != null) {
          vt = map.remove(v.getName());
        }
        if (vt == null) {
          vt = map.remove("#" + v.getId());
        }
        assertNotNull("No vertex test associated with vertex: " + v, vt);
        vt.getConsumer().accept(v);
      }
    }

    if (regionTest.getTransitionTests() != null) {
      Map<String, TransitionTest> map = new HashMap<>(regionTest.getTransitionTests().length);
      for (TransitionTest t : regionTest.getTransitionTests()) {
        map.put(t.getTransition(), t);
      }

      for (Transition t : region.transition()) {
        TransitionTest tt = null;
        if (t.getName() != null) {
          tt = map.remove(t.getName());
        }
        if (tt == null) {
          tt = map.remove("#" + t.getId());
        }
        assertTransition(tt, t);
      }
      if (!map.isEmpty()) {
        fail("Some transition tests don't match any existing transitions: " + map.keySet());
      }
    }
  }

  private static void assertTransition(TransitionTest expected, Transition actual) {
    assertNotNull("No transition test associated with transition: " + actual, expected);
    assertNameOrId(expected.getTransition(), actual);
    assertNameOrId(expected.getSource(), actual.source());
    assertNameOrId(expected.getTarget(), actual.target());
    assertEquals(expected.getKind(), actual.kind());
  }

  private static void assertNameOrId(final String nameOrId, final UmlElement element) {
    if (nameOrId.charAt(0) == '#') {
      assertEquals(nameOrId.substring(1), element.getId());
    } else {
      assertEquals(nameOrId, element.getName());
    }
  }

  public static PseudoState assertPseudoState(Vertex vertex, PseudoStateKind expectedKind) {
    assertTrue(vertex instanceof PseudoState);
    PseudoState pseudo = (PseudoState) vertex;
    assertEquals(expectedKind, pseudo.kind());
    return pseudo;
  }

  public static PseudoState assertPseudoStateChoice(Vertex vertex) {
    return assertPseudoState(vertex, PseudoStateKind.CHOICE);
  }

  public static PseudoState assertPseudoStateDeepHistory(Vertex vertex) {
    return assertPseudoState(vertex, PseudoStateKind.DEEP_HISTORY);
  }

  public static PseudoState assertPseudoStateEntryPoint(Vertex vertex) {
    return assertPseudoState(vertex, PseudoStateKind.ENTRY_POINT);
  }

  public static PseudoState assertPseudoStateExitPoint(Vertex vertex) {
    return assertPseudoState(vertex, PseudoStateKind.EXIT_POINT);
  }

  public static PseudoState assertPseudoStateFork(Vertex vertex) {
    return assertPseudoState(vertex, PseudoStateKind.FORK);
  }

  public static PseudoState assertPseudoStateInitial(Vertex vertex) {
    return assertPseudoState(vertex, PseudoStateKind.INITIAL);
  }

  public static PseudoState assertPseudoStateJoin(Vertex vertex) {
    return assertPseudoState(vertex, PseudoStateKind.JOIN);
  }

  public static PseudoState assertPseudoStateJunction(Vertex vertex) {
    return assertPseudoState(vertex, PseudoStateKind.JUNCTION);
  }

  public static PseudoState assertPseudoStateShallowHistory(Vertex vertex) {
    return assertPseudoState(vertex, PseudoStateKind.SHALLOW_HISTORY);
  }

  public static PseudoState assertPseudoStateTerminate(Vertex vertex) {
    return assertPseudoState(vertex, PseudoStateKind.TERMINATE);
  }
}
