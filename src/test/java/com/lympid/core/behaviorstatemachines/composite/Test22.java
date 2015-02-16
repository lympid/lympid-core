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
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.TransitionTest;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexTest;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.EntryPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.ExitPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests entry/exit points with simple states.
 * The state machine auto starts.
 * @author Fabien Renaud 
 */
public class Test22 extends AbstractStateMachineTest {
  
  @Test
  public void model() {
    assertEquals(getClass().getSimpleName(), topLevelStateMachine().getName());
    Region region = StateMachineTester.assertTopLevelStateMachine(topLevelStateMachine());

    StateMachineTester.assertRegion(region, 5, 5, 
      new VertexTest[]{
        new InitialPseudoStateTest("#4"),
        new SimpleStateTest("A"),
        new VertexTest("#9", this::verifyComposite),
        new SimpleStateTest("C"),
        new FinalStateTest("#3")
      },
      new TransitionTest[]{
        new TransitionTest("#5", "#4", "A"),
        new TransitionTest("#7", "A", "B_entryPoint"),
        new TransitionTest("#8", "A", "A"),
        new TransitionTest("#19", "B_exitPoint", "C"),
        new TransitionTest("#22", "C", "#3")
      }
    );
  }
  
  private void verifyComposite(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    StateMachineTester.assertRegions(s.region(), 1, new RegionTest("10", null, 1, 4, 
      new VertexTest[] {
        new SimpleStateTest("Ba")
      },
      new TransitionTest[] {
        new TransitionTest("#12", "Ba", "B_exitPoint"),
        new TransitionTest("#13", "Ba", "Ba"),
        new TransitionTest("#14", "Ba", "Ba", TransitionKind.INTERNAL),
        new TransitionTest("#16", "B_entryPoint", "Ba", TransitionKind.LOCAL)
      }
    ));
  }
  
  @Test
  public void run_0() {
    run(0);
  }
  
  @Test
  public void run_1() {
    run(1);
  }
  
  @Test
  public void run_2() {
    run(2);
  }
  
  @Test
  public void run_5() {
    run(5);
  }
  
  @Test
  public void run_neg1() {
    run(-1);
  }
  
  @Test
  public void run_neg2() {
    run(-2);
  }
  
  @Test
  public void run_neg5() {
    run(-5);
  }
  
  private void run(int c) {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A");
    
    Context ctx = new Context(c);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    if (ctx.c >= 0) {
      ActiveStateTree tree = new ActiveStateTree(this).branch("A");
      assertStateConfiguration(fsm, tree);
      assertSequentialContextEquals(expected, ctx);

      while (ctx.c > 0) {
        fsm.take(new StringEvent("dec"));
        expected.exit("A").effect("t7").enter("A");
        assertStateConfiguration(fsm, tree);
        assertSequentialContextEquals(expected, ctx);
      }

      fsm.take(new StringEvent("dec"));
      expected.exit("A").effect("t7").enter("A");
    }
    
    expected.exit("A").effect("t1").enter("B").effect("t2").enter("Ba");
    ActiveStateTree tree = new ActiveStateTree(this).branch("#9", "Ba");
    assertStateConfiguration(fsm, tree);
    assertSequentialContextEquals(expected, ctx);
    
    while (ctx.c <= 0) {
      fsm.take(new StringEvent("go")); // has no effect
      assertStateConfiguration(fsm, tree);
      assertSequentialContextEquals(expected, ctx);
      
      fsm.take(new StringEvent("inc"));
      expected.exit("Ba").effect("t6").enter("Ba");
      assertStateConfiguration(fsm, tree);
      assertSequentialContextEquals(expected, ctx);
      
      fsm.take(new StringEvent("dec"));
      expected.effect("t7");
      assertStateConfiguration(fsm, tree);
      assertSequentialContextEquals(expected, ctx);
      
      fsm.take(new StringEvent("inc"));
      expected.exit("Ba").effect("t6").enter("Ba");
      assertStateConfiguration(fsm, tree);
      assertSequentialContextEquals(expected, ctx);
    }
    
    fsm.take(new StringEvent("go"));
    expected
      .exit("Ba").effect("t3").exit("B").effect("t4").enter("C")
      .exit("C").effect("t5");
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("end").get());
    assertSequentialContextEquals(expected, ctx);
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
            .target("A");

    builder
      .region()
        .state("A")
          .transition("t1")
            .target("B_entryPoint")
          .transition("t7")
            .on("dec")
            .effect((e, c) -> { c.c--; })
            .target("A");
    
    builder
      .region()
        .state(composite());

    builder
      .region()
        .state("C")
          .transition("t5")
            .target("end");

    return builder;
  }
  
  private CompositeStateBuilder composite() {
    CompositeStateBuilder<Context> builder = new CompositeStateBuilder();
    
    builder
      .entry((c) -> c.enter("B"))
      .exit((c) -> c.exit("B"));
        
    builder
      .connectionPoint()
        .entryPoint(new EntryPointBuilder<Context>("B_entryPoint")
          .transition()
            .guard((c) -> { return c.c < 0; })
            .effect((c) -> { c.effect("t2"); })
            .target("Ba"))
        .exitPoint(new ExitPointBuilder<Context>("B_exitPoint")
          .transition()
            .guard((c) -> { return c.c > 0; })
            .effect((c) -> { c.effect("t4"); })
            .target("C"));
    
    builder
      .region()
        .state("Ba")
          .transition("t3")
            .on("go")
            .target("B_exitPoint")
          .transition("t6")
            .on("inc")
            .effect((e, c) -> { c.c++; })
            .target("Ba")
          .selfTransition()
            .on("dec")
            .effect((e, c) -> { c.effect("t7"); c.c--; })
            .target();
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final class Context extends SequentialContext {
    int c;
    
    Context(final int c) {
      this.c = c;
    }
    
    Context() {
    }
  }

  private static final String STDOUT = "StateMachine: \"" + Test22.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    State: #9\n" +
"      EntryPoint: \"B_entryPoint\"\n" +
"      ExitPoint: \"B_exitPoint\"\n" +
"      Region: #10\n" +
"        State: \"Ba\"\n" +
"        Transition: \"t3\" --- \"Ba\" -> \"B_exitPoint\"\n" +
"        Transition: \"t6\" --- \"Ba\" -> \"Ba\"\n" +
"        Transition: #14 -I- \"Ba\" -> \"Ba\"\n" +
"        Transition: #16 -L- \"B_entryPoint\" -> \"Ba\"\n" +
"    State: \"C\"\n" +
"    Transition: \"t0\" --- #4 -> \"A\"\n" +
"    Transition: \"t1\" --- \"A\" -> \"B_entryPoint\"\n" +
"    Transition: \"t7\" --- \"A\" -> \"A\"\n" +
"    Transition: #19 --- \"B_exitPoint\" -> \"C\"\n" +
"    Transition: \"t5\" --- \"C\" -> \"end\"";
}
