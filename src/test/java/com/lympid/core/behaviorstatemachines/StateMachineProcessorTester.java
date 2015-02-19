/*
 * Copyright 2015 Fabien Renaud.
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

import com.lympid.core.common.StringTree;
import com.lympid.core.common.TreeNode;
import java.util.Comparator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Fabien Renaud
 */
public final class StateMachineProcessorTester {
  
  
  public static void assertSnapshotHistoryEquals(final StateMachineExecutor fsm, final String region, final ActiveStateTree expected) {
    StateMachineSnapshot<?> snapshot = fsm.snapshot();
    assertEquals(fsm.stateMachine().getId(), snapshot.stateMachine());
    
    final String regionId = region.charAt(0) == '#'
      ? region.substring(1)
      : region; // note: names are not supported
    assertSnapshotHistoryEquals(snapshot, regionId, expected.tree());
  }
  
  private static void assertSnapshotHistoryEquals(final StateMachineSnapshot<?> snapshot, final String regionId, final TreeNode<String> expected) {
    assert regionId != null;
    
    assertNotNull(snapshot);
    assertNotNull(snapshot.history());
    StringTree history = snapshot.history().get(regionId);
    assertNotNull(history);
    
    assertStateConfigurationEquals(expected, history);
  }

  public static void assertSnapshotEquals(final StateMachineExecutor fsm, final ActiveStateTree expected) {
    StateMachineSnapshot<?> snapshot = fsm.snapshot();
    assertEquals(fsm.stateMachine().getId(), snapshot.stateMachine());
    assertSnapshotEquals(snapshot, expected.tree());
  }

  public static void assertSnapshotEquals(final StateMachineSnapshot snapshot, final ActiveStateTree expected) {
    assertSnapshotEquals(snapshot, expected.tree());
  }

  private static void assertSnapshotEquals(final StateMachineSnapshot<?> snapshot, final TreeNode<String> expected) {
    assertNotNull(snapshot);
    assertStateConfigurationEquals(expected, snapshot.stateConfiguration());

    if (expected.content() == null) {
      assertNotStartedOrTerminated(snapshot);
    } else if (expected.size() == 0) {
      assertFinalIsTerminated(snapshot);
    }
  }
  
  private static void assertStateConfigurationEquals(final TreeNode<String> expected, final StringTree actual) {
    if (actual == null) {
      assertNull(expected.content());
    } else {
      assertSnapshotEquals(expected, actual);
    }
  }

  private static void assertSnapshotEquals(final TreeNode<String> expected, final StringTree actual) {
    if (expected.content().contains(":")) { // now ids, used to be names
      String wActual = ":" + actual.state() + ":";
      if (!expected.content().contains(wActual)) {
        assertEquals(expected.content(), wActual);
      }
    } else {
        assertEquals(expected.content(), actual.state());
    }
    if (actual.children() == null) {
      assertTrue(expected.children().isEmpty());
      return;
    }
    
    assertEquals(expected.children().size(), actual.children().size());
    expected.children().sort(ACTIVATE_STATE_COMPARATOR);
    actual.children().sort(STATE_COMPARATOR);
    for (int i = 0; i < expected.size(); i++) {
      assertSnapshotEquals(expected.children().get(i), actual.children().get(i));
    }
  }

  private static void assertNotStartedOrTerminated(final StateMachineSnapshot snapshot) {
    assertNull(snapshot.stateConfiguration());
    assertTrue(!snapshot.isStarted() || snapshot.isTerminated());
  }

  private static void assertFinalIsTerminated(final StateMachineSnapshot<?> snapshot) {
    assertNotNull(snapshot.stateConfiguration());
    
    assertNotNull(snapshot.stateConfiguration());
    assertNull(snapshot.stateConfiguration().children());
//    if (tree.state() instanceof FinalState) {
      /*
       * The final state of the top level state machine has been reached. Make
       * sure it has been terminated.
       */
//      assertTrue("State machine is on its top level final state but has not been terminated!", snapshot.isTerminated());
//    }
  }

  private static final Comparator<StringTree> STATE_COMPARATOR = new Comparator<StringTree>() {

    @Override
    public int compare(StringTree o1, StringTree o2) {
      if (o1 == o2) {
        return 0;
      }
      if (o1 == null || o1.state() == null) {
        return -1;
      }
      if (o2 == null || o2.state() == null) {
        return 1;
      }
      return o1.state().compareTo(o2.state());
    }

  };

  private static final Comparator<TreeNode<String>> ACTIVATE_STATE_COMPARATOR = new Comparator<TreeNode<String>>() {

    @Override
    public int compare(TreeNode<String> o1, TreeNode<String> o2) {
      if (o1 == o2) {
        return 0;
      }
      if (o1 == null || o1.content() == null) {
        return -1;
      }
      if (o2 == null || o2.content() == null) {
        return 1;
      }
      if (o1.equals(o2)) {
        return 0;
      }
      return o1.content().compareTo(o2.content());
    }

  };

}
