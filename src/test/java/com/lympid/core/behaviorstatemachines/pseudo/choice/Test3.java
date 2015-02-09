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

package com.lympid.core.behaviorstatemachines.pseudo.choice;

import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.TransitionConstraint;
import com.lympid.core.behaviorstatemachines.builder.ChoiceBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import org.junit.Test;

/**
 * Tests a cascade of choice pseudo states outgoing the initial pseudo state.
 * The state machine auto starts.
 * @author Fabien Renaud
 */
public class Test3 extends AbstractStateMachineTest {
    
  @Test
  public void run_A() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t1")
      .effect("t11").enter("A")
      .exit("A").effect("Aend");
    
    Context ctx = new Context();
    ctx.c = 0;
    ctx.d = 0;
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
  }
    
  @Test
  public void run_B() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t1")
      .effect("t12").enter("B")
      .exit("B").effect("Bend");
    
    Context ctx = new Context();
    ctx.c = 0;
    ctx.d = -1;
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
  }
    
  @Test
  public void run_C() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t2")
      .effect("t21").enter("C")
      .exit("C").effect("Cend");
    
    Context ctx = new Context();
    ctx.c = -1;
    ctx.d = -1;
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
  }
    
  @Test
  public void run_D() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t2")
      .effect("t22").enter("D")
      .exit("D").effect("Dend");
    
    Context ctx = new Context();
    ctx.c = -1;
    ctx.d = 1;
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
  }
    
  @Test
  public void run_E() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t2")
      .effect("t23").enter("E")
      .exit("E").effect("Eend");
    
    Context ctx = new Context();
    ctx.c = -1;
    ctx.d = 2;
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    VertexBuilderReference end = builder
      .region()
        .finalState("end");
    
    ChoiceBuilder choice1 = new ChoiceBuilder<Context>()
      .transition("t11")
        .guard(PositiveD.class)
        .target("A")
      .transition("t12")
        .guardElse(PositiveD.class)
        .target("B");
    
    ChoiceBuilder choice2 = new ChoiceBuilder<Context>()
      .transition("t21")
        .guard((c) -> { return c.d != 1 && c.d != 2; })
        .target("C")
      .transition("t22")
        .guard((c) -> { return c.d == 1; })
        .target("D")
      .transition("t23")
        .guard((c) -> { return c.d == 2; })
        .target("E");
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target(new ChoiceBuilder<Context>()
              .transition("t1")
                .guard(PositiveC.class)
                .target(choice1)
              .transition("t2")
                .guardElse(PositiveC.class)
                .target(choice2)
            );
    
    builder
      .region()
        .state("A")
          .transition("Aend")
            .target(end);
    
    builder
      .region()
        .state("B")
          .transition("Bend")
            .target(end);
    
    builder
      .region()
        .state("C")
          .transition("Cend")
            .target(end);
    
    builder
      .region()
        .state("D")
          .transition("Dend")
            .target(end);
    
    builder
      .region()
        .state("E")
          .transition("Eend")
            .target(end);
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  public static final class Context extends SequentialContext {
    int c;
    int d;
  }
  
  public static final class PositiveC implements TransitionConstraint<Context> {

    @Override
    public boolean test(final Context ctx) {
      return ctx.c >= 0;
    }
    
  }
  
  public static final class PositiveD implements TransitionConstraint<Context> {

    @Override
    public boolean test(final Context ctx) {
      return ctx.d >= 0;
    }
    
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test3.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #22 kind: CHOICE\n" +
"    State: \"D\"\n" +
"    FinalState: \"end\"\n" +
"    State: \"E\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    PseudoState: #16 kind: CHOICE\n" +
"    State: \"A\"\n" +
"    State: \"B\"\n" +
"    PseudoState: #19 kind: CHOICE\n" +
"    State: \"C\"\n" +
"    Transition: \"t0\" --- #4 -> #16\n" +
"    Transition: \"Aend\" --- \"A\" -> \"end\"\n" +
"    Transition: \"Bend\" --- \"B\" -> \"end\"\n" +
"    Transition: \"Cend\" --- \"C\" -> \"end\"\n" +
"    Transition: \"Dend\" --- \"D\" -> \"end\"\n" +
"    Transition: \"Eend\" --- \"E\" -> \"end\"\n" +
"    Transition: \"t1\" --- #16 -> #19\n" +
"    Transition: \"t2\" --- #16 -> #22\n" +
"    Transition: \"t11\" --- #19 -> \"A\"\n" +
"    Transition: \"t12\" --- #19 -> \"B\"\n" +
"    Transition: \"t21\" --- #22 -> \"C\"\n" +
"    Transition: \"t22\" --- #22 -> \"D\"\n" +
"    Transition: \"t23\" --- #22 -> \"E\"";
}
