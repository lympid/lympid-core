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
import static org.junit.Assert.assertFalse;
import org.junit.Test;

/**
 * Tests a time transition gets canceled.
 * 
 * @author Fabien Renaud 
 */
public class Test2 extends AbstractStateMachineTest {

  private static final long DELAY = 50;

  @Test
  public void run() throws InterruptedException {
        SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .exit("A").effect("t2").enter("B")
      .exit("B").effect("t3");
    
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));

    Thread.sleep(2);
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("B"));
    
    ctx.latch.await(10 * DELAY, TimeUnit.MILLISECONDS);

    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertSequentialContextEquals(expected, fsm);
    assertFalse(ctx.fired);
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
            .after(DELAY, TimeUnit.MILLISECONDS)
            .effect((e, c) -> { c.fired = true; c.latch.countDown(); })
            .target("B")
          .transition("t2")
            .on("go")
            .target("B");

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
    volatile boolean fired;
    long enterA;
    long exitA;
  }

  private static final String STDOUT = "StateMachine: \"" + Test2.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    State: \"B\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"A\"\n" +
"    Transition: \"t1\" --- \"A\" -> \"B\"\n" +
"    Transition: \"t2\" --- \"A\" -> \"B\"\n" +
"    Transition: \"t3\" --- \"B\" -> \"end\"";
}
