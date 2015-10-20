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
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.TransitionBehavior;
import com.lympid.core.behaviorstatemachines.builder.ChoiceBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import com.lympid.core.behaviorstatemachines.impl.IllStateMachineException;
import org.junit.Test;

/**
 * Tests the choice pseudo state realizes dynamic conditional branching. 
 * This means that the effect fired on the incoming transition of the pseudo
 * state impacts which outgoing transition will be selected.
 * The state machine auto starts.
 * @author Fabien Renaud 
 */
public class Test4 extends AbstractStateMachineTest {

  private static int newValueForC;
  
  @Test
  public void run_end1() {
    newValueForC = 1;
    
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t1");
    
    /*
     * By default, context.c is equal to 0.
     * According to the state machine model, there is no transition for such case.
     * If the conditional branching is not dynamic, this test will therefore fail.
     */
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end1"));
    assertSequentialContextEquals(expected, fsm);
  }
    
  @Test
  public void run_end2() {
    newValueForC = 2;
    
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t2");
    
    /*
     * By default, context.c is equal to 0.
     * According to the state machine model, there is no transition for such case.
     * If the conditional branching is not dynamic, this test will therefore fail.
     */
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end2"));
    assertSequentialContextEquals(expected, fsm);
  }
  
  @Test
  public void run_end3() {
    newValueForC = 3;
    
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t3");
    
    /*
     * By default, context.c is equal to 0.
     * According to the state machine model, there is no transition for such case.
     * If the conditional branching is not dynamic, this test will therefore fail.
     */
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    /*
     * Machine has started and is on state A.
     */
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end3"));
    assertSequentialContextEquals(expected, fsm);
  }
    
  @Test(expected = IllStateMachineException.class)
  public void run_noStart() {
    newValueForC = 4;
    
    /*
     * By default, context.c is equal to 0.
     * According to the state machine model, there is no transition for such case.
     * If the conditional branching is not dynamic, this test will therefore fail.
     */
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    VertexBuilderReference<Context> end1 = builder
      .region()
        .finalState("end1");
    
    VertexBuilderReference<Context> end2 = builder
      .region()
        .finalState("end2");
    
    VertexBuilderReference<Context> end3 = builder
      .region()
        .finalState("end3");
        
    builder
      .region()
        .initial()
          .transition("t0")
            .effect((c) -> { c.c = newValueForC; })
            .target(new ChoiceBuilder<Context>()
              .transition("t1")
                .guard((c) -> { return c.c == 1; })
                .target(end1)
              .transition("t2")
                .guard((c) -> { return c.c == 2; })
                .target(end2)
              .transition()
                .guard((c) -> { return c.c == 3; })
                .effect(Transition3Effect.class)
                .target(end3)
            );
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  public static final class Context extends SequentialContext {
    int c;
  }
  
  public static final class Transition3Effect implements TransitionBehavior<Context> {

    @Override
    public void accept(Context t) {
      t.effect("t3");
    }
    
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test4.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end1\"\n" +
"    FinalState: \"end2\"\n" +
"    FinalState: \"end3\"\n" +
"    PseudoState: #6 kind: INITIAL\n" +
"    PseudoState: #8 kind: CHOICE\n" +
"    Transition: \"t0\" --- #6 -> #8\n" +
"    Transition: \"t1\" --- #8 -> \"end1\"\n" +
"    Transition: \"t2\" --- #8 -> \"end2\"\n" +
"    Transition: #11 --- #8 -> \"end3\"";
}
