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
package com.lympid.core.behaviorstatemachines.submachine;

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.BiTransitionConstraint;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests chaining the same sub state machine with no entry/exit points.
 * 
 * @author Fabien Renaud 
 */
public class Test3 extends AbstractStateMachineTest {
  
  @Test
  public void run() {
    int counter = 0;
    
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);    
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("sub1", "A"));
    assertEquals(counter, ctx.c);
    
    fsm.take(new StringEvent("dec"));
    assertStateConfiguration(fsm, new ActiveStateTree("sub1", "A"));
    assertEquals(counter, ctx.c);
    for (int i = 0; i < 4; i++) {
      fsm.take(new StringEvent("inc"));
      assertStateConfiguration(fsm, new ActiveStateTree("sub1", "A"));
      assertEquals(++counter, ctx.c);
    }
    
    fsm.take(new StringEvent("go"));
    assertStateConfiguration(fsm, new ActiveStateTree("sub2", "A"));
    
    fsm.take(new StringEvent("inc"));
    assertStateConfiguration(fsm, new ActiveStateTree("sub2", "A"));
    assertEquals(counter, ctx.c);
    for (int i = 0; i < 7; i++) {
      fsm.take(new StringEvent("dec"));
      assertStateConfiguration(fsm, new ActiveStateTree("sub2", "A"));
      if (counter >= 0) {
        --counter;
      }
      assertEquals(counter, ctx.c);
    }
    
    fsm.take(new StringEvent("go"));    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder(name());
    
    builder
      .region()
        .initial("init")
          .transition("t0")
            .target("sub1");
    
    StateMachineBuilder<Context> subMachine = subStateMachine("sub");
    
    builder
      .region()
        .state(subMachine, "sub1")
          .transition("t1")
            .target("sub2")
          .selfTransition("t2")
            .on("inc")
            .guard((e,c) -> c.c >= 0)
            .effect((e, c) -> c.c++)
            .target();
    
    builder
      .region()
        .state(subMachine, "sub2")
          .transition()
            .target("end")
          .selfTransition()
            .on("dec")
            .guard(DecrementTransitionGuard.class)
            .effect((e, c) -> c.c--)
            .target();
    
    builder
      .region()
        .finalState("end");
    
    return builder;
  }
  
  private StateMachineBuilder<Context> subStateMachine(final String name) {
    StateMachineBuilder<Context> builder = new StateMachineBuilder(name);
    
    builder
      .region()
        .initial("init")
          .transition("t0")
            .target("A");
    
    builder
      .region()
        .state("A")
          .transition("t1")
            .on("go")
            .target("end");
    
    builder
      .region()
        .finalState("end");
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final class Context {
    int c;
  }
  
  public static final class DecrementTransitionGuard implements BiTransitionConstraint<StringEvent, Context>  {

    @Override
    public boolean test(StringEvent t, Context ctx) {
      return ctx.c >= 0;
    }
    
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test3.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: \"init\" kind: INITIAL\n" +
"    State: \"sub1\"\n" +
"      StateMachine: \"sub\"\n" +
"        Region: #13\n" +
"          PseudoState: \"init\" kind: INITIAL\n" +
"          State: \"A\"\n" +
"          FinalState: \"end\"\n" +
"          Transition: \"t0\" --- \"init\" -> \"A\"\n" +
"          Transition: \"t1\" --- \"A\" -> \"end\"\n" +
"    State: \"sub2\"\n" +
"      StateMachine: \"sub\"\n" +
"        Region: #21\n" +
"          PseudoState: \"init\" kind: INITIAL\n" +
"          State: \"A\"\n" +
"          FinalState: \"end\"\n" +
"          Transition: \"t0\" --- \"init\" -> \"A\"\n" +
"          Transition: \"t1\" --- \"A\" -> \"end\"\n" +
"    Transition: \"t0\" --- \"init\" -> \"sub1\"\n" +
"    Transition: \"t1\" --- \"sub1\" -> \"sub2\"\n" +
"    Transition: \"t2\" -I- \"sub1\" -> \"sub1\"\n" +
"    Transition: #9 --- \"sub2\" -> \"end\"\n" +
"    Transition: #10 -I- \"sub2\" -> \"sub2\"";
}
