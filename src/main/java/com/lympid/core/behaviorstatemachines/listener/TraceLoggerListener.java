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
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_ACTIVITY_AFTER_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_ACTIVITY_BEFORE_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_ENTER_AFTER_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_ENTER_BEFORE_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_EXIT_AFTER_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.STATE_EXIT_BEFORE_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.TRANSITION_EFFECT_AFTER_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.TRANSITION_EFFECT_BEFORE_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.TRANSITION_GUARD_AFTER_EXECUTION;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.TRANSITION_GUARD_BEFORE_EXECUTION;
import org.slf4j.Logger;

/**
 *
 * @author Fabien Renaud
 */
public class TraceLoggerListener extends DebugLoggerListener implements AllListener {

  public TraceLoggerListener(Logger log) {
    super(log);
  }

  @Override
  public void onStateActivityBeforeExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    log(STATE_ACTIVITY_BEFORE_EXECUTION, executor, machine, context, state);
  }

  @Override
  public void onStateActivityAfterExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    log(STATE_ACTIVITY_AFTER_EXECUTION, executor, machine, context, state);
  }

  @Override
  public void onStateEnterBeforeExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    log(STATE_ENTER_BEFORE_EXECUTION, executor, machine, context, state);
  }

  @Override
  public void onStateEnterAfterExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    log(STATE_ENTER_AFTER_EXECUTION, executor, machine, context, state);
  }

  @Override
  public void onStateExitBeforeExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    log(STATE_EXIT_BEFORE_EXECUTION, executor, machine, context, state);
  }

  @Override
  public void onStateExitAfterExecution(StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    log(STATE_EXIT_AFTER_EXECUTION, executor, machine, context, state);
  }

  private void log(ExecutorEvent tag, StateMachineExecutor executor, StateMachine machine, Object context, State state) {
    log.trace("executor=\"{}\" machine=\"{}\" tag=\"{}\" state=\"{}\" context=\"{}\"", executor.getId(), VertexUtils.nameOrId(machine), tag, VertexUtils.nameOrId(state), context);
  }

  @Override
  public void onTransitionGuardBeforeExecution(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    log(TRANSITION_GUARD_BEFORE_EXECUTION, executor, machine, context, event, transition);
  }

  @Override
  public void onTransitionGuardAfterExecution(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    log(TRANSITION_GUARD_AFTER_EXECUTION, executor, machine, context, event, transition);
  }

  @Override
  public void onTransitionEffectBeforeExecution(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    log(TRANSITION_EFFECT_BEFORE_EXECUTION, executor, machine, context, event, transition);
  }

  @Override
  public void onTransitionEffectAfterExecution(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    log(TRANSITION_EFFECT_AFTER_EXECUTION, executor, machine, context, event, transition);
  }

  private void log(ExecutorEvent tag, StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    log.trace("executor=\"{}\" machine=\"{}\" tag=\"{}\" event=\"{}\" transition=\"{}\" source=\"{}\" target=\"{}\" context=\"{}\"", executor.getId(), VertexUtils.nameOrId(machine), tag, event, VertexUtils.nameOrId(transition), VertexUtils.nameOrId(transition.source()), VertexUtils.nameOrId(transition.target()), context);
  }

}
