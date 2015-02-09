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

import com.lympid.core.behaviorstatemachines.StateMachineMeta;

/**
 *
 * @author Fabien Renaud
 */
public class ImmutableStateMachineMeta implements StateMachineMeta {

  private final boolean completionEvents;
  private final boolean timeEvents;
  private final boolean activities;
  private final int historyNodes;
  private final int treeDepth;
  private final int countOfLeaves;
  
  public ImmutableStateMachineMeta(final StateMachineMeta meta) {
    this.completionEvents = meta.hasCompletionEvents();
    this.timeEvents = meta.hasTimeEvents();
    this.activities = meta.hasActivities();
    this.historyNodes = meta.countOfHistoryNodes();
    this.treeDepth = meta.treeDepth();
    this.countOfLeaves = meta.countOfLeaves();
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
  public int countOfHistoryNodes() {
    return historyNodes;
  }

  @Override
  public int treeDepth() {
    return treeDepth;
  }

  @Override
  public int countOfLeaves() {
    return countOfLeaves;
  }

}
