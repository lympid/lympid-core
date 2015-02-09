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

import com.lympid.core.behaviorstatemachines.State;
import java.util.List;
import java.util.concurrent.Future;

/**
 *
 * @author Fabien Renaud
 */
class StateStatus {

  private final State state;
  private final long activationTime;
  private Future activity;
  private List<Future> eventTimers;

  public StateStatus(final State state, final long activationTime) {
    this.state = state;
    this.activationTime = activationTime;
  }

  public StateStatus(final State state) {
    this(state, System.currentTimeMillis());
  }

  public State getState() {
    return state;
  }

  public long getActivationTime() {
    return activationTime;
  }

  public void setActivity(final Future f) {
    this.activity = f;
  }

  public Future getActivity() {
    return activity;
  }

  public boolean hasEventTimers() {
    return eventTimers != null;
  }

  public void setEventTimers(final List<Future> eventTimers) {
    this.eventTimers = eventTimers;
  }

  public List<Future> getEventTimers() {
    return eventTimers;
  }

}
