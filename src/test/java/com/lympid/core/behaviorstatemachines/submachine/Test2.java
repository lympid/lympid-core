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
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.builder.EntryPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.SubMachineStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.SubStateMachineBuilder;
import com.lympid.core.behaviorstatemachines.submachine.Test2.Context;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Fabien Renaud 
 */
public class Test2 extends AbstractStateMachineTest<Context> {
  
  @Test
  public void run0() {
    run(0);
  }
  
  @Test
  public void run1() {
    run(1);
  }

  @Test
  public void runNegative1() {
    run(-1);
  }
  
  @Test
  public void run3() {
    run(3);
  }

  @Test
  public void runNegative5() {
    run(-5);
  }
  
  private void run(final int c) {
    Context ctx = new Context(c);
    StateMachineExecutor<Context> fsm = fsm(ctx);
    
    assertFalse(ctx.enteredSubMachine);
    assertFalse(ctx.exitedSubMachine);
    fsm.go();
    
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("sub")
      .effect("t0").enter("A");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("sub", "A"));
    assertTrue(ctx.enteredSubMachine);
    assertFalse(ctx.exitedSubMachine);
    
    for (int i = c; i <= 0; i++) {
      fsm.take(new StringEvent("go"));
      expected
        .exit("A").effect("t1").exit("sub")
        .effect("t1").enter("sub").effect("t2").enter("A");
      assertSequentialContextEquals(expected, fsm);
      assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("sub", "A"));
      assertTrue(ctx.enteredSubMachine);
      assertTrue(ctx.exitedSubMachine);
    }
    
    fsm.take(new StringEvent("go"));
    expected
      .exit("A").effect("t1").exit("sub")
      .effect("t2");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertTrue(ctx.enteredSubMachine);
    assertTrue(ctx.exitedSubMachine);
  }

  @Override
  public StateMachineBuilder<Context> topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    builder
      .region()
        .initial("init");
    
    builder
      .region()
        .initial("init")
          .transition("t0")
            .target("sub");
    
    SubMachineStateBuilder<Context> subState = builder
      .region()
        .state(subStateMachine("sub"));
    
    subState
      .entry(EntrySubMachineBehavior.class)
      .exit(ExitSubMachineBehavior.class);
    
    subState
      .connectionPoint()
        .exitPoint("exitPoint")
          .transition("t1")
            .guard(c -> c.c <= 0)
            .target("sub::entryPoint")
          .transition("t2")
            .guard(c -> c.c > 0)
            .target("end");
    
    builder
      .region()
        .finalState("end");
    
    return builder;
  }
  
  private StateMachineBuilder<Context> subStateMachine(final String name) {
    SubStateMachineBuilder<Context> builder = new SubStateMachineBuilder<>(name);
        
    builder
      .connectionPoint()
        .entryPoint(new EntryPointBuilder<Context>("entryPoint")
          .transition("t2")
            .effect(c -> c.c++)
            .target("A")
        )
        .exitPoint("exitPoint");
    
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
            .target("exitPoint");
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  public static final class Context extends SequentialContext {
    int c;
    boolean enteredSubMachine;
    boolean exitedSubMachine;

    Context(final int c) {
      this.c = c;
    }
  }
  
  public static final class EntrySubMachineBehavior implements StateBehavior<Context> {

    @Override
    public void accept(Context t) {
      t.enteredSubMachine = true;
    }
    
  }
  
  public static final class ExitSubMachineBehavior implements StateBehavior<Context> {

    @Override
    public void accept(Context t) {
      t.exitedSubMachine = true;
    }
    
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test2.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: \"init\" kind: INITIAL\n" +
"    State: \"sub\"\n" +
"      StateMachine: \"sub\"\n" +
"        Region: #13\n" +
"          PseudoState: \"init\" kind: INITIAL\n" +
"          State: \"A\"\n" +
"          Transition: \"t2\" -L- \"sub::entryPoint\" -> \"A\"\n" +
"          Transition: \"t0\" --- \"init\" -> \"A\"\n" +
"          Transition: \"t1\" --- \"A\" -> \"sub::exitPoint\"\n" +
"        ExitPoint: \"sub::exitPoint\"\n" +
"        EntryPoint: \"sub::entryPoint\"\n" +
"      ConnectionPointReference: #18\n" +
"        EntryPoint: \"sub::entryPoint\"\n" +
"        ExitPoint: \"sub::exitPoint\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- \"init\" -> \"sub\"\n" +
"    Transition: \"t1\" --- \"sub::exitPoint\" -> \"sub::entryPoint\"\n" +
"    Transition: \"t2\" --- \"sub::exitPoint\" -> \"end\"";
}
