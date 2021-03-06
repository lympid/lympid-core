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
package com.lympid.core.behaviorstatemachines.builder;

import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.impl.MutablePseudoState;
import java.util.Arrays;
import java.util.HashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud
 */
public class ConnectionPointReferenceBuilderTest {

  private ConnectionPointReferenceBuilder refBuilder;
  private MutablePseudoState exitPoint1;
  private MutablePseudoState exitPoint2;
  private MutablePseudoState exitPoint3;

  @Before
  public void setUp() {
    refBuilder = new ConnectionPointReferenceBuilder<>("foo");

    exitPoint1 = new MutablePseudoState(PseudoStateKind.EXIT_POINT);
    exitPoint2 = new MutablePseudoState(PseudoStateKind.EXIT_POINT);
    exitPoint3 = new MutablePseudoState(PseudoStateKind.EXIT_POINT);

    exitPoint1.setName("foo::exitPoint1");
    exitPoint2.setName("foo::exitPoint2");
    exitPoint3.setName("foo::exitPoint3");

    refBuilder.exitPoint("exitPoint1");
    refBuilder.exitPoint("exitPoint2");
    refBuilder.exitPoint("exitPoint5");
  }

  @Test
  public void testName() {
    assertNull(refBuilder.getName());
  }
  
  @Test
  public void testSingletonByName() {
    ExitPointBuilder point1 = refBuilder.exitPoint("exitPoint10");
    ExitPointBuilder point2 = refBuilder.exitPoint("exitPoint10");
    assertTrue(point1 == point2);
  }

  @Test
  public void testMergeExitPoints_success() {
    refBuilder.mergeExitPoint(exitPoint1);
    refBuilder.mergeExitPoint(exitPoint2);
  }

  @Test(expected = ConnectionPointBindingException.class)
  public void testMergeExitPoints_fail() {
    try {
      refBuilder.mergeExitPoint(exitPoint3);
    } catch (ConnectionPointBindingException ex) {
      assertEquals("foo::exitPoint3", ex.getName());
      assertEquals(new HashSet<>(Arrays.asList("foo::exitPoint1", "foo::exitPoint2", "foo::exitPoint5")), ex.getCandidates());
      throw ex;
    }
  }
}
