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

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Tests time transitions apply guards and that multiple time transitions can
 * be fired from the same state under different conditions.
 * 
 * @author Fabien Renaud 
 */
public class Test3 extends AbstractStateMachineTest {

  private static final long TIME_SHORT = 50;
  private static final long TIME_MEDIUM = 100;
  private static final long TIME_LONG = 150;
  
  @Test
  public void run_short() throws InterruptedException {
    run(1, "t1");
  }

  @Test
  public void run_medium() throws InterruptedException {
    run(2, "t2");
  }

  @Test
  public void run_long() throws InterruptedException {
    run(3, "t3");
  }
  
  private void run(final int c, final String transition) throws InterruptedException {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .exit("A").effect(transition).enter("B")
      .exit("B").effect("t4");

    Context ctx = new Context(c);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));

    ctx.latch.await();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("B"));

    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertSequentialContextEquals(expected, fsm);
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
          .entry((c) -> { c.enterA = System.currentTimeMillis(); })
          .exit((c) -> { c.exitA = System.currentTimeMillis(); })
          .transition("t1")
            .after(TIME_SHORT, TimeUnit.MILLISECONDS)
            .guard((e, c) -> { return c.c == 1; })
            .target("B")
          .transition("t2")
            .after(TIME_MEDIUM, TimeUnit.MILLISECONDS)
            .guard((e, c) -> { return c.c == 2; })
            .target("B")
          .transition("t3")
            .after(TIME_LONG, TimeUnit.MILLISECONDS)
            .guard((e, c) -> { return c.c == 3; })
            .target("B");

    builder
      .region()
        .state("B")
          .entry((c) -> c.latch.countDown())
          .transition("t4")
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
    int c;
    long enterA;
    long exitA;
   
    Context(final int c) {
      this.c = c;
    }

    Context() {

    }
  }

  private static final String STDOUT = "StateMachine: \"" + Test3.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    State: \"B\"\n" +
"    Transition: \"t0\" --- #3 -> \"A\"\n" +
"    Transition: \"t1\" --- \"A\" -> \"B\"\n" +
"    Transition: \"t2\" --- \"A\" -> \"B\"\n" +
"    Transition: \"t3\" --- \"A\" -> \"B\"\n" +
"    Transition: \"t4\" --- \"B\" -> \"end\"";
}
