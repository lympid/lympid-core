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
package com.lympid.core.common;

import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.impl.MutablePseudoState;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;

/**
 *
 * @author Fabien Renaud
 */
public class TestUtils {
  
  public static void assertSequentialContextEquals(final SequentialContext expected, final SequentialContext actual) {
    Assert.assertEquals(expected.toString(), actual.toString());
  }
  
  public static void callPrivateConstructor(final Class c) throws Exception {
    Constructor<?> constr = c.getDeclaredConstructor();
    constr.setAccessible(true);
    constr.newInstance();
  }

  public static MutablePseudoState randomPseudoState() {
    PseudoStateKind[] kinds = PseudoStateKind.values();
    int i = new Random().nextInt(kinds.length);
    return new MutablePseudoState(kinds[i]);
  }

  public static MutablePseudoState randomPseudoStateBut(PseudoStateKind... exclusions) {
    Set<PseudoStateKind> exclusionSet = new HashSet<>(Arrays.asList(exclusions));
    PseudoStateKind[] kinds = PseudoStateKind.values();
    PseudoStateKind[] filteredKinds = new PseudoStateKind[kinds.length - exclusions.length];
    for (int i = 0, j = 0; i < kinds.length; i++) {
      if (!exclusionSet.contains(kinds[i])) {
        filteredKinds[j++] = kinds[i];
      }
    }

    int i = new Random().nextInt(filteredKinds.length);
    return new MutablePseudoState(filteredKinds[i]);
  }
}
