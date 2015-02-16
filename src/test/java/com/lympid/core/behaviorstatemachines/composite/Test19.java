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
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.StateMachineTester;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.TransitionTest;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexTest;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.EntryPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests a local transition that starts from an entry point and targets the
 * composite state of that entry point.
 *
 * @author Fabien Renaud 
 */
public class Test19 extends AbstractStateMachineTest {
  
  @Test
  public void model() {
    assertEquals(getClass().getSimpleName(), topLevelStateMachine().getName());
    Region region = StateMachineTester.assertTopLevelStateMachine(topLevelStateMachine());

    StateMachineTester.assertRegion(region, 3, 2, 
      new VertexTest[]{
        new InitialPseudoStateTest("#4"),
        new VertexTest("A", this::verifyA),
        new FinalStateTest("#3")
      },
      new TransitionTest[]{
        new TransitionTest("#5", "#4", "A_entryPoint"),
        new TransitionTest("#7", "A", "#3")
      }
    );
  }
  
  private void verifyA(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    StateMachineTester.assertRegions(s.region(), 1, 
      new RegionTest("8", null, 3, 3, 
        new VertexTest[]{
          new InitialPseudoStateTest("#10"),
          new SimpleStateTest("Aa"),
          new FinalStateTest("#9")
        },
        new TransitionTest[]{
          new TransitionTest("#11", "#10", "Aa"),
          new TransitionTest("#13", "Aa", "#9"),
          new TransitionTest("#15", "A_entryPoint", "A", TransitionKind.LOCAL)
        }
      )
    );
  }
  
  @Test
  public void run() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .effect("t1").effect("t2").enter("Aa")
      .exit("Aa").effect("t3")
      .exit("A").effect("t4");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("#3").get());
    
    assertSequentialContextEquals(expected, ctx);
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<SequentialContext> builder = new StateMachineBuilder(name());

    VertexBuilderReference end = builder
      .region()
        .finalState();

    builder
      .region()
        .initial()
          .transition("t0")
            .target("A_entryPoint");
    
    builder
      .region()
        .state(compositeA("A"))
          .transition()
            .effect((e, c) -> c.effect("t4"))
            .target(end);

    return builder;
  }
  
  private CompositeStateBuilder<SequentialContext> compositeA(final String name) {
    CompositeStateBuilder<SequentialContext> builder = new CompositeStateBuilder(name);
    
    VertexBuilderReference rend = builder
      .region()
        .finalState();
    
    builder
      .region()
        .initial()
          .transition("t2")
            .target("Aa");
    
    builder
      .region()
        .state("Aa")
          .transition("t3")
            .target(rend);
    
    builder
      .connectionPoint()
        .entryPoint(new EntryPointBuilder<SequentialContext>("A_entryPoint")
          .transition("t1")
            .target("A")
        );
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }

  private static final String STDOUT = "StateMachine: \"" + Test19.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: #3\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"      EntryPoint: \"A_entryPoint\"\n" +
"      Region: #8\n" +
"        State: \"Aa\"\n" +
"        FinalState: #9\n" +
"        PseudoState: #10 kind: INITIAL\n" +
"        Transition: \"t2\" --- #10 -> \"Aa\"\n" +
"        Transition: \"t3\" --- \"Aa\" -> #9\n" +
"        Transition: \"t1\" -L- \"A_entryPoint\" -> \"A\"\n" +
"    Transition: \"t0\" --- #4 -> \"A_entryPoint\"\n" +
"    Transition: #7 --- \"A\" -> #3";
}
