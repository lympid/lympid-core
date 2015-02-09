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

import com.lympid.core.behaviorstatemachines.StateMachineMeta;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud
 */
public class StateMachineMetaVisitorTest {

  private StateMachineMetaVisitor visitor;

  @Before
  public void setUp() {
    visitor = new StateMachineMetaVisitor();
  }

  @Test
  public void testEmptyStateMachine() {
    MutableStateMachine machine = new MutableStateMachine();
    machine.accept(visitor);
    StateMachineMeta meta = visitor.getMeta();

    assertNotNull(meta);
    assertEquals(0, meta.countOfHistoryNodes());
    assertEquals(0, meta.countOfLeaves());
    assertEquals(0, meta.treeDepth());
    assertFalse(meta.hasActivities());
    assertFalse(meta.hasCompletionEvents());
    assertFalse(meta.hasTimeEvents());
  }

}
