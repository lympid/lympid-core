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
package com.lympid.core.behaviorstatemachines.listener;

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.VertexUtils;
import com.lympid.core.behaviorstatemachines.impl.ExecutorEvent;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.EVENT_ACCEPTED;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.EVENT_DEFERRED;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.EVENT_DENIED;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.MACHINE_STARTED;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.MACHINE_TERMINATED;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_ACTIVITY_AFTER_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_ACTIVITY_BEFORE_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_ACTIVITY_EXCEPTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_ENTER;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_ENTER_AFTER_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_ENTER_BEFORE_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_ENTER_EXCEPTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_EXIT;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_EXIT_AFTER_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_EXIT_BEFORE_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_EXIT_EXCEPTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.TRANSITION_EFFECT_AFTER_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.TRANSITION_EFFECT_BEFORE_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.TRANSITION_EFFECT_EXCEPTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.TRANSITION_ENDED;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.TRANSITION_GUARD_AFTER_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.TRANSITION_GUARD_BEFORE_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.TRANSITION_GUARD_EXCEPTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.TRANSITION_STARTED;

/**
 *
 * @author Fabien Renaud
 */
public abstract class AbstractStringLoggerListener implements AllListener {

  @Override
  public void onStateEnterException(StateMachineExecutor executor, StateMachine machine, Object context, State state, Exception exception) {
    log(STATE_ENTER_EXCEPTION, format(STATE_ENTER_EXCEPTION, executor, machine, context, state), exception);
  }

  @Override
  public void onStateExitException(StateMachineExecutor executor, StateMachine machine, Object context, State state, Exception exception) {
    log(STATE_EXIT_EXCEPTION, format(STATE_EXIT_EXCEPTION, executor, machine, context, state), exception);
  }

  @Override
  public void onStateActivityException(StateMachineExecutor executor, StateMachine machine, Object context, State state, Exception exception) {
    logActivity(STATE_ACTIVITY_EXCEPTION, format(STATE_ACTIVITY_EXCEPTION, executor, machine, context, state), exception);
  }

  @Override
  public void onTransitionGuardException(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition, Exception exception) {
    log(TRANSITION_GUARD_EXCEPTION, format(TRANSITION_GUARD_EXCEPTION, executor, machine, context, event, transition), exception);
  }

  @Override
  public void onTransitionEffectException(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition, Exception exception) {
    log(TRANSITION_EFFECT_EXCEPTION, format(TRANSITION_EFFECT_EXCEPTION, executor, machine, context, event, transition), exception);
  }

  @Override
  public void onMachineStarted(StateMachineExecutor executor, StateMachine machine, Object context) {
    log(MACHINE_STARTED, format(MACHINE_STARTED, executor, machine, context));
  }

  @Override
  public void onMachineTerminated(StateMachineExecutor executor, StateMachine machine, Object context) {
    log(MACHINE_TERMINATED, format(MACHINE_TERMINATED, executor, machine, context));
  }

  @Override
  public void onEventAccepted(StateMachineExecutor executor, StateMachine machine, Object context, Event event) {
    log(EVENT_ACCEPTED, format(EVENT_ACCEPTED, executor, machine, context, event));
  }

  @Override
  public void onTransitionStarted(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    log(TRANSITION_STARTED, format(TRANSITION_STARTED, executor, machine, context, event, transition));
  }

  @Override
  public void onTransitionEnded(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    log(TRANSITION_ENDED, format(TRANSITION_ENDED, executor, machine, context, event, transition));
  }

  @Override
  public void onEventDenied(StateMachineExecutor executor, StateMachine machine, Object context, Event event) {
    log(EVENT_DENIED, format(EVENT_DENIED, executor, machine, context, event));
  }

  @Override
  public void onEventDeferred(StateMachineExecutor executor, StateMachine machine, Object context, Event event) {
    log(EVENT_DEFERRED, format(EVENT_DEFERRED, executor, machine, context, event));
  }

  @Override
  public void onStateActivityBeforeExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    logActivity(STATE_ACTIVITY_BEFORE_EXECUTION, format(STATE_ACTIVITY_BEFORE_EXECUTION, executor, machine, context, state));
  }

  @Override
  public void onStateActivityAfterExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    logActivity(STATE_ACTIVITY_AFTER_EXECUTION, format(STATE_ACTIVITY_AFTER_EXECUTION, executor, machine, context, state));
  }

  @Override
  public void onStateEnter(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    log(STATE_ENTER, format(STATE_ENTER, executor, machine, context, state));
  }

  @Override
  public void onStateEnterBeforeExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    log(STATE_ENTER_BEFORE_EXECUTION, format(STATE_ENTER_BEFORE_EXECUTION, executor, machine, context, state));
  }

  @Override
  public void onStateEnterAfterExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    log(STATE_ENTER_AFTER_EXECUTION, format(STATE_ENTER_AFTER_EXECUTION, executor, machine, context, state));
  }

  @Override
  public void onStateExit(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    log(STATE_EXIT, format(STATE_EXIT, executor, machine, context, state));
  }

  @Override
  public void onStateExitBeforeExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    log(STATE_EXIT_BEFORE_EXECUTION, format(STATE_EXIT_BEFORE_EXECUTION, executor, machine, context, state));
  }

  @Override
  public void onStateExitAfterExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    log(STATE_EXIT_AFTER_EXECUTION, format(STATE_EXIT_AFTER_EXECUTION, executor, machine, context, state));
  }

  @Override
  public void onTransitionGuardBeforeExecution(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    log(TRANSITION_GUARD_BEFORE_EXECUTION, format(TRANSITION_GUARD_BEFORE_EXECUTION, executor, machine, context, event, transition));
  }

  @Override
  public void onTransitionGuardAfterExecution(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    log(TRANSITION_GUARD_AFTER_EXECUTION, format(TRANSITION_GUARD_AFTER_EXECUTION, executor, machine, context, event, transition));
  }

  @Override
  public void onTransitionEffectBeforeExecution(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    log(TRANSITION_EFFECT_BEFORE_EXECUTION, format(TRANSITION_EFFECT_BEFORE_EXECUTION, executor, machine, context, event, transition));
  }

  @Override
  public void onTransitionEffectAfterExecution(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    log(TRANSITION_EFFECT_AFTER_EXECUTION, format(TRANSITION_EFFECT_AFTER_EXECUTION, executor, machine, context, event, transition));
  }

  private String format(ExecutorEvent tag, StateMachineExecutor executor, StateMachine machine, Object context, State state, Exception exception) {
    return String.format("tag=\"%s\" executor=\"%s\" state=\"%s\" context=\"%s\" %s", tag, executor.getName(), VertexUtils.nameOrId(state), context, exception);
  }

  private String format(ExecutorEvent tag, StateMachineExecutor executor, StateMachine machine, Object context, Event event) {
    return String.format("tag=\"%s\" executor=\"%s\" event=\"%s\" context=\"%s\"", tag, executor.getName(), event, context);
  }

  private String format(ExecutorEvent tag, StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    return String.format("tag=\"%s\" executor=\"%s\" event=\"%s\" transition=\"%s\" source=\"%s\" target=\"%s\" context=\"%s\"", tag, executor.getName(), event, VertexUtils.nameOrId(transition), VertexUtils.nameOrId(transition.source()), VertexUtils.nameOrId(transition.target()), context);
  }

  private String format(ExecutorEvent tag, StateMachineExecutor executor, StateMachine machine, Object context) {
    return String.format("tag=\"%s\" executor=\"%s\" context=\"%s\"", tag, executor.getName(), context);
  }

  private String format(ExecutorEvent tag, StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    return String.format("tag=\"%s\" executor=\"%s\" state=\"%s\" context=\"%s\"", tag, executor.getName(), VertexUtils.nameOrId(state), context);
  }

  protected abstract void log(final ExecutorEvent tag, final String s);

  protected abstract void log(final ExecutorEvent tag, final String s, final Exception exception);

  protected abstract void logActivity(final ExecutorEvent tag, final String s);

  protected abstract void logActivity(final ExecutorEvent tag, final String s, final Exception exception);

}
