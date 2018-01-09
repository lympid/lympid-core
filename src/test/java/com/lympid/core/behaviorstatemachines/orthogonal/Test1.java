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

package com.lympid.core.behaviorstatemachines.orthogonal;

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.StateMachineSnapshot;
import com.lympid.core.behaviorstatemachines.builder.OrthogonalStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import com.lympid.core.behaviorstatemachines.orthogonal.Test1.Context;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;

/**
 * 
 * @author Fabien Renaud 
 */
public class Test1 extends AbstractStateMachineTest<Context> {
  
  @Test
  public void run_end() {
    SequentialContext expected = new SequentialContext();    
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    
    expected.exit("C").exit("A");
    fireEnd(fsm, expected);
  }
  
  @Test
  public void run_go1_end() {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    fireGo1(fsm, expected, "C");
    
    expected.exit("C");
    fireEnd(fsm, expected);
  }
  
  @Test
  public void run_go1_go2_end() {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    fireGo1(fsm, expected, "C");
    fireGo2(fsm, expected, "end1");
    fireEnd(fsm, expected);
  }
  
  @Test
  public void run_go1_pause_go2_end() {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    fireGo1(fsm, expected, "C");
    pauseAndResume(expected, fsm, (e, f) -> {
      fireGo2(f, e, "end1");
      fireEnd(f, e);
    });
  }
  
  @Test
  public void run_go2_end() {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    fireGo2(fsm, expected, "A");
    
    expected.exit("A");
    fireEnd(fsm, expected);
  }
  
  @Test
  public void run_go2_go1_end() {
    SequentialContext expected = new SequentialContext();    
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    fireGo2(fsm, expected, "A");
    fireGo1(fsm, expected, "end2");
    fireEnd(fsm, expected);
  }
  
  @Test
  public void run_go2_pause_go1_end() {
    SequentialContext expected = new SequentialContext();    
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    fireGo2(fsm, expected, "A");
    pauseAndResume(expected, fsm, (e, f) -> {
      fireGo1(f, e, "end2");
      fireEnd(f, e);
    });
  }

  private void begin(StateMachineExecutor<Context> fsm, SequentialContext expected) {
    expected
      .effect("t0").enter("ortho")
      .effect("t4").enter("C")
      .effect("t1").enter("A");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ortho", "A").branch("ortho", "C"));
    assertSequentialContextEquals(expected, fsm);
  }

  private void fireGo1(StateMachineExecutor<Context> fsm, SequentialContext expected, String otherRegionState) {
    fsm.take(new StringEvent("go1"));
    expected.exit("A").effect("t2").enter("B").exit("B").effect("t3");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ortho", "end1").branch("ortho", otherRegionState));
    assertSequentialContextEquals(expected, fsm);
  }

  private void fireGo2(StateMachineExecutor<Context> fsm, SequentialContext expected, String otherRegionState) {
    fsm.take(new StringEvent("go2"));
    expected.exit("C").effect("t5").enter("D").exit("D").effect("t6");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ortho", otherRegionState).branch("ortho", "end2"));
    assertSequentialContextEquals(expected, fsm);
  }

  private void fireEnd(StateMachineExecutor<Context> fsm, SequentialContext expected) {
    fsm.take(new StringEvent("end"));
    expected.exit("ortho").effect("t7");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertSequentialContextEquals(expected, fsm);
  }
  
  private void pauseAndResume(SequentialContext expected1, StateMachineExecutor<Context> fsm1, FsmRunSequence sequence) {
    fsm1.pause();
    StateMachineSnapshot<Context> snapshot1 = fsm1.snapshot();
    SequentialContext expected2 = expected1.copy();
    
    /*
     * First state machine
     */
    fsm1.take(new StringEvent("go1"));
    fsm1.take(new StringEvent("go2"));
    fsm1.take(new StringEvent("end"));
    
    fsm1.resume();
    sequence.run(expected1, fsm1);

    /*
     * Second/cloned state machine
     */
    StateMachineExecutor<Context> fsm2 = fsm(snapshot1);
    assertSnapshotEquals(snapshot1, fsm2);

    fsm2.take(new StringEvent("go1"));
    assertSnapshotEquals(snapshot1, fsm2);
    fsm2.take(new StringEvent("go2"));
    assertSnapshotEquals(snapshot1, fsm2);
    fsm2.take(new StringEvent("end"));
    assertSnapshotEquals(snapshot1, fsm2);

    fsm2.resume();
    assertSnapshotEquals(snapshot1, fsm2);

    sequence.run(expected2, fsm2);
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
            .target("ortho");
    
    builder
      .region()
        .state(orthogonal("ortho"))
          .transition("t7")
            .on("end")
            .target(end);

    return builder;
  }
  
  private OrthogonalStateBuilder<Context> orthogonal(final String name) {
    OrthogonalStateBuilder<Context> builder = new OrthogonalStateBuilder<>(name);
    
    builder
      .region("r1")
        .finalState("end1");
    
    builder
      .region("r1")
        .initial()
          .transition("t1")
            .target("A");
    
    builder
      .region("r1")
        .state("A")
          .transition("t2")
            .on("go1")
            .target("B");
    
    builder
      .region("r1")
        .state("B")
          .transition("t3")
            .target("end1");
    
    builder
      .region("r2")
        .finalState("end2");
    
    builder
      .region("r2")
        .initial()
          .transition("t4")
            .target("C");
    
    builder
      .region("r2")
        .state("C")
          .transition("t5")
            .on("go2")
            .target("D");
    
    builder
      .region("r2")
        .state("D")
          .transition("t6")
            .target("end2");
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }

  public static final class Context extends SequentialContext {
  }
  
  private interface FsmRunSequence {
    
    void run(SequentialContext expected, StateMachineExecutor<Context> fsm);
    
  }

  private static final String STDOUT = "StateMachine: \"" + Test1.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"ortho\"\n" +
"      Region: \"r2\"\n" +
"        State: \"C\"\n" +
"        State: \"D\"\n" +
"        FinalState: \"end2\"\n" +
"        PseudoState: #10 kind: INITIAL\n" +
"        Transition: \"t4\" --- #10 -> \"C\"\n" +
"        Transition: \"t5\" --- \"C\" -> \"D\"\n" +
"        Transition: \"t6\" --- \"D\" -> \"end2\"\n" +
"      Region: \"r1\"\n" +
"        State: \"B\"\n" +
"        FinalState: \"end1\"\n" +
"        PseudoState: #18 kind: INITIAL\n" +
"        State: \"A\"\n" +
"        Transition: \"t1\" --- #18 -> \"A\"\n" +
"        Transition: \"t2\" --- \"A\" -> \"B\"\n" +
"        Transition: \"t3\" --- \"B\" -> \"end1\"\n" +
"    Transition: \"t0\" --- #4 -> \"ortho\"\n" +
"    Transition: \"t7\" --- \"ortho\" -> \"end\"";
}
