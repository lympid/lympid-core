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

import com.lympid.core.behaviorstatemachines.listener.EventAcceptedListener;
import com.lympid.core.behaviorstatemachines.listener.EventDeferredListener;
import com.lympid.core.behaviorstatemachines.listener.EventDeniedListener;
import com.lympid.core.behaviorstatemachines.listener.MachineStartedListener;
import com.lympid.core.behaviorstatemachines.listener.MachineTerminatedListener;
import com.lympid.core.behaviorstatemachines.listener.StateActivityAfterExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.StateActivityBeforeExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.StateActivityExceptionListener;
import com.lympid.core.behaviorstatemachines.listener.StateEnterAfterExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.StateEnterBeforeExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.StateEnterExceptionListener;
import com.lympid.core.behaviorstatemachines.listener.StateExitAfterExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.StateExitBeforeExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.StateExitExceptionListener;
import com.lympid.core.behaviorstatemachines.listener.TransitionEffectAfterExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.TransitionEffectBeforeExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.TransitionEffectExceptionListener;
import com.lympid.core.behaviorstatemachines.listener.TransitionEndedListener;
import com.lympid.core.behaviorstatemachines.listener.TransitionGuardAfterExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.TransitionGuardBeforeExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.TransitionGuardExceptionListener;
import com.lympid.core.behaviorstatemachines.listener.TransitionStartedListener;

/**
 *
 * @author Fabien Renaud
 */
public enum ExecutorEvent {

  /*
   *
   */
  EVENT_ACCEPTED(EventAcceptedListener.class),
  EVENT_DENIED(EventDeniedListener.class),
  EVENT_DEFERRED(EventDeferredListener.class),
  /*
   *
   */
  MACHINE_STARTED(MachineStartedListener.class),
  MACHINE_TERMINATED(MachineTerminatedListener.class),
  /*
   *
   */
  TRANSITION_STARTED(TransitionStartedListener.class),
  TRANSITION_ENDED(TransitionEndedListener.class),
  TRANSITION_GUARD_BEFORE_EXECUTION(TransitionGuardBeforeExecutionListener.class),
  TRANSITION_GUARD_AFTER_EXECUTION(TransitionGuardAfterExecutionListener.class),
  TRANSITION_GUARD_EXCEPTION(TransitionGuardExceptionListener.class),
  TRANSITION_EFFECT_BEFORE_EXECUTION(TransitionEffectBeforeExecutionListener.class),
  TRANSITION_EFFECT_AFTER_EXECUTION(TransitionEffectAfterExecutionListener.class),
  TRANSITION_EFFECT_EXCEPTION(TransitionEffectExceptionListener.class),
  /*
   *
   */
  STATE_ENTER_BEFORE_EXECUTION(StateEnterBeforeExecutionListener.class),
  STATE_ENTER_AFTER_EXECUTION(StateEnterAfterExecutionListener.class),
  STATE_ENTER_EXCEPTION(StateEnterExceptionListener.class),
  STATE_EXIT_BEFORE_EXECUTION(StateExitBeforeExecutionListener.class),
  STATE_EXIT_AFTER_EXECUTION(StateExitAfterExecutionListener.class),
  STATE_EXIT_EXCEPTION(StateExitExceptionListener.class),
  STATE_ACTIVITY_BEFORE_EXECUTION(StateActivityBeforeExecutionListener.class),
  STATE_ACTIVITY_AFTER_EXECUTION(StateActivityAfterExecutionListener.class),
  STATE_ACTIVITY_EXCEPTION(StateActivityExceptionListener.class);

  private final Class listenerClass;

  private ExecutorEvent(Class listenerClass) {
    this.listenerClass = listenerClass;
  }

  public Class getListenerClass() {
    return listenerClass;
  }
}
