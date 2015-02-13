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
package com.lympid.core.behaviorstatemachines.time;

import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.impl.ExecutorListener;
import com.lympid.core.behaviorstatemachines.listener.StringLoggerListener;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests an internal transition can be fired by a time event.
 * 
 * @author Fabien Renaud 
 */
public class Test7 extends AbstractStateMachineTest {

  private static final long SHORT_DELAY = 10;
  private static final long LONG_DELAY = 50;

  @Test
  public void run() throws InterruptedException {
    final StringLoggerListener log = new StringLoggerListener();
    ExecutorListener listener = new ExecutorListener();
    listener.add(log);
    
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.setListeners(listener);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("A"));

    ctx.latch.await(10 * LONG_DELAY, TimeUnit.MILLISECONDS);
    
    assertEquals(1, ctx.local);
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    
    assertEquals(MAIN_LOG, log.mainBuffer());
    assertEquals(ACTIVITY_LOG, log.activityBuffer());
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());

    builder
      .region()
        .initial()
          .transition("t0")
            .target("A");

    builder
      .region()
        .state("A")
          .selfTransition("t1")
            .after(SHORT_DELAY, TimeUnit.MILLISECONDS)
            .effect((e, c) -> c.local++)
            .target()
          .transition("t2")
            .after(LONG_DELAY, TimeUnit.MILLISECONDS)
            .effect((e, c) -> c.latch.countDown())
            .target("end");

    builder
      .region()
        .finalState("end");

    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }

  private static final class Context {
    CountDownLatch latch = new CountDownLatch(1);
    volatile int local;
    
    @Override
    public String toString() {
      return null;
    }
  }
  
  private static final String MAIN_LOG = "tag=\"MACHINE_STARTED\" context=\"null\"\n" +
"tag=\"EVENT_ACCEPTED\" event=\"CompletionEvent\" context=\"null\"\n" +
"tag=\"TRANSITION_STARTED\" event=\"CompletionEvent\" transition=\"t0\" source=\"#3\" target=\"A\" context=\"null\"\n" +
"tag=\"TRANSITION_EFFECT_BEFORE_EXECUTION\" event=\"CompletionEvent\" transition=\"t0\" source=\"#3\" target=\"A\" context=\"null\"\n" +
"tag=\"TRANSITION_EFFECT_AFTER_EXECUTION\" event=\"CompletionEvent\" transition=\"t0\" source=\"#3\" target=\"A\" context=\"null\"\n" +
"tag=\"STATE_ENTER_BEFORE_EXECUTION\" state=\"A\" context=\"null\"\n" +
"tag=\"STATE_ENTER_AFTER_EXECUTION\" state=\"A\" context=\"null\"\n" +
"tag=\"TRANSITION_ENDED\" event=\"CompletionEvent\" transition=\"t0\" source=\"#3\" target=\"A\" context=\"null\"\n" +
"tag=\"EVENT_ACCEPTED\" event=\"10 ms\" context=\"null\"\n" +
"tag=\"TRANSITION_STARTED\" event=\"10 ms\" transition=\"t1\" source=\"A\" target=\"A\" context=\"null\"\n" +
"tag=\"TRANSITION_EFFECT_BEFORE_EXECUTION\" event=\"10 ms\" transition=\"t1\" source=\"A\" target=\"A\" context=\"null\"\n" +
"tag=\"TRANSITION_EFFECT_AFTER_EXECUTION\" event=\"10 ms\" transition=\"t1\" source=\"A\" target=\"A\" context=\"null\"\n" +
"tag=\"TRANSITION_ENDED\" event=\"10 ms\" transition=\"t1\" source=\"A\" target=\"A\" context=\"null\"\n" +
"tag=\"EVENT_ACCEPTED\" event=\"50 ms\" context=\"null\"\n" +
"tag=\"TRANSITION_STARTED\" event=\"50 ms\" transition=\"t2\" source=\"A\" target=\"end\" context=\"null\"\n" +
"tag=\"STATE_EXIT_BEFORE_EXECUTION\" state=\"A\" context=\"null\"\n" +
"tag=\"STATE_EXIT_AFTER_EXECUTION\" state=\"A\" context=\"null\"\n" +
"tag=\"TRANSITION_EFFECT_BEFORE_EXECUTION\" event=\"50 ms\" transition=\"t2\" source=\"A\" target=\"end\" context=\"null\"\n" +
"tag=\"TRANSITION_EFFECT_AFTER_EXECUTION\" event=\"50 ms\" transition=\"t2\" source=\"A\" target=\"end\" context=\"null\"\n" +
"tag=\"TRANSITION_ENDED\" event=\"50 ms\" transition=\"t2\" source=\"A\" target=\"end\" context=\"null\"\n" +
"tag=\"MACHINE_TERMINATED\" context=\"null\"\n";
  private static final String ACTIVITY_LOG = "";
  
  private static final String STDOUT = "StateMachine: \"" + Test7.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"A\"\n" +
"    Transition: \"t1\" -I- \"A\" -> \"A\"\n" +
"    Transition: \"t2\" --- \"A\" -> \"end\"";
}
