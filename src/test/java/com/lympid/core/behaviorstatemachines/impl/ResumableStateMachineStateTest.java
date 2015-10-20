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

import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.StateMachineSnapshot;
import com.lympid.core.behaviorstatemachines.pseudo.history.deep.Test9;
import com.lympid.core.common.StringTree;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud
 */
public class ResumableStateMachineStateTest {

  @Test(expected = VertexNotFoundException.class)
  public void testStateNotFound() {
    MutableStateMachineSnapshot snapshot = new MutableStateMachineSnapshot();
    StringTree tree = new StringTree("100");
    snapshot.setStateConfiguration(tree);

    StateMachineExecutor fsm = fsm(snapshot);
    try {
      fsm.resume();
    } catch (VertexNotFoundException ex) {
      assertEquals("100", ex.getId());
      throw ex;
    }
  }

  @Test(expected = RegionNotFoundException.class)
  public void testHistoryRegionNotFound() {
    MutableStateMachineSnapshot<?> snapshot = new MutableStateMachineSnapshot();
    snapshot.history().put("1", new StringTree("2"));

    StateMachineExecutor fsm = fsm(snapshot);
    try {
      fsm.resume();
    } catch (RegionNotFoundException ex) {
      assertEquals("1", ex.getId());
      throw ex;
    }
  }

  @Test(expected = VertexNotFoundException.class)
  public void testHistoryStateNotFound() {
    MutableStateMachineSnapshot<?> snapshot = new MutableStateMachineSnapshot();
    snapshot.history().put("2", new StringTree("4"));

    StateMachineExecutor fsm = fsm(snapshot);
    try {
      fsm.resume();
    } catch (RegionNotFoundException ex) {
      assertEquals("4", ex.getId());
      throw ex;
    }
  }

  @Test(expected = VertexNotFoundException.class)
  public void testHistoryChildStateNotFound() {
    MutableStateMachineSnapshot<?> snapshot = new MutableStateMachineSnapshot();
    StringTree history = new StringTree("5");
    history.setChildren(Arrays.asList(new StringTree("9")));
    snapshot.history().put("2", history);

    StateMachineExecutor fsm = fsm(snapshot);
    try {
      fsm.resume();
    } catch (VertexNotFoundException ex) {
      assertEquals("9", ex.getId());
      throw ex;
    }
  }

  private StateMachineExecutor fsm(final StateMachineSnapshot snapshot) {
    Test9 test = new Test9();
    test.setUp();
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = new LockStateMachineExecutor.Builder()
      .setStateMachine(test.topLevelStateMachine())
      .setContext(ctx)
      .setSnapshot(snapshot)
      .build();
    fsm.go();
    fsm.pause();
    
    ActiveStateTree active = new ActiveStateTree(test).branch("compo", "A");
    assertSnapshotEquals(fsm, active);
    
    return fsm;
  }
}
