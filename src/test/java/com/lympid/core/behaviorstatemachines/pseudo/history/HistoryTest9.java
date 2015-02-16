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

package com.lympid.core.behaviorstatemachines.pseudo.history;

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.OrthogonalStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.impl.StateMachineSnapshot;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public abstract class HistoryTest9 extends AbstractHistoryTest {
  
  private String stdout;
  private boolean pause;

  public HistoryTest9(final PseudoStateKind historyKind) {
    super(historyKind);
    setStdOut(historyKind);
  }
  
  @Test
  public void run_a_b_end1_B2B_sub2_end2_c_end() {
    run_a_b_end1_B2B_sub2_end2_c_end(false);
  }
  
  @Test
  public void run_a_b_end1_B2B_sub2_end2_c_end_pause() {
    run_a_b_end1_B2B_sub2_end2_c_end(true);
  }
  
  private void run_a_b_end1_B2B_sub2_end2_c_end(final boolean pause) {
    SequentialContext expected = new SequentialContext();
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    
    expected
      .effect("t0").enter("compo").enter("A");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "A"));
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("go"));
    expected
      .exit("A").effect("t1").enter("B")
        .effect("t6").enter("B2A")
        .effect("t9").enter("sub1").effect("t0").enter("Z")
        .effect("t4").enter("B1").enter("B1A");
    assertSnapshotEquals(fsm, new ActiveStateTree(this)
      .branch("compo", "B", "B1", "B1A")
      .branch("compo", "B", "B2A")
      .branch("compo", "B", "sub1", "Z")
    );
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("go1"));
    expected.exit("B1A").exit("B1").effect("t5");
    assertSnapshotEquals(fsm, new ActiveStateTree(this)
      .branch("compo", "B", "end1")
      .branch("compo", "B", "B2A")
      .branch("compo", "B", "sub1", "Z")
    );
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("go21"));
    expected
      .exit("B2A").effect("t7").enter("B2B");
    assertSnapshotEquals(fsm, new ActiveStateTree(this)
      .branch("compo", "B", "end1")
      .branch("compo", "B", "B2B")
      .branch("compo", "B", "sub1", "Z")
    );
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("zEnd"));
    expected
      .exit("Z").effect("t1");
    assertSnapshotEquals(fsm, new ActiveStateTree(this)
      .branch("compo", "B", "end1")
      .branch("compo", "B", "B2B")
      .branch("compo", "B", "sub1", "end")
    );
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("go31"));
    expected
      .exit("sub1").effect("t10").enter("sub2")
      .effect("t0").enter("Z");
    assertSnapshotEquals(fsm, new ActiveStateTree(this)
      .branch("compo", "B", "end1")
      .branch("compo", "B", "B2B")
      .branch("compo", "B", "sub2", "Z")
    );
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("go22"));
    expected
      .exit("B2B").effect("t8");
    assertSnapshotEquals(fsm, new ActiveStateTree(this)
      .branch("compo", "B", "end1")
      .branch("compo", "B", "end2")
      .branch("compo", "B", "sub2", "Z")
    );
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("zEnd"));
    expected
      .exit("Z").effect("t1");
    assertSnapshotEquals(fsm, new ActiveStateTree(this)
      .branch("compo", "B", "end1")
      .branch("compo", "B", "end2")
      .branch("compo", "B", "sub2", "end")
    );
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("go32"));
    expected
      .exit("sub2").effect("t11")
      .exit("B").effect("t2").enter("C");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "C"));
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("end"));
    expected
      .exit("C").exit("compo").effect("t3");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertSequentialContextEquals(expected, fsm);
  }
  
  @Test
  public void run_a_b_P_end1_B2B_sub2_end2_c_end() {
    run_a_b_P_end1_B2B_sub2_end2_c_end(false);
  }
  
  @Test
  public void run_a_b_P_end1_B2B_sub2_end2_c_end_pause() {
    run_a_b_P_end1_B2B_sub2_end2_c_end(true);
  }
  
  private void run_a_b_P_end1_B2B_sub2_end2_c_end(final boolean pause) {
    this.pause = pause;
    SequentialContext expected = new SequentialContext();
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    
    expected
      .effect("t0").enter("compo").enter("A");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "A"));
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("go"));
    expected
      .exit("A").effect("t1").enter("B")
        .effect("t6").enter("B2A")
        .effect("t9").enter("sub1").effect("t0").enter("Z")
        .effect("t4").enter("B1").enter("B1A");
    assertSnapshotEquals(fsm, new ActiveStateTree(this)
      .branch("compo", "B", "B1", "B1A")
      .branch("compo", "B", "B2A")
      .branch("compo", "B", "sub1", "Z")
    );
    assertSequentialContextEquals(expected, fsm);
    
    
    /*
     * Pause
     */
    pauseAndResume(fsm);
    fsm.take(new StringEvent("pause"));
    expected
        .exit("B2A")
        .exit("Z").exit("sub1")
        .exit("B1A").exit("B1")
      .exit("B").exit("compo").effect("t12").enter("P");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("P"));
    assertSequentialContextEquals(expected, fsm);
    
    resume_B1A_B2A_sub1(fsm, expected);
    
    
    /*
     * Back to normal
     */
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("go1"));
    expected.exit("B1A").exit("B1").effect("t5");
    assertSnapshotEquals(fsm, new ActiveStateTree(this)
      .branch("compo", "B", "end1")
      .branch("compo", "B", "B2A")
      .branch("compo", "B", "sub1", "Z")
    );
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("go21"));
    expected
      .exit("B2A").effect("t7").enter("B2B");
    assertSnapshotEquals(fsm, new ActiveStateTree(this)
      .branch("compo", "B", "end1")
      .branch("compo", "B", "B2B")
      .branch("compo", "B", "sub1", "Z")
    );
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("zEnd"));
    expected
      .exit("Z").effect("t1");
    assertSnapshotEquals(fsm, new ActiveStateTree(this)
      .branch("compo", "B", "end1")
      .branch("compo", "B", "B2B")
      .branch("compo", "B", "sub1", "end")
    );
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("go31"));
    expected
      .exit("sub1").effect("t10").enter("sub2")
      .effect("t0").enter("Z");
    assertSnapshotEquals(fsm, new ActiveStateTree(this)
      .branch("compo", "B", "end1")
      .branch("compo", "B", "B2B")
      .branch("compo", "B", "sub2", "Z")
    );
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("go22"));
    expected
      .exit("B2B").effect("t8");
    assertSnapshotEquals(fsm, new ActiveStateTree(this)
      .branch("compo", "B", "end1")
      .branch("compo", "B", "end2")
      .branch("compo", "B", "sub2", "Z")
    );
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("zEnd"));
    expected
      .exit("Z").effect("t1");
    assertSnapshotEquals(fsm, new ActiveStateTree(this)
      .branch("compo", "B", "end1")
      .branch("compo", "B", "end2")
      .branch("compo", "B", "sub2", "end")
    );
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("go32"));
    expected
      .exit("sub2").effect("t11")
      .exit("B").effect("t2").enter("C");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "C"));
    assertSequentialContextEquals(expected, fsm);
    
    
    pauseAndResume(fsm);
    fsm.take(new StringEvent("end"));
    expected
      .exit("C").exit("compo").effect("t3");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertSequentialContextEquals(expected, fsm);
  }

  private void pauseAndResume(final StateMachineExecutor fsm) {
    if (pause) {
      StateMachineSnapshot snapshot = fsm.pause();
      fsm.take(new StringEvent("go"));
      fsm.take(new StringEvent("go1"));
      fsm.take(new StringEvent("go21"));
      fsm.take(new StringEvent("go22"));
      fsm.take(new StringEvent("zEnd"));
      fsm.take(new StringEvent("go31"));
      fsm.take(new StringEvent("go32"));
      fsm.take(new StringEvent("end"));
      fsm.take(new StringEvent("pause"));
      fsm.take(new StringEvent("resume"));
      fsm.resume(snapshot);
    }
  }
  
  protected abstract void resume_B1A_B2A_sub1(StateMachineExecutor fsm, SequentialContext expected);
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder builder = new StateMachineBuilder(name());
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target("A");
    
    builder
      .region()
        .state(compo("compo"))
          .transition("t12")
            .on("pause")
            .target("P");
    
    builder
      .region()
        .state("P")
          .transition("t13")
            .on("resume")
            .target("history");
    
    builder
      .region()
        .finalState("end");
    
    return builder;
  }
  
  private CompositeStateBuilder compo(final String name) {
    CompositeStateBuilder builder = new CompositeStateBuilder(name);
    
    builder
      .region()
        .state("A")
          .transition("t1")
            .on("go")
            .target("B");
    
    builder
      .region()
        .state(ortho("B"))
          .transition("t2")
            .target("C");
    
    builder
      .region()
        .state("C")
          .transition("t3")
            .on("end")
            .target("end");
    
    history(builder, "history");
    
    return builder;
  }

  private OrthogonalStateBuilder ortho(final String name) {
    OrthogonalStateBuilder builder = new OrthogonalStateBuilder(name);
    
    /*
     * Region 1
     */
    
    builder
      .region("r1")
        .initial()
          .transition("t4")
            .target("B1A");
    
    builder
      .region("r1")
        .state(compositeB1("B1"));
    
    builder
      .region("r1")
        .finalState("end1");
    
    /*
     * Region 2
     */
    
    builder
      .region("r2")
        .initial()
          .transition("t6")
            .target("B2A");
    
    builder
      .region("r2")
        .state("B2A")
          .transition("t7")
            .on("go21")
            .target("B2B");
    
    builder
      .region("r2")
        .state("B2B")
          .transition("t8")
            .on("go22")
            .target("end2");
    
    builder
      .region("r2")
        .finalState("end2");
    
    /*
     * Region 3
     */
    
    builder
      .region("r3")
        .initial()
          .transition("t9")
            .target("sub1");
    
    StateMachineBuilder sub = submachine("sub");
    
    builder
      .region("r3")
        .state(sub, "sub1")
          .transition("t10")
            .on("go31")
            .target("sub2");
    
    builder
      .region("r3")
        .state(sub, "sub2")
          .transition("t11")
            .on("go32")
            .target("end3");
    
    builder
      .region("r3")
        .finalState("end3");
    
    return builder;
  }

  private CompositeStateBuilder compositeB1(final String name) {
    CompositeStateBuilder builder = new CompositeStateBuilder(name);
    
    builder
      .region()
        .state("B1A")
          .transition("t5")
            .on("go1")
            .target("end1");
    
    return builder;
  }
  
  private StateMachineBuilder submachine(final String name) {
    StateMachineBuilder builder = new StateMachineBuilder(name);
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target("Z");
    
    builder
      .region()
        .state("Z")
          .transition("t1")
            .on("zEnd")
            .target("end");
    
    builder
      .region()
        .finalState("end");

    return builder;
  }
  
  @Override
  public String stdOut() {
    return stdout;
  }
  
  @Override
  final void setStdOut(final PseudoStateKind historyKind) {
    stdout = "StateMachine: \"" + name() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"compo\"\n" +
"      Region: #7\n" +
"        State: \"C\"\n" +
"        PseudoState: \"history\" kind: " + historyKind + "\n" +
"        State: \"A\"\n" +
"        State: \"B\"\n" +
"          Region: \"r2\"\n" +
"            PseudoState: #13 kind: INITIAL\n" +
"            State: \"B2A\"\n" +
"            State: \"B2B\"\n" +
"            FinalState: \"end2\"\n" +
"            Transition: \"t6\" --- #13 -> \"B2A\"\n" +
"            Transition: \"t7\" --- \"B2A\" -> \"B2B\"\n" +
"            Transition: \"t8\" --- \"B2B\" -> \"end2\"\n" +
"          Region: \"r3\"\n" +
"            State: \"sub1\"\n" +
"              StateMachine: \"sub\"\n" +
"                Region: #43\n" +
"                  PseudoState: #44 kind: INITIAL\n" +
"                  State: \"Z\"\n" +
"                  FinalState: \"end\"\n" +
"                  Transition: \"t0\" --- #44 -> \"Z\"\n" +
"                  Transition: \"t1\" --- \"Z\" -> \"end\"\n" +
"            State: \"sub2\"\n" +
"              StateMachine: \"sub\"\n" +
"                Region: #51\n" +
"                  FinalState: \"end\"\n" +
"                  PseudoState: #52 kind: INITIAL\n" +
"                  State: \"Z\"\n" +
"                  Transition: \"t0\" --- #52 -> \"Z\"\n" +
"                  Transition: \"t1\" --- \"Z\" -> \"end\"\n" +
"            FinalState: \"end3\"\n" +
"            PseudoState: #21 kind: INITIAL\n" +
"            Transition: \"t9\" --- #21 -> \"sub1\"\n" +
"            Transition: \"t10\" --- \"sub1\" -> \"sub2\"\n" +
"            Transition: \"t11\" --- \"sub2\" -> \"end3\"\n" +
"          Region: \"r1\"\n" +
"            FinalState: \"end1\"\n" +
"            PseudoState: #29 kind: INITIAL\n" +
"            State: \"B1\"\n" +
"              Region: #32\n" +
"                State: \"B1A\"\n" +
"            Transition: \"t4\" --- #29 -> \"B1A\"\n" +
"            Transition: \"t5\" --- \"B1A\" -> \"end1\"\n" +
"        Transition: \"t1\" --- \"A\" -> \"B\"\n" +
"        Transition: \"t2\" --- \"B\" -> \"C\"\n" +
"    State: \"P\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"A\"\n" +
"    Transition: \"t12\" --- \"compo\" -> \"P\"\n" +
"    Transition: \"t3\" --- \"C\" -> \"end\"\n" +
"    Transition: \"t13\" --- \"P\" -> \"history\"";
  }
}
