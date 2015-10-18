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
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.StateMachineSnapshot;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.common.Copyable;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class Test1 extends AbstractStateMachineTest {
  
  @Test
  public void run() {
    run(false);
  }
  
  @Test
  public void run_pause() {
    run(true);
  }

  private void run(final boolean pause) {
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    
    assertFalse(ctx.enteredSubMachine);
    assertFalse(ctx.exitedSubMachine);
    fsm.go();
    
    ActiveStateTree active = new ActiveStateTree(this).branch("sub", "A");
    assertSnapshotEquals(fsm, active);
    assertTrue(ctx.enteredSubMachine);
    assertFalse(ctx.exitedSubMachine);
    
    if (pause) {
      fsm.pause();
      StateMachineSnapshot snapshot = fsm.snapshot();
      
      fsm.take(new StringEvent("go"));
      assertSnapshotEquals(fsm, active);
      
      resume(fsm);
      resume(snapshot);
    } else {
      goEnd(fsm);
    }
  }
  
  private void resume(final StateMachineExecutor<SequentialContext> fsm) {
    fsm.resume();
    goEnd(fsm);
  }
  
  private void resume(final StateMachineSnapshot snapshot) {
    resume(fsm(snapshot));
  }

  private void goEnd(StateMachineExecutor fsm) {
      fsm.take(new StringEvent("go"));
      
      assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
      StateMachineSnapshot<Context> snapshot = fsm.snapshot();
      assertTrue(snapshot.context().enteredSubMachine);
      assertTrue(snapshot.context().exitedSubMachine);
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder(name());
    
    builder
      .region()
        .initial("init")
          .transition("t0")
            .target("sub");
    
    builder
      .region()
        .state(subStateMachine("sub"))
          .entry((c) -> { c.enteredSubMachine = true; })
          .exit((c) -> { c.exitedSubMachine = true; })
          .transition("t1")
            .target("end");
    
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
  
  private static final class Context implements Copyable<Context> {
    boolean enteredSubMachine;
    boolean exitedSubMachine;

    @Override
    public Context copy() {
      Context copy = new Context();
      copy.enteredSubMachine = enteredSubMachine;
      copy.exitedSubMachine = exitedSubMachine;
      return copy;
    }
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test1.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: \"init\" kind: INITIAL\n" +
"    State: \"sub\"\n" +
"      StateMachine: \"sub\"\n" +
"        Region: #9\n" +
"          State: \"A\"\n" +
"          FinalState: \"end\"\n" +
"          PseudoState: \"init\" kind: INITIAL\n" +
"          Transition: \"t0\" --- \"init\" -> \"A\"\n" +
"          Transition: \"t1\" --- \"A\" -> \"end\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- \"init\" -> \"sub\"\n" +
"    Transition: \"t1\" --- \"sub\" -> \"end\"";
}
