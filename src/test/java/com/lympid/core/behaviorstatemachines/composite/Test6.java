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
 * Tests the same event fires the innermost transition.
 * @author Fabien Renaud 
 */
public class Test6 extends AbstractStateMachineTest {
  
  @Test
  public void model() {
    assertEquals(getClass().getSimpleName(), topLevelStateMachine().getName());
    Region region = StateMachineTester.assertTopLevelStateMachine(topLevelStateMachine());

    StateMachineTester.assertRegion(region, 5, 5, 
      new VertexTest[]{
        new InitialPseudoStateTest("#4"),
        new SimpleStateTest("A"),
        new VertexTest("B", this::verifyB),
        new SimpleStateTest("C"),
        new FinalStateTest("#3")
      },
      new TransitionTest[]{
        new TransitionTest("#5", "#4", "A"),
        new TransitionTest("t1", "A", "Ba"),
        new TransitionTest("#9", "B", "B", TransitionKind.INTERNAL),
        new TransitionTest("t3", "B", "C"),
        new TransitionTest("t4", "C", "#3")
      }
    );
  }
  
  private void verifyB(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    StateMachineTester.assertRegions(s.region(), 1, 
      new RegionTest("11", null, 2, 1, 
        new VertexTest[]{
          new SimpleStateTest("Ba"),
          new FinalStateTest("rend")
        },
        new TransitionTest[]{
          new TransitionTest("t2", "Ba", "rend")
        }
      )
    );
  }
  
  @Test
  public void run() {
    SequentialContext expected = new SequentialContext()
      .enter("A")
      .exit("A").effect("t1").enter("B").enter("Ba")
      .exit("Ba").effect("t2").exit("B").effect("t3").enter("C")
      .exit("C").effect("t4");
    
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("B", "Ba").get());
    
    fsm.take(new StringEvent("let"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("B", "Ba").get());
    assertEquals(1, ctx.c);
    
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("B", "rend").get());
    
    fsm.take(new StringEvent("let"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("B", "rend").get());
    assertEquals(2, ctx.c);
    
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("#3").get());
    
    assertSequentialContextEquals(expected, ctx);
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder(name());

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
            .target("Ba");
    
    builder
      .region()
        .state(compositeB(builder, "B"))
          .selfTransition()
            .on(new StringEvent("let"))
            .effect((e, c) -> c.c++)
            .target()
          .transition("t3")
            .on("go")
            .target("C");

    builder
      .region()
        .state("C")
          .transition("t4")
            .target(end);

    return builder;
  }
  
  private CompositeStateBuilder<Context> compositeB(final StateMachineBuilder b, final String name) {
    CompositeStateBuilder builder = new CompositeStateBuilder(name);
      
    VertexBuilderReference end = builder
      .region()
        .finalState("rend");
        
    builder
      .region()
        .state("Ba")
          .transition("t2")
            .on("go")
            .target(end);
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final class Context extends SequentialContext {
    int c;
  }

  private static final String STDOUT = "StateMachine: \"" + Test6.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: #3\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"C\"\n" +
"    State: \"A\"\n" +
"    State: \"B\"\n" +
"      Region: #11\n" +
"        FinalState: \"rend\"\n" +
"        State: \"Ba\"\n" +
"        Transition: \"t2\" --- \"Ba\" -> \"rend\"\n" +
"    Transition: #5 --- #4 -> \"A\"\n" +
"    Transition: \"t1\" --- \"A\" -> \"Ba\"\n" +
"    Transition: #9 -I- \"B\" -> \"B\"\n" +
"    Transition: \"t3\" --- \"B\" -> \"C\"\n" +
"    Transition: \"t4\" --- \"C\" -> #3";
}
