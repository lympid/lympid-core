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
package com.lympid.core.common;

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.basicbehaviors.StringEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class TriggerTest {

  /**
   * Test of event method, of class Trigger.
   */
  @Test
  public void testEvent() {
    Event evt = new StringEvent("hello");
    Trigger t = new Trigger(evt);
    assertEquals(evt, t.event());
  }

  /**
   * Test of equals method, of class Trigger.
   */
  @Test
  public void testEquals() {
    Event evt = new StringEvent("hello");
    Trigger t1 = new Trigger(evt);

    Event evt2 = new StringEvent("hello");
    Trigger t2 = new Trigger(evt2);

    Trigger t3 = new Trigger(evt);

    Event evt4 = new StringEvent("Hello");
    Trigger t4 = new Trigger(evt4);

    Event evt5 = new Event() {
    };
    Trigger t5 = new Trigger(evt5);

    assertFalse(t1.equals(null));
    assertFalse(t1.equals(new Object()));

    assertTrue(t1.equals(t2));
    assertTrue(t1.equals(t3));
    assertTrue(t2.equals(t3));

    assertTrue(t2.equals(t1));
    assertTrue(t3.equals(t1));
    assertTrue(t3.equals(t2));

    assertFalse(t1.equals(t4));
    assertFalse(t4.equals(t1));

    assertFalse(t1.equals(t5));
    assertFalse(t5.equals(t1));
  }

  /**
   * Test of toString method, of class Trigger.
   */
  @Test
  public void testToString() {
    Event evt = new StringEvent("hello");
    Trigger t1 = new Trigger(evt);
    assertEquals(evt.toString(), t1.toString());

    evt = new StringEvent("HeLlO");
    t1 = new Trigger(evt);
    assertEquals(evt.toString(), t1.toString());
  }

}
