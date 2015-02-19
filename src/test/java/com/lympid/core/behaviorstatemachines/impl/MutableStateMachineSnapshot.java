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

import com.lympid.core.behaviorstatemachines.StateMachineSnapshot;
import com.lympid.core.common.StringTree;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Fabien Renaud
 */
public class MutableStateMachineSnapshot<C> implements StateMachineSnapshot<C> {

  private C context;
  private Map<String, StringTree> history = new HashMap<>();
  private boolean started;
  private boolean terminated;
  private StringTree stateConfiguration;
  private String stateMachine;

  @Override
  public C context() {
    return context;
  }

  @Override
  public Map<String, StringTree> history() {
    return history;
  }

  @Override
  public boolean isStarted() {
    return started;
  }

  @Override
  public boolean isTerminated() {
    return terminated;
  }

  @Override
  public StringTree stateConfiguration() {
    return stateConfiguration;
  }

  @Override
  public String stateMachine() {
    return stateMachine;
  }

  public void setContext(C context) {
    this.context = context;
  }

  public void setHistory(Map<String, StringTree> history) {
    this.history = history;
  }

  public void setStarted(boolean started) {
    this.started = started;
  }

  public void setTerminated(boolean terminated) {
    this.terminated = terminated;
  }

  public void setStateConfiguration(StringTree stateConfiguration) {
    this.stateConfiguration = stateConfiguration;
  }

  public void setStateMachine(String stateMachine) {
    this.stateMachine = stateMachine;
  }

}
