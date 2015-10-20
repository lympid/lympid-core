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

package com.lympid.core.behaviorstatemachines.composite;

import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.FinalStateTest;
import com.lympid.core.behaviorstatemachines.InitialPseudoStateTest;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.RegionTest;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.StateMachineTester;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.TransitionTest;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexTest;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.EntryPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.ExitPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests a local transition going from an entry point to an exit point.
 * The state machine auto starts.
 * @author Fabien Renaud 
 */
public class Test10 extends AbstractStateMachineTest {
  
  @Test
  public void model() {
    assertEquals(getClass().getSimpleName(), topLevelStateMachine().getName());
    Region region = StateMachineTester.assertTopLevelStateMachine(topLevelStateMachine());

    StateMachineTester.assertRegion(region, 3, 2, 
      new VertexTest[]{
        new InitialPseudoStateTest("#3"),
        new VertexTest("#6", this::verifyA),
        new FinalStateTest("end")
      },
      new TransitionTest[]{
        new TransitionTest("#4", "#3", "A_entryPoint"),
        new TransitionTest("#12", "A_exitPoint", "end")
      }
    );
  }
  
  private void verifyA(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    StateMachineTester.assertRegions(s.region(), 1, new RegionTest("7", null, 0, 1, 
      new VertexTest[] {
      },
      new TransitionTest[] {
        new TransitionTest("#9", "A_entryPoint", "A_exitPoint", TransitionKind.LOCAL),
      }
    ));
  }
  
  @Test
  public void run() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .effect("t1").exit("A")
      .effect("t2");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("#5"));
    
    assertSequentialContextEquals(expected, fsm);
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<SequentialContext> builder = new StateMachineBuilder<>(name());

    builder
      .region()
        .initial()
          .transition("t0")
            .target("A_entryPoint");
    
    builder
      .region()
        .finalState("end");
    
    builder
      .region()
        .state(compositeA());

    return builder;
  }
  
  private CompositeStateBuilder<SequentialContext> compositeA() {
    CompositeStateBuilder<SequentialContext> builder = new CompositeStateBuilder();
    
    builder
      .entry(EntryABehavior.class)
      .exit(ExitABehavior.class);
        
    builder
      .connectionPoint()
        .entryPoint(new EntryPointBuilder<SequentialContext>("A_entryPoint")
          .transition("t1")
            .target("A_exitPoint"))
        .exitPoint(new ExitPointBuilder<SequentialContext>("A_exitPoint")
          .transition("t2")
            .target("end"));
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }

  public static final class EntryABehavior implements StateBehavior<SequentialContext> {

    @Override
    public void accept(SequentialContext t) {
      t.enter("A");
    }
    
  }

  public static final class ExitABehavior implements StateBehavior<SequentialContext> {

    @Override
    public void accept(SequentialContext t) {
      t.exit("A");
    }
    
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test10.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    FinalState: \"end\"\n" +
"    State: #6\n" +
"      ExitPoint: \"A_exitPoint\"\n" +
"      EntryPoint: \"A_entryPoint\"\n" +
"      Region: #7\n" +
"        Transition: \"t1\" -L- \"A_entryPoint\" -> \"A_exitPoint\"\n" +
"    Transition: \"t0\" --- #3 -> \"A_entryPoint\"\n" +
"    Transition: \"t2\" --- \"A_exitPoint\" -> \"end\"";
}
