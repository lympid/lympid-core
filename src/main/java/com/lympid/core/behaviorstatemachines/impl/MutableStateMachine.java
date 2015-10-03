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
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.StateMachineMeta;
import com.lympid.core.behaviorstatemachines.VertexUtils;
import com.lympid.core.behaviorstatemachines.Visitor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author Fabien Renaud
 */
public final class MutableStateMachine implements StateMachine {

  private final String id;
  private String name;
  private List<Region> region = Collections.EMPTY_LIST;
  private final Set<PseudoState> connectionPoint = new HashSet<>();
  private StateMachineMeta metadata;

  public MutableStateMachine(final String id) {
    this.id = id;
  }

  public MutableStateMachine() {
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

  public void addRegion(final MutableRegion r) {
    if (region.isEmpty()) {
      region = new LinkedList<>();
    }
    region.add(r);
    r.setStateMachine(this);
  }

  @Override
  public List<Region> region() {
    return region;
  }

  public void setRegions(final Collection<Region> regions) {
    this.region = new ArrayList<>(regions);
  }

  @Override
  public Collection<PseudoState> connectionPoint() {
    return connectionPoint;
  }

  public void setMetadata(final StateMachineMeta meta) {
    this.metadata = meta;
  }

  @Override
  public StateMachineMeta metadata() {
    return metadata;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visitOnEntry(this);
    for (Region r : region) {
      r.accept(visitor);
    }
    for (PseudoState cp : connectionPoint) {
      cp.accept(visitor);
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
    final MutableStateMachine other = (MutableStateMachine) obj;
    return Objects.equals(this.id, other.id);
  }

  @Override
  public String toString() {
    return VertexUtils.nameOrId(this);
  }
}
