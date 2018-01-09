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
import com.lympid.core.behaviorstatemachines.SimpleStateTest;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.StateMachineTester;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.TransitionTest;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexTest;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.EntryPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.ExitPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.composite.Test16.Context;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests an external transition which has an exit point of a composite state as
 * as source and an entry point of the same composite state as target.
 *
 * @author Fabien Renaud 
 */
public class Test16 extends AbstractStateMachineTest<Context> {
  
  @Test
  public void model() {
    assertEquals(getClass().getSimpleName(), topLevelStateMachine().getName());
    Region region = StateMachineTester.assertTopLevelStateMachine(topLevelStateMachine());

    StateMachineTester.assertRegion(region, 3, 3, 
      new VertexTest[]{
        new InitialPseudoStateTest("#4"),
        new VertexTest("A", this::verifyA),
        new FinalStateTest("end")
      },
      new TransitionTest[]{
        new TransitionTest("#5", "#4", "Aa"),
        new TransitionTest("#11", "Ab", "end"),
        new TransitionTest("#16", "A_exitPoint", "A_entryPoint")
      }
    );
  }
  
  private void verifyA(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    StateMachineTester.assertRegions(s.region(), 1, 
      new RegionTest("7", null, 2, 2, 
        new VertexTest[]{
          new SimpleStateTest("Aa"),
          new SimpleStateTest("Ab")
        },
        new TransitionTest[]{
          new TransitionTest("#9", "Aa", "A_exitPoint"),
          new TransitionTest("#13", "A_entryPoint", "Ab", TransitionKind.LOCAL)
        }
      )
    );
  }
  
  @Test
  public void run() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A").enter("Aa")
      .exit("Aa").effect("t1")
      .exit("A").effect("t2")
      .enter("A").effect("t3").enter("Ab")
      .exit("Ab").exit("A").effect("t4");

    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
        
    assertSequentialContextEquals(expected, fsm);
  }
  
  @Override
  public StateMachineBuilder<Context> topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());

    builder
      .region()
        .finalState("end");

    builder
      .region()
        .initial()
          .transition("t0")
            .target("Aa");
    
    builder
      .region()
        .state(compositeA("A"));

    return builder;
  }
  
  private CompositeStateBuilder<Context> compositeA(final String name) {
    CompositeStateBuilder<Context> builder = new CompositeStateBuilder<>(name);
    
    builder
      .region()
        .state("Aa")
          .transition("t1")
            .target("A_exitPoint");
    
    builder
      .region()
        .state("Ab")
          .transition("t4")
            .target("end");
        
    builder
      .connectionPoint()
        .entryPoint(new EntryPointBuilder<Context>("A_entryPoint")
          .transition("t3")
            .target("Ab")
        )
        .exitPoint(new ExitPointBuilder<Context>("A_exitPoint")
          .transition("t2")
            .target("A_entryPoint")
        );
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }

  public static final class Context extends SequentialContext {
  }

  private static final String STDOUT = "StateMachine: \"" + Test16.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"      EntryPoint: \"A_entryPoint\"\n" +
"      ExitPoint: \"A_exitPoint\"\n" +
"      Region: #7\n" +
"        State: \"Aa\"\n" +
"        State: \"Ab\"\n" +
"        Transition: \"t1\" --- \"Aa\" -> \"A_exitPoint\"\n" +
"        Transition: \"t3\" -L- \"A_entryPoint\" -> \"Ab\"\n" +
"    Transition: \"t0\" --- #4 -> \"Aa\"\n" +
"    Transition: \"t4\" --- \"Ab\" -> \"end\"\n" +
"    Transition: \"t2\" --- \"A_exitPoint\" -> \"A_entryPoint\"";
}
