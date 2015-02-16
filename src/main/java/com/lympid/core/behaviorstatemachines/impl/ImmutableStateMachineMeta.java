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

import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachineMeta;
import java.util.Map;

/**
 *
 * @author Fabien Renaud
 */
public class ImmutableStateMachineMeta implements StateMachineMeta {

  private final boolean simpleStates;
  private final boolean compositeStates;
  private final boolean orthogonalStates;
  private final boolean submachineStates;
  private final boolean completionEvents;
  private final boolean timeEvents;
  private final boolean activities;
  private final Map<PseudoStateKind, Integer> pseudoStateCounts;
  private final Map<String, State> stateById;
  private final Map<String, Region> regionById;
  private final int treeDepth;
  private final int countOfLeaves;

  public ImmutableStateMachineMeta(final MutableStateMachineMeta meta) {
    this.simpleStates = meta.hasSimpleStates();
    this.compositeStates = meta.hasCompositeStates();
    this.orthogonalStates = meta.hasOrthogonalStates();
    this.submachineStates = meta.hasSubmachineStates();
    this.completionEvents = meta.hasCompletionEvents();
    this.timeEvents = meta.hasTimeEvents();
    this.activities = meta.hasActivities();
    this.pseudoStateCounts = meta.pseudoStateCounts();
    this.stateById = meta.stateById();
    this.regionById = meta.regionById();
    this.treeDepth = meta.treeDepth();
    this.countOfLeaves = meta.countOfLeaves();
  }

  @Override
  public boolean hasSimpleStates() {
    return simpleStates;
  }

  @Override
  public boolean hasCompositeStates() {
    return compositeStates;
  }

  @Override
  public boolean hasOrthogonalStates() {
    return orthogonalStates;
  }

  @Override
  public boolean hasSubmachineStates() {
    return submachineStates;
  }

  @Override
  public boolean hasCompletionEvents() {
    return completionEvents;
  }

  @Override
  public boolean hasTimeEvents() {
    return timeEvents;
  }

  @Override
  public boolean hasActivities() {
    return activities;
  }

  @Override
  public int countOf(final PseudoStateKind kind) {
    return pseudoStateCounts.get(kind);
  }

  @Override
  public int treeDepth() {
    return treeDepth;
  }

  @Override
  public int countOfLeaves() {
    return countOfLeaves;
  }

  @Override
  public State state(final String id) {
    return stateById.get(id);
  }

  @Override
  public Region region(final String id) {
    return regionById.get(id);
  }

}
