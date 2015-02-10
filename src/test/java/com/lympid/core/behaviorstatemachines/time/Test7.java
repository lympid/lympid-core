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
import com.lympid.core.behaviorstatemachines.listener.InfoLoggerListener;
import static com.lympid.core.behaviorstatemachines.listener.StringBufferLoggerTester.assertLogEquals;
import com.lympid.core.behaviorstatemachines.listener.StringBuilderLogger;
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
    final StringBuilderLogger bufferLogger = new StringBuilderLogger(StringBuilderLogger.LogLevel.INFO);
    ExecutorListener listener = new ExecutorListener();
    listener.add(new InfoLoggerListener(bufferLogger));
    
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.setListeners(listener);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("A"));

    ctx.latch.await(10 * LONG_DELAY, TimeUnit.MILLISECONDS);
    
    assertEquals(1, ctx.local);
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    
    assertLogEquals(INFO_LOG, bufferLogger);
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
  
  private static final String INFO_LOG = "[INFO] machine=\"" + Test7.class.getSimpleName() + "\" tag=\"MACHINE_STARTED\" context=\"null\"\n" +
"[INFO] machine=\"" + Test7.class.getSimpleName() + "\" tag=\"EVENT_ACCEPTED\" event=\"CompletionEvent\" context=\"null\"\n" +
"[INFO] machine=\"" + Test7.class.getSimpleName() + "\" tag=\"EVENT_ACCEPTED\" event=\"" + SHORT_DELAY + " ms\" context=\"null\"\n" +
"[INFO] machine=\"" + Test7.class.getSimpleName() + "\" tag=\"EVENT_ACCEPTED\" event=\"" + LONG_DELAY + " ms\" context=\"null\"\n" +
"[INFO] machine=\"" + Test7.class.getSimpleName() + "\" tag=\"MACHINE_TERMINATED\" context=\"null\"";

  private static final String STDOUT = "StateMachine: \"" + Test7.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"A\"\n" +
"    Transition: \"t1\" -I- \"A\" -> \"A\"\n" +
"    Transition: \"t2\" --- \"A\" -> \"end\"";
}
