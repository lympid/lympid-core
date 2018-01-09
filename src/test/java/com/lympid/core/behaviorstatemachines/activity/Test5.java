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
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import java.util.concurrent.CountDownLatch;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests activities in sub machine states.
 * 
 * @author Fabien Renaud 
 */
public class Test5 extends AbstractStateMachineTest {
  
  private static final int ACTIVITY_VALUE_SUB1 = -7;
  private static final int ACTIVITY_VALUE_SUB2 = 11;
  
  @Test
  public void run() throws InterruptedException {    
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);    
    fsm.go();
    
    
    ctx.latch11.await();
    expected
      .effect("t0").enter("sub1").effect("t0").enter("A");
    assertEquals(ACTIVITY_VALUE_SUB1, ctx.c);
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("sub1", "A"));
    ctx.latch12.countDown();
    
    
    fsm.take(new StringEvent("go"));
    ctx.latch21.await();
    expected
      .exit("A").effect("t1")
      .exit("sub1").effect("t1").enter("sub2").effect("t0").enter("A");
    assertEquals(ACTIVITY_VALUE_SUB2, ctx.c);
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("sub2", "A"));
    ctx.latch22.countDown();
    Thread.sleep(2);
    
    fsm.take(new StringEvent("go"));
    expected
      .exit("A").effect("t1")
      .exit("sub2");
    assertSequentialContextEquals(expected, fsm);    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    builder
      .region()
        .initial("init")
          .transition("t0")
            .target("sub1");
    
    StateMachineBuilder<Context> subMachine = subStateMachine("sub");
    
    builder
      .region()
        .state(subMachine, "sub1")
          .activity((ctx) -> { 
            ctx.c = ACTIVITY_VALUE_SUB1;
            ctx.latch11.countDown();
            try {
              ctx.latch12.await();
            } catch (InterruptedException ex) {
              throw new RuntimeException(ex);
            }
          })
          .transition("t1")
            .target("sub2");
    
    builder
      .region()
        .state(subMachine, "sub2")
          .activity(Sub2Activity.class)
          .transition()
            .target("end");
    
    builder
      .region()
        .finalState("end");
    
    return builder;
  }
  
  private StateMachineBuilder<Context> subStateMachine(final String name) {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name);
    
    builder
      .region()
        .initial("init")
          .transition("t0")
            .target("A");
    
    builder
      .region()
        .state("A")
          .transition("t1")
            .on("go")
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
    CountDownLatch latch11 = new CountDownLatch(1);
    CountDownLatch latch12 = new CountDownLatch(1);
    CountDownLatch latch21 = new CountDownLatch(1);
    CountDownLatch latch22 = new CountDownLatch(1);
    volatile int c;
  }
  
  public static final class Sub2Activity implements StateBehavior<Context> {

    @Override
    public void accept(Context ctx) {
      ctx.c = ACTIVITY_VALUE_SUB2;
      ctx.latch21.countDown();
      try {
        ctx.latch22.await();
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
    }
    
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test5.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: \"init\" kind: INITIAL\n" +
"    State: \"sub1\"\n" +
"      StateMachine: \"sub\"\n" +
"        Region: #11\n" +
"          PseudoState: \"init\" kind: INITIAL\n" +
"          State: \"A\"\n" +
"          FinalState: \"end\"\n" +
"          Transition: \"t0\" --- \"init\" -> \"A\"\n" +
"          Transition: \"t1\" --- \"A\" -> \"end\"\n" +
"    State: \"sub2\"\n" +
"      StateMachine: \"sub\"\n" +
"        Region: #19\n" +
"          State: \"A\"\n" +
"          FinalState: \"end\"\n" +
"          PseudoState: \"init\" kind: INITIAL\n" +
"          Transition: \"t0\" --- \"init\" -> \"A\"\n" +
"          Transition: \"t1\" --- \"A\" -> \"end\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- \"init\" -> \"sub1\"\n" +
"    Transition: \"t1\" --- \"sub1\" -> \"sub2\"\n" +
"    Transition: #8 --- \"sub2\" -> \"end\"";
}
