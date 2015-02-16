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

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud
 */
public class SimpleStateMachineStateTest {

  private SimpleStateMachineState machineState;

  @Before
  public void setUp() {
    this.machineState = new SimpleStateMachineState(null);
  }

  @Test(expected = IllegalStateException.class)
  public void testCompletedOne() {
    machineState.completedOne(null);
  }

  @Test(expected = IllegalStateException.class)
  public void testJoinReached() {
    machineState.joinReached(null, null);
  }

  @Test(expected = IllegalStateException.class)
  public void testClearJoin() {
    machineState.clearJoin(null);
  }

  @Test(expected = IllegalStateException.class)
  public void testRestore() {
    machineState.restore(null);
  }

  @Test(expected = IllegalStateException.class)
  public void testSaveDeepHistory() {
    machineState.saveDeepHistory(null);
  }

  @Test(expected = IllegalStateException.class)
  public void testSaveShallowHistory() {
    machineState.saveShallowHistory(null);
  }

  @Test(expected = IllegalStateException.class)
  public void testSaveHistory() {
    machineState.saveHistory(null, null);
  }
}
