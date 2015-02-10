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
package com.lympid.core.basicbehaviors;

import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class RelativeTimeEventTest {

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor() {
    new RelativeTimeEvent(-1, TimeUnit.DAYS);
  }

  /**
   * Test of time method, of class RelativeTimeEvent.
   */
  @Test
  public void testTime() {
    RelativeTimeEvent event = new RelativeTimeEvent(3, TimeUnit.DAYS);
    long ms = TimeUnit.DAYS.toMillis(3);
    assertEquals(ms, event.time());
    assertEquals(ms + " ms", event.toString());
    
    event = new RelativeTimeEvent(11, TimeUnit.MINUTES);
    ms = TimeUnit.MINUTES.toMillis(11);
    assertEquals(ms, event.time());
    assertEquals(ms + " ms", event.toString());
  }

  /**
   * Test of equals method, of class RelativeTimeEvent.
   */
  @Test
  public void testEquals() {
    RelativeTimeEvent event1 = new RelativeTimeEvent(1, TimeUnit.MILLISECONDS);
    RelativeTimeEvent event2 = new RelativeTimeEvent(1, TimeUnit.MILLISECONDS);
    assertFalse(event1.equals(null));
    assertFalse(event1.equals(new StringEvent("1 ms")));
    assertTrue(event1.equals(event2));
  }

}
