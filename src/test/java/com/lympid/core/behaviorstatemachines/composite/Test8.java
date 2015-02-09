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
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.StateMachineTester;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.TransitionTest;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexTest;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.EntryPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.ExitPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests entry/exit points with simple states.
 * The state machine auto starts.
 * @author Fabien Renaud
 */
public class Test8 extends AbstractStateMachineTest {
  
  @Test
  public void model() {
    assertEquals(getClass().getSimpleName(), topLevelStateMachine().getName());
    Region region = StateMachineTester.assertTopLevelStateMachine(topLevelStateMachine());

    StateMachineTester.assertRegion(region, 5, 4, 
      new VertexTest[]{
        new InitialPseudoStateTest("#4"),
        new SimpleStateTest("A"),
        new VertexTest("B", this::verifyB),
        new SimpleStateTest("C"),
        new FinalStateTest("#3")
      },
      new TransitionTest[]{
        new TransitionTest("#5", "#4", "A"),
        new TransitionTest("#7", "A", "B_entryPoint"),
        new TransitionTest("#16", "B_exitPoint", "C"),
        new TransitionTest("#19", "C", "#3")
      }
    );
  }
  
  private void verifyB(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    StateMachineTester.assertRegions(s.region(), 1, new RegionTest("9", null, 1, 2, 
      new VertexTest[] {
        new SimpleStateTest("Ba")
      },
      new TransitionTest[] {
        new TransitionTest("#11", "Ba", "B_exitPoint"),
        new TransitionTest("#13", "B_entryPoint", "Ba", TransitionKind.LOCAL)
      }
    ));
  }
  
  @Test
  public void runIt() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .exit("A").effect("t1").enter("B")
      .effect("t2").enter("Ba")
      .exit("Ba").effect("t3")
      .exit("B").effect("t4").enter("C")
      .exit("C").effect("t5");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("#3"));
    assertSequentialContextEquals(expected, ctx);
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder builder = new StateMachineBuilder(name());

    VertexBuilderReference end = builder
      .region()
        .finalState();

    builder
      .region()
        .initial()
          .transition("t0")
            .target("A");

    builder
      .region()
        .state("A")
          .transition("t1")
            .target("B_entryPoint");
    
    builder
      .region()
        .state(compositeB("B"));

    builder
      .region()
        .state("C")
          .transition("t5")
            .target(end);

    return builder;
  }
  
  private CompositeStateBuilder compositeB(final String name) {
    CompositeStateBuilder builder = new CompositeStateBuilder(name);
        
    builder
      .connectionPoint()
        .entryPoint(new EntryPointBuilder<>("B_entryPoint")
          .transition("t2")
            .target("Ba"))
        .exitPoint(new ExitPointBuilder<>("B_exitPoint")
          .transition("t4")
            .target("C"));
    
    builder
      .region()
        .state("Ba")
          .transition("t3")
            .target("B_exitPoint");
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }

  private static final String STDOUT = "StateMachine: \"" + Test8.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: #3\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    State: \"C\"\n" +
"    State: \"B\"\n" +
"      EntryPoint: \"B_entryPoint\"\n" +
"      ExitPoint: \"B_exitPoint\"\n" +
"      Region: #9\n" +
"        State: \"Ba\"\n" +
"        Transition: \"t3\" --- \"Ba\" -> \"B_exitPoint\"\n" +
"        Transition: \"t2\" -L- \"B_entryPoint\" -> \"Ba\"\n" +
"    Transition: \"t0\" --- #4 -> \"A\"\n" +
"    Transition: \"t1\" --- \"A\" -> \"B_entryPoint\"\n" +
"    Transition: \"t4\" --- \"B_exitPoint\" -> \"C\"\n" +
"    Transition: \"t5\" --- \"C\" -> #3";
}