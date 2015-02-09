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

import com.lympid.core.behaviorstatemachines.FinalState;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.impl.MutableFinalState;
import com.lympid.core.behaviorstatemachines.impl.MutableRegion;
import com.lympid.core.behaviorstatemachines.impl.MutableState;
import com.lympid.core.behaviorstatemachines.impl.MutableStateMachine;
import com.lympid.core.behaviorstatemachines.impl.MutableTransition;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud
 */
public class FinalStateConstraintsTest {

  /**
   * [1] A final state cannot have any outgoing transitions.
   */
  @Test
  public void all_success() {
    FinalState fs = new MutableFinalState();
    StandardValidator.validate(fs);

    fs = new FreeFinalState();
    StandardValidator.validate(fs);
  }

  /**
   * [1] A final state cannot have any outgoing transitions.
   */
  @Test(expected = FinalStateConstraintException.class)
  public void constraint1_fail() {
    FreeFinalState ffs = new FreeFinalState();
    ffs.setOutgoing(Arrays.asList(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL)));

    try {
      StandardValidator.validate(ffs);
    } catch (FinalStateConstraintException ex) {
      assertEquals(ffs, ex.getFinalState());
      throw ex;
    }
  }

  /**
   * [2] A final state cannot have regions.
   */
  @Test(expected = FinalStateConstraintException.class)
  public void constraint2_fail() {
    FreeFinalState ffs = new FreeFinalState();
    ffs.setRegions(Arrays.asList(new MutableRegion()));
    StandardValidator.validate(ffs);
  }

  /**
   * [3] A final state cannot reference a submachine.
   */
  @Test(expected = FinalStateConstraintException.class)
  public void constraint3_fail() {
    FreeFinalState ffs = new FreeFinalState();
    ffs.setSubStateMachine(new MutableStateMachine());
    StandardValidator.validate(ffs);
  }

  /**
   * [4] A final state has no entry behavior.
   */
  @Test(expected = FinalStateConstraintException.class)
  public void constraint4_fail() {
    FreeFinalState ffs = new FreeFinalState();
    ffs.setEntry(Arrays.asList(EMPTY_BEHAVIOR));
    StandardValidator.validate(ffs);
  }

  /**
   * [5] A final state has no exit behavior.
   */
  @Test(expected = FinalStateConstraintException.class)
  public void constraint5_fail() {
    FreeFinalState ffs = new FreeFinalState();
    ffs.setExit(Arrays.asList(EMPTY_BEHAVIOR));
    StandardValidator.validate(ffs);
  }

  /**
   * [6] A final state has no state (doActivity) behavior.
   */
  @Test(expected = FinalStateConstraintException.class)
  public void constraint6_fail() {
    FreeFinalState ffs = new FreeFinalState();
    ffs.setDoActivity(EMPTY_BEHAVIOR);
    StandardValidator.validate(ffs);
  }

  private static final class FreeFinalState extends MutableState implements FinalState {
  }

  private static final StateBehavior EMPTY_BEHAVIOR = (StateBehavior) (Object t) -> {
  };
}
