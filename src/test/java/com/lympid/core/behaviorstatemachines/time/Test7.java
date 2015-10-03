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
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.impl.ExecutorListener;
import com.lympid.core.behaviorstatemachines.listener.StringBufferLoggerListener;
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

  private static final long SHORT_DELAY = 50;
  private static final long LONG_DELAY = 200;

  @Test
  public void run() throws InterruptedException {
    final StringBufferLoggerListener log = new StringBufferLoggerListener();
    ExecutorListener listener = new ExecutorListener();
    listener.add(log);
    
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.setListeners(listener);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));

    ctx.latch.await(10 * LONG_DELAY, TimeUnit.MILLISECONDS);
    
    assertEquals(1, ctx.local);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    
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
  protected String executorName() {
    return "Time." + getClass().getSimpleName();
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
      return "";
    }
  }
  
  private static final String MAIN_LOG = "tag=\"MACHINE_STARTED\" executor=\"Time.Test7\" context=\"\"\n" +
"tag=\"EVENT_ACCEPTED\" executor=\"Time.Test7\" event=\"CompletionEvent\" context=\"\"\n" +
"tag=\"TRANSITION_STARTED\" executor=\"Time.Test7\" event=\"CompletionEvent\" transition=\"t0\" source=\"#3\" target=\"A\" context=\"\"\n" +
"tag=\"TRANSITION_EFFECT_BEFORE_EXECUTION\" executor=\"Time.Test7\" event=\"CompletionEvent\" transition=\"t0\" source=\"#3\" target=\"A\" context=\"\"\n" +
"tag=\"TRANSITION_EFFECT_AFTER_EXECUTION\" executor=\"Time.Test7\" event=\"CompletionEvent\" transition=\"t0\" source=\"#3\" target=\"A\" context=\"\"\n" +
"tag=\"STATE_ENTER\" executor=\"Time.Test7\" state=\"A\" context=\"\"\n" +
"tag=\"STATE_ENTER_BEFORE_EXECUTION\" executor=\"Time.Test7\" state=\"A\" context=\"\"\n" +
"tag=\"STATE_ENTER_AFTER_EXECUTION\" executor=\"Time.Test7\" state=\"A\" context=\"\"\n" +
"tag=\"TRANSITION_ENDED\" executor=\"Time.Test7\" event=\"CompletionEvent\" transition=\"t0\" source=\"#3\" target=\"A\" context=\"\"\n" +
"tag=\"EVENT_ACCEPTED\" executor=\"Time.Test7\" event=\"50 ms\" context=\"\"\n" +
"tag=\"TRANSITION_STARTED\" executor=\"Time.Test7\" event=\"50 ms\" transition=\"t1\" source=\"A\" target=\"A\" context=\"\"\n" +
"tag=\"TRANSITION_EFFECT_BEFORE_EXECUTION\" executor=\"Time.Test7\" event=\"50 ms\" transition=\"t1\" source=\"A\" target=\"A\" context=\"\"\n" +
"tag=\"TRANSITION_EFFECT_AFTER_EXECUTION\" executor=\"Time.Test7\" event=\"50 ms\" transition=\"t1\" source=\"A\" target=\"A\" context=\"\"\n" +
"tag=\"TRANSITION_ENDED\" executor=\"Time.Test7\" event=\"50 ms\" transition=\"t1\" source=\"A\" target=\"A\" context=\"\"\n" +
"tag=\"EVENT_ACCEPTED\" executor=\"Time.Test7\" event=\"200 ms\" context=\"\"\n" +
"tag=\"TRANSITION_STARTED\" executor=\"Time.Test7\" event=\"200 ms\" transition=\"t2\" source=\"A\" target=\"end\" context=\"\"\n" +
"tag=\"STATE_EXIT\" executor=\"Time.Test7\" state=\"A\" context=\"\"\n" +
"tag=\"STATE_EXIT_BEFORE_EXECUTION\" executor=\"Time.Test7\" state=\"A\" context=\"\"\n" +
"tag=\"STATE_EXIT_AFTER_EXECUTION\" executor=\"Time.Test7\" state=\"A\" context=\"\"\n" +
"tag=\"TRANSITION_EFFECT_BEFORE_EXECUTION\" executor=\"Time.Test7\" event=\"200 ms\" transition=\"t2\" source=\"A\" target=\"end\" context=\"\"\n" +
"tag=\"TRANSITION_EFFECT_AFTER_EXECUTION\" executor=\"Time.Test7\" event=\"200 ms\" transition=\"t2\" source=\"A\" target=\"end\" context=\"\"\n" +
"tag=\"STATE_ENTER\" executor=\"Time.Test7\" state=\"end\" context=\"\"\n" +
"tag=\"TRANSITION_ENDED\" executor=\"Time.Test7\" event=\"200 ms\" transition=\"t2\" source=\"A\" target=\"end\" context=\"\"\n" +
"tag=\"MACHINE_TERMINATED\" executor=\"Time.Test7\" context=\"\"\n";
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
