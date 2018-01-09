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

import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.activity.Test1.Context;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests the state activity is being executed and that its completion triggers
 * the completion transition.
 * 
 * @author Fabien Renaud 
 */
public class Test1 extends AbstractStateMachineTest<Context> {
  
  private static final int EXPECTED_C = 10;
  
  @Test
  public void run() throws InterruptedException {
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    ctx.latch.await();
    Thread.sleep(2);
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertEquals(EXPECTED_C, ctx.c);
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

  public static final class Context {
    CountDownLatch latch = new CountDownLatch(1);
    volatile int c;
  }
  
  public static final class Activity implements StateBehavior<Context> {

    @Override
    public void accept(Context ctx) {
      ctx.c = EXPECTED_C;
    }
    
  }

  private static final String STDOUT = "StateMachine: \"" + Test1.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"A\"\n" +
"    Transition: \"t1\" --- \"A\" -> \"end\"";
}
