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

import com.lympid.core.basicbehaviors.StringEvent;
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
import com.lympid.core.behaviorstatemachines.TransitionTest;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexTest;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests an external transition ending on the edge of a composite state triggers
 * the initial transition of the composite state.
 * The state machine auto starts.
 * @author Fabien Renaud 
 */
public class Test4 extends AbstractStateMachineTest {
  
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
        new TransitionTest("#7", "A", "B"),
        new TransitionTest("#13", "Ba", "C"),
        new TransitionTest("#15", "C", "#3")
      }
    );
  }
  
  private void verifyB(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    StateMachineTester.assertRegions(s.region(), 1, 
      new RegionTest("9", null, 2, 1, 
        new VertexTest[]{
          new InitialPseudoStateTest("#10"),
          new SimpleStateTest("Ba")
        },
        new TransitionTest[]{
          new TransitionTest("#11", "#10", "Ba")
        }
      )
    );
  }
  
  @Test
  public void runIt() {
    SequentialContext expected = new SequentialContext()
      .enter("A")
      .exit("A").effect("t1").enter("B").effect("t2").enter("Ba")
      .exit("Ba").exit("B").effect("t3").enter("C")
      .exit("C").effect("t4");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("B", "Ba").get());
    
    fsm.take(new StringEvent("finishIt"));
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("#3").get());
    
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
          .transition()
            .target("A");

    builder
      .region()
        .state("A")
          .transition("t1")
            .target("B");
    
    builder
      .region()
        .state(compositeB(builder, "B"));

    builder
      .region()
        .state("C")
          .transition("t4")
            .target(end);

    return builder;
  }
  
  private CompositeStateBuilder compositeB(final StateMachineBuilder b, final String name) {
    CompositeStateBuilder builder = new CompositeStateBuilder(name);
    
    builder
      .region()
        .initial()
          .transition("t2")
            .target("Ba");
    
    builder
      .region()
        .state("Ba")
          .transition("t3")
            .on("finishIt")
            .target("C");
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }

  private static final String STDOUT = "StateMachine: \"" + Test4.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: #3\n" +
"    State: \"C\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    State: \"B\"\n" +
"      Region: #9\n" +
"        State: \"Ba\"\n" +
"        PseudoState: #10 kind: INITIAL\n" +
"        Transition: \"t2\" --- #10 -> \"Ba\"\n" +
"    Transition: #5 --- #4 -> \"A\"\n" +
"    Transition: \"t1\" --- \"A\" -> \"B\"\n" +
"    Transition: \"t3\" --- \"Ba\" -> \"C\"\n" +
"    Transition: \"t4\" --- \"C\" -> #3";
}
