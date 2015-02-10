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
package com.lympid.core.behaviorstatemachines;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class TransitionKindTest {
  
  @Test
  public void valuesOf_success() {
    assertEquals(TransitionKind.EXTERNAL, TransitionKind.valueOf("EXTERNAL"));
    assertEquals(TransitionKind.INTERNAL, TransitionKind.valueOf("INTERNAL"));
    assertEquals(TransitionKind.LOCAL, TransitionKind.valueOf("LOCAL"));
  }
  
  @Test(expected = NullPointerException.class)
  public void valueOf_fail() {
    TransitionKind.valueOf(null);
  }
}
