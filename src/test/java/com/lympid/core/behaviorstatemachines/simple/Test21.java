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
import com.lympid.core.behaviorstatemachines.BiTransitionBehavior;
import com.lympid.core.behaviorstatemachines.BiTransitionConstraint;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.simple.Test21.Context;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;

/**
 * Tests two internal transitions on a simple state.
 * @author Fabien Renaud 
 */
public class Test21 extends AbstractStateMachineTest<Context> {
  
  @Test
  public void run_go() {
    run_tryN_go(0);
  }
  
  @Test
  public void run_try1_go() {
    run_tryN_go(1);
  }
  
  @Test
  public void run_try4_go() {
    run_tryN_go(4);
  }
  
  private void run_tryN_go(final int n) {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A");
    for (int i = 0; i < n; i++) {
      expected.effect("t1").effect("try");
      expected.effect("retry");
    }
    expected
      .exit("A").effect("t2");
    
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    /*
     * Machine has started and is on state A.
     */
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));
    
    for (int i = 0; i < n; i++) {
      fsm.take(new StringEvent("try"));
      assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));
      fsm.take(new StringEvent("retry"));
      assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));
    }
    
    /*
     * "go" event moves the state machine to its final state
     */
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    
    assertSequentialContextEquals(expected, fsm);
  }

  @Override
  public StateMachineBuilder<Context> topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
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
          .selfTransition("t1")
            .on("try")
            .guard(TryConstraint.class)
            .effect(TryEffect.class)
            .target()
          .selfTransition()
            .on(new StringEvent("retry"))
            .guardElse(TryConstraint.class)
            .effect(RetryEffect.class)
            .target()
          .transition("t2")
            .on(new StringEvent("go"))
            .target("end");
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  public static final class Context extends SequentialContext {
    private boolean lastIsTry;
  }
  
  public static final class TryConstraint implements BiTransitionConstraint<StringEvent, Context> {

    @Override
    public boolean test(StringEvent t, Context ctx) {
      return !ctx.lastIsTry;
    }

  }
  
  public static final class TryEffect implements BiTransitionBehavior<StringEvent, Context> {

    @Override
    public void accept(StringEvent t, Context ctx) {
      ctx.effect("try");
      ctx.lastIsTry = true;
    }
    
  }
  
  public static final class RetryEffect implements BiTransitionBehavior<StringEvent, Context> {

    @Override
    public void accept(StringEvent t, Context ctx) {
      ctx.effect("retry");
      ctx.lastIsTry = false;
    }
    
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test21.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    Transition: \"t0\" --- #4 -> \"A\"\n" +
"    Transition: \"t1\" -I- \"A\" -> \"A\"\n" +
"    Transition: #8 -I- \"A\" -> \"A\"\n" +
"    Transition: \"t2\" --- \"A\" -> \"end\"";
}
