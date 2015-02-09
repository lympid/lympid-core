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

import com.lympid.core.basicbehaviors.Constraint;
import com.lympid.core.behaviorstatemachines.ConnectionPointReference;
import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.Visitor;
import com.lympid.core.common.Trigger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Fabien Renaud
 */
public class MutableState extends MutableVertex implements State {

  private final Set<PseudoState> connectionPoint = new HashSet<>();
  private final List<Trigger> deferrableTrigger = Collections.EMPTY_LIST;
  private ConnectionPointReference connection;
  private StateBehavior doActivity;
  private List<StateBehavior> entry = Collections.EMPTY_LIST;
  private List<StateBehavior> exit = Collections.EMPTY_LIST;
  private List<Region> region = Collections.EMPTY_LIST;
  private StateMachine subStateMachine;

  public MutableState(final String id) {
    super(id);
  }

  public MutableState() {
    super();
  }

  @Override
  public boolean isComposite() {
    return region.size() > 0;
  }

  @Override
  public boolean isOrthogonal() {
    return region.size() > 1;
  }

  @Override
  public boolean isSimple() {
    return region.isEmpty();
  }

  @Override
  public boolean isSubMachineState() {
    return subStateMachine != null;
  }

  @Override
  public ConnectionPointReference connection() {
    return connection;
  }

  public void connection(final ConnectionPointReference connection) {
    this.connection = connection;
  }

  @Override
  public Collection<PseudoState> connectionPoint() {
    return connectionPoint;
  }

  @Override
  public Collection<Trigger> deferrableTrigger() {
    return deferrableTrigger;
  }

  @Override
  public StateBehavior doActivity() {
    return doActivity;
  }

  public void setDoActivity(StateBehavior activity) {
    this.doActivity = activity;
  }

  @Override
  public Collection<StateBehavior> entry() {
    return entry;
  }

  public void setEntry(final Collection<StateBehavior> elements) {
    if (elements.isEmpty()) {
      entry = Collections.EMPTY_LIST;
    } else {
      entry = new ArrayList<>(elements);
    }
  }

  @Override
  public Collection<StateBehavior> exit() {
    return exit;
  }

  public void setExit(final Collection<StateBehavior> elements) {
    if (elements.isEmpty()) {
      exit = Collections.EMPTY_LIST;
    } else {
      exit = new ArrayList<>(elements);
    }
  }

  public void setRegions(final Collection<Region> regions) {
    this.region = new ArrayList<>(regions);
  }

  @Override
  public List<Region> region() {
    return region;
  }

  @Override
  public StateMachine subStateMachine() {
    return subStateMachine;
  }

  public void setSubStateMachine(StateMachine subStateMachine) {
    this.subStateMachine = subStateMachine;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visitOnEntry(this);

    if (subStateMachine == null) {
      for (PseudoState cp : connectionPoint) {
        cp.accept(visitor);
      }
      for (Region r : region) {
        r.accept(visitor);
      }
    } else {
      subStateMachine.accept(visitor);
      if (connection != null) {
        connection.accept(visitor);
      }
    }

    visitor.visitOnExit(this);
  }
}
