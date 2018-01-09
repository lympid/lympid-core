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
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration;
import com.lympid.core.behaviorstatemachines.simple.Test13.Context;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import static org.junit.Assert.assertEquals;

/**
 * Entering a simple state executes its exit action.
 * @author Fabien Renaud 
 */
public class Test13 extends AbstractStateMachineTest<Context> {
  
  @Test
  public void run() {
    ExecutorConfiguration config = new ExecutorConfiguration()
      .autoStart(true);
    
    Context ctx = new Context();
    assertEquals(0, ctx.hash);
    
    StateMachineExecutor<Context> fsm = fsm(ctx, config);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertEquals(ctx.hashCode(), ctx.hash);
  }

  @Override
  public StateMachineBuilder<Context> topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    VertexBuilderReference<Context> end = builder
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
          .exit(c -> c.hash = c.hashCode())
          .transition()
            .target(end);
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  public static final class Context {
    int hash;
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test13.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    Transition: #5 --- #4 -> \"A\"\n" +
"    Transition: #7 --- \"A\" -> \"end\"";
}
