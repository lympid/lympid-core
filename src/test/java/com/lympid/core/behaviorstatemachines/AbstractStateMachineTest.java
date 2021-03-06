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

import com.lympid.core.behaviorstatemachines.builder.SequentialContextInjector;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration;
import com.lympid.core.behaviorstatemachines.impl.SyncStateMachineExecutor;
import com.lympid.core.behaviorstatemachines.impl.TextVisitor;
import com.lympid.core.common.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.lympid.core.behaviorstatemachines.StateMachineTester.assertTextVisitor;

/**
 *
 * @author Fabien Renaud
 */
public abstract class AbstractStateMachineTest<C> implements StateMachineTest {

  public static final ScheduledExecutorService THREAD_POOL = Executors.newScheduledThreadPool(1);
  private static final Map<Class, StateMachine> MACHINES = new HashMap<>();

  protected static void assertSequentialContextEquals(final SequentialContext expected, final StateMachineExecutor<?> fsm) {
    TestUtils.assertSequentialContextEquals(expected, fsm);
  }

  @Before
  public void setUp() {
    if (MACHINES.get(getClass()) == null) {
      StateMachineBuilder<?> b = topLevelMachineBuilder();
      if (sequentialContextInjection()) {
        b.accept(new SequentialContextInjector());
      }
      MACHINES.put(getClass(), b.instance());
    }
  }

  @Test
  public void stdout() {
    String stdout = stdOut();
    if (stdOut() == null || stdout.isEmpty()) {
      TextVisitor v = new TextVisitor();
      topLevelStateMachine().accept(v);
      System.out.println(v);  // to help
//      fail("stdout is null");
    } else {
      assertTextVisitor(stdOut(), topLevelStateMachine());
    }
  }

  @Override
  public final StateMachine topLevelStateMachine() {
    return MACHINES.get(getClass());
  }

  public StateMachineExecutor<C> fsm() {
    return fsm(null, null);
  }
  
  public StateMachineExecutor<C> fsm(ExecutorConfiguration configuration) {
    return fsm(null, configuration);
  }
  
  public StateMachineExecutor<C> fsm(C context) {
    return fsm(context, null);
  }
  
  public StateMachineExecutor<C> fsm(StateMachineSnapshot<C> snapshot) {
    return fsm(null, null, snapshot);
  }
  
  public StateMachineExecutor<C> fsm(C context, ExecutorConfiguration configuration) {
    return fsm(context, configuration, null);
  }

  public StateMachineExecutor<C> fsm(C context, ExecutorConfiguration configuration, StateMachineSnapshot<C> snapshot) {
    StateMachine machine = topLevelStateMachine();

    if (machine.metadata().hasActivities() || machine.metadata().hasTimeEvents()) {
      if (configuration == null) {
        configuration = new ExecutorConfiguration();
      }
      configuration.executor(THREAD_POOL);
    }
    
    return new SyncStateMachineExecutor.Builder<C>()
      .setName(executorName())
      .setStateMachine(machine)
      .setContext(context)
      .setConfiguration(configuration)
      .setSnapshot(snapshot)
      .build();
  }

  protected final String name() {
    return getClass().getSimpleName();
  }
  
  protected String executorName() {
    return null;
  }

  protected boolean sequentialContextInjection() {
    return true;
  }

  public abstract String stdOut();
}
