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

package com.lympid.core.behaviorstatemachines.simple;

import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import java.util.concurrent.CountDownLatch;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Entering a simple state executes its activity.
 * 
 * @author Fabien Renaud 
 */
public class Test11 extends AbstractStateMachineTest {
  
  @Test
  public void run() throws InterruptedException {
    Context ctx = new Context();
    assertEquals(0, ctx.hash);
    
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
  
    ctx.latch.await();
    Thread.sleep(2);
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertEquals(ctx.hashCode(), ctx.hash);
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    VertexBuilderReference end = builder
      .region()
        .finalState("end");
    
    builder
      .region()
        .initial()
          .transition()
            .target("A");
    
    builder
      .region()
        .state("A")
          .activity((c) -> c.hash = c.hashCode())
          .transition()
            .effect((e, c) -> c.latch.countDown())
            .target(end);
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final class Context {
    CountDownLatch latch = new CountDownLatch(1);
    int hash;
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test11.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    Transition: #5 --- #4 -> \"A\"\n" +
"    Transition: #7 --- \"A\" -> \"end\"";
}
