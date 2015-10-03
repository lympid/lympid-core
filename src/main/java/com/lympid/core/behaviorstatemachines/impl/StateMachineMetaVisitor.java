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

import com.lympid.core.basicbehaviors.TimeEvent;
import com.lympid.core.behaviorstatemachines.ConnectionPointReference;
import com.lympid.core.behaviorstatemachines.FinalState;
import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.SimpleVisitor;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.StateMachineMeta;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.common.Trigger;

/**
 *
 * @author Fabien Renaud
 */
public class StateMachineMetaVisitor extends SimpleVisitor {
  
  private final MutableStateMachineMeta meta = new MutableStateMachineMeta();
  
  public StateMachineMeta getMeta() {
    return new ImmutableStateMachineMeta(meta);
  }
  
  @Override
  public void visit(final ConnectionPointReference v) {
  }
  
  @Override
  public void visit(final State v) {
    meta.register(v);
  }
  
  @Override
  public void visit(final PseudoState v) {
    meta.register(v);
  }
  
  @Override
  public void visit(final FinalState v) {
    meta.register(v);
  }
  
  @Override
  public void visit(final Region v) {
    meta.register(v);
  }
  
  @Override
  public void visit(final StateMachine v) {
  }
  
  @Override
  public void visit(final Transition v) {
    if (v.triggers().isEmpty()) {
      meta.incCompletionEvents();
    } else if (hasTimeEvents(v)) {
      meta.incTimeEvents();
    }
  }
  
  private boolean hasTimeEvents(final Transition v) {
    for (Trigger tr : v.triggers()) {
      if (tr.event() instanceof TimeEvent) {
        return true;
      }
    }
    return false;
  }
}
