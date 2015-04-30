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

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud
 */
public class SimpleStateConfigurationTest {

  private SimpleStateConfiguration config;
  private MutableState state1;
  private MutableState state2;

  @Before
  public void setUp() {
    state1 = new MutableState("1");
    state1.setName("A");
    state2 = new MutableState("2");
    config = new SimpleStateConfiguration(state1);
  }

  @Test(expected = IllegalStateException.class)
  public void testParent() {
    config.parent();
  }

  @Test
  public void testState() {
    assertEquals(state1, config.state());
  }

  @Test
  public void testChildren() {
    assertTrue(config.children().isEmpty());
  }

  @Test
  public void testSetState() {
    assertNotEquals(state2, config.state());

    config.clear();
    config.setState(state2);
    assertEquals(state2, config.state());
  }

  @Test(expected = IllegalStateException.class)
  public void testAddChild() {
    config.addChild(state2);
  }

  @Test(expected = IllegalStateException.class)
  public void testRemoveChild() {
    config.removeChild(null);
  }

  @Test
  public void testClear() {
    config.clear();
    assertNull(config.state());
  }

  @Test
  public void testSize() {
    assertEquals(0, config.size());
  }

  @Test
  public void testIsEmpty() {
    assertTrue(config.isEmpty());
  }

  @Test
  public void testForEach() {
    final AtomicInteger count = new AtomicInteger();
    Consumer<SimpleStateConfiguration> consumer = (sc) -> {
      count.incrementAndGet();
    };
    config.forEach(consumer);
    assertEquals(0, count.get());
  }

  @Test
  public void testCopy() {
    StateConfiguration config2 = config.copy();
    assertTrue(config != config2);
    assertEquals(config, config2);
  }

  @Test
  public void testHashCode() {
    assertEquals(Objects.hashCode(config.state()), config.hashCode());
  }

  @Test
  public void testEquals() {
    assertFalse(config.equals(null));
    assertFalse(config.equals(new Object()));
  }

  @Test
  public void testToString() {
    assertEquals("A", config.toString());

    config.clear();
    config.setState(state2);
    assertEquals("#2", config.toString());
  }

}
