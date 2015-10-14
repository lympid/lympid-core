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

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class FunctionRelativeTimeEventTest {

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor() {
    new FunctionRelativeTimeEvent(null);
  }

  /**
   * Test of time method, of class RelativeTimeEvent.
   */
  @Test
  public void testTime() {
    final long ms1 = TimeUnit.DAYS.toMillis(3);
    FunctionRelativeTimeEvent event = new FunctionRelativeTimeEvent((c) -> Duration.ofMillis(ms1));
    assertEquals("() ms", event.toString());
    assertEquals(ms1, event.time(null));
    assertEquals(ms1 + " ms", event.toString());
    
    final long ms2 = TimeUnit.MINUTES.toMillis(11);
    event = new FunctionRelativeTimeEvent((c) -> Duration.ofMillis(ms2));
    assertEquals("() ms", event.toString());
    assertEquals(ms2, event.time(null));
    assertEquals(ms2 + " ms", event.toString());
  }

  /**
   * Test of equals method, of class FunctionRelativeTimeEvent.
   */
  @Test
  public void testEquals() {
    FunctionRelativeTimeEvent event1 = new FunctionRelativeTimeEvent((c) -> Duration.ofMillis(1));
    FunctionRelativeTimeEvent event2 = new FunctionRelativeTimeEvent((c) -> Duration.ofMillis(1));
    assertFalse(event1.equals(null));
    assertFalse(event1.equals(new StringEvent("1 ms")));
    assertFalse(event1.equals(event2));
  }

}
