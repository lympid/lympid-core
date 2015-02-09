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
package com.lympid.core.behaviorstatemachines.validation;

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.BiTransitionBehavior;
import com.lympid.core.behaviorstatemachines.BiTransitionConstraint;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.impl.MutablePseudoState;
import com.lympid.core.behaviorstatemachines.impl.MutableRegion;
import com.lympid.core.behaviorstatemachines.impl.MutableState;
import com.lympid.core.behaviorstatemachines.impl.MutableTransition;
import com.lympid.core.common.Trigger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud
 */
public class PseudoStateConstraintsTest {

  /**
   * [1] An initial vertex can have at most one outgoing transition.
   */
  @Test
  public void constraint1_success() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.INITIAL);
    StandardValidator.validate(mps);

    mps.setOutgoing(Arrays.asList(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)));
    StandardValidator.validate(mps);
  }

  /**
   * [1] An initial vertex can have at most one outgoing transition.
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint1_fail() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.INITIAL);
    mps.setOutgoing(Arrays.asList(
      new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL),
      new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)
    ));

    try {
      StandardValidator.validate(mps);
    } catch (PseudoStateConstraintException ex) {
      assertEquals(mps, ex.getPseudoState());
      throw ex;
    }
  }

  /**
   * [2] History vertices can have at most one outgoing transition.
   */
  @Test
  public void constraint2_shallow_success() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.SHALLOW_HISTORY);
    StandardValidator.validate(mps);

    mps.setOutgoing(Arrays.asList(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)));
    StandardValidator.validate(mps);
  }

  /**
   * [2] History vertices can have at most one outgoing transition.
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint2_shallow_fail() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.SHALLOW_HISTORY);
    mps.setOutgoing(Arrays.asList(
      new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL),
      new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)
    ));
    StandardValidator.validate(mps);
  }

  /**
   * [2] History vertices can have at most one outgoing transition.
   */
  @Test
  public void constraint2_deep_success() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.DEEP_HISTORY);
    StandardValidator.validate(mps);

    mps.setOutgoing(Arrays.asList(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)));
    StandardValidator.validate(mps);
  }

  /**
   * [2] History vertices can have at most one outgoing transition.
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint2_deep_fail() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.DEEP_HISTORY);
    mps.setOutgoing(Arrays.asList(
      new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL),
      new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)
    ));
    StandardValidator.validate(mps);
  }

  /**
   * [3] In a complete statemachine, a join vertex must have at least two
   * incoming transitions and exactly one outgoing transition.
   * [4] All transitions incoming a join vertex must originate in different
   * regions of an orthogonal state.
   */
  @Test
  public void constraint3and4_success() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.JOIN);
    mps.setOutgoing(Arrays.asList(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)));

    MutableState orthogonalState = new MutableState();

    List<Region> regions = new ArrayList<>();
    MutableRegion r = new MutableRegion();
    regions.add(r);
    r.setState(orthogonalState);
    orthogonalState.setRegions(regions);

    MutableState source = new MutableState();
    r.addVertex(source);

    mps.incoming().add(new MutableTransition(r, source, null, null, null, TransitionKind.EXTERNAL));
    for (int i = 0; i < 5; i++) {
      r = new MutableRegion();
      regions.add(r);
      r.setState(orthogonalState);
      orthogonalState.setRegions(regions);

      source = new MutableState();
      r.addVertex(source);

      mps.incoming().add(new MutableTransition(r, source, null, null, null, TransitionKind.EXTERNAL));
      StandardValidator.validate(mps);
    }
  }

  /**
   * [3] In a complete statemachine, a join vertex must have at least two
   * incoming transitions and exactly one outgoing transition.
   *
   * Tests when a join does not have enough incoming transitions.
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint3_fail_1() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.JOIN);
    mps.setOutgoing(Arrays.asList(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)));

    MutableState orthogonalState = new MutableState();

    List<Region> regions = new ArrayList<>();
    MutableRegion r = new MutableRegion();
    regions.add(r);
    r.setState(orthogonalState);
    orthogonalState.setRegions(regions);

    MutableState source = new MutableState();
    r.addVertex(source);

    mps.incoming().add(new MutableTransition(r, source, null, null, null, TransitionKind.EXTERNAL));
    StandardValidator.validate(mps);
  }

  /**
   * [3] In a complete statemachine, a join vertex must have at least two
   * incoming transitions and exactly one outgoing transition.
   *
   * Tests when a join does not have any outgoing transitions.
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint3_fail_2() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.JOIN);

    MutableState orthogonalState = new MutableState();

    List<Region> regions = new ArrayList<>();
    MutableRegion r = new MutableRegion();
    regions.add(r);
    r.setState(orthogonalState);
    orthogonalState.setRegions(regions);

    MutableState source = new MutableState();
    r.addVertex(source);

    mps.incoming().add(new MutableTransition(r, source, null, null, null, TransitionKind.EXTERNAL));
    for (int i = 0; i < 5; i++) {
      r = new MutableRegion();
      regions.add(r);
      r.setState(orthogonalState);
      orthogonalState.setRegions(regions);

      source = new MutableState();
      r.addVertex(source);

      mps.incoming().add(new MutableTransition(r, source, null, null, null, TransitionKind.EXTERNAL));
      StandardValidator.validate(mps);
    }
  }

  /**
   * [3] In a complete statemachine, a join vertex must have at least two
   * incoming transitions and exactly one outgoing transition.
   *
   * Tests when a join has too many outgoing transitions.
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint3_fail_3() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.JOIN);
    mps.setOutgoing(Arrays.asList(
      new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL),
      new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)
    ));

    MutableState orthogonalState = new MutableState();

    List<Region> regions = new ArrayList<>();
    MutableRegion r = new MutableRegion();
    regions.add(r);
    r.setState(orthogonalState);
    orthogonalState.setRegions(regions);

    MutableState source = new MutableState();
    r.addVertex(source);

    mps.incoming().add(new MutableTransition(r, source, null, null, null, TransitionKind.EXTERNAL));
    for (int i = 0; i < 5; i++) {
      r = new MutableRegion();
      regions.add(r);
      r.setState(orthogonalState);
      orthogonalState.setRegions(regions);

      source = new MutableState();
      r.addVertex(source);

      mps.incoming().add(new MutableTransition(r, source, null, null, null, TransitionKind.EXTERNAL));
      StandardValidator.validate(mps);
    }
  }

  /**
   * [4] All transitions incoming a join vertex must originate in different
   * regions of an orthogonal state.
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint4_fail() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.JOIN);
    mps.setOutgoing(Arrays.asList(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)));

    MutableState orthogonalState = new MutableState();

    List<Region> regions = new ArrayList<>();
    MutableRegion r = new MutableRegion();
    regions.add(r);
    r.setState(orthogonalState);
    orthogonalState.setRegions(regions);

    MutableState source = new MutableState();
    r.addVertex(source);

    mps.incoming().add(new MutableTransition(r, source, null, null, null, TransitionKind.EXTERNAL));
    for (int i = 0; i < 5; i++) {
      r = new MutableRegion();
      regions.add(r);
      r.setState(orthogonalState);
      orthogonalState.setRegions(regions);

      source = new MutableState();
      r.addVertex(source);

      mps.incoming().add(new MutableTransition(r, source, null, null, null, TransitionKind.EXTERNAL));
      StandardValidator.validate(mps);
    }
    r = new MutableRegion();
    regions.add(r);
    r.setState(orthogonalState);
    orthogonalState.setRegions(regions);

    mps.incoming().add(new MutableTransition(r, source, null, null, null, TransitionKind.EXTERNAL));
    StandardValidator.validate(mps);
  }

  /**
   * [5] In a complete statemachine, a fork vertex must have at least two
   * outgoing transitions and exactly one incoming transition.
   * [6] All transitions outgoing a fork vertex must target states in different
   * regions of an orthogonal state.
   */
  @Test
  public void constraint5and6_success() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.FORK);
    mps.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));

    MutableState orthogonalState = new MutableState();

    List<Region> regions = new ArrayList<>();
    MutableRegion r = new MutableRegion();
    regions.add(r);
    r.setState(orthogonalState);
    orthogonalState.setRegions(regions);

    MutableState target = new MutableState();
    r.addVertex(target);

    List<Transition> outgoing = new ArrayList<>();
    outgoing.add(new MutableTransition(r, null, target, null, null, TransitionKind.EXTERNAL));
    mps.setOutgoing(outgoing);
    for (int i = 0; i < 5; i++) {
      r = new MutableRegion();
      regions.add(r);
      r.setState(orthogonalState);
      orthogonalState.setRegions(regions);

      target = new MutableState();
      r.addVertex(target);

      outgoing.add(new MutableTransition(r, null, target, null, null, TransitionKind.EXTERNAL));
      mps.setOutgoing(outgoing);
      StandardValidator.validate(mps);
    }
  }

  /**
   * [5] In a complete statemachine, a fork vertex must have at least two
   * outgoing transitions and exactly one incoming transition.
   *
   * Tests when a fork has not enough incoming transition.
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint5_fail_1() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.FORK);

    MutableState orthogonalState = new MutableState();

    List<Region> regions = new ArrayList<>();
    MutableRegion r = new MutableRegion();
    regions.add(r);
    r.setState(orthogonalState);
    orthogonalState.setRegions(regions);

    MutableState target = new MutableState();
    r.addVertex(target);

    List<Transition> outgoing = new ArrayList<>();
    outgoing.add(new MutableTransition(r, null, target, null, null, TransitionKind.EXTERNAL));
    mps.setOutgoing(outgoing);
    for (int i = 0; i < 1; i++) {
      r = new MutableRegion();
      regions.add(r);
      r.setState(orthogonalState);
      orthogonalState.setRegions(regions);

      target = new MutableState();
      r.addVertex(target);

      outgoing.add(new MutableTransition(r, null, target, null, null, TransitionKind.EXTERNAL));
      mps.setOutgoing(outgoing);
      StandardValidator.validate(mps);
    }
  }

  /**
   * [5] In a complete statemachine, a fork vertex must have at least two
   * outgoing transitions and exactly one incoming transition.
   *
   * Tests when a fork has too many incoming transitions.
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint5_fail_2() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.FORK);
    mps.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));
    mps.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));

    MutableState orthogonalState = new MutableState();

    List<Region> regions = new ArrayList<>();
    MutableRegion r = new MutableRegion();
    regions.add(r);
    r.setState(orthogonalState);
    orthogonalState.setRegions(regions);

    MutableState target = new MutableState();
    r.addVertex(target);

    List<Transition> outgoing = new ArrayList<>();
    outgoing.add(new MutableTransition(r, null, target, null, null, TransitionKind.EXTERNAL));
    mps.setOutgoing(outgoing);
    for (int i = 0; i < 1; i++) {
      r = new MutableRegion();
      regions.add(r);
      r.setState(orthogonalState);
      orthogonalState.setRegions(regions);

      target = new MutableState();
      r.addVertex(target);

      outgoing.add(new MutableTransition(r, null, target, null, null, TransitionKind.EXTERNAL));
      mps.setOutgoing(outgoing);
      StandardValidator.validate(mps);
    }
  }

  /**
   * [5] In a complete statemachine, a fork vertex must have at least two
   * outgoing transitions and exactly one incoming transition.
   *
   * Tests when a fork has not enough outgoing transitions.
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint5_fail_3() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.FORK);
    mps.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));

    MutableState orthogonalState = new MutableState();

    List<Region> regions = new ArrayList<>();
    MutableRegion r = new MutableRegion();
    regions.add(r);
    r.setState(orthogonalState);
    orthogonalState.setRegions(regions);

    MutableState target = new MutableState();
    r.addVertex(target);

    List<Transition> outgoing = new ArrayList<>();
    outgoing.add(new MutableTransition(r, null, target, null, null, TransitionKind.EXTERNAL));
    mps.setOutgoing(outgoing);
    StandardValidator.validate(mps);
  }

  /**
   * [6] All transitions outgoing a fork vertex must target states in different
   * regions of an orthogonal state.
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint6_fail() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.FORK);
    mps.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));

    MutableState orthogonalState = new MutableState();

    List<Region> regions = new ArrayList<>();
    MutableRegion r = new MutableRegion();
    regions.add(r);
    r.setState(orthogonalState);
    orthogonalState.setRegions(regions);

    MutableState target = new MutableState();
    r.addVertex(target);

    List<Transition> outgoing = new ArrayList<>();
    outgoing.add(new MutableTransition(r, null, target, null, null, TransitionKind.EXTERNAL));
    mps.setOutgoing(outgoing);
    for (int i = 0; i < 5; i++) {
      r = new MutableRegion();
      regions.add(r);
      r.setState(orthogonalState);
      orthogonalState.setRegions(regions);

      target = new MutableState();
      r.addVertex(target);

      outgoing.add(new MutableTransition(r, null, target, null, null, TransitionKind.EXTERNAL));
      mps.setOutgoing(outgoing);
      StandardValidator.validate(mps);
    }

    outgoing.add(new MutableTransition(r, null, target, null, null, TransitionKind.EXTERNAL));
    mps.setOutgoing(outgoing);
    StandardValidator.validate(mps);
  }

  /**
   * [7] In a complete statemachine, a junction vertex must have at least one
   * incoming and one outgoing transition.
   */
  @Test
  public void constraint7_success() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.JUNCTION);
    mps.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));
    mps.setOutgoing(Arrays.asList(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)));
    StandardValidator.validate(mps);

    for (int i = 0; i < 3; i++) {
      mps.setOutgoing(Arrays.asList(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)));
      StandardValidator.validate(mps);
    }

    for (int i = 0; i < 5; i++) {
      mps.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));
      StandardValidator.validate(mps);
    }
  }

  /**
   * [7] In a complete statemachine, a junction vertex must have at least one
   * incoming and one outgoing transition.
   *
   * Tests when a junction has not enough incoming transitions
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint7_fail_1() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.JUNCTION);
    mps.setOutgoing(Arrays.asList(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)));
    StandardValidator.validate(mps);
  }

  /**
   * [7] In a complete statemachine, a junction vertex must have at least one
   * incoming and one outgoing transition.
   *
   * Tests when a junction has not enough outgoing transitions
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint7_fail_2() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.JUNCTION);
    mps.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));
    StandardValidator.validate(mps);
  }

  /**
   * [7] In a complete statemachine, a junction vertex must have at least one
   * incoming and one outgoing transition.
   *
   * Tests when a junction has no transitions
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint7_fail_3() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.JUNCTION);
    StandardValidator.validate(mps);
  }

  /**
   * [8] In a complete statemachine, a choice vertex must have at least one
   * incoming and one outgoing transition.
   */
  @Test
  public void constraint8_success() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.CHOICE);
    mps.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));
    mps.setOutgoing(Arrays.asList(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)));
    StandardValidator.validate(mps);

    for (int i = 0; i < 3; i++) {
      mps.setOutgoing(Arrays.asList(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)));
      StandardValidator.validate(mps);
    }

    for (int i = 0; i < 5; i++) {
      mps.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));
      StandardValidator.validate(mps);
    }
  }

  /**
   * [8] In a complete statemachine, a choice vertex must have at least one
   * incoming and one outgoing transition.
   *
   * Tests when a choice has not enough incoming transitions
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint8_fail_1() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.CHOICE);
    mps.setOutgoing(Arrays.asList(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)));
    StandardValidator.validate(mps);
  }

  /**
   * [8] In a complete statemachine, a choice vertex must have at least one
   * incoming and one outgoing transition.
   *
   * Tests when a choice has not enough outgoing transitions
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint8_fail_2() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.CHOICE);
    mps.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));
    StandardValidator.validate(mps);
  }

  /**
   * [8] In a complete statemachine, a choice vertex must have at least one
   * incoming and one outgoing transition.
   *
   * Tests when a choice has no transitions
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint8_fail_3() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.CHOICE);
    StandardValidator.validate(mps);
  }

  /**
   * [9] The outgoing transition from an initial vertex may have a behavior, but
   * not a trigger or guard.
   */
  @Test
  public void constraint9_success() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.INITIAL);
    StandardValidator.validate(mps);

    mps.setOutgoing(Arrays.asList(new MutableTransition(null, mps, null, null, EMPTY_BEHAVIOR, TransitionKind.EXTERNAL)));
    StandardValidator.validate(mps);
  }

  /**
   * [9] The outgoing transition from an initial vertex may have a behavior, but
   * not a trigger or guard.
   *
   * Tests the outgoing transition of an initial vertex can not have a guard.
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint9_fail_1() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.INITIAL);
    StandardValidator.validate(mps);

    mps.setOutgoing(Arrays.asList(new MutableTransition(null, mps, null, EMPTY_GUARD, EMPTY_BEHAVIOR, TransitionKind.EXTERNAL)));
    StandardValidator.validate(mps);
  }

  /**
   * [9] The outgoing transition from an initial vertex may have a behavior, but
   * not a trigger or guard.
   *
   * Tests the outgoing transition of an initial vertex can not have a trigger.
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint9_fail_2() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.INITIAL);
    StandardValidator.validate(mps);

    MutableTransition transition = new MutableTransition(null, mps, null, null, EMPTY_BEHAVIOR, TransitionKind.EXTERNAL);
    transition.triggers().add(new Trigger(new StringEvent("hi")));
    mps.setOutgoing(Arrays.asList(transition));
    StandardValidator.validate(mps);
  }

  /**
   * [9] The outgoing transition from an initial vertex may have a behavior, but
   * not a trigger or guard.
   *
   * Tests the outgoing transition of an initial vertex can not have a trigger
   * nor a guard.
   */
  @Test(expected = PseudoStateConstraintException.class)
  public void constraint9_fail_3() {
    MutablePseudoState mps = new MutablePseudoState(PseudoStateKind.INITIAL);
    StandardValidator.validate(mps);

    MutableTransition transition = new MutableTransition(null, mps, null, EMPTY_GUARD, EMPTY_BEHAVIOR, TransitionKind.EXTERNAL);
    transition.triggers().add(new Trigger(new StringEvent("hi")));
    mps.setOutgoing(Arrays.asList(transition));
    StandardValidator.validate(mps);
  }

  private static final BiTransitionBehavior EMPTY_BEHAVIOR = (BiTransitionBehavior) (Object t, Object u) -> {
  };

  private static final BiTransitionConstraint EMPTY_GUARD = (BiTransitionConstraint) (Object t, Object u) -> {
    return false;
  };
}
