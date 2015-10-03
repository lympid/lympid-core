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
package com.lympid.core.behaviorstatemachines;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class PseudoStateKindTest {

  @Test
  public void valuesOf_success() {
    assertEquals(PseudoStateKind.CHOICE, PseudoStateKind.valueOf("CHOICE"));
    assertEquals(PseudoStateKind.DEEP_HISTORY, PseudoStateKind.valueOf("DEEP_HISTORY"));
    assertEquals(PseudoStateKind.ENTRY_POINT, PseudoStateKind.valueOf("ENTRY_POINT"));
    assertEquals(PseudoStateKind.EXIT_POINT, PseudoStateKind.valueOf("EXIT_POINT"));
    assertEquals(PseudoStateKind.FORK, PseudoStateKind.valueOf("FORK"));
    assertEquals(PseudoStateKind.INITIAL, PseudoStateKind.valueOf("INITIAL"));
    assertEquals(PseudoStateKind.JOIN, PseudoStateKind.valueOf("JOIN"));
    assertEquals(PseudoStateKind.JUNCTION, PseudoStateKind.valueOf("JUNCTION"));
    assertEquals(PseudoStateKind.SHALLOW_HISTORY, PseudoStateKind.valueOf("SHALLOW_HISTORY"));
    assertEquals(PseudoStateKind.TERMINATE, PseudoStateKind.valueOf("TERMINATE"));
  }

  @Test(expected = NullPointerException.class)
  public void valueOf_fail() {
    PseudoStateKind.valueOf(null);
  }
}
