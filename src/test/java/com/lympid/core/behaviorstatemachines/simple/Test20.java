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
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.SimpleStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import com.lympid.core.behaviorstatemachines.listener.StringLoggerListener;
import static org.junit.Assert.assertEquals;
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
    final StringLoggerListener log = new StringLoggerListener();
   
    StateMachineExecutor fsm = fsm();
    fsm.listeners().add(log);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("#6"));
    
    Thread.sleep(10);
    
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("#6"));
    
    fsm.take(new StringEvent("go2"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    
    assertEquals(MAIN_LOG, log.mainBuffer());
    assertEquals(ACTIVITY_LOG, log.activityBuffer());
  }
  
  @Test
  public void run_nolog() throws InterruptedException {   
    StateMachineExecutor fsm = fsm();
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("#6"));
    
    Thread.sleep(10);
    
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("#6"));
    
    fsm.take(new StringEvent("go2"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
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
            .guard((e, c) -> { throw new RuntimeException("guard"); })
            .target(end)
          .transition("t2")
            .on("go2")
            .effect((e, c) -> { throw new RuntimeException("effect"); })
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
  
  private static final String MAIN_LOG = "tag=\"MACHINE_STARTED\" context=\"null\"\n" +
"tag=\"EVENT_ACCEPTED\" event=\"CompletionEvent\" context=\"null\"\n" +
"tag=\"TRANSITION_STARTED\" event=\"CompletionEvent\" transition=\"t0\" source=\"#4\" target=\"#6\" context=\"null\"\n" +
"tag=\"STATE_ENTER_BEFORE_EXECUTION\" state=\"#6\" context=\"null\"\n" +
"tag=\"STATE_ENTER_EXCEPTION\" state=\"#6\" context=\"null\" java.lang.RuntimeException: foo\n" +
"tag=\"TRANSITION_ENDED\" event=\"CompletionEvent\" transition=\"t0\" source=\"#4\" target=\"#6\" context=\"null\"\n" +
"tag=\"TRANSITION_GUARD_BEFORE_EXECUTION\" event=\"go\" transition=\"t1\" source=\"#6\" target=\"end\" context=\"null\"\n" +
"tag=\"TRANSITION_GUARD_EXCEPTION\" event=\"go\" transition=\"t1\" source=\"#6\" target=\"end\" context=\"null\" java.lang.RuntimeException: guard\n" +
"tag=\"EVENT_DENIED\" event=\"go\" context=\"null\"\n" +
"tag=\"EVENT_ACCEPTED\" event=\"go2\" context=\"null\"\n" +
"tag=\"TRANSITION_STARTED\" event=\"go2\" transition=\"t2\" source=\"#6\" target=\"end\" context=\"null\"\n" +
"tag=\"STATE_EXIT_BEFORE_EXECUTION\" state=\"#6\" context=\"null\"\n" +
"tag=\"STATE_EXIT_EXCEPTION\" state=\"#6\" context=\"null\" java.lang.RuntimeException: bar\n" +
"tag=\"TRANSITION_EFFECT_BEFORE_EXECUTION\" event=\"go2\" transition=\"t2\" source=\"#6\" target=\"end\" context=\"null\"\n" +
"tag=\"TRANSITION_EFFECT_EXCEPTION\" event=\"go2\" transition=\"t2\" source=\"#6\" target=\"end\" context=\"null\" java.lang.RuntimeException: effect\n" +
"tag=\"TRANSITION_ENDED\" event=\"go2\" transition=\"t2\" source=\"#6\" target=\"end\" context=\"null\"\n" +
"tag=\"MACHINE_TERMINATED\" context=\"null\"\n";
  private static final String  ACTIVITY_LOG = "tag=\"STATE_ACTIVITY_BEFORE_EXECUTION\" state=\"#6\" context=\"null\"\n" +
"tag=\"STATE_ACTIVITY_EXCEPTION\" state=\"#6\" context=\"null\" java.lang.RuntimeException: something\n";
  
  private static final String STDOUT = "StateMachine: \"" + Test20.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: #6\n" +
"    Transition: \"t0\" --- #4 -> #6\n" +
"    Transition: \"t1\" --- #6 -> \"end\"\n" +
"    Transition: \"t2\" --- #6 -> \"end\"";
}
