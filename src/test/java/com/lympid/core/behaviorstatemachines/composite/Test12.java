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
import com.lympid.core.behaviorstatemachines.builder.ExitPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests an external transition starting off the edge of the composite state and
 * targeting a substate of the composite state.
 *
 * @author Fabien Renaud 
 */
public class Test12 extends AbstractStateMachineTest {
  
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
        new TransitionTest("#5", "#4", "A"),
        new TransitionTest("#7", "A", "Ab"),
        new TransitionTest("#17", "A_exitPoint", "end")
      }
    );
  }
  
  private void verifyA(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    StateMachineTester.assertRegions(s.region(), 1, 
      new RegionTest("8", null, 4, 3, 
        new VertexTest[]{
          new InitialPseudoStateTest("#10"),
          new SimpleStateTest("Aa"),
          new SimpleStateTest("Ab"),
          new FinalStateTest("#9")
        },
        new TransitionTest[]{
          new TransitionTest("#11", "#10", "Aa"),
          new TransitionTest("#13", "Aa", "#9"),
          new TransitionTest("#15", "Ab", "A_exitPoint")
        }
      )
    );
  }
  
  @Test
  public void run_try() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .effect("t1").enter("Aa")
      .exit("Aa").exit("A").effect("t3").enter("A").enter("Ab")
      .exit("Ab").effect("t4")
      .exit("A").effect("t5");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("A", "Aa").get());
    
    fsm.take(new StringEvent("try"));
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("end").get());
        
    assertSequentialContextEquals(expected, ctx);
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder builder = new StateMachineBuilder(name());

    builder
      .region()
        .finalState("end");

    builder
      .region()
        .initial()
          .transition("t0")
            .target("A");
    
    builder
      .region()
        .state(compositeA("A"));

    return builder;
  }
  
  private CompositeStateBuilder compositeA(final String name) {
    CompositeStateBuilder<Object> builder = new CompositeStateBuilder(name);
    
    VertexBuilderReference rend = builder
      .region()
        .finalState();
    
    builder
      .region()
        .initial()
          .transition("t1")
            .target("Aa");
    
    builder
      .region()
        .state("Aa")
          .transition("t2")
            .on("go")
            .target(rend);
    
    builder
      .transition("t3")
        .on("try")
        .target("Ab");
    
    builder
      .region()
        .state("Ab")
          .transition("t4")
            .target("A_exitPoint");
    
    builder
      .connectionPoint()
        .exitPoint(new ExitPointBuilder<>("A_exitPoint")
          .transition("t5")
            .target("end")
        );
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }

  private static final String STDOUT = "StateMachine: \"" + Test12.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"      ExitPoint: \"A_exitPoint\"\n" +
"      Region: #8\n" +
"        State: \"Aa\"\n" +
"        State: \"Ab\"\n" +
"        FinalState: #9\n" +
"        PseudoState: #10 kind: INITIAL\n" +
"        Transition: \"t1\" --- #10 -> \"Aa\"\n" +
"        Transition: \"t2\" --- \"Aa\" -> #9\n" +
"        Transition: \"t4\" --- \"Ab\" -> \"A_exitPoint\"\n" +
"    Transition: \"t0\" --- #4 -> \"A\"\n" +
"    Transition: \"t3\" --- \"A\" -> \"Ab\"\n" +
"    Transition: \"t5\" --- \"A_exitPoint\" -> \"end\"";
}
