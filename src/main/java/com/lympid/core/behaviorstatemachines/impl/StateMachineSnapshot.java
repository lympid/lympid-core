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

import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.StateMachine;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Fabien Renaud
 */
public final class StateMachineSnapshot<C> implements Serializable {
  
  private final String stateMachine;
  private final StringTree active;
  private final Map<String, StringTree> history = new HashMap<>();
  private final boolean started;
  private final boolean terminated;
  private final C context;

  StateMachineSnapshot(final StateMachine machine, final StateMachineState state, final C context) {
    this.stateMachine = machine.getId();
    this.started = state.hasStarted();
    this.terminated = state.isTerminated();
    this.context = context;
    
    this.active = createStateConfiguration(state.activeStates());
    createHistory(state.history());
  }
  
  public String stateMachine() {
    return stateMachine;
  }

  public StringTree stateConfiguration() {
    return active;
  }
  
  public Map<String, StringTree> history() {
    return history;
  }

  public C context() {
    return context;
  }

  public boolean isStarted() {
    return started;
  }

  public boolean isTerminated() {
    return terminated;
  }
  
  private StringTree createStateConfiguration(final StateConfiguration<?> config) {
    if (config.state() == null) {
      return null;
    }
    
    StringTree node = new StringTree();
    node.state = config.state().getId();
    if (!config.isEmpty()) {
      node.children = new ArrayList<>(config.size());
      config.forEach((s) -> createStateConfiguration(s, node.children));
    }
    return node;
  }

  private void createStateConfiguration(final StateConfiguration<?> config, final List<StringTree> current) {StringTree node = new StringTree();
    node.state = config.state().getId();
    if (!config.isEmpty()) {
      node.children = new ArrayList<>(config.size());
      config.forEach((s) -> createStateConfiguration(s, node.children));
    }
    current.add(node);
  }

  private void createHistory(final Map<Region, StateConfiguration<?>> histo) {
    for (Map.Entry<Region, StateConfiguration<?>> e : histo.entrySet()) {
      history.put(e.getKey().getId(), createStateConfiguration(e.getValue()));
    }
  }

  public static final class StringTree implements Serializable {

    private String state;
    private List<StringTree> children;

    public String state() {
      return state;
    }

    public List<StringTree> children() {
      return children;
    }

  }

}
