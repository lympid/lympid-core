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
 * Tests a shallow/deep history pseudo state with an outgoing transition.
 * The test attempts to restore the composite state after it had
 * reached its final state. Because the history pseudo state has an outgoing
 * transition, the test is expected to be successful.
 * 
 * @author Fabien Renaud 
 */
public abstract class HistoryTest6 extends AbstractHistoryTest<SequentialContext> {
  
  private String stdout;
  
  protected HistoryTest6(final PseudoStateKind historyKind) {
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
        new TransitionTest("#7", "compo", "end"),
        new TransitionTest("#8", "compo", "P"),
        new TransitionTest("#20", "P", "#10")
      }
    );
  }
  
  private void verifyCompo(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    StateMachineTester.assertRegions(s.region(), 1, new RegionTest("9", null, 5, 4,
      new VertexTest[]{
        new SimpleStateTest("A"),
        new SimpleStateTest("B"),
        new SimpleStateTest("C"),
        historyVertexTest("#10"),
        new FinalStateTest("rend")
      },
      new TransitionTest[]{
        new TransitionTest("#13", "A", "B"),
        new TransitionTest("#15", "B", "C"),
        new TransitionTest("#17", "C", "rend"),
        new TransitionTest("#11", "#10", "B")
      }
    ));
  }
  
  @Test
  public void run_a_b_c_rend_end() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo").enter("A")
      .exit("A").effect("t1").enter("B")
      .exit("B").effect("t2").enter("C")
      .exit("C").effect("t3")
      .exit("compo").effect("t4");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "A"));
    
    fsm.take(new StringEvent("toB"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "B"));
    
    fsm.take(new StringEvent("toC"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "C"));
    
    fsm.take(new StringEvent("toE"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "rend"));
    
    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    
    assertSequentialContextEquals(expected, fsm);
  }
  
  @Test
  public void run_a_P_a_b_c_rend_end() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo").enter("A")
      .exit("A").exit("compo").effect("t5").enter("P")
      .exit("P").effect("t6").enter("compo").enter("A")
      .exit("A").effect("t1").enter("B")
      .exit("B").effect("t2").enter("C")
      .exit("C").effect("t3")
      .exit("compo").effect("t4");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "A"));
    
    fsm.take(new StringEvent("pause"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("P"));
    
    fsm.take(new StringEvent("resume"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "A"));
    
    fsm.take(new StringEvent("toB"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "B"));
    
    fsm.take(new StringEvent("toC"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "C"));
    
    fsm.take(new StringEvent("toE"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "rend"));
    
    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    
    assertSequentialContextEquals(expected, fsm);
  }
  
  @Test
  public void run_a_b_P_b_c_rend_end() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo").enter("A")
      .exit("A").effect("t1").enter("B")
      .exit("B").exit("compo").effect("t5").enter("P")
      .exit("P").effect("t6").enter("compo").enter("B")
      .exit("B").effect("t2").enter("C")
      .exit("C").effect("t3")
      .exit("compo").effect("t4");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "A"));
    
    fsm.take(new StringEvent("toB"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "B"));
    
    fsm.take(new StringEvent("pause"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("P"));
    
    fsm.take(new StringEvent("resume"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "B"));
    
    fsm.take(new StringEvent("toC"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "C"));
    
    fsm.take(new StringEvent("toE"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "rend"));
    
    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    
    assertSequentialContextEquals(expected, fsm);
  }
  
  @Test
  public void run_a_b_c_P_c_rend_end() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo").enter("A")
      .exit("A").effect("t1").enter("B")
      .exit("B").effect("t2").enter("C")
      .exit("C").exit("compo").effect("t5").enter("P")
      .exit("P").effect("t6").enter("compo").enter("C")
      .exit("C").effect("t3")
      .exit("compo").effect("t4");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "A"));
    
    fsm.take(new StringEvent("toB"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "B"));
    
    fsm.take(new StringEvent("toC"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "C"));
    
    fsm.take(new StringEvent("pause"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("P"));
    
    fsm.take(new StringEvent("resume"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "C"));
    
    fsm.take(new StringEvent("toE"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "rend"));
    
    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    
    assertSequentialContextEquals(expected, fsm);
  }
  
  @Test
  public void run_a_b_c_rend_P_b_c_rend_end() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo").enter("A")
      .exit("A").effect("t1").enter("B")
      .exit("B").effect("t2").enter("C")
      .exit("C").effect("t3")
      .exit("compo").effect("t5").enter("P")
      .exit("P").effect("t6").enter("compo").effect("t7").enter("B")
      .exit("B").effect("t2").enter("C")
      .exit("C").effect("t3")
      .exit("compo").effect("t4");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "A"));
    
    fsm.take(new StringEvent("toB"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "B"));
    
    fsm.take(new StringEvent("toC"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "C"));
    
    fsm.take(new StringEvent("toE"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "rend"));
    
    fsm.take(new StringEvent("pause"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("P"));
    
    fsm.take(new StringEvent("resume"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "B"));
    
    fsm.take(new StringEvent("toC"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "C"));
    
    fsm.take(new StringEvent("toE"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "rend"));
    
    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    
    assertSequentialContextEquals(expected, fsm);
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<SequentialContext> builder = new StateMachineBuilder<>(name());

    builder
      .region()
        .finalState("end");

    builder
      .region()
        .initial()
          .transition("t0")
            .target("A");
    
    CompositeStateBuilder<SequentialContext> compo = builder
      .region()
        .state(composite("compo"))
          .transition("t4")
            .on("end")
            .target("end")
          .transition("t5")
            .on("pause")
            .target("P");
    
    VertexBuilderReference historyVertex =
      historyTransitionTo(compo, "B", (c) -> c.effect("t7"));
    
    builder
      .region()
        .state("P")
          .transition("t6")
            .on("resume")
            .target(historyVertex);
    
    
    return builder;
  }
  
  private CompositeStateBuilder<SequentialContext> composite(final String name) {
    CompositeStateBuilder<SequentialContext> builder = new CompositeStateBuilder<>(name);
  
    history(builder);
    
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
            .on("toE")
            .target("rend");
    
    builder
      .region()
        .finalState("rend");
    
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
"    State: \"compo\"\n" +
"      Region: #9\n" +
"        State: \"A\"\n" +
"        State: \"B\"\n" +
"        State: \"C\"\n" +
"        FinalState: \"rend\"\n" +
"        PseudoState: #10 kind: " + historyKind + "\n" +
"        Transition: #11 --- #10 -> \"B\"\n" +
"        Transition: \"t1\" --- \"A\" -> \"B\"\n" +
"        Transition: \"t2\" --- \"B\" -> \"C\"\n" +
"        Transition: \"t3\" --- \"C\" -> \"rend\"\n" +
"    State: \"P\"\n" +
"    Transition: \"t0\" --- #4 -> \"A\"\n" +
"    Transition: \"t4\" --- \"compo\" -> \"end\"\n" +
"    Transition: \"t5\" --- \"compo\" -> \"P\"\n" +
"    Transition: \"t6\" --- \"P\" -> #10";
  }

}
