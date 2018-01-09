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
import com.lympid.core.behaviorstatemachines.orthogonal.Test3.Context;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;

/**
 * 
 * @author Fabien Renaud 
 */
public class Test3 extends AbstractStateMachineTest<Context> {
   
  private static final int PARALLEL_REGIONS = 2;
  private static final int DEPTH = 2;
  
  @Test
  public void run_noevents() {
    SequentialContext expected = new SequentialContext();    
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
  }
  
  @Test
  public void run_e_f_b_c() {
    run_e_f_b_c(false);
  }
  
  @Test
  public void run_e_f_b_c_pause() {
    run_e_f_b_c(true);
  }
  
  private void run_e_f_b_c(final boolean pause) {
    SequentialContext expected = new SequentialContext();    
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    
    pauseAndResume(expected, fsm, pause, this::run_e_f_b_c_Part2);
  }
  
  private void run_e_f_b_c_Part2(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause) {
    fsm.take(new StringEvent("goE"));
    expected.exit("E").effect("t9");
    ActiveStateTree tree = new ActiveStateTree(this)
      .branch("ortho", "D", "endE")
      .branch("ortho", "D", "F")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
    
    pauseAndResume(expected, fsm, pause, this::run_e_f_b_c_Part3);
  }
  
  private void run_e_f_b_c_Part3(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause) {
    fsm.take(new StringEvent("goF"));
    expected.exit("F").effect("t11")
      .exit("D").effect("t12");
    ActiveStateTree tree = new ActiveStateTree(this)
      .branch("ortho", "endD")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
    
    pauseAndResume(expected, fsm, pause, this::run_e_f_b_c_Part4);
  }
  
  private void run_e_f_b_c_Part4(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause) {
    fsm.take(new StringEvent("goB"));
    expected.exit("B").effect("t3");
    ActiveStateTree tree = new ActiveStateTree(this)
      .branch("ortho", "endD")
      .branch("ortho", "A", "endB")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
    
    pauseAndResume(expected, fsm, pause, this::run_e_f_b_c_Part5);
  }
  
  private void run_e_f_b_c_Part5(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause) {
    fsm.take(new StringEvent("goC"));
    expected.exit("C").effect("t5")
      .exit("A").effect("t6")
      .exit("ortho").effect("t13");
    ActiveStateTree tree = new ActiveStateTree(this).branch("end");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
  }
  
  @Test
  public void run_f_e_b_c() {
    run_f_e_b_c(false);
  }
  @Test
  public void run_f_e_b_c_pause() {
    run_f_e_b_c(true);
  }
  
  private void run_f_e_b_c(final boolean pause) {
    SequentialContext expected = new SequentialContext();    
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    
    pauseAndResume(expected, fsm, pause, this::run_f_e_b_c_Part2);
  }
  
  private void run_f_e_b_c_Part2(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause) {
    fsm.take(new StringEvent("goF"));
    expected.exit("F").effect("t11");
    ActiveStateTree tree = new ActiveStateTree(this)
      .branch("ortho", "D", "E")
      .branch("ortho", "D", "endF")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
    
    pauseAndResume(expected, fsm, pause, this::run_f_e_b_c_Part3);
  }
  
  private void run_f_e_b_c_Part3(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause) {
    fsm.take(new StringEvent("goE"));
    expected.exit("E").effect("t9")
      .exit("D").effect("t12");
    ActiveStateTree tree = new ActiveStateTree(this)
      .branch("ortho", "endD")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
    
    pauseAndResume(expected, fsm, pause, this::run_f_e_b_c_Part4);
  }
  
  private void run_f_e_b_c_Part4(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause) {
    fsm.take(new StringEvent("goB"));
    expected.exit("B").effect("t3");
    ActiveStateTree tree = new ActiveStateTree(this)
      .branch("ortho", "endD")
      .branch("ortho", "A", "endB")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
    
    pauseAndResume(expected, fsm, pause, this::run_f_e_b_c_Part5);
  }
  
  private void run_f_e_b_c_Part5(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause) {
    fsm.take(new StringEvent("goC"));
    expected.exit("C").effect("t5")
      .exit("A").effect("t6")
      .exit("ortho").effect("t13");
    ActiveStateTree tree = new ActiveStateTree(this).branch("end");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
  }
  
  @Test
  public void run_e_f_c_b() {
    run_e_f_c_b(false);
  }
  @Test
  public void run_e_f_c_b_pause() {
    run_e_f_c_b(true);
  }
  
  private void run_e_f_c_b(final boolean pause) {
    SequentialContext expected = new SequentialContext();    
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    
    pauseAndResume(expected, fsm, pause, this::run_e_f_c_b_Part2);
  }
  
  private void run_e_f_c_b_Part2(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause) {
    fsm.take(new StringEvent("goE"));
    expected.exit("E").effect("t9");
    ActiveStateTree tree = new ActiveStateTree(this)
      .branch("ortho", "D", "endE")
      .branch("ortho", "D", "F")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
    
    pauseAndResume(expected, fsm, pause, this::run_e_f_c_b_Part3);
  }
  
  private void run_e_f_c_b_Part3(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause) {
    fsm.take(new StringEvent("goF"));
    expected.exit("F").effect("t11")
      .exit("D").effect("t12");
    ActiveStateTree tree = new ActiveStateTree(this)
      .branch("ortho", "endD")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
    
    pauseAndResume(expected, fsm, pause, this::run_e_f_c_b_Part4);
  }
  
  private void run_e_f_c_b_Part4(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause) {
    fsm.take(new StringEvent("goC"));
    expected.exit("C").effect("t5");
    ActiveStateTree tree = new ActiveStateTree(this)
      .branch("ortho", "endD")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "endC");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
    
    pauseAndResume(expected, fsm, pause, this::run_e_f_c_b_Part5);
  }
  
  private void run_e_f_c_b_Part5(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause) {
    fsm.take(new StringEvent("goB"));
    expected.exit("B").effect("t3")
      .exit("A").effect("t6")
      .exit("ortho").effect("t13");
    ActiveStateTree tree = new ActiveStateTree(this).branch("end");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
  }
  
  @Test
  public void run_e_b_f_c() {
    run_e_b_f_c(false);
  }
  @Test
  public void run_e_b_f_c_pause() {
    run_e_b_f_c(true);
  }
  
  private void run_e_b_f_c(final boolean pause) {
    SequentialContext expected = new SequentialContext();    
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    
    pauseAndResume(expected, fsm, pause, this::run_e_b_f_c_Part2);
  }
  
  private void run_e_b_f_c_Part2(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause) {
    fsm.take(new StringEvent("goE"));
    expected.exit("E").effect("t9");
    ActiveStateTree tree = new ActiveStateTree(this)
      .branch("ortho", "D", "endE")
      .branch("ortho", "D", "F")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
    
    pauseAndResume(expected, fsm, pause, this::run_e_b_f_c_Part3);
  }
  
  private void run_e_b_f_c_Part3(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause) {
    fsm.take(new StringEvent("goB"));
    expected.exit("B").effect("t3");
    ActiveStateTree tree = new ActiveStateTree(this)
      .branch("ortho", "D", "endE")
      .branch("ortho", "D", "F")
      .branch("ortho", "A", "endB")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
    
    pauseAndResume(expected, fsm, pause, this::run_e_b_f_c_Part4);
  }
  
  private void run_e_b_f_c_Part4(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause) {
    fsm.take(new StringEvent("goF"));
    expected.exit("F").effect("t11")
      .exit("D").effect("t12");
    ActiveStateTree tree = new ActiveStateTree(this)
      .branch("ortho", "endD")
      .branch("ortho", "A", "endB")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
    
    pauseAndResume(expected, fsm, pause, this::run_e_b_f_c_Part5);
  }
  
  private void run_e_b_f_c_Part5(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause) {
    fsm.take(new StringEvent("goC"));
    expected.exit("C").effect("t5")
      .exit("A").effect("t6")
      .exit("ortho").effect("t13");
    ActiveStateTree tree = new ActiveStateTree(this).branch("end");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
  }

  private void begin(StateMachineExecutor<Context> fsm, SequentialContext expected) {
    expected
      .effect("t0").enter("ortho")
        .effect("t7").enter("D")
          .effect("t8").enter("E")
          .effect("t10").enter("F")
        .effect("t1").enter("A")
          .effect("t2").enter("B")
          .effect("t4").enter("C");
    ActiveStateTree tree = new ActiveStateTree(this)
      .branch("ortho", "D", "E")
      .branch("ortho", "D", "F")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, tree);
  }
  
  private void pauseAndResume(final SequentialContext expected1, final StateMachineExecutor<Context> fsm1, final boolean pause, final FsmRunSequence sequence) {
    if (!pause) {
      sequence.run(expected1, fsm1, pause);
      return;
    }
    
    fsm1.pause();
    StateMachineSnapshot<Context> snapshot1 = fsm1.snapshot();
    SequentialContext expected2 = expected1.copy();

    /*
     * First state machine
     */
    fsm1.take(new StringEvent("goB"));
    fsm1.take(new StringEvent("goC"));
    fsm1.take(new StringEvent("goE"));
    fsm1.take(new StringEvent("goF"));
    fsm1.resume();
    sequence.run(expected1, fsm1, pause);

    /*
     * Second/cloned state machine
     */
    StateMachineExecutor<Context> fsm2 = fsm(snapshot1);
    assertSnapshotEquals(snapshot1, fsm2);

    fsm2.take(new StringEvent("goB"));
    assertSnapshotEquals(snapshot1, fsm2);
    fsm2.take(new StringEvent("goC"));
    assertSnapshotEquals(snapshot1, fsm2);
    fsm2.take(new StringEvent("goE"));
    assertSnapshotEquals(snapshot1, fsm2);
    fsm2.take(new StringEvent("goF"));
    assertSnapshotEquals(snapshot1, fsm2);

    fsm2.resume();
    assertSnapshotEquals(snapshot1, fsm2);

    sequence.run(expected2, fsm2, pause);
  }
  
  @Override
  public StateMachineBuilder<Context> topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());

    builder
      .region()
        .finalState("end");

    builder
      .region()
        .initial()
          .transition("t0")
            .target("ortho");
    
    builder
      .region()
        .state(orthogonal("ortho", DEPTH - 1))
          .transition("t" + transitionCount)
            .target("end");

    return builder;
  }
  
  private int transitionCount = 1;
  private char stateName = 'A';
  
  private OrthogonalStateBuilder<Context> orthogonal(final String name, int depth) {
    OrthogonalStateBuilder<Context> builder = new OrthogonalStateBuilder<>(name);
    
    for (int i = 0; i < PARALLEL_REGIONS; i++) {
      String sName = Character.toString(stateName);
      String rName = "r" + sName;
      stateName++;
      
      builder
        .region(rName)
          .finalState("end" + sName);

      builder
        .region(rName)
          .initial()
            .transition("t" + transitionCount)
              .target(sName);
      transitionCount++;

      if (depth == 0) {
        builder
          .region(rName)
            .state(sName)
              .transition("t" + transitionCount)
                .on("go" + sName)
                .target("end" + sName);
      } else {
        builder
          .region(rName)
            .state(orthogonal(sName, depth - 1))
              .transition("t" + transitionCount)
                .target("end" + sName);
      }
      transitionCount++;
    }
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }

  public static final class Context extends SequentialContext {
  }
  
  private interface FsmRunSequence {
    
    void run(SequentialContext expected, StateMachineExecutor<Context> fsm, boolean pause);
    
  }

  private static final String STDOUT = "StateMachine: \"" + Test3.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"ortho\"\n" +
"      Region: \"rD\"\n" +
"        State: \"D\"\n" +
"          Region: \"rE\"\n" +
"            FinalState: \"endE\"\n" +
"            PseudoState: #16 kind: INITIAL\n" +
"            State: \"E\"\n" +
"            Transition: \"t8\" --- #16 -> \"E\"\n" +
"            Transition: \"t9\" --- \"E\" -> \"endE\"\n" +
"          Region: \"rF\"\n" +
"            PseudoState: #22 kind: INITIAL\n" +
"            State: \"F\"\n" +
"            FinalState: \"endF\"\n" +
"            Transition: \"t10\" --- #22 -> \"F\"\n" +
"            Transition: \"t11\" --- \"F\" -> \"endF\"\n" +
"        FinalState: \"endD\"\n" +
"        PseudoState: #10 kind: INITIAL\n" +
"        Transition: \"t7\" --- #10 -> \"D\"\n" +
"        Transition: \"t12\" --- \"D\" -> \"endD\"\n" +
"      Region: \"rA\"\n" +
"        FinalState: \"endA\"\n" +
"        PseudoState: #28 kind: INITIAL\n" +
"        State: \"A\"\n" +
"          Region: \"rB\"\n" +
"            FinalState: \"endB\"\n" +
"            PseudoState: #34 kind: INITIAL\n" +
"            State: \"B\"\n" +
"            Transition: \"t2\" --- #34 -> \"B\"\n" +
"            Transition: \"t3\" --- \"B\" -> \"endB\"\n" +
"          Region: \"rC\"\n" +
"            FinalState: \"endC\"\n" +
"            PseudoState: #40 kind: INITIAL\n" +
"            State: \"C\"\n" +
"            Transition: \"t4\" --- #40 -> \"C\"\n" +
"            Transition: \"t5\" --- \"C\" -> \"endC\"\n" +
"        Transition: \"t1\" --- #28 -> \"A\"\n" +
"        Transition: \"t6\" --- \"A\" -> \"endA\"\n" +
"    Transition: \"t0\" --- #4 -> \"ortho\"\n" +
"    Transition: \"t13\" --- \"ortho\" -> \"end\"";
}
