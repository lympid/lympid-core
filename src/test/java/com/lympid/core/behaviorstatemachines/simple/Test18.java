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

package com.lympid.core.behaviorstatemachines.simple;

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import org.junit.Test;

/**
 * Tests multiple outgoing guarded transitions can be executed from a simple state.
 * The state machine auto starts.
 * @author Fabien Renaud 
 */
public class Test18 extends AbstractStateMachineTest {
  
  @Test
  public void run_NoWhere() {
    SequentialContext expected = new SequentialContext()
      .effect("t0");
    
    Context ctx = new Context();
    ctx.c = 0;
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("A").get());
    assertSequentialContextEquals(expected, ctx);
  }
  
  @Test
  public void run_T1() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t1");
    
    Context ctx = new Context();
    ctx.c = 1;
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("B").get());
    assertSequentialContextEquals(expected, ctx);
    
    fsm.take(new StringEvent("close"));
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("end").get());
  }
  
  @Test
  public void run_T2() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t2");
    
    Context ctx = new Context();
    ctx.c = 2;
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("B").get());
    assertSequentialContextEquals(expected, ctx);
    
    fsm.take(new StringEvent("close"));
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("end").get());
  }
  
  @Test
  public void run_T3() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t3");
    
    Context ctx = new Context();
    ctx.c = 3;
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("B").get());
    assertSequentialContextEquals(expected, ctx);
    
    fsm.take(new StringEvent("close"));
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("end").get());
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
            .guard((e, c) -> { return c.c == 1; })
            .target("B");
    
    builder
      .region()
        .state("A")
          .transition("t2")
            .guard((e, c) -> { return c.c == 2; })
            .target("B")
          .transition("t3")
            .guard((e, c) -> { return c.c == 3; })
            .target("B");
    
    builder
      .region()
        .state("B")
          .transition("t4")
            .on("close")
            .target(end);
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final class Context extends SequentialContext {
    
    int c;
    
    private Context() {
      withoutEntry();
      withoutExit();
    }
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test18.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    State: \"B\"\n" +
"    Transition: \"t0\" --- #4 -> \"A\"\n" +
"    Transition: \"t1\" --- \"A\" -> \"B\"\n" +
"    Transition: \"t2\" --- \"A\" -> \"B\"\n" +
"    Transition: \"t3\" --- \"A\" -> \"B\"\n" +
"    Transition: \"t4\" --- \"B\" -> \"end\"";
}
