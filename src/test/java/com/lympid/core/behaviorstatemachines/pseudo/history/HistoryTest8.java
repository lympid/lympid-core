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

import com.lympid.core.basicbehaviors.CompletionEvent;
import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.BiTransitionConstraint;
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
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.TransitionTest;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexTest;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.impl.DefaultHistoryEntryException;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration.DefaultHistoryFailover;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * Tests the default history entry of shallow and deep history vertices.
 *
 * @author Fabien Renaud 
 */
public abstract class HistoryTest8 extends AbstractHistoryTest {
  
  private String stdout;
  
  protected HistoryTest8(final PseudoStateKind historyKind) {
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
        new TransitionTest("t0", "#4", "T"),
        new TransitionTest("t5", "compo", "P"),
        new TransitionTest("t4", "C", "end"),
        new TransitionTest("t6", "P", "history")
      }
    );
  }
  
  private void verifyCompo(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    
    StateMachineTester.assertRegions(s.region(), 1, new RegionTest("8", null, 5, 5,
      new VertexTest[]{
        new SimpleStateTest("A"),
        new SimpleStateTest("B"),
        new SimpleStateTest("C"),
        new SimpleStateTest("T"),
        historyVertexTest("history")
      },
      new TransitionTest[]{
        new TransitionTest("t1", "T", "A"),
        new TransitionTest("t7", "T", "history"),
        new TransitionTest("#12", "T", "T", TransitionKind.INTERNAL),
        new TransitionTest("t2", "A", "B"),
        new TransitionTest("t3", "B", "C")
      }
    ));
  }
  
  @Test
  public void runNormal() {
    Context ctx = new Context(false);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    SequentialContext expected = new SequentialContext();
    expected.effect("t0").enter("compo").enter("T");
    run(fsm, ctx, expected);
  }
  
  @Test(expected = DefaultHistoryEntryException.class)
  public void runHistory_failOverException() {
    Context ctx = new Context(true);
    StateMachineExecutor fsm = fsm(ctx);
    
    try {
      fsm.go();
    } catch (DefaultHistoryEntryException ex) {
      assertEquals(DefaultHistoryFailover.EXCEPTION, ex.getFailOver());
      assertNotNull(ex.getHistoryVertex());
      assertEquals("history", ex.getHistoryVertex().toString());
      throw ex;
    }
  }
  
  @Test
  public void runHistory_failOverDisable() {
    Context ctx = new Context(true);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.configuration().defaultHistoryFailover(DefaultHistoryFailover.DISABLE_TRANSITION);
    fsm.go();
    
    SequentialContext expected = new SequentialContext();
    expected.effect("t0").enter("compo").enter("T");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "T"));
    assertSequentialContextEquals(expected, fsm);
    
    fsm.take(new StringEvent("tog"));    
    run(fsm, ctx, expected);
  }
  
  private void run(StateMachineExecutor fsm, Context ctx, SequentialContext expected) {   
    expected.exit("T").effect("t1").enter("A");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "A"));
    assertSequentialContextEquals(expected, fsm);
    
    fsm.take(new StringEvent("toB"));
    expected.exit("A").effect("t2").enter("B");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "B"));
    assertSequentialContextEquals(expected, fsm);
    
    fsm.take(new StringEvent(("pause")));
    expected.exit("B").exit("compo").effect("t5").enter("P");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("P"));
    assertSequentialContextEquals(expected, fsm);
    
    fsm.take(new StringEvent("resume"));
    expected.exit("P").effect("t6").enter("compo").enter("B");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "B"));
    assertSequentialContextEquals(expected, fsm);
    
    fsm.take(new StringEvent("toC"));
    expected.exit("B").effect("t3").enter("C");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "C"));
    assertSequentialContextEquals(expected, fsm);
    
    fsm.take(new StringEvent("toEnd"));
    expected.exit("C").exit("compo").effect("t4");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertSequentialContextEquals(expected, fsm);
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder(name());

    builder
      .region()
        .finalState("end");

    builder
      .region()
        .initial()
          .transition("t0")
            .target("T");
    
    builder
      .region()
        .state(composite("compo"))
          .transition("t5")
            .on("pause")
            .target("P");

    builder
      .region()
        .state("P")
          .transition("t6")
            .on("resume")
            .target("history");
    
    return builder;
  }
  
  private CompositeStateBuilder<Context> composite(final String name) {
    CompositeStateBuilder<Context> builder = new CompositeStateBuilder(name);
    
    builder
      .region()
        .state("T")
          .transition("t1")
            .guardElse(ImmediateHistoryGuard.class)
            .target("A")
          .transition("t7")
            .guard(ImmediateHistoryGuard.class)
            .target("history")
          .selfTransition()
            .on("tog")
            .effect((e, c) -> c.immediateHistory = !c.immediateHistory)
            .target();
    
    history(builder, "history");
    
    builder
      .region()
        .state("A")
          .transition("t2")
            .on("toB")
            .target("B");
  
    builder
      .region()
        .state("B")
          .transition("t3")
            .on("toC")
            .target("C");
  
    builder
      .region()
        .state("C")
          .transition("t4")
            .on("toEnd")
            .target("end");
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return stdout;
  }
  
  private static final class Context extends SequentialContext {
    boolean immediateHistory;

    public Context(boolean immediateHistory) {
      this.immediateHistory = immediateHistory;
    }
    
  }
  
  public static final class ImmediateHistoryGuard implements BiTransitionConstraint<CompletionEvent, Context> {

    @Override
    public boolean test(CompletionEvent e, Context ctx) {
      return ctx.immediateHistory;
    }
    
  }

  @Override
  final void setStdOut(final PseudoStateKind historyKind) {
    stdout = "StateMachine: \"" + name() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"compo\"\n" +
"      Region: #8\n" +
"        PseudoState: \"history\" kind: " + historyKind + "\n" +
"        State: \"A\"\n" +
"        State: \"B\"\n" +
"        State: \"C\"\n" +
"        State: \"T\"\n" +
"        Transition: \"t1\" --- \"T\" -> \"A\"\n" +
"        Transition: \"t7\" --- \"T\" -> \"history\"\n" +
"        Transition: #12 -I- \"T\" -> \"T\"\n" +
"        Transition: \"t2\" --- \"A\" -> \"B\"\n" +
"        Transition: \"t3\" --- \"B\" -> \"C\"\n" +
"    State: \"P\"\n" +
"    Transition: \"t0\" --- #4 -> \"T\"\n" +
"    Transition: \"t5\" --- \"compo\" -> \"P\"\n" +
"    Transition: \"t4\" --- \"C\" -> \"end\"\n" +
"    Transition: \"t6\" --- \"P\" -> \"history\"";
  }

}
