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
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.TransitionTest;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexTest;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests a local transition which source is the composite state and target is a
 * substate of the composite state.
 *
 * @author Fabien Renaud 
 */
public class Test17 extends AbstractStateMachineTest {

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
        new TransitionTest("#5", "#4", "A"),
        new TransitionTest("#8", "A", "#3")
      }
    );
  }

  private void verifyA(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    StateMachineTester.assertRegions(s.region(), 1,
      new RegionTest("9", null, 4, 3,
        new VertexTest[]{
          new InitialPseudoStateTest("#11"),
          new SimpleStateTest("Aa"),
          new SimpleStateTest("Ab"),
          new FinalStateTest("#10")
        },
        new TransitionTest[]{
          new TransitionTest("#7", "A", "Ab", TransitionKind.LOCAL),
          new TransitionTest("#12", "#11", "Aa"),
          new TransitionTest("#14", "Aa", "#10")
        }
      )
    );
  }

  @Test
  public void run_try_go() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .effect("t1").enter("Aa")
      .exit("Aa").effect("t3").enter("Ab")
      .exit("Ab").exit("A").effect("t4");

    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();

    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "Aa"));

    fsm.take(new StringEvent("try"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "Ab"));

    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("#3"));

    assertSequentialContextEquals(expected, fsm);
  }

  @Test
  public void run_let_go() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .effect("t1").enter("Aa")
      .exit("Aa").effect("t2")
      .exit("A").effect("t4");

    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();

    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "Aa"));

    fsm.take(new StringEvent("let"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "#10"));

    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("#3"));

    assertSequentialContextEquals(expected, fsm);
  }

  @Test
  public void run_let_try_go() {
    run_let_tryN_go(1);
  }

  @Test
  public void run_let_try2_go() {
    run_let_tryN_go(2);
  }

  private void run_let_tryN_go(final int n) {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .effect("t1").enter("Aa")
      .exit("Aa").effect("t2");
    for (int i = 0; i < n; i++) {
      expected.effect("t3").enter("Ab").exit("Ab");
    }
    expected.exit("A").effect("t4");

    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();

    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "Aa"));

    fsm.take(new StringEvent("let"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "#10"));

    for (int i = 0; i < n; i++) {
      fsm.take(new StringEvent("try"));
      assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "Ab"));
    }

    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("#3"));

    assertSequentialContextEquals(expected, fsm);
  }

  @Test
  public void run_try_let_go() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .effect("t1").enter("Aa")
      .exit("Aa").effect("t3").enter("Ab")
      .exit("Ab").exit("A").effect("t4");

    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();

    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "Aa"));

    fsm.take(new StringEvent("try"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "Ab"));

    /*
     * let event has no effect because there is no active state that has an
     * outgoing transition accepting the 'let' event.
     */
    fsm.take(new StringEvent("let"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "Ab"));

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
          .transition("t0")
            .target("A");

    builder
      .region()
        .state(compositeA(builder, "A"))
          .transition("t4")
            .on("go")
            .target(end);

    return builder;
  }

  private CompositeStateBuilder compositeA(final StateMachineBuilder b, final String name) {
    CompositeStateBuilder builder = new CompositeStateBuilder(name);

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
            .on("let")
            .target(rend);

    builder
      .localTransition("t3")
        .on("try")
          .target("Ab");

    builder
      .region()
        .state("Ab");

    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }

  private static final String STDOUT = "StateMachine: \"" + Test17.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: #3\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"      Region: #9\n" +
"        PseudoState: #11 kind: INITIAL\n" +
"        State: \"Aa\"\n" +
"        State: \"Ab\"\n" +
"        FinalState: #10\n" +
"        Transition: \"t3\" -L- \"A\" -> \"Ab\"\n" +
"        Transition: \"t1\" --- #11 -> \"Aa\"\n" +
"        Transition: \"t2\" --- \"Aa\" -> #10\n" +
"    Transition: \"t0\" --- #4 -> \"A\"\n" +
"    Transition: \"t4\" --- \"A\" -> #3";
}
