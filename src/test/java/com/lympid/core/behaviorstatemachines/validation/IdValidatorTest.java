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
package com.lympid.core.behaviorstatemachines.validation;

import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.impl.MutableState;
import com.lympid.core.behaviorstatemachines.impl.MutableTransition;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class IdValidatorTest {

  private IdValidator validator;

  @Before
  public void setUp() {
    validator = new IdValidator();
  }

  @Test(expected = RuntimeException.class)
  public void nullId() {
    validator.validate(new MutableState(null));
  }

  @Test(expected = RuntimeException.class)
  public void duplicateId_1() {
    validator.validate(new MutableState("10"));
    validator.validate(new MutableState("10"));
  }

  @Test(expected = RuntimeException.class)
  public void duplicateId_2() {
    validator.validate(new MutableState("10"));
    validator.validate(new MutableTransition(null, null, null, null, null, TransitionKind.EXTERNAL, "10"));
  }
}
