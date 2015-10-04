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
import com.lympid.core.behaviorstatemachines.StateMachineSnapshot;
import com.lympid.core.behaviorstatemachines.StateMachineTester;
import com.lympid.core.behaviorstatemachines.TransitionKind;
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
 * Tests a local transition which has the same composite state as a source
 * and target.
 *
 * @author Fabien Renaud 
 */
public class Test20 extends AbstractStateMachineTest {
  
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
        new TransitionTest("#8", "A", "#3"),
        new TransitionTest("#16", "A_exitPoint", "#3")
      }
    );
  }
  
  private void verifyA(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    StateMachineTester.assertRegions(s.region(), 1, 
      new RegionTest("9", null, 3, 3, 
        new VertexTest[]{
          new InitialPseudoStateTest("#11"),
          new SimpleStateTest("Aa"),
          new FinalStateTest("#10")
        },
        new TransitionTest[]{
          new TransitionTest("#7", "A", "A_exitPoint", TransitionKind.LOCAL),
          new TransitionTest("#12", "#11", "Aa"),
          new TransitionTest("#14", "Aa", "#10")
        }
      )
    );
  }
  
  @Test
  public void run_let() {
    run_let(false);
  }
  
  @Test
  public void run_let_pauseAndResume() {
    run_let(true);
  }
  
  private void run_let(final boolean pause) {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .effect("t1").enter("Aa")
      .exit("Aa").effect("t3")
      .exit("A").effect("t4");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor<SequentialContext> fsm = fsm(ctx);
    fsm.go();
    
    ActiveStateTree active = new ActiveStateTree(this).branch("A", "Aa");
    assertSnapshotEquals(fsm, active);
    
    if (pause) {
      StateMachineSnapshot snapshot = fsm.pause();
      
      fsm.take(new StringEvent("let"));
      assertSnapshotEquals(fsm, active);
      
      resume(fsm, expected);
      resume(snapshot, expected);
    } else {
      letEnd(fsm, expected);
    }
  }
  
  private void resume(final StateMachineExecutor<SequentialContext> fsm, final SequentialContext expected) {
    fsm.resume();
    letEnd(fsm, expected);
  }
  
  private void resume(final StateMachineSnapshot snapshot, final SequentialContext expected) {
    resume(fsm(snapshot), expected);
  }

  private void letEnd(StateMachineExecutor<SequentialContext> fsm, SequentialContext expected) {
    fsm.take(new StringEvent("let"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));

    assertSequentialContextEquals(expected, fsm);
  }
  
  @Test
  public void run_go_end() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .effect("t1").enter("Aa")
      .exit("Aa").effect("t2")
      .exit("A").effect("t5");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "Aa"));
        
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "#10"));
    
    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    
    assertSequentialContextEquals(expected, fsm);
  }
  
  @Test
  public void run_go_let() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .effect("t1").enter("Aa")
      .exit("Aa").effect("t2")
      .effect("t3")
      .exit("A").effect("t4");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "Aa"));
        
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "#10"));
    
    letEnd(fsm, expected);
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Object> builder = new StateMachineBuilder(name());

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
        .state(compositeA("A"))
          .localTransition("t3")
            .on("let")
            .target("A_exitPoint")
          .transition("t5")
            .on("end")
            .target("end");

    return builder;
  }
  
  private CompositeStateBuilder<Object> compositeA(final String name) {
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
            .on("go")
            .target(rend);
    
    builder
      .connectionPoint()
        .exitPoint(new ExitPointBuilder<>("A_exitPoint")
          .transition("t4")
            .target("end")
        );
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }

  private static final String STDOUT = "StateMachine: \"" + Test20.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"      ExitPoint: \"A_exitPoint\"\n" +
"      Region: #9\n" +
"        PseudoState: #11 kind: INITIAL\n" +
"        State: \"Aa\"\n" +
"        FinalState: #10\n" +
"        Transition: \"t3\" -L- \"A\" -> \"A_exitPoint\"\n" +
"        Transition: \"t1\" --- #11 -> \"Aa\"\n" +
"        Transition: \"t2\" --- \"Aa\" -> #10\n" +
"    Transition: \"t0\" --- #4 -> \"A\"\n" +
"    Transition: \"t5\" --- \"A\" -> \"end\"\n" +
"    Transition: \"t4\" --- \"A_exitPoint\" -> \"end\"";
}
