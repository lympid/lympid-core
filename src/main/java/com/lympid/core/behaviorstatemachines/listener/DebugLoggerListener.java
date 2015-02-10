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
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.VertexUtils;
import com.lympid.core.behaviorstatemachines.impl.ExecutorEvent;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.EVENT_DEFERRED;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.EVENT_DENIED;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.TRANSITION_ENDED;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.TRANSITION_STARTED;
import org.slf4j.Logger;

/**
 *
 * @author Fabien Renaud
 */
public class DebugLoggerListener extends InfoLoggerListener implements
        TransitionStartedListener, TransitionEndedListener, EventDeniedListener, EventDeferredListener {

  public DebugLoggerListener(Logger log) {
    super(log);
  }

  @Override
  public void onTransitionStarted(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    log(TRANSITION_STARTED, executor, machine, context, event, transition);
  }

  @Override
  public void onTransitionEnded(StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    log(TRANSITION_ENDED, executor, machine, context, event, transition);
  }

  private void log(ExecutorEvent tag, StateMachineExecutor executor, StateMachine machine, Object context, Event event, Transition transition) {
    log.debug("executor=\"{}\" machine=\"{}\" tag=\"{}\" event=\"{}\" transition=\"{}\" source=\"{}\" target=\"{}\" context=\"{}\"", executor.getId(), VertexUtils.nameOrId(machine), tag, event, VertexUtils.nameOrId(transition), VertexUtils.nameOrId(transition.source()), VertexUtils.nameOrId(transition.target()), context);
  }

  @Override
  public void onEventDenied(StateMachineExecutor executor, StateMachine machine, Object context, Event event) {
    log(EVENT_DENIED, executor, machine, context, event);
  }

  @Override
  public void onEventDeferred(StateMachineExecutor executor, StateMachine machine, Object context, Event event) {
    log(EVENT_DEFERRED, executor, machine, context, event);
  }

  private void log(ExecutorEvent tag, StateMachineExecutor executor, StateMachine machine, Object context, Event event) {
    log.info("executor=\"{}\" machine=\"{}\" tag=\"{}\" event=\"{}\" context=\"{}\"", executor.getId(), VertexUtils.nameOrId(machine), tag, event, context);
  }

}
