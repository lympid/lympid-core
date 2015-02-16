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
import com.lympid.core.behaviorstatemachines.listener.StringLoggerListener;
import java.util.concurrent.CountDownLatch;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * State entries, activity and state exists are performed in the right order.
 * @author Fabien Renaud 
 */
public class Test16 extends AbstractStateMachineTest {
  
  @Test
  public void run() throws InterruptedException {
    final StringLoggerListener log = new StringLoggerListener();
    
    SequentialContext expected = new SequentialContext();
    expected
      .enter("foo")
      .enter("iak")
      .enter("dir")
      .enter("bar")
      .activity("something")
      .exit("abs")
      .exit("bat");
    
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    addLogger(fsm, log);
    fsm.go();
    
    ctx.latch.await();
    Thread.sleep(2);
        
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("end").get());
    assertSequentialContextEquals(expected, ctx);
    
    assertEquals(MAIN_LOG, log.mainBuffer());
    assertEquals(ACTIVITY_LOG, log.activityBuffer());
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
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
          .activity((c) -> { 
            c.activity("something");
            c.latch.countDown();
          })
          .transition()
            .target(end);
    
    return builder;
  }

  @Override
  protected boolean sequentialContextInjection() {
    return false;
  }
  
  private void addLogger(StateMachineExecutor fsm, StringLoggerListener log) {    
    fsm.listeners().addEventAcceptedListener(log);
    fsm.listeners().addEventDeferredListener(log);
    fsm.listeners().addEventDeniedListener(log);
    fsm.listeners().addMachineStartedListener(log);
    fsm.listeners().addMachineTerminatedListener(log);
    fsm.listeners().addStateActivityAfterExecution(log);
    fsm.listeners().addStateActivityBeforeExecution(log);
    fsm.listeners().addStateActivityException(log);
    fsm.listeners().addStateEnterAfterExecution(log);
    fsm.listeners().addStateEnterBeforeExecution(log);
    fsm.listeners().addStateEnterException(log);
    fsm.listeners().addStateExitAfterExecution(log);
    fsm.listeners().addStateExitBeforeExecution(log);
    fsm.listeners().addStateExitException(log);
    fsm.listeners().addTransitionEffectAfterExecutionListener(log);
    fsm.listeners().addTransitionEffectBeforeExecutionListener(log);
    fsm.listeners().addTransitionEffectExceptionListener(log);
    fsm.listeners().addTransitionEndedListener(log);
    fsm.listeners().addTransitionGuardAfterExecutionListener(log);
    fsm.listeners().addTransitionGuardBeforeExecutionListener(log);
    fsm.listeners().addTransitionGuardExceptionListener(log);
    fsm.listeners().addTransitionStartedListener(log);
    fsm.listeners().addTransitionStartedListener(null);
    
    
    fsm.listeners().removeEventAcceptedListener(log);
    fsm.listeners().removeEventDeferredListener(log);
    fsm.listeners().removeEventDeniedListener(log);
    fsm.listeners().removeMachineStartedListener(log);
    fsm.listeners().removeMachineTerminatedListener(log);
    fsm.listeners().removeStateActivityAfterExecution(log);
    fsm.listeners().removeStateActivityBeforeExecution(log);
    fsm.listeners().removeStateActivityException(log);
    fsm.listeners().removeStateEnterAfterExecution(log);
    fsm.listeners().removeStateEnterBeforeExecution(log);
    fsm.listeners().removeStateEnterException(log);
    fsm.listeners().removeStateExitAfterExecution(log);
    fsm.listeners().removeStateExitBeforeExecution(log);
    fsm.listeners().removeStateExitException(log);
    fsm.listeners().removeTransitionEffectAfterExecutionListener(log);
    fsm.listeners().removeTransitionEffectBeforeExecutionListener(log);
    fsm.listeners().removeTransitionEffectExceptionListener(log);
    fsm.listeners().removeTransitionEndedListener(log);
    fsm.listeners().removeTransitionGuardAfterExecutionListener(log);
    fsm.listeners().removeTransitionGuardBeforeExecutionListener(log);
    fsm.listeners().removeTransitionGuardExceptionListener(log);
    fsm.listeners().removeTransitionStartedListener(log);
    fsm.listeners().removeTransitionStartedListener(null);
    
    fsm.listeners().add(log);
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final class Context extends SequentialContext {
    CountDownLatch latch = new CountDownLatch(1);
  }
  
  private static final String MAIN_LOG = "tag=\"MACHINE_STARTED\" context=\"\"\n" +
"tag=\"EVENT_ACCEPTED\" event=\"CompletionEvent\" context=\"\"\n" +
"tag=\"TRANSITION_STARTED\" event=\"CompletionEvent\" transition=\"#5\" source=\"#4\" target=\"A\" context=\"\"\n" +
"tag=\"STATE_ENTER_BEFORE_EXECUTION\" state=\"A\" context=\"\"\n" +
"tag=\"STATE_ENTER_AFTER_EXECUTION\" state=\"A\" context=\"enter:foo enter:iak enter:dir enter:bar \"\n" +
"tag=\"TRANSITION_ENDED\" event=\"CompletionEvent\" transition=\"#5\" source=\"#4\" target=\"A\" context=\"enter:foo enter:iak enter:dir enter:bar \"\n" +
"tag=\"EVENT_ACCEPTED\" event=\"CompletionEvent\" context=\"enter:foo enter:iak enter:dir enter:bar \"\n" +
"tag=\"TRANSITION_STARTED\" event=\"CompletionEvent\" transition=\"#7\" source=\"A\" target=\"end\" context=\"enter:foo enter:iak enter:dir enter:bar \"\n" +
"tag=\"STATE_EXIT_BEFORE_EXECUTION\" state=\"A\" context=\"enter:foo enter:iak enter:dir enter:bar \"\n" +
"tag=\"STATE_EXIT_AFTER_EXECUTION\" state=\"A\" context=\"enter:foo enter:iak enter:dir enter:bar exit:abs exit:bat \"\n" +
"tag=\"TRANSITION_ENDED\" event=\"CompletionEvent\" transition=\"#7\" source=\"A\" target=\"end\" context=\"enter:foo enter:iak enter:dir enter:bar exit:abs exit:bat \"\n" +
"tag=\"MACHINE_TERMINATED\" context=\"enter:foo enter:iak enter:dir enter:bar exit:abs exit:bat \"\n";
  private static final String ACTIVITY_LOG = "tag=\"STATE_ACTIVITY_BEFORE_EXECUTION\" state=\"A\" context=\"enter:foo enter:iak enter:dir enter:bar \"\n" +
"tag=\"STATE_ACTIVITY_AFTER_EXECUTION\" state=\"A\" context=\"enter:foo enter:iak enter:dir enter:bar \"\n";
  
  private static final String STDOUT = "StateMachine: \"" + Test16.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    Transition: #5 --- #4 -> \"A\"\n" +
"    Transition: #7 --- \"A\" -> \"end\"";
}
