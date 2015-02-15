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

import com.lympid.core.behaviorstatemachines.State;
import java.util.ArrayList;
import java.util.List;
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
public class CompositeStateConfigurationTest {
  
  private CompositeStateConfiguration config;
  private CompositeStateConfiguration child1;
  private CompositeStateConfiguration child2;
  private CompositeStateConfiguration child3;
  private State state0;
  private State state1;
  private State state2;
  private State state3;
  
  @Before
  public void setUp() {
    state0 = new MutableState();
    state1 = new MutableState();
    state2 = new MutableState();
    state3 = new MutableState();
    
    config = new CompositeStateConfiguration();
    config.setState(state0);
    child1 = config.addChild(state1);
    child2 = child1.addChild(state2);
    child3 = child2.addChild(state3);
  }
  
  @Test
  public void testParent() {
    assertNull(config.parent());
    assertTrue(config == child1.parent());
    assertTrue(child1 == child2.parent());
    assertTrue(child2 == child3.parent());
  }
  
  @Test
  public void testState() {
    assertTrue(state0 == config.state());
    assertTrue(state1 == child1.state());
    assertTrue(state2 == child2.state());
    assertTrue(state3 == child3.state());
  }
  
  @Test
  public void testSize() {
    assertEquals(1, config.size());
    assertEquals(1, child1.size());
    assertEquals(1, child2.size());
    assertEquals(0, child3.size());
  }
  
  @Test
  public void testIsEmpty() {
    assertFalse(config.isEmpty());
    assertFalse(child1.isEmpty());
    assertFalse(child2.isEmpty());
    assertTrue(child3.isEmpty());
  }
  
  @Test
  public void testForEach() {
    List<State> collect = new ArrayList<>(1);
    config.forEach((s) -> collect.add(s.state()));
    assertEquals(1, collect.size());
    assertEquals(state1, collect.get(0));
    
    collect.clear();
    child1.forEach((s) -> collect.add(s.state()));
    assertEquals(1, collect.size());
    assertEquals(state2, collect.get(0));
    
    collect.clear();
    child2.forEach((s) -> collect.add(s.state()));
    assertEquals(1, collect.size());
    assertEquals(state3, collect.get(0));
  }
  
  @Test(expected = AssertionError.class)
  public void testAddChild_fail() {
    config.addChild(state3);
  }
  
  @Test
  public void testRemoveThenAddChild() {
    assertFalse(config.isEmpty());
    config.removeChild(child1);
    assertTrue(config.isEmpty());
    assertNull(child1.parent());
    
    child1 = config.addChild(state2);
    assertFalse(config.isEmpty());
    assertTrue(config == child1.parent());
  }
  
  @Test(expected = AssertionError.class)
  public void testRemoveChild_fail() {
    config.removeChild(child2);
  }
}
