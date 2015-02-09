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

import com.lympid.core.basicbehaviors.Behavior;
import com.lympid.core.common.TestUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud
 */
public class BehaviorFactoryTest {
  
  @Test
  public void coverPrivateConstructor() throws Exception {
    TestUtils.callPrivateConstructor(BehaviorFactory.class);
  }

  @Test
  public void testToBehavior() {
    Behavior a = BehaviorFactory.toBehavior(Behavior1.class);
    assertTrue(a instanceof Behavior1);
    Behavior b = BehaviorFactory.toBehavior(Behavior1.class);
    assertTrue(a == b);
    Behavior c = new Behavior1();
    Behavior d = BehaviorFactory.toBehavior(c);
    assertTrue(c == d);

    a = BehaviorFactory.toBehavior(Behavior2.class);
    assertTrue(a instanceof Behavior2);
    b = BehaviorFactory.toBehavior(Behavior2.class);
    assertTrue(a == b);
  }
  
  @Test
  public void testToBehaviorList() {
    assertEquals(Collections.EMPTY_LIST, BehaviorFactory.toBehaviorList(new LinkedList<>()));
     
    Collection coll = new LinkedList();
    List<Behavior> expected = new ArrayList<>(3);
    
    Behavior b1 = new Behavior1(); 
    coll.add(b1);
    expected.add(b1);
    assertEquals(expected, BehaviorFactory.toBehaviorList(coll));
    
    Behavior b2 = new Behavior1();  
    coll.add(b2);
    expected.add(b2);
    assertEquals(expected, BehaviorFactory.toBehaviorList(coll));
    
    coll.add(Behavior2.class);
    expected.add(BehaviorFactory.toBehavior(Behavior2.class));
    assertEquals(expected, BehaviorFactory.toBehaviorList(coll));
  }
  
  @Test(expected = RuntimeException.class)
  public void illegalAccess() {
    BehaviorFactory.toBehavior(Behavior3.class);
  }

  public static final class Behavior1 implements Behavior {

  }

  public static final class Behavior2 implements Behavior {

  }

  private static final class Behavior3 implements Behavior {

  }

}
