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

import com.lympid.core.behaviorstatemachines.ConnectionPointReference;
import com.lympid.core.behaviorstatemachines.FinalState;
import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.common.UmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Fabien Renaud
 */
public final class IdValidator {

  private final Map<String, UmlElement> elements = new HashMap<>();

  public void validate(final ConnectionPointReference connectionPointReference) {
    check(connectionPointReference);
  }

  public void validate(final FinalState finalState) {
    check(finalState);
  }

  public void validate(final PseudoState pseudoState) {
    check(pseudoState);
  }

  public void validate(final Region region) {
    check(region);
  }

  public void validate(final State state) {
    check(state);
  }

  public void validate(final StateMachine stateMachine) {
    check(stateMachine);
  }

  public void validate(final Transition transition) {
    check(transition);
  }

  private void check(final UmlElement el) {
    if (el.getId() == null) {
      throw new RuntimeException("Null id");
    }
    UmlElement current = elements.get(el.getId());
    if (current != null && current != el) {
      throw new RuntimeException("Non unique id: " + el.getId());
    }
    elements.put(el.getId(), el);
  }
}
