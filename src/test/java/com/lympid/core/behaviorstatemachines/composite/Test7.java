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
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
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
 * Tests the innermost transitions have priority on the outermost transitions.
 * The state machine auto starts.
 * @author Fabien Renaud 
 */
public class Test7 extends AbstractStateMachineTest {
  
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
        new TransitionTest("#9", "B", "C"),
        new TransitionTest("#17", "C", "#3")
      }
    );
  }
  
  private void verifyB(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    StateMachineTester.assertRegions(s.region(), 1, 
      new RegionTest("10", null, 3, 2, 
        new VertexTest[]{
          new InitialPseudoStateTest("#12"),
          new SimpleStateTest("Ba"),
          new FinalStateTest("rend")
        },
        new TransitionTest[]{
          new TransitionTest("#13", "#12", "Ba"),
          new TransitionTest("#15", "Ba", "rend")
        }
      )
    );
  }
  
  @Test
  public void runIt() {
    SequentialContext expected = new SequentialContext()
      .enter("A")
      .exit("A").effect("t1").enter("B").effect("t2").enter("Ba")
      .exit("Ba").effect("t3").exit("B").effect("t4").enter("C")
      .exit("C").effect("t5");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("B", "Ba"));
    
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("B", "rend"));
    
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("#3"));
    
    assertSequentialContextEquals(expected, fsm);
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
        .state(compositeB(builder, "B"))
          .transition("t4")
            .on("go")
            .target("C");

    builder
      .region()
        .state("C")
          .transition("t5")
            .target(end);

    return builder;
  }
  
  private CompositeStateBuilder compositeB(final StateMachineBuilder b, final String name) {
    CompositeStateBuilder builder = new CompositeStateBuilder(name);
      
    VertexBuilderReference end = builder
      .region()
        .finalState("rend");
    
    builder
      .region()
        .initial()
          .transition("t2")
            .target("Ba");
    
    builder
      .region()
        .state("Ba")
          .transition("t3")
            .on("go")
            .target(end);
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }

  private static final String STDOUT = "StateMachine: \"" + Test7.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: #3\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"C\"\n" +
"    State: \"A\"\n" +
"    State: \"B\"\n" +
"      Region: #10\n" +
"        FinalState: \"rend\"\n" +
"        PseudoState: #12 kind: INITIAL\n" +
"        State: \"Ba\"\n" +
"        Transition: \"t2\" --- #12 -> \"Ba\"\n" +
"        Transition: \"t3\" --- \"Ba\" -> \"rend\"\n" +
"    Transition: #5 --- #4 -> \"A\"\n" +
"    Transition: \"t1\" --- \"A\" -> \"B\"\n" +
"    Transition: \"t4\" --- \"B\" -> \"C\"\n" +
"    Transition: \"t5\" --- \"C\" -> #3";
}
