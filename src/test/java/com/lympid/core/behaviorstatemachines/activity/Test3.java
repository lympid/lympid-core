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
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import java.util.concurrent.CountDownLatch;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests the state activity is being executed and that its completion triggers
 * the completion transition.
 * 
 * @author Fabien Renaud 
 */
public class Test3 extends AbstractStateMachineTest {
  
  private static final int EXPECTED_A = 654;
  private static final int EXPECTED_B = -741;
  private static final int EXPECTED_C = 3;
  
  @Test
  public void run() throws InterruptedException {
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    ctx.latch.await();
    Thread.sleep(2);
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end").get());
    assertEquals(EXPECTED_A, ctx.a);
    assertEquals(EXPECTED_B, ctx.b);
    assertEquals(EXPECTED_C, ctx.c);
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());

    builder
      .region()
        .initial()
          .transition("t0")
            .target("C");

    builder
      .region()
        .state(compositeA("A"))
          .activity(ActivityA.class);

    builder
      .region()
        .finalState("end");

    return builder;
  }
  
  private CompositeStateBuilder<Context> compositeA(String name) {
    CompositeStateBuilder<Context> builder = new CompositeStateBuilder(name);
    
    builder
      .region()
        .state(compositeB("B"))
          .activity((c) -> {
            c.b = EXPECTED_B; 
            c.latch.countDown();
          });
    
    return builder;
  }
  
  private CompositeStateBuilder<Context> compositeB(String name) {
    CompositeStateBuilder<Context> builder = new CompositeStateBuilder(name);
    
    builder
      .region()
        .state("C")
          .activity(ActivityC.class)
          .transition("t1")
            .target("end");
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }

  private static final class Context {
    volatile int a, b, c;
    CountDownLatch latch = new CountDownLatch(3);
  }
  
  public static final class ActivityA implements StateBehavior<Context> {

    @Override
    public void accept(Context ctx) {
      ctx.a = EXPECTED_A;
      ctx.latch.countDown();
    }
    
  }
  
  public static final class ActivityC implements StateBehavior<Context> {

    @Override
    public void accept(Context ctx) {
      ctx.c = EXPECTED_C;
      ctx.latch.countDown();
    }
    
  }

  private static final String STDOUT = "StateMachine: \"" + Test3.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"A\"\n" +
"      Region: #6\n" +
"        State: \"B\"\n" +
"          Region: #8\n" +
"            State: \"C\"\n" +
"    Transition: \"t0\" --- #3 -> \"C\"\n" +
"    Transition: \"t1\" --- \"C\" -> \"end\"";
}
