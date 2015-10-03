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
package com.lympid.core.behaviorstatemachines.impl;

import com.lympid.core.behaviorstatemachines.ConnectionPointReference;
import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.Visitor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Fabien Renaud
 */
public class MutableConnectionPointReference extends MutableVertex implements ConnectionPointReference {

  private final Set<PseudoState> entry = new HashSet<>();
  private final Set<PseudoState> exit = new HashSet<>();
  private State state;

  public MutableConnectionPointReference(final String id) {
    super(id);
  }

  @Override
  public Collection<PseudoState> entry() {
    return entry;
  }

  @Override
  public Collection<PseudoState> exit() {
    return exit;
  }

  @Override
  public State state() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visitOnEntry(this);
    for (PseudoState e : entry) {
      e.accept(visitor);
    }
    for (PseudoState e : exit) {
      e.accept(visitor);
    }
    visitor.visitOnExit(this);
  }

}
