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

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.TransitionConstraint;
import com.lympid.core.behaviorstatemachines.builder.ChoiceBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import org.junit.Test;

/**
 * Tests a choice pseudo state coming from a simple state.
 * The state machine auto starts.
 * @author Fabien Renaud 
 */
public class Test2 extends AbstractStateMachineTest {
    
  @Test
  public void run_B() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .exit("A").effect("t1")
      .effect("t11").enter("B")
      .exit("B").effect("t2");
    
    Context ctx = new Context();
    ctx.c = 0;
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A").get());
    
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end").get());
    assertSequentialContextEquals(expected, ctx);
  }
    
  @Test
  public void run_C() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .exit("A").effect("t1")
      .effect("t12").enter("C")
      .exit("C").effect("t3");
    
    Context ctx = new Context();
    ctx.c = -1;
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A").get());
    
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end").get());
    assertSequentialContextEquals(expected, ctx);
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    VertexBuilderReference end = builder
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
            .on("go")
            .target(new ChoiceBuilder<Context>()
              .transition("t11")
                .guard(PositiveC.class)
                .target("B")
              .transition("t12")
                .guardElse(PositiveC.class)
                .target("C")
            );
    
    builder
      .region()
        .state("B")
          .transition("t2")
            .target(end);
    
    builder
      .region()
        .state("C")
          .transition("t3")
            .target(end);
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  public static final class Context extends SequentialContext {
    int c;
  }
  
  public static final class PositiveC implements TransitionConstraint<Context> {

    @Override
    public boolean test(final Context ctx) {
      return ctx.c >= 0;
    }
    
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test2.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #12 kind: CHOICE\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    State: \"B\"\n" +
"    State: \"C\"\n" +
"    Transition: \"t0\" --- #4 -> \"A\"\n" +
"    Transition: \"t1\" --- \"A\" -> #12\n" +
"    Transition: \"t2\" --- \"B\" -> \"end\"\n" +
"    Transition: \"t3\" --- \"C\" -> \"end\"\n" +
"    Transition: \"t11\" --- #12 -> \"B\"\n" +
"    Transition: \"t12\" --- #12 -> \"C\"";
}
