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
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration;
import com.lympid.core.behaviorstatemachines.impl.IllegalStartException;
import com.lympid.core.behaviorstatemachines.simple.Test4.Context;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;

/**
 * Tests an external transition with 1 trigger can execute upon a given event.
 * The state machine auto starts.
 * @author Fabien Renaud 
 */
public class Test4 extends AbstractStateMachineTest<Context> {
  
  @Test
  public void run() {
    StateMachineExecutor<Context> fsm = fsm();
    fsm.go();
    
    /*
     * Machine has started and is on state A.
     */
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));
    
    /*
     * A string event other than "go" has no effect.
     */
    fsm.take(new StringEvent("pass"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));
    
    /*
     * "go" event moves the state machine to its final state
     */
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }
  
  @Test(expected = IllegalStartException.class)
  public void go_afterStarted_withAutoStart() {
    StateMachineExecutor<Context> fsm = fsm();
    fsm.go();
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));
    
    fsm.go();
  }
  
  @Test(expected = IllegalStartException.class)
  public void go_afterStarted_withoutAutoStart() {
    StateMachineExecutor<Context> fsm = fsm(new ExecutorConfiguration().autoStart(false));
    fsm.go();
    fsm.take(new StringEvent("blah"));
    fsm.go();
  }
  
  @Test(expected = IllegalStartException.class)
  public void go_afterTerminated_withAutoStart() {
    StateMachineExecutor<Context> fsm = fsm();
    fsm.go();
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    
    fsm.go();
  }
  
  @Test(expected = IllegalStartException.class)
  public void go_afterTerminated_withoutAutoStart() {
    StateMachineExecutor<Context> fsm = fsm(new ExecutorConfiguration().autoStart(false));
    fsm.go();
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    
    fsm.go();
  }
  
  @Test(expected = IllegalStartException.class)
  public void noGo_take_withAutoStart() {
    StateMachineExecutor<Context> fsm = fsm();
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }
  
  @Test(expected = IllegalStartException.class)
  public void noGo_take_withoutAutoStart() {
    StateMachineExecutor<Context> fsm = fsm(new ExecutorConfiguration().autoStart(false));
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }
  
  @Test(expected = IllegalStartException.class)
  public void noPause_resume() {
    StateMachineExecutor<Context> fsm = fsm();
    fsm.go();
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));
    
    fsm.resume();
  }

  @Override
  public StateMachineBuilder<Context> topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    VertexBuilderReference<Context> end = builder
      .region()
        .finalState("end");
    
    builder
      .region()
        .initial()
          .transition()
            .target("A");
    
    builder
      .region()
        .state("A")
          .transition()
            .on("go")
            .target(end);
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }

  public static final class Context {
  }

  private static final String STDOUT = "StateMachine: \"" + Test4.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    Transition: #5 --- #4 -> \"A\"\n" +
"    Transition: #7 --- \"A\" -> \"end\"";
}
