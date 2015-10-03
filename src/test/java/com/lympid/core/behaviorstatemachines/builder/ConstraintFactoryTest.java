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
package com.lympid.core.behaviorstatemachines.builder;

import com.lympid.core.basicbehaviors.Constraint;
import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.basicbehaviors.OppositeBiTransitionConstraint;
import com.lympid.core.basicbehaviors.OppositeConstraint;
import com.lympid.core.behaviorstatemachines.BiTransitionConstraint;
import com.lympid.core.common.TestUtils;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class ConstraintFactoryTest {
  
  @Test
  public void coverPrivateConstructor() throws Exception {
    TestUtils.callPrivateConstructor(ConstraintFactory.class);
  }

  @Test
  public void factory() {
    assertNull(ConstraintFactory.get(null));
    
    Constraint a = ConstraintFactory.get(Constraint1.class);
    assertTrue(a instanceof Constraint1);
    Constraint b = ConstraintFactory.get(Constraint1.class);
    assertTrue(a == b);

    a = ConstraintFactory.get(Constraint2.class);
    assertTrue(a instanceof Constraint2);
    b = ConstraintFactory.get(Constraint2.class);
    assertTrue(a == b);
  }
  
  @Test
  public void negationFactory_1() {
    assertNull(ConstraintFactory.getNegation(null));
    
    Constraint a = ConstraintFactory.get(Constraint4.class);
    assertTrue(a instanceof Constraint4);
    
    OppositeConstraint b = ConstraintFactory.getNegation(Constraint4.class);
    assertTrue(b instanceof OppositeBiTransitionConstraint);
    assertTrue(b.opposite() == a);
    
    OppositeConstraint c = ConstraintFactory.getNegation(Constraint4.class);
    assertTrue(b == c);
  }
  
  @Test
  public void negationFactory_2() {
    OppositeConstraint a = ConstraintFactory.getNegation(Constraint4.class);
    assertTrue(a instanceof OppositeBiTransitionConstraint);
    assertTrue(a.opposite() instanceof Constraint4);
    
    OppositeConstraint b = ConstraintFactory.getNegation(Constraint4.class);
    assertTrue(a == b);
    
    Constraint c = ConstraintFactory.get(Constraint4.class);
    assertTrue(a.opposite() == c);
  }

  @Test(expected = RuntimeException.class)
  public void illegalAccess() {
    ConstraintFactory.get(Constraint3.class);
  }

  @Test(expected = RuntimeException.class)
  public void nonPublicConstructor() {
    ConstraintFactory.getNegation(Constraint3.class);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void unsupported() {
    ConstraintFactory.getNegation(Constraint2.class);
  }

  @Test(expected = RuntimeException.class)
  public void negationIllegalAccess() {
    ConstraintFactory.getNegation(Constraint5.class);
  }

  public static final class Constraint1 implements Constraint {

  }

  public static final class Constraint2 implements Constraint {

  }

  private static final class Constraint3 implements Constraint {

  }

  public static final class Constraint4 implements BiTransitionConstraint<Event, Object> {

    @Override
    public boolean test(Event t, Object u) {
      return true;
    }

  }

  private static final class Constraint5 implements BiTransitionConstraint<Event, Object> {

    @Override
    public boolean test(Event t, Object u) {
      return true;
    }

  }
}
