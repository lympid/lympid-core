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
package com.lympid.core.behaviorstatemachines.impl;

import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachineMeta;
import com.lympid.core.behaviorstatemachines.StateMachineSnapshot;
import com.lympid.core.common.StringTree;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Fabien Renaud
 */
abstract class ResumableStateMachineState implements StateMachineState {

  private final StateMachineMeta metadata;
  private boolean started;
  private boolean terminated;

  protected ResumableStateMachineState(final StateMachineMeta metadata) {
    this.metadata = metadata;
  }

  @Override
  public void start() {
    this.started = true;
  }

  @Override
  public boolean hasStarted() {
    return started;
  }

  @Override
  public void terminate() {
    this.terminated = true;
  }

  @Override
  public boolean isTerminated() {
    return terminated;
  }

  @Override
  public void resume(final StateMachineSnapshot<?> snapshot) {
    this.started = snapshot.isStarted();
    this.terminated = snapshot.isTerminated();

    if (snapshot.stateConfiguration() != null) {
      reactivate(snapshot.stateConfiguration());
    }
    if (!snapshot.history().isEmpty()) {
      learnHistory(snapshot.history());
    }
  }

  private void reactivate(final StringTree active) {
    State state = metadata.state(active.state());
    if (state == null) {
      throw new StateNotFoundException(active.state());
    }
    activate(state);

    if (active.children() != null) {
      for (StringTree child : active.children()) {
        reactivate(child);
      }
    }
  }

  private void learnHistory(final Map<String, StringTree> history) {
    for (Entry<String, StringTree> e : history.entrySet()) {
      Region region = metadata.region(e.getKey());
      if (region == null) {
        throw new RegionNotFoundException(e.getKey());
      }

      StringTree tree = e.getValue();
      if (tree != null) {
        MutableStateConfiguration config = learnHistory(tree);
        saveHistory(region, config);
      }
    }
  }

  private MutableStateConfiguration learnHistory(final StringTree tree) {
    State state = metadata.state(tree.state());
    if (state == null) {
      throw new StateNotFoundException(tree.state());
    }

    if (tree.children() == null || tree.children().isEmpty()) {
      return new SimpleStateConfiguration(state);
    }

    MutableStateConfiguration config = metadata.hasOrthogonalStates()
            ? new OrthogonalStateConfiguration()
            : new CompositeStateConfiguration();
    config.setState(state);

    for (StringTree child : tree.children()) {
      learnHistoryNode(config, child);
    }

    return config;
  }

  private void learnHistoryNode(final MutableStateConfiguration config, final StringTree node) {
    State childState = metadata.state(node.state());
    if (childState == null) {
      throw new StateNotFoundException(node.state());
    }

    MutableStateConfiguration childConfig = config.addChild(childState);
    if (node.children() != null) {
      for (StringTree child : node.children()) {
        learnHistoryNode(childConfig, child);
      }
    }
  }

  abstract void saveHistory(Region region, MutableStateConfiguration history);
}
