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

  @Test(expected = StateNotFoundException.class)
  public void testStateNotFound() {
    StateMachineExecutor fsm = fsm();

    MutableStateMachineSnapshot snapshot = new MutableStateMachineSnapshot();
    StringTree tree = new StringTree("100");
    snapshot.setStateConfiguration(tree);

    try {
      fsm.resume(snapshot);
    } catch (StateNotFoundException ex) {
      assertEquals("100", ex.getElementId());
      throw ex;
    }
  }

  @Test(expected = RegionNotFoundException.class)
  public void testHistoryRegionNotFound() {
    StateMachineExecutor fsm = fsm();

    MutableStateMachineSnapshot<?> snapshot = new MutableStateMachineSnapshot();
    snapshot.history().put("1", new StringTree("2"));

    try {
      fsm.resume(snapshot);
    } catch (RegionNotFoundException ex) {
      assertEquals("1", ex.getElementId());
      throw ex;
    }
  }

  @Test(expected = StateNotFoundException.class)
  public void testHistoryStateNotFound() {
    StateMachineExecutor fsm = fsm();

    MutableStateMachineSnapshot<?> snapshot = new MutableStateMachineSnapshot();
    snapshot.history().put("2", new StringTree("4"));

    try {
      fsm.resume(snapshot);
    } catch (RegionNotFoundException ex) {
      assertEquals("4", ex.getElementId());
      throw ex;
    }
  }

  @Test(expected = StateNotFoundException.class)
  public void testHistoryChildStateNotFound() {
    StateMachineExecutor fsm = fsm();

    MutableStateMachineSnapshot<?> snapshot = new MutableStateMachineSnapshot();
    StringTree history = new StringTree("5");
    history.setChildren(Arrays.asList(new StringTree("9")));
    snapshot.history().put("2", history);

    try {
      fsm.resume(snapshot);
    } catch (StateNotFoundException ex) {
      assertEquals("9", ex.getElementId());
      throw ex;
    }
  }

  private StateMachineExecutor fsm() {
    Test9 test = new Test9();
    test.setUp();
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = new LockStateMachineExecutor();
    fsm.setStateMachine(test.topLevelStateMachine());
    fsm.setContext(ctx);
    fsm.go();
    fsm.pause();
    
    ActiveStateTree active = new ActiveStateTree(test).branch("compo", "A");
    assertSnapshotEquals(fsm, active);
    
    return fsm;
  }
}
