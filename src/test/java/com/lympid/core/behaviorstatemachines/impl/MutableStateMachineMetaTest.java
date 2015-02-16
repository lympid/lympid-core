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
package com.lympid.core.behaviorstatemachines.impl;

import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud
 */
public class MutableStateMachineMetaTest {

  private MutableStateMachineMeta meta;
  private Random rnd;

  @Before
  public void setUp() {
    this.meta = new MutableStateMachineMeta();
    this.rnd = new Random();
  }

  @Test
  public void testCountOf() {
    Map<PseudoStateKind, Integer> expected = new EnumMap<>(PseudoStateKind.class);
    for (PseudoStateKind kind : PseudoStateKind.values()) {
      int n = rnd.nextInt(10);
      expected.put(kind, n);

      for (int i = 0; i < n; i++) {
        meta.register(new MutablePseudoState(kind));
      }
    }

    for (PseudoStateKind kind : PseudoStateKind.values()) {
      assertEquals((int) expected.get(kind), meta.countOf(kind));
    }
  }
  
  @Test
  public void testStateById() {
    String id1 = Integer.toString(rnd.nextInt());
    String id2 = Integer.toString(rnd.nextInt());
    String id3 = Integer.toString(rnd.nextInt());
    String id4 = Integer.toString(rnd.nextInt());
    
    State state1 = new MutableState(id1);
    State state2 = new MutableState(id2);
    State state3 = new MutableState(id3);
    
    assertNull(meta.state(id1));
    assertNull(meta.state(id2));
    assertNull(meta.state(id3));
    assertNull(meta.state(id4));
    assertNull(meta.region(id1));
    assertNull(meta.region(id2));
    assertNull(meta.region(id3));
    assertNull(meta.region(id4));
    
    meta.register(state1);
    meta.register(state2);
    meta.register(state3);
    
    assertEquals(state1, meta.state(id1));
    assertEquals(state2, meta.state(id2));
    assertEquals(state3, meta.state(id3));
    assertNull(meta.state(id4));
    assertNull(meta.region(id1));
    assertNull(meta.region(id2));
    assertNull(meta.region(id3));
    assertNull(meta.region(id4));
  }
  
  @Test
  public void testRegionById() {
    String id1 = Integer.toString(rnd.nextInt());
    String id2 = Integer.toString(rnd.nextInt());
    String id3 = Integer.toString(rnd.nextInt());
    String id4 = Integer.toString(rnd.nextInt());
    
    Region region1 = new MutableRegion(id1);
    Region region2 = new MutableRegion(id2);
    Region region3 = new MutableRegion(id3);
    
    assertNull(meta.state(id1));
    assertNull(meta.state(id2));
    assertNull(meta.state(id3));
    assertNull(meta.state(id4));
    assertNull(meta.region(id1));
    assertNull(meta.region(id2));
    assertNull(meta.region(id3));
    assertNull(meta.region(id4));
    
    meta.register(region1);
    meta.register(region2);
    meta.register(region3);
    
    assertEquals(region1, meta.region(id1));
    assertEquals(region2, meta.region(id2));
    assertEquals(region3, meta.region(id3));
    assertNull(meta.region(id4));
    assertNull(meta.state(id1));
    assertNull(meta.state(id2));
    assertNull(meta.state(id3));
    assertNull(meta.state(id4));
  }

  @Test
  public void registerSubmachineState() {
    MutableStateMachine machine = new MutableStateMachine();
    MutableState state = new MutableState();
    state.setSubStateMachine(machine);
    
    assertFalse(meta.hasSubmachineStates());
    meta.register(state);
    assertTrue(meta.hasSubmachineStates());
  }

}
