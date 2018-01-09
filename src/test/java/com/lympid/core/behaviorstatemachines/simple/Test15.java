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
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import com.lympid.core.behaviorstatemachines.simple.Test15.Context;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;

/**
 * Entering a simple state executes all its exit actions in the default order.
 * @author Fabien Renaud 
 */
public class Test15 extends AbstractStateMachineTest<Context> {
  
  @Test
  public void run() {
    SequentialContext expected = new SequentialContext();
    expected
      .exit("foo")
      .exit("iak")
      .exit("bar")
      .exit("dir");
    
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    
    assertSequentialContextEquals(expected, fsm);
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
          .exit(c -> c.exit("foo"))
          .exit(c -> c.exit("iak"))
          .exit(BarExitBehavior.class)
          .exit(c -> c.exit("dir"))
          .transition()
            .target(end);
    
    return builder;
  }

  @Override
  protected boolean sequentialContextInjection() {
    return false;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }

  public static final class Context extends SequentialContext {
  }

  public static final class BarExitBehavior implements StateBehavior<Context> {

    @Override
    public void accept(Context c) {
      c.exit("bar");
    }
    
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test15.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    Transition: #5 --- #4 -> \"A\"\n" +
"    Transition: #7 --- \"A\" -> \"end\"";
}
