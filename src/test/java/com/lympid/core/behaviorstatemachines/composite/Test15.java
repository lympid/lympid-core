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
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests an external transition which as the exit point of a composite state as
 * source and which targets the composite state itself.
 *
 * @author Fabien Renaud
 */
public class Test15 extends AbstractStateMachineTest {
  
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
        new TransitionTest("#12", "Aa", "end"),
        new TransitionTest("#14", "A_exitPoint", "A")
      }
    );
  }
  
  private void verifyA(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    StateMachineTester.assertRegions(s.region(), 1, 
      new RegionTest("7", null, 2, 2, 
        new VertexTest[]{
          new InitialPseudoStateTest("#8"),
          new SimpleStateTest("Aa")
        },
        new TransitionTest[]{
          new TransitionTest("#9", "#8", "Aa"),
          new TransitionTest("#11", "Aa", "A_exitPoint")
        }
      )
    );
  }
  
  @Test
  public void run_go() {
    run_letN_go(0);
  }
  
  @Test
  public void run_let_go() {
    run_letN_go(1);
  }
  
  @Test
  public void run_let5_go() {
    run_letN_go(5);
  }
  
  private void run_letN_go(final int n) {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .effect("t1").enter("Aa");
    for (int i = 0; i < n; i++) {
      expected
        .exit("Aa").effect("t2")
        .exit("A").effect("t3").enter("A").effect("t1").enter("Aa");
    }
    expected
      .exit("Aa").exit("A").effect("t4");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("A", "Aa"));
    
    for (int i = 0; i < n; i++) {
      fsm.take(new StringEvent("let"));
      assertStateConfiguration(fsm, new ActiveStateTree("A", "Aa"));
    }
    
    fsm.take(new StringEvent("go"));
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
        
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
        
    builder
      .region()
        .initial()
          .transition("t1")
            .target("Aa");
    
    builder
      .region()
        .state("Aa")
          .transition("t2")
            .on("let")
            .target("A_exitPoint")
          .transition("t4")
            .on("go")
            .target("end");
        
    builder
      .connectionPoint()
        .exitPoint(new ExitPointBuilder<>("A_exitPoint")
          .transition("t3")
            .target("A")
        );
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }

  private static final String STDOUT = "StateMachine: \"" + Test15.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"      ExitPoint: \"A_exitPoint\"\n" +
"      Region: #7\n" +
"        PseudoState: #8 kind: INITIAL\n" +
"        State: \"Aa\"\n" +
"        Transition: \"t1\" --- #8 -> \"Aa\"\n" +
"        Transition: \"t2\" --- \"Aa\" -> \"A_exitPoint\"\n" +
"    Transition: \"t0\" --- #4 -> \"A\"\n" +
"    Transition: \"t4\" --- \"Aa\" -> \"end\"\n" +
"    Transition: \"t3\" --- \"A_exitPoint\" -> \"A\"";
}
