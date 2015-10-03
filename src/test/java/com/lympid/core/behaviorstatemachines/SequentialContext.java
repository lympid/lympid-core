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
package com.lympid.core.behaviorstatemachines;

import com.lympid.core.common.Copyable;

/**
 *
 * @author Fabien Renaud 
 */
public class SequentialContext implements Copyable<SequentialContext> {

  private static final char SEPARATOR = ' ';
  private StringBuilder b = new StringBuilder();

  private boolean entryActions = true;
  private boolean exitActions = true;
  private boolean transitionEffects = true;
  private boolean stateActivities = false;
  
  public SequentialContext() {
  }
  
  public SequentialContext(final SequentialContext inst) {
    this.b = new StringBuilder(inst.b);
    this.entryActions = inst.entryActions;
    this.exitActions = inst.exitActions;
    this.transitionEffects = inst.transitionEffects;
    this.stateActivities = inst.stateActivities;
  }

  public <T extends SequentialContext> T withEntry() {
    this.entryActions = true;
    return (T) this;
  }

  public <T extends SequentialContext> T withoutEntry() {
    this.entryActions = false;
    return (T) this;
  }

  public <T extends SequentialContext> T withExit() {
    this.exitActions = true;
    return (T) this;
  }

  public <T extends SequentialContext> T withoutExit() {
    this.exitActions = false;
    return (T) this;
  }

  public <T extends SequentialContext> T withTransition() {
    this.transitionEffects = true;
    return (T) this;
  }

  public <T extends SequentialContext> T withoutTransition() {
    this.transitionEffects = false;
    return (T) this;
  }

  public <T extends SequentialContext> T withActivities() {
    this.stateActivities = true;
    return (T) this;
  }

  public <T extends SequentialContext> T withoutActivities() {
    this.stateActivities = false;
    return (T) this;
  }

  public void clear() {
    b = new StringBuilder();
  }

  public SequentialContext enter(String s) {
    if (entryActions) {
      b.append("enter:").append(s).append(SEPARATOR);
    }
    return this;
  }

  public SequentialContext exit(String s) {
    if (exitActions) {
      b.append("exit:").append(s).append(SEPARATOR);
    }
    return this;
  }

  public SequentialContext effect(String s) {
    if (transitionEffects) {
      b.append("effect:").append(s).append(SEPARATOR);
    }
    return this;
  }

  public SequentialContext activity(String s) {
    if (stateActivities) {
      b.append("activity:").append(s).append(SEPARATOR);
    }
    return this;
  }

  @Override
  public SequentialContext copy() {
    return new SequentialContext(this);
  }

  @Override
  public String toString() {
    return b.toString();
  }
}
