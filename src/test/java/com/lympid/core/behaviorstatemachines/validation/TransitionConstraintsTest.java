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

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.BiTransitionBehavior;
import com.lympid.core.behaviorstatemachines.BiTransitionConstraint;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.impl.MutablePseudoState;
import com.lympid.core.behaviorstatemachines.impl.MutableRegion;
import com.lympid.core.behaviorstatemachines.impl.MutableState;
import com.lympid.core.behaviorstatemachines.impl.MutableTransition;
import com.lympid.core.common.Trigger;
import org.junit.Test;

import java.util.Arrays;

import static com.lympid.core.common.TestUtils.randomPseudoState;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author Fabien Renaud
 */
public class TransitionConstraintsTest {

  /**
   * [1] A fork segment must not have guards or triggers.
   */
  @Test
  public void constraint1_success() {
    MutablePseudoState fork = new MutablePseudoState(PseudoStateKind.FORK);
    fork.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));

    MutableState orthogonalState = new MutableState();

    MutableRegion r1 = new MutableRegion();
    MutableRegion r2 = new MutableRegion();
    r1.setState(orthogonalState);
    r2.setState(orthogonalState);
    orthogonalState.setRegions(Arrays.asList(r1, r2));

    MutableState target1 = new MutableState();
    MutableState target2 = new MutableState();
    r2.addVertex(target2);
    r1.addVertex(target1);

    fork.setOutgoing(Arrays.asList(
      new MutableTransition(r1, fork, target1, null, EMPTY_BEHAVIOR, TransitionKind.EXTERNAL),
      new MutableTransition(r2, fork, target2, null, null, TransitionKind.EXTERNAL)
    ));

    test(fork);
  }

  /**
   * [1] A fork segment must not have guards or triggers.
   *
   * Tests a fork segment can not have a guard.
   */
  @Test(expected = TransitionConstraintException.class)
  public void constraint1_fail_1() {
    MutablePseudoState fork = new MutablePseudoState(PseudoStateKind.FORK);
    fork.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));

    MutableState orthogonalState = new MutableState();

    MutableRegion r1 = new MutableRegion();
    MutableRegion r2 = new MutableRegion();
    r1.setState(orthogonalState);
    r2.setState(orthogonalState);
    orthogonalState.setRegions(Arrays.asList(r1, r2));

    MutableState target1 = new MutableState();
    MutableState target2 = new MutableState();
    r2.addVertex(target2);
    r1.addVertex(target1);

    fork.setOutgoing(Arrays.asList(
      new MutableTransition(r1, fork, target1, null, EMPTY_BEHAVIOR, TransitionKind.EXTERNAL),
      new MutableTransition(r2, fork, target2, EMPTY_GUARD, null, TransitionKind.EXTERNAL)
    ));

    test(fork);
  }

  /**
   *
   * [1] A fork segment must not have guards or triggers.
   *
   * Tests a fork segment can not have a trigger.
   */
  @Test(expected = TransitionConstraintException.class)
  public void constraint1_fail_2() {
    MutablePseudoState fork = new MutablePseudoState(PseudoStateKind.FORK);
    fork.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));

    MutableState orthogonalState = new MutableState();

    MutableRegion r1 = new MutableRegion();
    MutableRegion r2 = new MutableRegion();
    r1.setState(orthogonalState);
    r2.setState(orthogonalState);
    orthogonalState.setRegions(Arrays.asList(r1, r2));

    MutableState target1 = new MutableState();
    MutableState target2 = new MutableState();
    r2.addVertex(target2);
    r1.addVertex(target1);

    MutableTransition t2 = new MutableTransition(r2, fork, target2, null, null, TransitionKind.EXTERNAL);
    t2.triggers().add(new Trigger(new StringEvent("hi")));
    fork.setOutgoing(Arrays.asList(new MutableTransition(r1, fork, target1, null, EMPTY_BEHAVIOR, TransitionKind.EXTERNAL), t2));

    test(fork);
  }

  /**
   * [1] A fork segment must not have guards or triggers.
   *
   * Tests a fork segment can not have a trigger nor a guard.
   */
  @Test(expected = TransitionConstraintException.class)
  public void constraint1_fail_3() {
    MutablePseudoState fork = new MutablePseudoState(PseudoStateKind.FORK);
    fork.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));

    MutableState orthogonalState = new MutableState();

    MutableRegion r1 = new MutableRegion();
    MutableRegion r2 = new MutableRegion();
    r1.setState(orthogonalState);
    r2.setState(orthogonalState);
    orthogonalState.setRegions(Arrays.asList(r1, r2));

    MutableState target1 = new MutableState();
    MutableState target2 = new MutableState();
    r2.addVertex(target2);
    r1.addVertex(target1);

    MutableTransition t2 = new MutableTransition(r2, fork, target2, EMPTY_GUARD, EMPTY_BEHAVIOR, TransitionKind.EXTERNAL);
    t2.triggers().add(new Trigger(new StringEvent("hi")));
    fork.setOutgoing(Arrays.asList(new MutableTransition(r1, fork, target1, null, EMPTY_BEHAVIOR, TransitionKind.EXTERNAL), t2));

    try {
    test(fork);
    } catch (TransitionConstraintException ex) {
      assertEquals(t2, ex.getTransition());
      throw ex;
    }
  }

  /**
   * [2] A join segment must not have guards or triggers.
   */
  @Test
  public void constraint2_success() {
    MutablePseudoState join = new MutablePseudoState(PseudoStateKind.JOIN);
    join.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));

    MutableState orthogonalState = new MutableState();

    MutableRegion r1 = new MutableRegion();
    MutableRegion r2 = new MutableRegion();
    r1.setState(orthogonalState);
    r2.setState(orthogonalState);
    orthogonalState.setRegions(Arrays.asList(r1, r2));

    MutableState source1 = new MutableState();
    MutableState source2 = new MutableState();
    r2.addVertex(source2);
    r1.addVertex(source1);

    join.setOutgoing(Arrays.asList(
      new MutableTransition(r1, source1, join, null, EMPTY_BEHAVIOR, TransitionKind.EXTERNAL),
      new MutableTransition(r2, source2, join, null, null, TransitionKind.EXTERNAL)
    ));

    test(join);
  }

  /**
   * [2] A join segment must not have guards or triggers.
   *
   * Tests a fork segment can not have a guard.
   */
  @Test(expected = TransitionConstraintException.class)
  public void constraint2_fail_1() {
    MutablePseudoState join = new MutablePseudoState(PseudoStateKind.JOIN);
    join.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));

    MutableState orthogonalState = new MutableState();

    MutableRegion r1 = new MutableRegion();
    MutableRegion r2 = new MutableRegion();
    r1.setState(orthogonalState);
    r2.setState(orthogonalState);
    orthogonalState.setRegions(Arrays.asList(r1, r2));

    MutableState source1 = new MutableState();
    MutableState source2 = new MutableState();
    r2.addVertex(source2);
    r1.addVertex(source1);

    join.setOutgoing(Arrays.asList(
      new MutableTransition(r1, source1, join, null, EMPTY_BEHAVIOR, TransitionKind.EXTERNAL),
      new MutableTransition(r2, source2, join, EMPTY_GUARD, null, TransitionKind.EXTERNAL)
    ));

    test(join);
  }

  /**
   * [2] A join segment must not have guards or triggers.
   *
   * Tests a fork segment can not have a trigger.
   */
  @Test(expected = TransitionConstraintException.class)
  public void constraint2_fail_2() {
    MutablePseudoState join = new MutablePseudoState(PseudoStateKind.JOIN);
    join.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));

    MutableState orthogonalState = new MutableState();

    MutableRegion r1 = new MutableRegion();
    MutableRegion r2 = new MutableRegion();
    r1.setState(orthogonalState);
    r2.setState(orthogonalState);
    orthogonalState.setRegions(Arrays.asList(r1, r2));

    MutableState source1 = new MutableState();
    MutableState source2 = new MutableState();
    r2.addVertex(source2);
    r1.addVertex(source1);

    MutableTransition t1 = new MutableTransition(r1, source1, join, null, EMPTY_BEHAVIOR, TransitionKind.EXTERNAL);
    t1.triggers().add(new Trigger(new StringEvent("hi")));
    join.setOutgoing(Arrays.asList(t1, new MutableTransition(r2, source2, join, null, null, TransitionKind.EXTERNAL)));

    test(join);
  }

  /**
   * [2] A join segment must not have guards or triggers.
   *
   * Tests a fork segment can not have a trigger nor a guard.
   */
  @Test(expected = TransitionConstraintException.class)
  public void constraint2_fail_3() {
    MutablePseudoState join = new MutablePseudoState(PseudoStateKind.JOIN);
    join.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));

    MutableState orthogonalState = new MutableState();

    MutableRegion r1 = new MutableRegion();
    MutableRegion r2 = new MutableRegion();
    r1.setState(orthogonalState);
    r2.setState(orthogonalState);
    orthogonalState.setRegions(Arrays.asList(r1, r2));

    MutableState source1 = new MutableState();
    MutableState source2 = new MutableState();
    r2.addVertex(source2);
    r1.addVertex(source1);

    MutableTransition t1 = new MutableTransition(r1, source1, join, EMPTY_GUARD, EMPTY_BEHAVIOR, TransitionKind.EXTERNAL);
    t1.triggers().add(new Trigger(new StringEvent("hi")));
    join.setOutgoing(Arrays.asList(t1, new MutableTransition(r2, source2, join, null, null, TransitionKind.EXTERNAL)));

    test(join);
  }

  /**
   * [3] A fork segment must always target a state.
   */
  @Test(expected = TransitionConstraintException.class)
  public void constraint3_fail() {
    MutablePseudoState fork = new MutablePseudoState(PseudoStateKind.FORK);
    fork.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));

    MutableState orthogonalState = new MutableState();

    MutableRegion r1 = new MutableRegion();
    MutableRegion r2 = new MutableRegion();
    r1.setState(orthogonalState);
    r2.setState(orthogonalState);
    orthogonalState.setRegions(Arrays.asList(r1, r2));

    MutableState target1 = new MutableState();
    MutablePseudoState target2 = randomPseudoState();
    r2.addVertex(target2);
    r1.addVertex(target1);

    fork.setOutgoing(Arrays.asList(
      new MutableTransition(r1, fork, target1, null, EMPTY_BEHAVIOR, TransitionKind.EXTERNAL),
      new MutableTransition(r2, fork, target2, null, null, TransitionKind.EXTERNAL)
    ));

    test(fork);
  }

  /**
   * [4] A join segment must always originate from a state.
   */
  @Test(expected = TransitionConstraintException.class)
  public void constraint4_fail() {
    MutablePseudoState join = new MutablePseudoState(PseudoStateKind.JOIN);
    join.incoming().add(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL));

    MutableState orthogonalState = new MutableState();

    MutableRegion r1 = new MutableRegion();
    MutableRegion r2 = new MutableRegion();
    r1.setState(orthogonalState);
    r2.setState(orthogonalState);
    orthogonalState.setRegions(Arrays.asList(r1, r2));

    MutableState source1 = new MutableState();
    MutablePseudoState source2 = randomPseudoState();
    r2.addVertex(source2);
    r1.addVertex(source1);

    join.setOutgoing(Arrays.asList(
      new MutableTransition(r1, source1, join, null, EMPTY_BEHAVIOR, TransitionKind.EXTERNAL),
      new MutableTransition(r2, source2, join, null, null, TransitionKind.EXTERNAL)
    ));

    test(join);
  }

  /**
   * [5] Transitions outgoing pseudostates may not have a trigger (except for
   * those coming out of the initial pseudostate).
   * [6] An initial transition at the topmost level (region of a statemachine)
   * either has no trigger or it has a trigger with the
   * stereotype “create.”
   *
   * Since stereotypes are not supported, transitions outgoing pseudostates may
   * never have a trigger.
   */
  @Test
  public void constraint5and6_fail() {
    for (PseudoStateKind kind : PseudoStateKind.values()) {
      MutablePseudoState ps = new MutablePseudoState(kind);

      MutableTransition t = new MutableTransition(null, ps, null, null, null, TransitionKind.EXTERNAL);
      t.triggers().add(new Trigger(null));
      ps.setOutgoing(Arrays.asList(t));
      try {
        test(ps);
        fail();
      } catch (TransitionConstraintException ex) {
      }
    }
  }

  /*
   * TODO
   * [7] In case of more than one trigger, the signatures of these must be
   * compatible in case the parameters of the signal are assigned to local
   * variables/attributes.
   */
  /*
   * TODO
   * [8] The redefinition context of a transition is the nearest containing
   * statemachine.
   */
  private void test(final Vertex v) {
    for (Transition t : v.outgoing()) {
      StandardValidator.validate(t);
    }
  }

  private static final BiTransitionBehavior EMPTY_BEHAVIOR = (t, u) -> {
  };

  private static final BiTransitionConstraint EMPTY_GUARD = (t, u) -> false;
}
