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
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_ENTER_AFTER_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_ENTER_BEFORE_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_ENTER_EXCEPTION;
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
public class StringLoggerListener implements AllListener {

  private final StringBuffer buffer = new StringBuffer();
  private final StringBuffer activityBuffer = new StringBuffer();

  @Override
  public void onStateEnterException(StateMachineExecutor executor, StateMachine machine, Object context, State state, Exception exception) {
    bufferAppend(format(STATE_ENTER_EXCEPTION, context, state, exception));
  }

  @Override
  public void onStateExitException(StateMachineExecutor executor, StateMachine machine, Object context, State state, Exception exception) {
    bufferAppend(format(STATE_EXIT_EXCEPTION, context, state, exception));
  }

  @Override
  public void onStateActivityException(StateMachineExecutor executor, StateMachine machine, Object context, State state, Exception exception) {
    activityBufferAppend(format(STATE_ACTIVITY_EXCEPTION, context, state, exception));
  }

  @Override
  public void onTransitionGuardException(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition, Exception exception) {
    bufferAppend(format(TRANSITION_GUARD_EXCEPTION, context, event, transition, exception));
  }

  @Override
  public void onTransitionEffectException(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition, Exception exception) {
    bufferAppend(format(TRANSITION_EFFECT_EXCEPTION, context, event, transition, exception));
  }

  @Override
  public void onMachineStarted(StateMachineExecutor executor, StateMachine machine, Object context) {
    bufferAppend(format(MACHINE_STARTED, context));
  }

  @Override
  public void onMachineTerminated(StateMachineExecutor executor, StateMachine machine, Object context) {
    bufferAppend(format(MACHINE_TERMINATED, context));
  }

  @Override
  public void onEventAccepted(StateMachineExecutor executor, StateMachine machine, Object context, Event event) {
    bufferAppend(format(EVENT_ACCEPTED, context, event));
  }

  @Override
  public void onTransitionStarted(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    bufferAppend(format(TRANSITION_STARTED, context, event, transition));
  }

  @Override
  public void onTransitionEnded(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    bufferAppend(format(TRANSITION_ENDED, context, event, transition));
  }

  @Override
  public void onEventDenied(StateMachineExecutor executor, StateMachine machine, Object context, Event event) {
    bufferAppend(format(EVENT_DENIED, context, event));
  }

  @Override
  public void onEventDeferred(StateMachineExecutor executor, StateMachine machine, Object context, Event event) {
    bufferAppend(format(EVENT_DEFERRED, context, event));
  }

  @Override
  public void onStateActivityBeforeExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    activityBufferAppend(format(STATE_ACTIVITY_BEFORE_EXECUTION, context, state));
  }

  @Override
  public void onStateActivityAfterExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    activityBufferAppend(format(STATE_ACTIVITY_AFTER_EXECUTION, context, state));
  }

  @Override
  public void onStateEnterBeforeExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    bufferAppend(format(STATE_ENTER_BEFORE_EXECUTION, context, state));
  }

  @Override
  public void onStateEnterAfterExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    bufferAppend(format(STATE_ENTER_AFTER_EXECUTION, context, state));
  }

  @Override
  public void onStateExitBeforeExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    bufferAppend(format(STATE_EXIT_BEFORE_EXECUTION, context, state));
  }

  @Override
  public void onStateExitAfterExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    bufferAppend(format(STATE_EXIT_AFTER_EXECUTION, context, state));
  }

  @Override
  public void onTransitionGuardBeforeExecution(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    bufferAppend(format(TRANSITION_GUARD_BEFORE_EXECUTION, context, event, transition));
  }

  @Override
  public void onTransitionGuardAfterExecution(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    bufferAppend(format(TRANSITION_GUARD_AFTER_EXECUTION, context, event, transition));
  }

  @Override
  public void onTransitionEffectBeforeExecution(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    bufferAppend(format(TRANSITION_EFFECT_BEFORE_EXECUTION, context, event, transition));
  }

  @Override
  public void onTransitionEffectAfterExecution(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    bufferAppend(format(TRANSITION_EFFECT_AFTER_EXECUTION, context, event, transition));
  }

  private String format(ExecutorEvent tag, Object context, State state, Exception exception) {
    return String.format("tag=\"%s\" state=\"%s\" context=\"%s\" %s", tag, VertexUtils.nameOrId(state), context, exception);
  }

  private String format(ExecutorEvent tag, Object context, Event event, Transition transition, Exception exception) {
    return String.format("tag=\"%s\" event=\"%s\" transition=\"%s\" source=\"%s\" target=\"%s\" context=\"%s\" %s", tag, event, VertexUtils.nameOrId(transition), VertexUtils.nameOrId(transition.source()), VertexUtils.nameOrId(transition.target()), context, exception);
  }

  private String format(ExecutorEvent tag, Object context, Event event) {
    return String.format("tag=\"%s\" event=\"%s\" context=\"%s\"", tag, event, context);
  }

  private String format(ExecutorEvent tag, Object context, Event event, Transition transition) {
    return String.format("tag=\"%s\" event=\"%s\" transition=\"%s\" source=\"%s\" target=\"%s\" context=\"%s\"", tag, event, VertexUtils.nameOrId(transition), VertexUtils.nameOrId(transition.source()), VertexUtils.nameOrId(transition.target()), context);
  }

  private String format(ExecutorEvent tag, Object context) {
    return String.format("tag=\"%s\" context=\"%s\"", tag, context);
  }

  private String format(ExecutorEvent tag, Object context, State state) {
    return String.format("tag=\"%s\" state=\"%s\" context=\"%s\"", tag, VertexUtils.nameOrId(state), context);
  }
  
  private void bufferAppend(final String s) {
    buffer.append(s).append('\n');
  }
  
  private void activityBufferAppend(final String s) {
    activityBuffer.append(s).append('\n');
  }

  public String mainBuffer() {
    return buffer.toString();
  }

  public String activityBuffer() {
    return activityBuffer.toString();
  }
}
