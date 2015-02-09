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

package com.lympid.core.behaviorstatemachines.simple;

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.builder.SimpleStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import static com.lympid.core.behaviorstatemachines.listener.StringBufferLoggerTester.assertLogEquals;
import com.lympid.core.behaviorstatemachines.listener.StringBuilderLogger;
import com.lympid.core.behaviorstatemachines.listener.TraceLoggerListener;
import org.junit.Test;

/**
 * Tests exceptions in state entry, exit and activity behaviors do not interrupt
 * the state machine and can be monitored.
 * Tests also exceptions in transition guards and effects. A guard throwing
 * an exception is considered as false.
 * 
 * @author Fabien Renaud
 */
public class Test20 extends AbstractStateMachineTest {
  
  @Test
  public void run_log() throws InterruptedException {
    final StringBuilderLogger bufferLogger = new StringBuilderLogger(StringBuilderLogger.LogLevel.TRACE);
   
    StateMachineExecutor fsm = fsm();
    fsm.listeners().add(new TraceLoggerListener(bufferLogger));
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("#6"));
    
    Thread.sleep(10);
    
    fsm.take(new StringEvent("go"));
    assertStateConfiguration(fsm, new ActiveStateTree("#6"));
    
    fsm.take(new StringEvent("go2"));
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    
    assertLogEquals(TRACE_LOG, bufferLogger);
  }
  
  @Test
  public void run_nolog() throws InterruptedException {   
    StateMachineExecutor fsm = fsm();
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("#6"));
    
    Thread.sleep(10);
    
    fsm.take(new StringEvent("go"));
    assertStateConfiguration(fsm, new ActiveStateTree("#6"));
    
    fsm.take(new StringEvent("go2"));
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Object> builder = new StateMachineBuilder<>(name());
    
    VertexBuilderReference end = builder
      .region()
        .finalState("end");
    
    SimpleStateBuilder<Object> stateA = new SimpleStateBuilder();
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target(stateA);
    
    
    builder
      .region()
        .state(stateA)
          .entry((c) -> { throw new RuntimeException("foo"); })
          .exit((c) -> { throw new RuntimeException("bar"); })
          .activity((c) -> { throw new RuntimeException("something"); })
          .transition("t1")
            .on("go")
            .guard((e,c) -> { throw new RuntimeException("guard"); })
            .target(end)
          .transition("t2")
            .on("go2")
            .effect((e,c) -> { throw new RuntimeException("effect"); })
            .target(end);
    
    return builder;
  }

  @Override
  protected boolean sequentialContextInjection() {
    return false;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final String TRACE_LOG = "[INFO] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"MACHINE_STARTED\" context=\"null\"\n" +
"[INFO] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"EVENT_ACCEPTED\" event=\"CompletionEvent\" context=\"null\"\n" +
"[DEBUG] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"TRANSITION_STARTED\" event=\"CompletionEvent\" transition=\"t0\" source=\"#4\" target=\"#6\" context=\"null\"\n" +
"[TRACE] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"STATE_ENTER_BEFORE_EXECUTION\" state=\"#6\" context=\"null\"\n" +
"[ERROR] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"STATE_ENTER_EXCEPTION\" state=\"#6\" context=\"null\"\n" +
"java.lang.RuntimeException: foo\n" +
"[DEBUG] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"TRANSITION_ENDED\" event=\"CompletionEvent\" transition=\"t0\" source=\"#4\" target=\"#6\" context=\"null\"\n" +
"[TRACE] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"STATE_ACTIVITY_BEFORE_EXECUTION\" state=\"#6\" context=\"null\"\n" +
"[ERROR] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"STATE_ACTIVITY_EXCEPTION\" state=\"#6\" context=\"null\"\n" +
"java.lang.RuntimeException: something\n" +
"[TRACE] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"TRANSITION_GUARD_BEFORE_EXECUTION\" event=\"go\" transition=\"t1\" source=\"#6\" target=\"end\" context=\"null\"\n" +
"[ERROR] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"TRANSITION_GUARD_EXCEPTION\" event=\"go\" transition=\"t1\" source=\"#6\" target=\"end\" context=\"null\"\n" +
"java.lang.RuntimeException: guard\n" +
"[INFO] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"EVENT_DENIED\" event=\"go\" context=\"null\"\n" +
"[INFO] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"EVENT_ACCEPTED\" event=\"go2\" context=\"null\"\n" +
"[DEBUG] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"TRANSITION_STARTED\" event=\"go2\" transition=\"t2\" source=\"#6\" target=\"end\" context=\"null\"\n" +
"[TRACE] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"STATE_EXIT_BEFORE_EXECUTION\" state=\"#6\" context=\"null\"\n" +
"[ERROR] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"STATE_EXIT_EXCEPTION\" state=\"#6\" context=\"null\"\n" +
"java.lang.RuntimeException: bar\n" +
"[TRACE] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"TRANSITION_EFFECT_BEFORE_EXECUTION\" event=\"go2\" transition=\"t2\" source=\"#6\" target=\"end\" context=\"null\"\n" +
"[ERROR] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"TRANSITION_EFFECT_EXCEPTION\" event=\"go2\" transition=\"t2\" source=\"#6\" target=\"end\" context=\"null\"\n" +
"java.lang.RuntimeException: effect\n" +
"[DEBUG] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"TRANSITION_ENDED\" event=\"go2\" transition=\"t2\" source=\"#6\" target=\"end\" context=\"null\"\n" +
"[INFO] executor=\"1\" machine=\"" + Test20.class.getSimpleName() + "\" tag=\"MACHINE_TERMINATED\" context=\"null\"";
  
  private static final String STDOUT = "StateMachine: \"" + Test20.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: #6\n" +
"    Transition: \"t0\" --- #4 -> #6\n" +
"    Transition: \"t1\" --- #6 -> \"end\"\n" +
"    Transition: \"t2\" --- #6 -> \"end\"";
}
