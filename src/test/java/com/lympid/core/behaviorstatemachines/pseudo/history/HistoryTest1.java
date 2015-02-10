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

package com.lympid.core.behaviorstatemachines.pseudo.history;

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.FinalStateTest;
import com.lympid.core.behaviorstatemachines.InitialPseudoStateTest;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
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
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests a shallow/deep history pseudo state as a default entry of a composite
 * state.
 *
 * @author Fabien Renaud 
 */
public abstract class HistoryTest1 extends AbstractHistoryTest {
  
  private String stdout;
  
  protected HistoryTest1(final PseudoStateKind historyKind) {
    super(historyKind);
    setStdOut(historyKind);
  }
  
  @Test
  public void model() {
    assertEquals(getClass().getSimpleName(), topLevelStateMachine().getName());
    Region region = StateMachineTester.assertTopLevelStateMachine(topLevelStateMachine());

    StateMachineTester.assertRegion(region, 4, 4, 
      new VertexTest[]{
        new InitialPseudoStateTest("#4"),
        new SimpleStateTest("P"),
        new VertexTest("compo", this::verifyCompo),
        new FinalStateTest("end")
      },
      new TransitionTest[]{
        new TransitionTest("#5", "#4", "A"),
        new TransitionTest("#7", "compo", "P"),
        new TransitionTest("#15", "C", "end"),
        new TransitionTest("#17", "P", "history")
      }
    );
  }
  
  private void verifyCompo(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    
    StateMachineTester.assertRegions(s.region(), 1, new RegionTest("8", null, 4, 2,
      new VertexTest[]{
        new SimpleStateTest("A"),
        new SimpleStateTest("B"),
        new SimpleStateTest("C"),
        historyVertexTest("history")
      },
      new TransitionTest[]{
        new TransitionTest("#11", "A", "B"),
        new TransitionTest("#13", "B", "C")
      }
    ));
  }
  
  @Test
  public void run_a_b_c_end() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo").enter("A")
      .exit("A").effect("t1").enter("B")
      .exit("B").effect("t2").enter("C")
      .exit("C").exit("compo").effect("t3");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "A"));
    
    fsm.take(new StringEvent("toB"));
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "B"));
    
    fsm.take(new StringEvent("toC"));
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "C"));
    
    fsm.take(new StringEvent("toEnd"));
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    
    assertSequentialContextEquals(expected, ctx);
  }
  
  @Test
  public void run_a_P_a_b_c_end() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo").enter("A")
      .exit("A").exit("compo").effect("t4").enter("P")
      .exit("P").effect("t5").enter("compo").enter("A")
      .exit("A").effect("t1").enter("B")
      .exit("B").effect("t2").enter("C")
      .exit("C").exit("compo").effect("t3");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "A"));
    
    fsm.take(new StringEvent("pause"));
    assertStateConfiguration(fsm, new ActiveStateTree("P"));
    
    fsm.take(new StringEvent("resume"));
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "A"));
    
    fsm.take(new StringEvent("toB"));
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "B"));
    
    fsm.take(new StringEvent("toC"));
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "C"));
    
    fsm.take(new StringEvent("toEnd"));
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    
    assertSequentialContextEquals(expected, ctx);
  }
  
  @Test
  public void run_a_b_P_b_c_end() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo").enter("A")
      .exit("A").effect("t1").enter("B")
      .exit("B").exit("compo").effect("t4").enter("P")
      .exit("P").effect("t5").enter("compo").enter("B")
      .exit("B").effect("t2").enter("C")
      .exit("C").exit("compo").effect("t3");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "A"));
    
    fsm.take(new StringEvent("toB"));
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "B"));
    
    fsm.take(new StringEvent("pause"));
    assertStateConfiguration(fsm, new ActiveStateTree("P"));
    
    fsm.take(new StringEvent("resume"));
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "B"));
    
    fsm.take(new StringEvent("toC"));
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "C"));
    
    fsm.take(new StringEvent("toEnd"));
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    
    assertSequentialContextEquals(expected, ctx);
  }
  
  @Test
  public void run_a_b_c_P_c_end() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo").enter("A")
      .exit("A").effect("t1").enter("B")
      .exit("B").effect("t2").enter("C")
      .exit("C").exit("compo").effect("t4").enter("P")
      .exit("P").effect("t5").enter("compo").enter("C")
      .exit("C").exit("compo").effect("t3");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "A"));
    
    fsm.take(new StringEvent("toB"));
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "B"));
    
    fsm.take(new StringEvent("toC"));
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "C"));
    
    fsm.take(new StringEvent("pause"));
    assertStateConfiguration(fsm, new ActiveStateTree("P"));
    
    fsm.take(new StringEvent("resume"));
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "C"));
    
    fsm.take(new StringEvent("toEnd"));
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
        .state(composite("compo"))
          .transition("t4")
            .on("pause")
            .target("P");

    builder
      .region()
        .state("P")
          .transition("t5")
            .on("resume")
            .target("history");
    
    return builder;
  }
  
  private CompositeStateBuilder composite(final String name) {
    CompositeStateBuilder builder = new CompositeStateBuilder(name);
  
    history(builder, "history");
    
    builder
      .region()
        .state("A")
          .transition("t1")
            .on("toB")
            .target("B");
  
    builder
      .region()
        .state("B")
          .transition("t2")
            .on("toC")
            .target("C");
  
    builder
      .region()
        .state("C")
          .transition("t3")
            .on("toEnd")
            .target("end");
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return stdout;
  }

  @Override
  final void setStdOut(final PseudoStateKind historyKind) {
    stdout = "StateMachine: \"" + name() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"P\"\n" +
"    State: \"compo\"\n" +
"      Region: #8\n" +
"        State: \"B\"\n" +
"        State: \"C\"\n" +
"        PseudoState: \"history\" kind: " + historyKind + "\n" +
"        State: \"A\"\n" +
"        Transition: \"t1\" --- \"A\" -> \"B\"\n" +
"        Transition: \"t2\" --- \"B\" -> \"C\"\n" +
"    Transition: \"t0\" --- #4 -> \"A\"\n" +
"    Transition: \"t4\" --- \"compo\" -> \"P\"\n" +
"    Transition: \"t3\" --- \"C\" -> \"end\"\n" +
"    Transition: \"t5\" --- \"P\" -> \"history\"";
  }

}
