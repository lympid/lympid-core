/*
 * Copyright 2015 Lympid.
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
package com.lympid.core.behaviorstatemachines.orthogonal;

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.StateMachineSnapshot;
import com.lympid.core.behaviorstatemachines.builder.EntryPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.OrthogonalStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.SubStateMachineBuilder;
import com.lympid.core.behaviorstatemachines.orthogonal.Test8.Context;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;

/**
 * Tests an entry point can be connect to a sub machine entry point
 * 
 * @author Fabien Renaud 
 */
public class Test8 extends AbstractStateMachineTest<Context> {
  
  @Test
  public void run() {
    run(false);
  }
  
  @Test
  public void run_pause() {
    run(true);
  }
  
  private void run(final boolean pause) {
    SequentialContext expected = new SequentialContext();
    
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
  
    ActiveStateTree active = new ActiveStateTree(this).branch("ortho", "sub", "A");
    expected
      .effect("t0").enter("ortho")
      .effect("t1").enter("sub").effect("t0").enter("A");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, active);
    
    if (pause) {
      fsm.pause();
      StateMachineSnapshot<Context> snapshot = fsm.snapshot();
      SequentialContext expected2 = expected.copy();
      
      fsm.take(new StringEvent("go"));
      assertSnapshotEquals(fsm, active);
    
      resume(fsm, expected);
      resume(snapshot, expected2);
    } else {
      goEnd(fsm, expected);
    }
  }
  
  private void resume(final StateMachineExecutor<Context> fsm, final SequentialContext expected) {
    fsm.resume();
    goEnd(fsm, expected);
  }
  
  private void resume(final StateMachineSnapshot<Context> snapshot, final SequentialContext expected) {
    resume(fsm(snapshot), expected);
  }

  private void goEnd(StateMachineExecutor<Context> fsm, SequentialContext expected) {
    fsm.take(new StringEvent("go"));
    expected
        .exit("A").effect("t1")
        .exit("sub").effect("t2")
        .exit("ortho").effect("t3");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }

  @Override
  public StateMachineBuilder<Context> topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target("entryPoint");
    
    builder
      .region()
        .state(ortho("ortho"))
          .transition("t3")
            .target("end");
    
    builder
      .region()
        .finalState("end");
    
    return builder;
  }
  
  private OrthogonalStateBuilder<Context> ortho(final String name) {
    OrthogonalStateBuilder<Context> builder = new OrthogonalStateBuilder<>(name);
    
    builder
      .connectionPoint()
        .entryPoint(new EntryPointBuilder<Context>("entryPoint")
          .transition("t1")
            .target("sub::entryPoint")
        );
    
    builder
      .region("r1");
    
    builder
      .region("r2")
        .state(subMachine("sub"))
          .transition("t2")
            .target("end2");
    
    builder
      .region("r2")
        .finalState("end2");
    
    
    return builder;
  }
  
  private StateMachineBuilder<Context> subMachine(final String name) {
    SubStateMachineBuilder<Context> builder = new SubStateMachineBuilder<>(name);
    
    builder
      .connectionPoint()
        .entryPoint(new EntryPointBuilder<Context>("entryPoint")
          .transition("t0")
            .target("A")
        );
    
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

  public static final class Context extends SequentialContext {
  }

  private static final String STDOUT = "StateMachine: \"" + Test8.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    FinalState: \"end\"\n" +
"    State: \"ortho\"\n" +
"      EntryPoint: \"entryPoint\"\n" +
"      Region: \"r2\"\n" +
"        State: \"sub\"\n" +
"          StateMachine: \"sub\"\n" +
"            Region: #20\n" +
"              FinalState: \"end\"\n" +
"              State: \"A\"\n" +
"              Transition: \"t0\" -L- \"sub::entryPoint\" -> \"A\"\n" +
"              Transition: \"t1\" --- \"A\" -> \"end\"\n" +
"            EntryPoint: \"sub::entryPoint\"\n" +
"          ConnectionPointReference: #24\n" +
"            EntryPoint: \"sub::entryPoint\"\n" +
"        FinalState: \"end2\"\n" +
"        Transition: \"t2\" --- \"sub\" -> \"end2\"\n" +
"        Transition: \"t1\" -L- \"entryPoint\" -> \"sub::entryPoint\"\n" +
"      Region: \"r1\"\n" +
"    Transition: \"t0\" --- #3 -> \"entryPoint\"\n" +
"    Transition: \"t3\" --- \"ortho\" -> \"end\"";
}
