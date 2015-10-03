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

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests the initial transition can fire its effect.
 * @author Fabien Renaud 
 */
public class Test2 extends AbstractStateMachineTest {
    
  @Test
  public void run_WithAutoStart() {
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();

    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertEquals(1, ctx.counter);
  }
  
  @Test
  public void run_WithoutAutoStart() {
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.configuration().autoStart(false);
    fsm.go();

    assertSnapshotEquals(fsm, new ActiveStateTree(this));

    fsm.take(new Event() {});
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertEquals(1, ctx.counter);
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    VertexBuilderReference end = builder
      .region("myr")
        .finalState("end");
    
    builder
      .region("myr")
        .initial()
          .transition()
            .effect((c) -> { c.counter++; })
            .target(end);
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final class Context {
    int counter;
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test2.class.getSimpleName() + "\"\n" +
"  Region: \"myr\"\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    Transition: #5 --- #4 -> \"end\"";
}
