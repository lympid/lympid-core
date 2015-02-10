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
import com.lympid.core.behaviorstatemachines.VertexUtils;
import com.lympid.core.behaviorstatemachines.impl.ExecutorEvent;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.EVENT_ACCEPTED;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.MACHINE_STARTED;
import static com.lympid.core.behaviorstatemachines.impl.ExecutorEvent.MACHINE_TERMINATED;
import org.slf4j.Logger;

/**
 *
 * @author Fabien Renaud
 */
public class InfoLoggerListener extends WarnLoggerListener
        implements MachineStartedListener, MachineTerminatedListener, EventAcceptedListener {

  public InfoLoggerListener(Logger log) {
    super(log);
  }

  @Override
  public void onMachineStarted(StateMachineExecutor executor, StateMachine machine, Object context) {
    log(MACHINE_STARTED, executor, machine, context);
  }

  @Override
  public void onMachineTerminated(StateMachineExecutor executor, StateMachine machine, Object context) {
    log(MACHINE_TERMINATED, executor, machine, context);
  }

  private void log(ExecutorEvent tag, StateMachineExecutor executor, StateMachine machine, Object context) {
    log.info("executor=\"{}\" machine=\"{}\" tag=\"{}\" context=\"{}\"", executor.getId(), VertexUtils.nameOrId(machine), tag, context);
  }

  @Override
  public void onEventAccepted(StateMachineExecutor executor, StateMachine machine, Object context, Event event) {
    log.info("executor=\"{}\" machine=\"{}\" tag=\"{}\" event=\"{}\" context=\"{}\"", executor.getId(), VertexUtils.nameOrId(machine), EVENT_ACCEPTED, event, context);
  }

}
