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

package com.lympid.core.behaviorstatemachines.pseudo.junction;

import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.TransitionConstraint;
import com.lympid.core.behaviorstatemachines.builder.JunctionBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import org.junit.Test;

/**
 * Tests transition default ordering with a junction pseudo state.
 * The default ordering is the order in which the transitions are coded.
 * In this test, the transitions are coded in the following order:
 *  - t1: [ c = 2 ]
 *  - t2: [ c % 2 = 0 ]
 *  - t3: else [ c % 2 = 0 ]
 * Ordering is the default one, i.e.: t1, t2, t3
 * All transitions can be fired.
 * 
 * @author Fabien Renaud
 */
public class Test3 extends AbstractStateMachineTest {
  
  @Test
  public void run_t1() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t1");
    
    Context ctx = new Context(2);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
  }
  
  @Test
  public void run_t2() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t2");
    
    Context ctx = new Context(4);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
  }
    
  @Test
  public void run_t3() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t3");
    
    Context ctx = new Context(1);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    builder
      .region()
        .finalState("end");
        
    builder
      .region()
        .initial()
          .transition("t0")
            .target(new JunctionBuilder<Context>()
              .transition("t1")
                .guard((c) -> { return c.c == 2; })
                .target("end")
              .transition("t2")
                .guard(IsMod2.class)
                .target("end")
              .transition("t3")
                .guardElse(IsMod2.class)
                .target("end")
            );
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final class Context extends SequentialContext {
    int c;
    
    private Context(final int c) {
      this.c = c;
    }
  }
  
  public static final class IsMod2 implements TransitionConstraint<Context> {

    @Override
    public boolean test(final Context ctx) {
      return ctx.c % 2 == 0;
    }
    
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test3.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    PseudoState: #6 kind: JUNCTION\n" +
"    Transition: \"t0\" --- #4 -> #6\n" +
"    Transition: \"t1\" --- #6 -> \"end\"\n" +
"    Transition: \"t2\" --- #6 -> \"end\"\n" +
"    Transition: \"t3\" --- #6 -> \"end\"";
}
