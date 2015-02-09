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
package com.lympid.core.behaviorstatemachines.listener;

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.VertexUtils;
import com.lympid.core.behaviorstatemachines.impl.ExecutorEvent;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_ACTIVITY_EXCEPTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_ENTER_EXCEPTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_EXIT_EXCEPTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.TRANSITION_EFFECT_EXCEPTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.TRANSITION_GUARD_EXCEPTION;
import org.slf4j.Logger;

/**
 *
 * @author Fabien Renaud
 */
public class ErrorLoggerListener extends LoggerListener implements
  StateEnterExceptionListener, StateExitExceptionListener, StateActivityExceptionListener,
  TransitionGuardExceptionListener, TransitionEffectExceptionListener {

  public ErrorLoggerListener(Logger log) {
    super(log);
  }

  @Override
  public void onStateEnterException(StateMachineExecutor executor, StateMachine machine, Object context, State state, Exception exception) {
    log(STATE_ENTER_EXCEPTION, executor, machine, context, state, exception);
  }

  @Override
  public void onStateExitException(StateMachineExecutor executor, StateMachine machine, Object context, State state, Exception exception) {
    log(STATE_EXIT_EXCEPTION, executor, machine, context, state, exception);
  }

  @Override
  public void onStateActivityException(StateMachineExecutor executor, StateMachine machine, Object context, State state, Exception exception) {
    log(STATE_ACTIVITY_EXCEPTION, executor, machine, context, state, exception);
  }

  private void log(ExecutorEvent tag, StateMachineExecutor executor, StateMachine machine, Object context, State state, Exception exception) {
    log.error("executor=\"{}\" machine=\"{}\" tag=\"{}\" state=\"{}\" context=\"{}\"", executor.getId(), VertexUtils.nameOrId(machine), tag, VertexUtils.nameOrId(state), context, exception);
  }

  @Override
  public void onTransitionGuardException(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition, Exception exception) {
    log(TRANSITION_GUARD_EXCEPTION, executor, machine, context, event, transition, exception);
  }

  @Override
  public void onTransitionEffectException(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition, Exception exception) {
    log(TRANSITION_EFFECT_EXCEPTION, executor, machine, context, event, transition, exception);
  }

  private void log(ExecutorEvent tag, StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition, Exception exception) {
    log.error("executor=\"{}\" machine=\"{}\" tag=\"{}\" event=\"{}\" transition=\"{}\" source=\"{}\" target=\"{}\" context=\"{}\"", executor.getId(), VertexUtils.nameOrId(machine), tag, event, VertexUtils.nameOrId(transition), VertexUtils.nameOrId(transition.source()), VertexUtils.nameOrId(transition.target()), context, exception);
  }
}
