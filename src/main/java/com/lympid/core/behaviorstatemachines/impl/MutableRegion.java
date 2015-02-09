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

import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexUtils;
import com.lympid.core.behaviorstatemachines.Visitor;
import com.lympid.core.behaviorstatemachines.validation.RegionConstraintException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Fabien Renaud
 */
public final class MutableRegion implements Region {

  private final String id;
  private String name;
  private StateMachine stateMachine;
  private State state;
  private final Collection<Transition> transition;
  private final Collection<Vertex> subVertex;
  private PseudoState initial;
  private PseudoState deepHistory;
  private PseudoState shallowHistory;

  public MutableRegion(final String id) {
    this.id = id;
    this.transition = new LinkedList<>();
    this.subVertex = new HashSet<>();
  }

  public MutableRegion() {
    this(UUID.randomUUID().toString());
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public StateMachine stateMachine() {
    return stateMachine;
  }

  public void setStateMachine(StateMachine machine) {
    this.stateMachine = machine;
  }

  @Override
  public State state() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  @Override
  public Collection<Transition> transition() {
    return transition;
  }

  @Override
  public Collection<Vertex> subVertex() {
    return subVertex;
  }

  public void addVertex(final MutableVertex v) {
    subVertex.add(v);
    v.setContainer(this);

    if (v instanceof PseudoState) {
      PseudoState ps = (PseudoState) v;
      switch (ps.kind()) {
        case INITIAL:
          setInitial(ps);
          break;
        case DEEP_HISTORY:
          setDeepHistory(ps);
          break;
        case SHALLOW_HISTORY:
          setShallowHistory(ps);
          break;
        default:
          break;
      }
    }
  }

  @Override
  public PseudoState initial() {
    return initial;
  }

  private void setInitial(final PseudoState pseudoState) {
    /*
     * [1] A region can have at most one initial vertex.
     */
    if (initial != null) {
      throw new RegionConstraintException(this, "A region can have at most one initial vertex.");
    }
    this.initial = pseudoState;
  }

  @Override
  public PseudoState deepHistory() {
    return deepHistory;
  }

  private void setDeepHistory(final PseudoState pseudoState) {
    /*
     * [2] A region can have at most one deep history vertex.
     */
    if (deepHistory != null) {
      throw new RegionConstraintException(this, "A region can have at most one deep history vertex.");
    }
    this.deepHistory = pseudoState;
  }

  @Override
  public PseudoState shallowHistory() {
    return shallowHistory;
  }

  private void setShallowHistory(final PseudoState pseudoState) {
    /*
     * [3] A region can have at most one shallow history vertex.
     */
    if (shallowHistory != null) {
      throw new RegionConstraintException(this, "A region can have at most one shallow history vertex.");
    }
    this.shallowHistory = pseudoState;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visitOnEntry(this);
    for (Vertex v : subVertex) {
      v.accept(visitor);
    }
    for (Transition t : transition) {
      t.accept(visitor);
    }
    visitor.visitOnExit(this);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final MutableRegion other = (MutableRegion) obj;
    return Objects.equals(this.id, other.id);
  }

  @Override
  public String toString() {
    return VertexUtils.nameOrId(this);
  }
}
