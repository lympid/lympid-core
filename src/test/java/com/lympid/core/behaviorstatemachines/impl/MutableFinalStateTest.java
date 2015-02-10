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

import java.util.Collections;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class MutableFinalStateTest {

  private MutableFinalState state;

  @Before
  public void test_setUp() {
    state = new MutableFinalState();
  }

  @Test
  public void test_outgoing() {
    assertTrue(Collections.EMPTY_LIST == state.outgoing());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void test_connection() {
    state.connection();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void test_connectionPoint() {
    state.connectionPoint();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void test_deferrableTrigger() {
    state.deferrableTrigger();
  }

  @Test
  public void test_doActivity() {
    assertNull(state.doActivity());
  }

  @Test
  public void test_entry() {
    assertTrue(Collections.EMPTY_LIST == state.entry());
  }

  @Test
  public void test_exit() {
    assertTrue(Collections.EMPTY_LIST == state.exit());
  }

  @Test
  public void test_region() {
    assertTrue(Collections.EMPTY_LIST == state.region());
  }

  @Test
  public void test_subStateMachine() {
    assertNull(state.subStateMachine());
  }
}
