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
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud
 */
public class MutableStateMachineMetaTest {

  private MutableStateMachineMeta meta;

  @Before
  public void setUp() {
    this.meta = new MutableStateMachineMeta();
  }

  @Test
  public void testCountOf() {
    Random rnd = new Random();

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
  public void registerSubmachineState() {
    MutableStateMachine machine = new MutableStateMachine();
    MutableState state = new MutableState();
    state.setSubStateMachine(machine);
    
    assertFalse(meta.hasSubmachineStates());
    meta.register(state);
    assertTrue(meta.hasSubmachineStates());
  }

}
