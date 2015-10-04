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
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.StateMachineSnapshot;
import com.lympid.core.behaviorstatemachines.builder.EntryPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.SubStateMachineBuilder;
import org.junit.Test;

/**
 * Tests chaining the same sub state machine with entry/exit points.
 * 
 * @author Fabien Renaud 
 */
public class Test4 extends AbstractStateMachineTest {
  
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
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("sub1", "A"));
    
    pauseAndResume(fsm, pause);
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("sub2", "A"));
    
    pauseAndResume(fsm, pause);
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }

  private void pauseAndResume(StateMachineExecutor fsm, boolean pause) {
    if (pause) {
      StateMachineSnapshot snapshot = fsm.pause();
      fsm.take(new StringEvent("go"));
      fsm.resume();
    }
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder(name());
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target("sub1::entryPoint");
    
    StateMachineBuilder<Context> subMachine = subStateMachine("sub");
    
    builder
      .region()
        .state(subMachine, "sub1")
          .connectionPoint()
            .exitPoint("exitPoint")
              .transition("t1")
                .target("sub2::entryPoint");
    
    builder
      .region()
        .state(subMachine, "sub2")
          .connectionPoint()
            .exitPoint("exitPoint")
              .transition("t2")
                .target("end");
    
    builder
      .region()
        .finalState("end");
    
    return builder;
  }
  
  private StateMachineBuilder<Context> subStateMachine(final String name) {
    SubStateMachineBuilder<Context> builder = new SubStateMachineBuilder(name);
    
    builder
      .connectionPoint()
        .entryPoint(new EntryPointBuilder<Context>("entryPoint")
          .transition("t2")
            .effect((c) -> { c.c++; })
            .target("A")
        )
        .exitPoint("exitPoint");
    
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
  
  private static final class Context {
    int c;
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test4.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"sub1\"\n" +
"      StateMachine: \"sub\"\n" +
"        Region: #14\n" +
"          State: \"A\"\n" +
"          Transition: \"t2\" -L- \"sub1::entryPoint\" -> \"A\"\n" +
"          Transition: \"t1\" --- \"A\" -> \"sub1::exitPoint\"\n" +
"        ExitPoint: \"sub1::exitPoint\"\n" +
"        EntryPoint: \"sub1::entryPoint\"\n" +
"      ConnectionPointReference: #17\n" +
"        EntryPoint: \"sub1::entryPoint\"\n" +
"        ExitPoint: \"sub1::exitPoint\"\n" +
"    State: \"sub2\"\n" +
"      StateMachine: \"sub\"\n" +
"        Region: #26\n" +
"          State: \"A\"\n" +
"          Transition: \"t2\" -L- \"sub2::entryPoint\" -> \"A\"\n" +
"          Transition: \"t1\" --- \"A\" -> \"sub2::exitPoint\"\n" +
"        EntryPoint: \"sub2::entryPoint\"\n" +
"        ExitPoint: \"sub2::exitPoint\"\n" +
"      ConnectionPointReference: #29\n" +
"        EntryPoint: \"sub2::entryPoint\"\n" +
"        ExitPoint: \"sub2::exitPoint\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"sub1::entryPoint\"\n" +
"    Transition: \"t1\" --- \"sub1::exitPoint\" -> \"sub2::entryPoint\"\n" +
"    Transition: \"t2\" --- \"sub2::exitPoint\" -> \"end\"";
}
