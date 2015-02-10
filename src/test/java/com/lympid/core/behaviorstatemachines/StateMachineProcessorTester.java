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

import com.lympid.core.behaviorstatemachines.impl.StateMachineSnapshot;
import com.lympid.core.common.TreeNode;
import java.util.Comparator;
import java.util.function.BiPredicate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Fabien Renaud 
 */
public final class StateMachineProcessorTester {

  public static void assertStateConfiguration(final StateMachineExecutor fsm, final ActiveStateTree expected) {
    assertStateConfiguration(fsm, expected.get());
  }

  public static void assertStateConfiguration(final StateMachineSnapshot snapshot, final ActiveStateTree expected) {
    assertStateConfiguration(snapshot, expected.get());
  }

  public static void assertStateConfiguration(final StateMachineExecutor fsm, final TreeNode<ActiveState> expected) {
    assertStateConfiguration(fsm.snapshot(), expected);
  }
  
  public static void assertStateConfiguration(final StateMachineSnapshot snapshot, final TreeNode<ActiveState> expected) {
    assertNotNull(snapshot);
    assertNotNull(snapshot.activateStates());
    assertTreeNodeEquals(expected, snapshot.activateStates());

    if (expected.content() == null) {
      assertNotStartedOrTerminated(snapshot);
    } else if (expected.size() == 0) {
      assertFinalIsTerminated(snapshot);
    }
  }

  private static void assertTreeNodeEquals(final TreeNode<ActiveState> expected, final TreeNode<State> actual) {
    assertTrue("Expected: " + expected.content() + "; got: " + actual.content(), ACTIVE_STATE_PREDICATE.test(expected.content(), actual.content()));
    assertEquals(expected.size(), actual.size());
    expected.children().sort(ACTIVATE_STATE_COMPARATOR);
    actual.children().sort(STATE_COMPARATOR);
    for (int i = 0; i < expected.size(); i++) {
      assertTreeNodeEquals(expected.children().get(i), actual.children().get(i));
    }
  }

  private static void assertNotStartedOrTerminated(final StateMachineSnapshot snapshot) {
    assert snapshot.activateStates().content() == null; // reminder, precondition
    assertEquals(0, snapshot.activateStates().size());
    assertTrue(!snapshot.isStarted() || snapshot.isTerminated());
  }

  private static void assertFinalIsTerminated(final StateMachineSnapshot snapshot) {
    assert snapshot.activateStates().content() != null; // reminder, precondition
    assertEquals(0, snapshot.activateStates().size());
    if (snapshot.activateStates().content() instanceof FinalState) {
      /*
       * The final state of the top level state machine has been reached. Make
       * sure it has been terminated.
       */
      assertTrue("State machine is on its top level final state but has not been terminated!", snapshot.isTerminated());
    }
  }

  private static final Comparator<TreeNode<State>> STATE_COMPARATOR = new Comparator<TreeNode<State>>() {

    @Override
    public int compare(TreeNode<State> o1, TreeNode<State> o2) {
      if (o1 == o2) {
        return 0;
      }
      if (o1 == null || o1.content() == null) {
        return -1;
      }
      if (o2 == null || o2.content() == null) {
        return 1;
      }
      State c1 = o1.content();
      State c2 = o2.content();
      if (c1.equals(c2)) {
        return 0;
      }
      if (c1.getName() != null && c2.getName() != null) {
        return c1.getName().compareTo(c2.getName());
      }
      return c1.getId().compareTo(c2.getId());
    }

  };

  private static final Comparator<TreeNode<ActiveState>> ACTIVATE_STATE_COMPARATOR = new Comparator<TreeNode<ActiveState>>() {

    @Override
    public int compare(TreeNode<ActiveState> o1, TreeNode<ActiveState> o2) {
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
      ActiveState c1 = o1.content();
      ActiveState c2 = o2.content();
      if (c1.equals(c2)) {
        return 0;
      }
      if (c1.getName() != null && c2.getName() != null) {
        return c1.getName().compareTo(c2.getName());
      }
      return Integer.compare(c1.getId(), c2.getId());
    }

  };
  
  private static final BiPredicate<ActiveState, State> ACTIVE_STATE_PREDICATE = new BiPredicate<ActiveState, State>() {

    @Override
    public boolean test(ActiveState expected, State actual) {
      if (expected == null) {
        return actual == null;
      }
      if (actual == null) {
        return false;
      }
      if (expected.getName() != null) {
        return expected.getName().equals(actual.getName());
      }
      return Integer.toString(expected.getId()).equals(actual.getId());
    }
  };
    
}
