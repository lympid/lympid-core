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
package com.lympid.core.behaviorstatemachines.time.relative;

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration;
import com.lympid.core.behaviorstatemachines.impl.SyncStateMachineExecutor;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests that of two concurrent time events, only one gets activated.
 * 
 * @author Fabien Renaud 
 */
public class Test10 extends AbstractStateMachineTest {

  private static final long DELAY = 50;

  @Test
  public void run() throws InterruptedException {
    ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(2);
    threadPool.prestartAllCoreThreads();
    
    Context ctx = new Context();
    
    StateMachineExecutor fsm = new SyncStateMachineExecutor.Builder()
      .setName(executorName())
      .setStateMachine(topLevelStateMachine())
      .setConfiguration(new ExecutorConfiguration().executor(threadPool))
      .setContext(ctx)
      .build();
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));

    ctx.latch.await(10 * DELAY, TimeUnit.MILLISECONDS);
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("B"));

    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertEquals(1, ctx.counter.get());
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
          .transition("t11")
            .after(DELAY, TimeUnit.MILLISECONDS)
            .effect((e,c) -> c.counter.incrementAndGet())
            .target("B")
          .transition("t12")
            .after(DELAY, TimeUnit.MILLISECONDS)
            .effect((e,c) -> c.counter.incrementAndGet())
            .target("B");

    builder
      .region()
        .state("B")
          .entry((c) -> c.latch.countDown())
          .transition("t2")
            .on("end")
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
    AtomicInteger counter = new AtomicInteger();
    
    @Override
    public String toString() {
      return "";
    }
  }

  private static final String STDOUT = "StateMachine: \"" + Test10.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    State: \"B\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"A\"\n" +
"    Transition: \"t11\" --- \"A\" -> \"B\"\n" +
"    Transition: \"t12\" --- \"A\" -> \"B\"\n" +
"    Transition: \"t2\" --- \"B\" -> \"end\"";
}
