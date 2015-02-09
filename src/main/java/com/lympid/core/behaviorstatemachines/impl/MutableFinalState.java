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
import com.lympid.core.behaviorstatemachines.FinalState;
import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.Visitor;
import com.lympid.core.common.Trigger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Fabien Renaud
 */
public class MutableFinalState extends MutableVertex implements FinalState {

  public MutableFinalState(final String id) {
    super(id);
  }

  public MutableFinalState() {
    super();
  }

  @Override
  public boolean isComposite() {
    return false;
  }

  @Override
  public boolean isOrthogonal() {
    return false;
  }

  @Override
  public boolean isSimple() {
    return true;
  }

  @Override
  public boolean isSubMachineState() {
    return false;
  }

  @Override
  public Collection<Transition> outgoing() {
    return Collections.EMPTY_LIST;
  }

  @Override
  public ConnectionPointReference connection() {
    throw new UnsupportedOperationException("Not supported by final states.");
  }

  @Override
  public Collection<PseudoState> connectionPoint() {
    throw new UnsupportedOperationException("Not supported by final states.");
  }

  @Override
  public Collection<Trigger> deferrableTrigger() {
    throw new UnsupportedOperationException("Not supported by final states.");
  }

  @Override
  public StateBehavior doActivity() {
    return null;
  }

  @Override
  public Collection<StateBehavior> entry() {
    return Collections.EMPTY_LIST;
  }

  @Override
  public Collection<StateBehavior> exit() {
    return Collections.EMPTY_LIST;
  }

  @Override
  public List<Region> region() {
    return Collections.EMPTY_LIST;
  }

  @Override
  public StateMachine subStateMachine() {
    return null;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visitOnEntry(this);
    visitor.visitOnExit(this);
  }
}
