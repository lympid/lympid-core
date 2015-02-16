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
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import org.junit.Test;

/**
 * Tests an internal transition on a simple state.
 * @author Fabien Renaud 
 */
public class Test19 extends AbstractStateMachineTest {
  
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
      expected.effect("t1");
    }
    expected
      .exit("A").effect("t2");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    /*
     * Machine has started and is on state A.
     */
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A").get());
    
    for (int i = 0; i < n; i++) {
      fsm.take(new StringEvent("try"));
      assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A").get());
    }
    
    /*
     * "go" event moves the state machine to its final state
     */
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end").get());
    
    assertSequentialContextEquals(expected, ctx);
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Object> builder = new StateMachineBuilder(name());
    
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
            .target()
          .transition("t2")
            .on("go")
            .target("end");
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test19.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    Transition: \"t0\" --- #4 -> \"A\"\n" +
"    Transition: \"t1\" -I- \"A\" -> \"A\"\n" +
"    Transition: \"t2\" --- \"A\" -> \"end\"";
}
