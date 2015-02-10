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

import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import static com.lympid.core.behaviorstatemachines.listener.StringBufferLoggerTester.assertLogEquals;
import com.lympid.core.behaviorstatemachines.listener.StringBuilderLogger;
import com.lympid.core.behaviorstatemachines.listener.TraceLoggerListener;
import org.junit.Test;
import org.slf4j.Logger;

/**
 * State entries, activity and state exists are performed in the right order.
 * @author Fabien Renaud 
 */
public class Test16 extends AbstractStateMachineTest {
  
  @Test
  public void run() throws InterruptedException {
    final StringBuilderLogger bufferLogger = new StringBuilderLogger(StringBuilderLogger.LogLevel.TRACE);
    
    SequentialContext expected = new SequentialContext();
    expected
      .enter("foo")
      .enter("iak")
      .enter("dir")
      .enter("bar")
      .activity("something")
      .exit("abs")
      .exit("bat");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    addLogger(fsm, bufferLogger);
    fsm.go();
    
    Thread.sleep(2);
        
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
    
    assertLogEquals(TRACE_LOG, bufferLogger);
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<SequentialContext> builder = new StateMachineBuilder<>(name());
    
    VertexBuilderReference end = builder
      .region()
        .finalState("end");
    
    builder
      .region()
        .initial()
          .transition()
            .target("A");
    
    builder
      .region()
        .state("A")
          .entry((c) -> { c.enter("foo"); })
          .entry((c) -> { c.enter("iak"); })
          .entry((c) -> { c.enter("dir"); })
          .entry((c) -> { c.enter("bar"); })
          .exit((c) -> { c.exit("abs"); })
          .exit((c) -> { c.exit("bat"); })
          .activity((c) -> { c.activity("something"); })
          .transition()
            .target(end);
    
    return builder;
  }

  @Override
  protected boolean sequentialContextInjection() {
    return false;
  }
  
  private void addLogger(StateMachineExecutor fsm, Logger log) {
    TraceLoggerListener l = new TraceLoggerListener(log);
    
    fsm.listeners().addEventAcceptedListener(l);
    fsm.listeners().addEventDeferredListener(l);
    fsm.listeners().addEventDeniedListener(l);
    fsm.listeners().addMachineStartedListener(l);
    fsm.listeners().addMachineTerminatedListener(l);
    fsm.listeners().addStateActivityAfterExecution(l);
    fsm.listeners().addStateActivityBeforeExecution(l);
    fsm.listeners().addStateActivityException(l);
    fsm.listeners().addStateEnterAfterExecution(l);
    fsm.listeners().addStateEnterBeforeExecution(l);
    fsm.listeners().addStateEnterException(l);
    fsm.listeners().addStateExitAfterExecution(l);
    fsm.listeners().addStateExitBeforeExecution(l);
    fsm.listeners().addStateExitException(l);
    fsm.listeners().addTransitionEffectAfterExecutionListener(l);
    fsm.listeners().addTransitionEffectBeforeExecutionListener(l);
    fsm.listeners().addTransitionEffectExceptionListener(l);
    fsm.listeners().addTransitionEndedListener(l);
    fsm.listeners().addTransitionGuardAfterExecutionListener(l);
    fsm.listeners().addTransitionGuardBeforeExecutionListener(l);
    fsm.listeners().addTransitionGuardExceptionListener(l);
    fsm.listeners().addTransitionStartedListener(l);
    fsm.listeners().addTransitionStartedListener(null);
    
    
    fsm.listeners().removeEventAcceptedListener(l);
    fsm.listeners().removeEventDeferredListener(l);
    fsm.listeners().removeEventDeniedListener(l);
    fsm.listeners().removeMachineStartedListener(l);
    fsm.listeners().removeMachineTerminatedListener(l);
    fsm.listeners().removeStateActivityAfterExecution(l);
    fsm.listeners().removeStateActivityBeforeExecution(l);
    fsm.listeners().removeStateActivityException(l);
    fsm.listeners().removeStateEnterAfterExecution(l);
    fsm.listeners().removeStateEnterBeforeExecution(l);
    fsm.listeners().removeStateEnterException(l);
    fsm.listeners().removeStateExitAfterExecution(l);
    fsm.listeners().removeStateExitBeforeExecution(l);
    fsm.listeners().removeStateExitException(l);
    fsm.listeners().removeTransitionEffectAfterExecutionListener(l);
    fsm.listeners().removeTransitionEffectBeforeExecutionListener(l);
    fsm.listeners().removeTransitionEffectExceptionListener(l);
    fsm.listeners().removeTransitionEndedListener(l);
    fsm.listeners().removeTransitionGuardAfterExecutionListener(l);
    fsm.listeners().removeTransitionGuardBeforeExecutionListener(l);
    fsm.listeners().removeTransitionGuardExceptionListener(l);
    fsm.listeners().removeTransitionStartedListener(l);
    fsm.listeners().removeTransitionStartedListener(null);
    
    fsm.listeners().add(l);
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final String TRACE_LOG = "[INFO] executor=\"1\" machine=\"Test16\" tag=\"MACHINE_STARTED\" context=\"\"\n" +
"[INFO] executor=\"1\" machine=\"Test16\" tag=\"EVENT_ACCEPTED\" event=\"CompletionEvent\" context=\"\"\n" +
"[DEBUG] executor=\"1\" machine=\"Test16\" tag=\"TRANSITION_STARTED\" event=\"CompletionEvent\" transition=\"#5\" source=\"#4\" target=\"A\" context=\"\"\n" +
"[TRACE] executor=\"1\" machine=\"Test16\" tag=\"STATE_ENTER_BEFORE_EXECUTION\" state=\"A\" context=\"\"\n" +
"[TRACE] executor=\"1\" machine=\"Test16\" tag=\"STATE_ENTER_AFTER_EXECUTION\" state=\"A\" context=\"enter:foo enter:iak enter:dir enter:bar \"\n" +
"[DEBUG] executor=\"1\" machine=\"Test16\" tag=\"TRANSITION_ENDED\" event=\"CompletionEvent\" transition=\"#5\" source=\"#4\" target=\"A\" context=\"enter:foo enter:iak enter:dir enter:bar \"\n" +
"[TRACE] executor=\"1\" machine=\"Test16\" tag=\"STATE_ACTIVITY_BEFORE_EXECUTION\" state=\"A\" context=\"enter:foo enter:iak enter:dir enter:bar \"\n" +
"[TRACE] executor=\"1\" machine=\"Test16\" tag=\"STATE_ACTIVITY_AFTER_EXECUTION\" state=\"A\" context=\"enter:foo enter:iak enter:dir enter:bar \"\n" +
"[INFO] executor=\"1\" machine=\"Test16\" tag=\"EVENT_ACCEPTED\" event=\"CompletionEvent\" context=\"enter:foo enter:iak enter:dir enter:bar \"\n" +
"[DEBUG] executor=\"1\" machine=\"Test16\" tag=\"TRANSITION_STARTED\" event=\"CompletionEvent\" transition=\"#7\" source=\"A\" target=\"end\" context=\"enter:foo enter:iak enter:dir enter:bar \"\n" +
"[TRACE] executor=\"1\" machine=\"Test16\" tag=\"STATE_EXIT_BEFORE_EXECUTION\" state=\"A\" context=\"enter:foo enter:iak enter:dir enter:bar \"\n" +
"[TRACE] executor=\"1\" machine=\"Test16\" tag=\"STATE_EXIT_AFTER_EXECUTION\" state=\"A\" context=\"enter:foo enter:iak enter:dir enter:bar exit:abs exit:bat \"\n" +
"[DEBUG] executor=\"1\" machine=\"Test16\" tag=\"TRANSITION_ENDED\" event=\"CompletionEvent\" transition=\"#7\" source=\"A\" target=\"end\" context=\"enter:foo enter:iak enter:dir enter:bar exit:abs exit:bat \"\n" +
"[INFO] executor=\"1\" machine=\"Test16\" tag=\"MACHINE_TERMINATED\" context=\"enter:foo enter:iak enter:dir enter:bar exit:abs exit:bat \"";
  
  private static final String STDOUT = "StateMachine: \"" + Test16.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    Transition: #5 --- #4 -> \"A\"\n" +
"    Transition: #7 --- \"A\" -> \"end\"";
}
