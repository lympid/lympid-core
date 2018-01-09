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
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.EntryPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.OrthogonalStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.orthogonal.Test7.Context;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;

/**
 * Tests an entry point can be connect to a sub sub entry point
 * 
 * @author Fabien Renaud 
 */
public class Test7 extends AbstractStateMachineTest<Context> {
  
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
  
    ActiveStateTree active = new ActiveStateTree(this).branch("ortho", "compo1", "compo2", "A");
    expected
      .effect("t0").enter("ortho")
      .effect("t1").enter("compo1").enter("compo2").effect("t2").enter("A");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, active);
    
    if (pause) {
      fsm.pause();
      StateMachineSnapshot<Context> snapshot = fsm.snapshot();
      SequentialContext expected2 = expected.copy();
      
      fsm.take(new StringEvent("go2"));
      assertSnapshotEquals(fsm, active);
    
      resume(fsm, expected);
      resume(snapshot, expected2);
    } else {
      go2End(fsm, expected);
    }
  }
  
  private void resume(final StateMachineExecutor<Context> fsm, final SequentialContext expected) {
    fsm.resume();
    go2End(fsm, expected);
  }
  
  private void resume(final StateMachineSnapshot<Context> snapshot, final SequentialContext expected) {
    resume(fsm(snapshot), expected);
  }

  private void go2End(StateMachineExecutor<Context> fsm, SequentialContext expected) {
    fsm.take(new StringEvent("go2"));
    expected
        .exit("A").effect("t3")
        .exit("compo2").effect("t4")
        .exit("compo1").effect("t5")
        .exit("ortho").effect("t6");
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
            .target("entryPoint1");
    
    builder
      .region()
        .state(ortho("ortho"))
          .transition("t6")
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
        .entryPoint(new EntryPointBuilder<Context>("entryPoint1")
          .transition("t1")
            .target("entryPoint2")
        );
    
    builder
      .region("r1");
    
    builder
      .region("r2")
        .state(compo1("compo1"))
          .transition("t5")
            .target("end2");
    
    builder
      .region("r2")
        .finalState("end2");
    
    
    return builder;
  }
  
  private CompositeStateBuilder<Context> compo1(final String name) {
    CompositeStateBuilder<Context> builder = new CompositeStateBuilder<>(name);
    
    builder
      .region()
        .state(compo2("compo2"))
          .transition("t4")
            .target("cend1");
    
    builder
      .region()
        .finalState("cend1");
    
    return builder;
  }
  
  private CompositeStateBuilder<Context> compo2(final String name) {
    CompositeStateBuilder<Context> builder = new CompositeStateBuilder<>(name);
    
    builder
      .connectionPoint()
        .entryPoint(new EntryPointBuilder<Context>("entryPoint2")
          .transition("t2")
            .target("A")
        );
    
    builder
      .region()
        .state("A")
          .transition("t3")
            .on("go2")
            .target("cend2");
    
    builder
      .region()
        .finalState("cend2");
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }

  public static final class Context extends SequentialContext {
  }

  private static final String STDOUT = "StateMachine: \"" + Test7.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    FinalState: \"end\"\n" +
"    State: \"ortho\"\n" +
"      EntryPoint: \"entryPoint1\"\n" +
"      Region: \"r2\"\n" +
"        State: \"compo1\"\n" +
"          Region: #10\n" +
"            State: \"compo2\"\n" +
"              EntryPoint: \"entryPoint2\"\n" +
"              Region: #13\n" +
"                State: \"A\"\n" +
"                FinalState: \"cend2\"\n" +
"                Transition: \"t3\" --- \"A\" -> \"cend2\"\n" +
"                Transition: \"t2\" -L- \"entryPoint2\" -> \"A\"\n" +
"            FinalState: \"cend1\"\n" +
"            Transition: \"t4\" --- \"compo2\" -> \"cend1\"\n" +
"        FinalState: \"end2\"\n" +
"        Transition: \"t5\" --- \"compo1\" -> \"end2\"\n" +
"        Transition: \"t1\" -L- \"entryPoint1\" -> \"entryPoint2\"\n" +
"      Region: \"r1\"\n" +
"    Transition: \"t0\" --- #3 -> \"entryPoint1\"\n" +
"    Transition: \"t6\" --- \"ortho\" -> \"end\"";
}
