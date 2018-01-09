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

package com.lympid.core.behaviorstatemachines.pseudo.terminate;

import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import com.lympid.core.behaviorstatemachines.pseudo.terminate.Test2.Context;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;

/**
 * Tests a terminate pseudo state which is the target of a transition which 
 * source is a simple state.
 * @author Fabien Renaud 
 */
public class Test2 extends AbstractStateMachineTest<Context> {
  
  @Test
  public void run() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .exit("A").effect("t1");
    
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this));
    assertSequentialContextEquals(expected, fsm);
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
        .terminate();
        
    VertexBuilderReference terminate = builder
      .region()
        .terminate(); // double-call to increase coverage
    
    builder
      .region()
        .state("A")
          .transition("t1")
            .target(terminate);
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }

  public static final class Context extends SequentialContext {
  }

  private static final String STDOUT = "StateMachine: \"" + Test2.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    PseudoState: #5 kind: TERMINATE\n" +
"    State: \"A\"\n" +
"    Transition: \"t0\" --- #3 -> \"A\"\n" +
"    Transition: \"t1\" --- \"A\" -> #5";
}
