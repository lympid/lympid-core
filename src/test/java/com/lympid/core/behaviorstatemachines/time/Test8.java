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
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Tests a time transition can reach a pseudo state and result in the state
 * machine termination.
 * 
 * @author Fabien Renaud 
 */
public class Test8 extends AbstractStateMachineTest {

  private static final long DELAY = 50;

  @Test
  public void run() throws InterruptedException {
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));

    ctx.latch.await(10 * DELAY, TimeUnit.MILLISECONDS);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
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
            .target("j");
    
    builder
      .region()
        .junction("j")
          .transition("t2")
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
  }

  private static final String STDOUT = "StateMachine: \"" + Test8.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    PseudoState: \"j\" kind: JUNCTION\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"A\"\n" +
"    Transition: \"t1\" --- \"A\" -> \"j\"\n" +
"    Transition: \"t2\" --- \"j\" -> \"end\"";
}
