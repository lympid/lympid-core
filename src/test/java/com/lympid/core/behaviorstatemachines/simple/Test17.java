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
import com.lympid.core.behaviorstatemachines.StateMachineSnapshot;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import com.lympid.core.behaviorstatemachines.simple.Test17.Context;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;

/**
 * Tests multiple outgoing  transitions with different triggers can be executed
 * from a simple state.
 * The state machine auto starts.
 * @author Fabien Renaud 
 */
public class Test17 extends AbstractStateMachineTest<Context> {
  
  @Test
  public void run_NoWhere() {
    SequentialContext expected = new SequentialContext()
      .effect("t0");
    
    Context ctx = new Context()
      .withoutEntry()
      .withoutExit();
    
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));
    assertSequentialContextEquals(expected, fsm);
  }
  
  @Test
  public void run_T1() {
    Context ctx = new Context()
      .withoutEntry()
      .withoutExit();
    
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A")); 
    go(fsm);
  }
  
  @Test
  public void run_T2() {
    Context ctx = new Context()
      .withoutEntry()
      .withoutExit();
    
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));
    va(fsm);
  }
  
  @Test
  public void run_T3() {
    Context ctx = new Context()
      .withoutEntry()
      .withoutExit();
    
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));
    weg(fsm);
  }

  private void go(StateMachineExecutor<Context> fsm) {
    SequentialContext expected = new SequentialContext()
            .effect("t0")
            .effect("t1");

    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("B"));
    assertSequentialContextEquals(expected, fsm);

    fsm.take(new StringEvent("close"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }

  private void va(StateMachineExecutor<Context> fsm) {
    SequentialContext expected = new SequentialContext()
            .effect("t0")
            .effect("t2");

    fsm.take(new StringEvent("va"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("B"));
    assertSequentialContextEquals(expected, fsm);

    fsm.take(new StringEvent("close"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }

  private void weg(StateMachineExecutor<Context> fsm) {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t3");
    
    fsm.take(new StringEvent("weg"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("B"));
      assertSequentialContextEquals(expected, fsm);

    fsm.take(new StringEvent("close"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }
  
  @Test
  public void run_T3_pauseAndResume() {
    Context ctx = new Context()
      .withoutEntry()
      .withoutExit();
    
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    ActiveStateTree active = new ActiveStateTree(this).branch("A");
    assertSnapshotEquals(fsm, active);
    
    fsm.pause();
    StateMachineSnapshot<Context> snapshot = fsm.snapshot();
    
    /*
     * All events are denied.
     */
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, active);
    fsm.take(new StringEvent("va"));
    assertSnapshotEquals(fsm, active);
    fsm.take(new StringEvent("weg"));
    assertSnapshotEquals(fsm, active);
    
    /*
     * Resuming
     */
    resume(fsm);
    resume(snapshot);
  }
  
  private void resume(final StateMachineExecutor<Context> fsm) {
    fsm.resume();
    weg(fsm);
  }
  
  private void resume(final StateMachineSnapshot<Context> snapshot) {
    resume(fsm(snapshot));
  }
  
  @Test
  public void runAll_pauseAndFork() {
    Context ctx = new Context()
      .withoutEntry()
      .withoutExit();
    
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    ActiveStateTree active = new ActiveStateTree(this).branch("A");
    assertSnapshotEquals(fsm, active);
    
    fsm.pause();
    StateMachineSnapshot<Context> snapshot1 = fsm.snapshot();
    StateMachineSnapshot<Context> snapshot2 = fsm.snapshot();
    StateMachineSnapshot<Context> snapshot3 = fsm.snapshot();
    
    /*
     * All events are denied.
     */
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, active);
    fsm.take(new StringEvent("va"));
    assertSnapshotEquals(fsm, active);
    fsm.take(new StringEvent("weg"));
    assertSnapshotEquals(fsm, active);
    
    /*
     * Resuming
     */
    StateMachineExecutor<Context> fork1 = fsm(snapshot1);
    StateMachineExecutor<Context> fork2 = fsm(snapshot2);
    StateMachineExecutor<Context> fork3 = fsm(snapshot3);
    
    fork1.resume();
    fork2.resume();
    fork3.resume();

    go(fork1);
    va(fork2);
    weg(fork3);
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
          .transition("t0")
            .target("A");
    
    builder
      .region()
        .state("A")
          .transition("t1")
            .on("go")
            .target("B")
          .transition("t2")
            .on("va")
            .target("B")
          .transition("t3")
            .on("weg")
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
  
  public static final class Context extends SequentialContext {
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test17.class.getSimpleName() + "\"\n" +
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
