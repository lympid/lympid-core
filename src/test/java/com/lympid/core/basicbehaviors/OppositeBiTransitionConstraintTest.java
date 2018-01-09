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
package com.lympid.core.basicbehaviors;

import com.lympid.core.behaviorstatemachines.BiTransitionConstraint;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Fabien Renaud 
 */
public class OppositeBiTransitionConstraintTest {

  private static final BiTransitionConstraint TRUE_CONSTRAINT = (t, u) -> true;
  private static final BiTransitionConstraint FALSE_CONSTRAINT = (t, u) -> false;

  @Test
  public void test() {
    OppositeBiTransitionConstraint notTrue = new OppositeBiTransitionConstraint(TRUE_CONSTRAINT);
    assertTrue(TRUE_CONSTRAINT.test(null, null));
    assertFalse(notTrue.test(null, null));
    assertTrue(notTrue.opposite().test(null, null));

    OppositeBiTransitionConstraint notFalse = new OppositeBiTransitionConstraint(FALSE_CONSTRAINT);
    assertFalse(FALSE_CONSTRAINT.test(null, null));
    assertTrue(notFalse.test(null, null));
    assertFalse(notFalse.opposite().test(null, null));
  }

}
