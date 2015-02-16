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
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import java.util.concurrent.CountDownLatch;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

/**
 * Tests an inner state activity is canceled when an outgoing transition of some
 * parent state is fired.
 * 
 * @author Fabien Renaud 
 */
public class Test8 extends AbstractStateMachineTest {
  
  private static final long WAIT_TIME = 1000;
  private static final int EXPECTED_C = 10;
  
  @Test
  public void run() throws InterruptedException {
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "B"));

    ctx.latchStarted.await();
    
    fsm.take(new StringEvent("end"));

    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertEquals(0, ctx.c);
    
    ctx.latchInterrupted.await();
    assertNotEquals(EXPECTED_C, ctx.c);
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());

    builder
      .region()
        .initial()
          .transition("t0")
            .target("B");

    builder
      .region()
        .state(composite("A"))
          .transition("t1")
            .on("end")
            .target("end");

    builder
      .region()
        .finalState("end");

    return builder;
  }
  
  private CompositeStateBuilder composite(final String name) {
    CompositeStateBuilder<Context> builder = new CompositeStateBuilder(name);
    
    builder
      .region()
        .state("B")
          .activity(Activity.class);
            
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }

  private static final class Context {
    int c;
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

  private static final String STDOUT = "StateMachine: \"" + Test8.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"A\"\n" +
"      Region: #7\n" +
"        State: \"B\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"B\"\n" +
"    Transition: \"t1\" --- \"A\" -> \"end\"";
}
