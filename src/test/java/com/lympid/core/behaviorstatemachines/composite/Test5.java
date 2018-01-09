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
import com.lympid.core.behaviorstatemachines.StateMachineSnapshot;
import com.lympid.core.behaviorstatemachines.StateMachineTester;
import com.lympid.core.behaviorstatemachines.TransitionTest;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexTest;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import com.lympid.core.behaviorstatemachines.composite.Test5.Context;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests the final state of a composite state triggers a completion event and
 * leaves the composite state when appropriate.
 * The state machine auto starts.
 * @author Fabien Renaud 
 */
public class Test5 extends AbstractStateMachineTest<Context> {
  
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
        new TransitionTest("#7", "A", "Ba"),
        new TransitionTest("#9", "B", "C"),
        new TransitionTest("#15", "C", "#3")
      }
    );
  }
  
  private void verifyB(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    StateMachineTester.assertRegions(s.region(), 1, 
      new RegionTest("10", null, 2, 1, 
        new VertexTest[]{
          new SimpleStateTest("Ba"),
          new FinalStateTest("rend")
        },
        new TransitionTest[]{
          new TransitionTest("#13", "Ba", "rend")
        }
      )
    );
  }
  
  @Test
  public void run() {
    run(false);
  }
  
  @Test
  public void run_pauseAndResume() {
    run(true);
  }
  
  private void run(final boolean pause) {
    SequentialContext expected = new SequentialContext()
      .enter("A")
      .exit("A").effect("t1").enter("B").enter("Ba")
      .exit("Ba").effect("t2").exit("B").effect("t3").enter("C")
      .exit("C").effect("t4");

    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    ActiveStateTree active = new ActiveStateTree(this).branch("B", "Ba");
    assertSnapshotEquals(fsm, active);
    
    if (pause) {
      fsm.pause();
      StateMachineSnapshot<Context> snapshot = fsm.snapshot();

      fsm.take(new StringEvent("finishIt"));
      assertSnapshotEquals(fsm, active);
      
      resume(fsm, expected);
      resume(snapshot, expected);
    } else {
      finishIt(fsm, expected);
    }
  }
  
  private void resume(final StateMachineExecutor<Context> fsm, final SequentialContext expected) {
    fsm.resume();
    finishIt(fsm, expected);
  }
  
  private void resume(final StateMachineSnapshot<Context> snapshot, final SequentialContext expected) {
    resume(fsm(snapshot), expected);
  }

  private void finishIt(StateMachineExecutor<Context> fsm, SequentialContext expected) {
    fsm.take(new StringEvent("finishIt"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("#3"));

    assertSequentialContextEquals(expected, fsm);
  }
  
  @Override
  public StateMachineBuilder<Context> topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());

    VertexBuilderReference<Context> end = builder
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
            .target("Ba");
    
    builder
      .region()
        .state(compositeB(builder, "B"))
          .transition("t3")
            .target("C");

    builder
      .region()
        .state("C")
          .transition("t4")
            .target(end);

    return builder;
  }
  
  private CompositeStateBuilder<Context> compositeB(final StateMachineBuilder<Context> b, final String name) {
    CompositeStateBuilder<Context> builder = new CompositeStateBuilder<>(name);
      
    VertexBuilderReference<Context> end = builder
      .region()
        .finalState("rend");
        
    builder
      .region()
        .state("Ba")
          .transition("t2")
            .on("finishIt")
            .target(end);
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }

  public static final class Context extends SequentialContext {
  }

  private static final String STDOUT = "StateMachine: \"" + Test5.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: #3\n" +
"    State: \"C\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    State: \"B\"\n" +
"      Region: #10\n" +
"        FinalState: \"rend\"\n" +
"        State: \"Ba\"\n" +
"        Transition: \"t2\" --- \"Ba\" -> \"rend\"\n" +
"    Transition: #5 --- #4 -> \"A\"\n" +
"    Transition: \"t1\" --- \"A\" -> \"Ba\"\n" +
"    Transition: \"t3\" --- \"B\" -> \"C\"\n" +
"    Transition: \"t4\" --- \"C\" -> #3";
}
