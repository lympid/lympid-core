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

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.listener.AllListener;
import com.lympid.core.behaviorstatemachines.listener.EventAcceptedListener;
import com.lympid.core.behaviorstatemachines.listener.EventDeferredListener;
import com.lympid.core.behaviorstatemachines.listener.EventDeniedListener;
import com.lympid.core.behaviorstatemachines.listener.MachineListener;
import com.lympid.core.behaviorstatemachines.listener.MachineStartedListener;
import com.lympid.core.behaviorstatemachines.listener.MachineTerminatedListener;
import com.lympid.core.behaviorstatemachines.listener.StateActivityAfterExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.StateActivityBeforeExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.StateActivityExceptionListener;
import com.lympid.core.behaviorstatemachines.listener.StateEnterAfterExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.StateEnterBeforeExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.StateEnterExceptionListener;
import com.lympid.core.behaviorstatemachines.listener.StateEnterListener;
import com.lympid.core.behaviorstatemachines.listener.StateExitAfterExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.StateExitBeforeExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.StateExitExceptionListener;
import com.lympid.core.behaviorstatemachines.listener.StateExitListener;
import com.lympid.core.behaviorstatemachines.listener.TransitionEffectAfterExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.TransitionEffectBeforeExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.TransitionEffectExceptionListener;
import com.lympid.core.behaviorstatemachines.listener.TransitionEndedListener;
import com.lympid.core.behaviorstatemachines.listener.TransitionGuardAfterExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.TransitionGuardBeforeExecutionListener;
import com.lympid.core.behaviorstatemachines.listener.TransitionGuardExceptionListener;
import com.lympid.core.behaviorstatemachines.listener.TransitionStartedListener;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Fabien Renaud
 */
public class ExecutorListener<C> implements AllListener<C> {

  public static final ExecutorListener DEFAULT = new ExecutorListener();
  private final Map<ExecutorEvent, List> listeners = new EnumMap<>(ExecutorEvent.class);

  public ExecutorListener() {
    for (ExecutorEvent ee : ExecutorEvent.values()) {
      listeners.put(ee, new ArrayList());
    }
  }

  public boolean hasEventAcceptedListener() {
    return has(ExecutorEvent.EVENT_ACCEPTED);
  }

  public boolean addEventAcceptedListener(final EventAcceptedListener listener) {
    return add(ExecutorEvent.EVENT_ACCEPTED, listener);
  }

  public boolean removeEventAcceptedListener(final EventAcceptedListener listener) {
    return remove(ExecutorEvent.EVENT_ACCEPTED, listener);
  }

  public boolean hasEventDeniedListener() {
    return has(ExecutorEvent.EVENT_DENIED);
  }

  public boolean addEventDeniedListener(final EventDeniedListener listener) {
    return add(ExecutorEvent.EVENT_DENIED, listener);
  }

  public boolean removeEventDeniedListener(final EventDeniedListener listener) {
    return remove(ExecutorEvent.EVENT_DENIED, listener);
  }

  public boolean hasEventDeferredListener() {
    return has(ExecutorEvent.EVENT_DEFERRED);
  }

  public boolean addEventDeferredListener(final EventDeferredListener listener) {
    return add(ExecutorEvent.EVENT_DEFERRED, listener);
  }

  public boolean removeEventDeferredListener(final EventDeferredListener listener) {
    return remove(ExecutorEvent.EVENT_DEFERRED, listener);
  }

  public boolean hasMachineStartedListener() {
    return has(ExecutorEvent.MACHINE_STARTED);
  }

  public boolean addMachineStartedListener(final MachineStartedListener listener) {
    return add(ExecutorEvent.MACHINE_STARTED, listener);
  }

  public boolean removeMachineStartedListener(final MachineStartedListener listener) {
    return remove(ExecutorEvent.MACHINE_STARTED, listener);
  }

  public boolean hasMachineTerminatedListener() {
    return has(ExecutorEvent.MACHINE_TERMINATED);
  }

  public boolean addMachineTerminatedListener(final MachineTerminatedListener listener) {
    return add(ExecutorEvent.MACHINE_TERMINATED, listener);
  }

  public boolean removeMachineTerminatedListener(final MachineTerminatedListener listener) {
    return remove(ExecutorEvent.MACHINE_TERMINATED, listener);
  }

  public boolean hasTransitionStartedListener() {
    return has(ExecutorEvent.TRANSITION_STARTED);
  }

  public boolean addTransitionStartedListener(final TransitionStartedListener listener) {
    return add(ExecutorEvent.TRANSITION_STARTED, listener);
  }

  public boolean removeTransitionStartedListener(final TransitionStartedListener listener) {
    return remove(ExecutorEvent.TRANSITION_STARTED, listener);
  }

  public boolean hasTransitionEndedListener() {
    return has(ExecutorEvent.TRANSITION_ENDED);
  }

  public boolean addTransitionEndedListener(final TransitionEndedListener listener) {
    return add(ExecutorEvent.TRANSITION_ENDED, listener);
  }

  public boolean removeTransitionEndedListener(final TransitionEndedListener listener) {
    return remove(ExecutorEvent.TRANSITION_ENDED, listener);
  }

  public boolean hasTransitionGuardBeforeExecutionListener() {
    return has(ExecutorEvent.TRANSITION_GUARD_BEFORE_EXECUTION);
  }

  public boolean addTransitionGuardBeforeExecutionListener(final TransitionGuardBeforeExecutionListener listener) {
    return add(ExecutorEvent.TRANSITION_GUARD_BEFORE_EXECUTION, listener);
  }

  public boolean removeTransitionGuardBeforeExecutionListener(final TransitionGuardBeforeExecutionListener listener) {
    return remove(ExecutorEvent.TRANSITION_GUARD_BEFORE_EXECUTION, listener);
  }

  public boolean hasTransitionGuardAfterExecutionListener() {
    return has(ExecutorEvent.TRANSITION_GUARD_AFTER_EXECUTION);
  }

  public boolean addTransitionGuardAfterExecutionListener(final TransitionGuardAfterExecutionListener listener) {
    return add(ExecutorEvent.TRANSITION_GUARD_AFTER_EXECUTION, listener);
  }

  public boolean removeTransitionGuardAfterExecutionListener(final TransitionGuardAfterExecutionListener listener) {
    return remove(ExecutorEvent.TRANSITION_GUARD_AFTER_EXECUTION, listener);
  }

  public boolean hasTransitionGuardExceptionListener() {
    return has(ExecutorEvent.TRANSITION_GUARD_EXCEPTION);
  }

  public boolean addTransitionGuardExceptionListener(final TransitionGuardExceptionListener listener) {
    return add(ExecutorEvent.TRANSITION_GUARD_EXCEPTION, listener);
  }

  public boolean removeTransitionGuardExceptionListener(final TransitionGuardExceptionListener listener) {
    return remove(ExecutorEvent.TRANSITION_GUARD_EXCEPTION, listener);
  }

  public boolean hasTransitionEffectBeforeExecutionListener() {
    return has(ExecutorEvent.TRANSITION_EFFECT_BEFORE_EXECUTION);
  }

  public boolean addTransitionEffectBeforeExecutionListener(final TransitionEffectBeforeExecutionListener listener) {
    return add(ExecutorEvent.TRANSITION_EFFECT_BEFORE_EXECUTION, listener);
  }

  public boolean removeTransitionEffectBeforeExecutionListener(final TransitionEffectBeforeExecutionListener listener) {
    return remove(ExecutorEvent.TRANSITION_EFFECT_BEFORE_EXECUTION, listener);
  }

  public boolean hasTransitionEffectAfterExecutionListener() {
    return has(ExecutorEvent.TRANSITION_EFFECT_AFTER_EXECUTION);
  }

  public boolean addTransitionEffectAfterExecutionListener(final TransitionEffectAfterExecutionListener listener) {
    return add(ExecutorEvent.TRANSITION_EFFECT_AFTER_EXECUTION, listener);
  }

  public boolean removeTransitionEffectAfterExecutionListener(final TransitionEffectAfterExecutionListener listener) {
    return remove(ExecutorEvent.TRANSITION_EFFECT_AFTER_EXECUTION, listener);
  }

  public boolean hasTransitionEffectExceptionListener() {
    return has(ExecutorEvent.TRANSITION_EFFECT_EXCEPTION);
  }

  public boolean addTransitionEffectExceptionListener(final TransitionEffectExceptionListener listener) {
    return add(ExecutorEvent.TRANSITION_EFFECT_EXCEPTION, listener);
  }

  public boolean removeTransitionEffectExceptionListener(final TransitionEffectExceptionListener listener) {
    return remove(ExecutorEvent.TRANSITION_EFFECT_EXCEPTION, listener);
  }
  
  public boolean hasStateEnter() {
      return has(ExecutorEvent.STATE_ENTER);
  }

  public boolean addStateEnter(final StateEnterListener listener) {
    return add(ExecutorEvent.STATE_ENTER, listener);
  }

  public boolean removeStateEnter(final StateEnterListener listener) {
    return remove(ExecutorEvent.STATE_ENTER, listener);
  }

  public boolean hasStateEnterBeforeExecution() {
    return has(ExecutorEvent.STATE_ENTER_BEFORE_EXECUTION);
  }

  public boolean addStateEnterBeforeExecution(final StateEnterBeforeExecutionListener listener) {
    return add(ExecutorEvent.STATE_ENTER_BEFORE_EXECUTION, listener);
  }

  public boolean removeStateEnterBeforeExecution(final StateEnterBeforeExecutionListener listener) {
    return remove(ExecutorEvent.STATE_ENTER_BEFORE_EXECUTION, listener);
  }

  public boolean hasStateEnterAfterExecution() {
    return has(ExecutorEvent.STATE_ENTER_AFTER_EXECUTION);
  }

  public boolean addStateEnterAfterExecution(final StateEnterAfterExecutionListener listener) {
    return add(ExecutorEvent.STATE_ENTER_AFTER_EXECUTION, listener);
  }

  public boolean removeStateEnterAfterExecution(final StateEnterAfterExecutionListener listener) {
    return remove(ExecutorEvent.STATE_ENTER_AFTER_EXECUTION, listener);
  }

  public boolean hasStateEnterException() {
    return has(ExecutorEvent.STATE_ENTER_EXCEPTION);
  }

  public boolean addStateEnterException(final StateEnterExceptionListener listener) {
    return add(ExecutorEvent.STATE_ENTER_EXCEPTION, listener);
  }

  public boolean removeStateEnterException(final StateEnterExceptionListener listener) {
    return remove(ExecutorEvent.STATE_ENTER_EXCEPTION, listener);
  }
  
  public boolean hasStateExit() {
      return has(ExecutorEvent.STATE_EXIT);
  }

  public boolean addStateExit(final StateExitListener listener) {
    return add(ExecutorEvent.STATE_EXIT, listener);
  }

  public boolean removeStateExit(final StateExitListener listener) {
    return remove(ExecutorEvent.STATE_EXIT, listener);
  }

  public boolean hasStateExitBeforeExecution() {
    return has(ExecutorEvent.STATE_EXIT_BEFORE_EXECUTION);
  }

  public boolean addStateExitBeforeExecution(final StateExitBeforeExecutionListener listener) {
    return add(ExecutorEvent.STATE_EXIT_BEFORE_EXECUTION, listener);
  }

  public boolean removeStateExitBeforeExecution(final StateExitBeforeExecutionListener listener) {
    return remove(ExecutorEvent.STATE_EXIT_BEFORE_EXECUTION, listener);
  }

  public boolean hasStateExitAfterExecution() {
    return has(ExecutorEvent.STATE_EXIT_AFTER_EXECUTION);
  }

  public boolean addStateExitAfterExecution(final StateExitAfterExecutionListener listener) {
    return add(ExecutorEvent.STATE_EXIT_AFTER_EXECUTION, listener);
  }

  public boolean removeStateExitAfterExecution(final StateExitAfterExecutionListener listener) {
    return remove(ExecutorEvent.STATE_EXIT_AFTER_EXECUTION, listener);
  }

  public boolean hasStateExitException() {
    return has(ExecutorEvent.STATE_EXIT_EXCEPTION);
  }

  public boolean addStateExitException(final StateExitExceptionListener listener) {
    return add(ExecutorEvent.STATE_EXIT_EXCEPTION, listener);
  }

  public boolean removeStateExitException(final StateExitExceptionListener listener) {
    return remove(ExecutorEvent.STATE_EXIT_EXCEPTION, listener);
  }

  public boolean hasStateActivityBeforeExecution() {
    return has(ExecutorEvent.STATE_ACTIVITY_BEFORE_EXECUTION);
  }

  public boolean addStateActivityBeforeExecution(final StateActivityBeforeExecutionListener listener) {
    return add(ExecutorEvent.STATE_ACTIVITY_BEFORE_EXECUTION, listener);
  }

  public boolean removeStateActivityBeforeExecution(final StateActivityBeforeExecutionListener listener) {
    return remove(ExecutorEvent.STATE_ACTIVITY_BEFORE_EXECUTION, listener);
  }

  public boolean hasStateActivityAfterExecution() {
    return has(ExecutorEvent.STATE_ACTIVITY_AFTER_EXECUTION);
  }

  public boolean addStateActivityAfterExecution(final StateActivityAfterExecutionListener listener) {
    return add(ExecutorEvent.STATE_ACTIVITY_AFTER_EXECUTION, listener);
  }

  public boolean removeStateActivityAfterExecution(final StateActivityAfterExecutionListener listener) {
    return remove(ExecutorEvent.STATE_ACTIVITY_AFTER_EXECUTION, listener);
  }

  public boolean hasStateActivityException() {
    return has(ExecutorEvent.STATE_ACTIVITY_EXCEPTION);
  }

  public boolean addStateActivityException(final StateActivityExceptionListener listener) {
    return add(ExecutorEvent.STATE_ACTIVITY_EXCEPTION, listener);
  }

  public boolean removeStateActivityException(final StateActivityExceptionListener listener) {
    return remove(ExecutorEvent.STATE_ACTIVITY_EXCEPTION, listener);
  }

  public void add(final MachineListener listener) {
    if (listener == null) {
      return;
    }

    for (ExecutorEvent ee : ExecutorEvent.values()) {
      if (ee.getListenerClass().isInstance(listener)) {
        add(ee, listener);
      }
    }
  }

  public void remove(final MachineListener listener) {
    if (listener == null) {
      return;
    }

    for (ExecutorEvent ee : ExecutorEvent.values()) {
      if (ee.getListenerClass().isInstance(listener)) {
        remove(ee, listener);
      }
    }
  }

  private boolean has(final ExecutorEvent event) {
    return listeners.get(event).size() > 0;
  }

  private boolean add(final ExecutorEvent event, final Object listener) {
    if (listener == null) {
      return false;
    }

    return listeners.get(event).add(listener);
  }

  private boolean remove(final ExecutorEvent event, final Object listener) {
    if (listener == null) {
      return false;
    }

    return listeners.get(event).remove(listener);
  }

  private <T> List<T> get(final ExecutorEvent event) {
    return listeners.get(event);
  }

  @Override
  public void onEventAccepted(StateMachineExecutor executor, StateMachine machine, C context, Event event) {
    List<EventAcceptedListener<C>> list = get(ExecutorEvent.EVENT_ACCEPTED);
    for (EventAcceptedListener<C> l : list) {
      l.onEventAccepted(executor, machine, context, event);
    }
  }

  @Override
  public void onEventDeferred(StateMachineExecutor executor, StateMachine machine, C context, Event event) {
    List<EventDeferredListener<C>> list = get(ExecutorEvent.EVENT_DEFERRED);
    for (EventDeferredListener<C> l : list) {
      l.onEventDeferred(executor, machine, context, event);
    }
  }

  @Override
  public void onEventDenied(StateMachineExecutor executor, StateMachine machine, C context, Event event) {
    List<EventDeniedListener<C>> list = get(ExecutorEvent.EVENT_DENIED);
    for (EventDeniedListener<C> l : list) {
      l.onEventDenied(executor, machine, context, event);
    }
  }

  @Override
  public void onMachineStarted(StateMachineExecutor executor, StateMachine machine, C context) {
    List<MachineStartedListener<C>> list = get(ExecutorEvent.MACHINE_STARTED);
    for (MachineStartedListener<C> l : list) {
      l.onMachineStarted(executor, machine, context);
    }
  }

  @Override
  public void onMachineTerminated(StateMachineExecutor executor, StateMachine machine, C context) {
    List<MachineTerminatedListener<C>> list = get(ExecutorEvent.MACHINE_TERMINATED);
    for (MachineTerminatedListener<C> l : list) {
      l.onMachineTerminated(executor, machine, context);
    }
  }

  @Override
  public void onStateActivityBeforeExecution(StateMachineExecutor executor, StateMachine machine, C context, State state) {
    List<StateActivityBeforeExecutionListener<C>> list = get(ExecutorEvent.STATE_ACTIVITY_BEFORE_EXECUTION);
    for (StateActivityBeforeExecutionListener<C> l : list) {
      l.onStateActivityBeforeExecution(executor, machine, context, state);
    }
  }

  @Override
  public void onStateActivityAfterExecution(StateMachineExecutor executor, StateMachine machine, C context, State state) {
    List<StateActivityAfterExecutionListener<C>> list = get(ExecutorEvent.STATE_ACTIVITY_AFTER_EXECUTION);
    for (StateActivityAfterExecutionListener<C> l : list) {
      l.onStateActivityAfterExecution(executor, machine, context, state);
    }
  }

  @Override
  public void onStateActivityException(StateMachineExecutor executor, StateMachine machine, C context, State state, Exception exception) {
    List<StateActivityExceptionListener<C>> list = get(ExecutorEvent.STATE_ACTIVITY_EXCEPTION);
    for (StateActivityExceptionListener<C> l : list) {
      l.onStateActivityException(executor, machine, context, state, exception);
    }
  }
  
  @Override
  public void onStateEnter(StateMachineExecutor executor, StateMachine machine, C context, State state) {
    List<StateEnterListener<C>> list = get(ExecutorEvent.STATE_ENTER);
    for (StateEnterListener<C> l : list) {
      l.onStateEnter(executor, machine, context, state);
    }
  }

  @Override
  public void onStateEnterBeforeExecution(StateMachineExecutor executor, StateMachine machine, C context, State state) {
    List<StateEnterBeforeExecutionListener<C>> list = get(ExecutorEvent.STATE_ENTER_BEFORE_EXECUTION);
    for (StateEnterBeforeExecutionListener<C> l : list) {
      l.onStateEnterBeforeExecution(executor, machine, context, state);
    }
  }

  @Override
  public void onStateEnterAfterExecution(StateMachineExecutor executor, StateMachine machine, C context, State state) {
    List<StateEnterAfterExecutionListener<C>> list = get(ExecutorEvent.STATE_ENTER_AFTER_EXECUTION);
    for (StateEnterAfterExecutionListener<C> l : list) {
      l.onStateEnterAfterExecution(executor, machine, context, state);
    }
  }

  @Override
  public void onStateEnterException(StateMachineExecutor executor, StateMachine machine, C context, State state, Exception exception) {
    List<StateEnterExceptionListener<C>> list = get(ExecutorEvent.STATE_ENTER_EXCEPTION);
    for (StateEnterExceptionListener<C> l : list) {
      l.onStateEnterException(executor, machine, context, state, exception);
    }
  }
  
  @Override
  public void onStateExit(StateMachineExecutor executor, StateMachine machine, C context, State state) {
    List<StateExitListener<C>> list = get(ExecutorEvent.STATE_EXIT);
    for (StateExitListener<C> l : list) {
      l.onStateExit(executor, machine, context, state);
    }
  }

  @Override
  public void onStateExitBeforeExecution(StateMachineExecutor executor, StateMachine machine, C context, State state) {
    List<StateExitBeforeExecutionListener<C>> list = get(ExecutorEvent.STATE_EXIT_BEFORE_EXECUTION);
    for (StateExitBeforeExecutionListener<C> l : list) {
      l.onStateExitBeforeExecution(executor, machine, context, state);
    }
  }

  @Override
  public void onStateExitAfterExecution(StateMachineExecutor executor, StateMachine machine, C context, State state) {
    List<StateExitAfterExecutionListener<C>> list = get(ExecutorEvent.STATE_EXIT_AFTER_EXECUTION);
    for (StateExitAfterExecutionListener<C> l : list) {
      l.onStateExitAfterExecution(executor, machine, context, state);
    }
  }

  @Override
  public void onStateExitException(StateMachineExecutor executor, StateMachine machine, C context, State state, Exception exception) {
    List<StateExitExceptionListener<C>> list = get(ExecutorEvent.STATE_EXIT_EXCEPTION);
    for (StateExitExceptionListener<C> l : list) {
      l.onStateExitException(executor, machine, context, state, exception);
    }
  }

  @Override
  public void onTransitionStarted(StateMachineExecutor executor, StateMachine machine, C context, Event event, Transition transition) {
    List<TransitionStartedListener<C>> list = get(ExecutorEvent.TRANSITION_STARTED);
    for (TransitionStartedListener<C> l : list) {
      l.onTransitionStarted(executor, machine, context, event, transition);
    }
  }

  @Override
  public void onTransitionEnded(StateMachineExecutor executor, StateMachine machine, C context, Event event, Transition transition) {
    List<TransitionEndedListener<C>> list = get(ExecutorEvent.TRANSITION_ENDED);
    for (TransitionEndedListener<C> l : list) {
      l.onTransitionEnded(executor, machine, context, event, transition);
    }
  }

  @Override
  public void onTransitionGuardBeforeExecution(StateMachineExecutor executor, StateMachine machine, C context, Event event, Transition transition) {
    List<TransitionGuardBeforeExecutionListener<C>> list = get(ExecutorEvent.TRANSITION_GUARD_BEFORE_EXECUTION);
    for (TransitionGuardBeforeExecutionListener<C> l : list) {
      l.onTransitionGuardBeforeExecution(executor, machine, context, event, transition);
    }
  }

  @Override
  public void onTransitionGuardAfterExecution(StateMachineExecutor executor, StateMachine machine, C context, Event event, Transition transition) {
    List<TransitionGuardAfterExecutionListener<C>> list = get(ExecutorEvent.TRANSITION_GUARD_AFTER_EXECUTION);
    for (TransitionGuardAfterExecutionListener<C> l : list) {
      l.onTransitionGuardAfterExecution(executor, machine, context, event, transition);
    }
  }

  @Override
  public void onTransitionGuardException(StateMachineExecutor executor, StateMachine machine, C context, Event event, Transition transition, Exception exception) {
    List<TransitionGuardExceptionListener<C>> list = get(ExecutorEvent.TRANSITION_GUARD_EXCEPTION);
    for (TransitionGuardExceptionListener<C> l : list) {
      l.onTransitionGuardException(executor, machine, context, event, transition, exception);
    }
  }

  @Override
  public void onTransitionEffectBeforeExecution(StateMachineExecutor executor, StateMachine machine, C context, Event event, Transition transition) {
    List<TransitionEffectBeforeExecutionListener<C>> list = get(ExecutorEvent.TRANSITION_EFFECT_BEFORE_EXECUTION);
    for (TransitionEffectBeforeExecutionListener<C> l : list) {
      l.onTransitionEffectBeforeExecution(executor, machine, context, event, transition);
    }
  }

  @Override
  public void onTransitionEffectAfterExecution(StateMachineExecutor executor, StateMachine machine, C context, Event event, Transition transition) {
    List<TransitionEffectAfterExecutionListener<C>> list = get(ExecutorEvent.TRANSITION_EFFECT_AFTER_EXECUTION);
    for (TransitionEffectAfterExecutionListener<C> l : list) {
      l.onTransitionEffectAfterExecution(executor, machine, context, event, transition);
    }
  }

  @Override
  public void onTransitionEffectException(StateMachineExecutor executor, StateMachine machine, C context, Event event, Transition transition, Exception exception) {
    List<TransitionEffectExceptionListener<C>> list = get(ExecutorEvent.TRANSITION_EFFECT_EXCEPTION);
    for (TransitionEffectExceptionListener<C> l : list) {
      l.onTransitionEffectException(executor, machine, context, event, transition, exception);
    }
  }
}
