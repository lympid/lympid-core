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
package com.lympid.core.behaviorstatemachines.activity;

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.activity.Test2.Context;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests the state activity is canceled when an outgoing transition is fired.
 * 
 * @author Fabien Renaud 
 */
public class Test2 extends AbstractStateMachineTest<Context> {
  
  private static final long WAIT_TIME = 1000;
  private static final int EXPECTED_C = 10;
  
  @Test
  public void run() throws InterruptedException {
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));

    ctx.latchStarted.await();
    
    fsm.take(new StringEvent("end"));

    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertEquals(0, ctx.c);
    
    ctx.latchInterrupted.await();
    assertNotEquals(EXPECTED_C, ctx.c);
  }

  @Override
  public StateMachineBuilder<Context> topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());

    builder
      .region()
        .initial()
          .transition("t0")
            .target("A");

    builder
      .region()
        .state("A")
          .activity(Activity.class)
            .transition("t1")
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

  public static final class Context {
    volatile int c;
    CountDownLatch latchStarted = new CountDownLatch(1);
    CountDownLatch latchInterrupted = new CountDownLatch(1);
  }
  
  public static final class Activity implements StateBehavior<Context> {

    @Override
    public void accept(Context ctx) {
      try {
        ctx.latchStarted.countDown();
        Thread.sleep(WAIT_TIME);
        ctx.c = EXPECTED_C;
      } catch (InterruptedException ex) {
        ctx.latchInterrupted.countDown();
      }
    }
    
  }

  private static final String STDOUT = "StateMachine: \"" + Test2.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"A\"\n" +
"    Transition: \"t1\" --- \"A\" -> \"end\"";
}
