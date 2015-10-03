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

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.StateMachineSnapshot;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests a time transition is canceled and recreated when an external transition
 * targeting its source state is fired.
 * 
 * @author Fabien Renaud 
 */
public class Test5 extends AbstractStateMachineTest {

  private static final long DELAY = 50;
  
  @Test
  public void run_2() throws InterruptedException {
    run(2);
  }
  
  @Test
  public void run_5() throws InterruptedException {
    run(5);
  }
  
  @Test
  public void run_10() throws InterruptedException {
    run(10);
  }
  
  @Test
  public void run_20() throws InterruptedException {
    run(20);
  }
  
  private void run(final int count) throws InterruptedException {    
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A");
    
    Event incEvent = new StringEvent("inc");
    
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    StateMachineSnapshot snapshot = fsm.snapshot();
    if (ctx.latch.getCount() > 0) {
        assertSnapshotEquals(snapshot, new ActiveStateTree(this).branch("A"));
    }

    int actualCount = 0;
    for (int i = 1; i <= count && ctx.latch.getCount() != 0; i++) {
      fsm.take(incEvent);
      
      if (i == ctx.c) {
        expected.exit("A").effect("t2").enter("A");
        actualCount++;

        Thread.sleep(1);
      }
    }
    
    expected
      .exit("A").effect("t1").enter("B")
      .exit("B").effect("t3");
    
    ctx.latch.await();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("B"));

    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertSequentialContextEquals(expected, fsm);
    assertEquals(actualCount, ctx.c);
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
          .transition("t1")
            .after(DELAY, TimeUnit.MILLISECONDS)
            .effect((e, c) -> c.latch.countDown())
            .target("B")
          .transition("t2")
            .on("inc")
            .effect((e, c) -> { c.c++; })
            .target("A");

    builder
      .region()
        .state("B")
          .transition("t3")
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

  private static final class Context extends SequentialContext {
    CountDownLatch latch = new CountDownLatch(1);
    volatile int c;
  }

  private static final String STDOUT = "StateMachine: \"" + Test5.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    State: \"B\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"A\"\n" +
"    Transition: \"t1\" --- \"A\" -> \"B\"\n" +
"    Transition: \"t2\" --- \"A\" -> \"A\"\n" +
"    Transition: \"t3\" --- \"B\" -> \"end\"";
}
