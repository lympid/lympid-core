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
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.OrthogonalStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import org.junit.Test;

/**
 * Tests an orthogonal state with:
 *  - named internal transition
 *  - named local transition
 *  - unamed internal transition
 *  - unamed local transition
 *  - unamed external transition
 * @author Fabien Renaud 
 */
public class Test4 extends AbstractStateMachineTest {
  
  @Test
  public void run_end() {
    SequentialContext expected = new SequentialContext();    
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    
    expected.exit("C").exit("A");
    fireEnd(fsm, expected);
  }
  
  @Test
  public void run_go1_end() {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    fireGoB(fsm, expected, "C");
    
    expected.exit("C");
    fireEnd(fsm, expected);
  }
  
  @Test
  public void run_go1_go2_end() {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    fireGoB(fsm, expected, "C");
    fireGoD(fsm, expected, "end1");
    fireEnd(fsm, expected);
  }
  
  @Test
  public void run_go2_end() {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    fireGoD(fsm, expected, "A");
    
    expected.exit("A");
    fireEnd(fsm, expected);
  }
  
  @Test
  public void run_go2_go1_end() {
    SequentialContext expected = new SequentialContext();    
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    fireGoD(fsm, expected, "A");
    fireGoB(fsm, expected, "end2");
    fireEnd(fsm, expected);
  }

  private void begin(StateMachineExecutor fsm, SequentialContext expected) {
    expected
      .effect("t0").enter("ortho")
      .effect("t5").enter("C")
      .effect("t1").enter("A");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("#6", "A").branch("#6", "C"));
    assertSequentialContextEquals(expected, fsm);
  }

  private void fireGoB(StateMachineExecutor fsm, SequentialContext expected, String otherRegionState) {
    ActiveStateTree active = new ActiveStateTree(this).branch("#6", "end1").branch("#6", otherRegionState);
    
    fsm.take(new StringEvent("goB"));
    expected.exit("A").effect("t3").enter("B").exit("B").effect("t4");
    assertSnapshotEquals(fsm, active);
    assertSequentialContextEquals(expected, fsm);
    
    fsm.take(new StringEvent("internal1"));
    expected.effect("self1");
    assertSnapshotEquals(fsm, active);
    assertSequentialContextEquals(expected, fsm);
    
    fsm.take(new StringEvent("internal2"));
    expected.effect("self2");
    assertSnapshotEquals(fsm, active);
    assertSequentialContextEquals(expected, fsm);
  }

  private void fireGoD(StateMachineExecutor fsm, SequentialContext expected, String otherRegionState) {
    ActiveStateTree active = new ActiveStateTree(this).branch("#6", otherRegionState).branch("#6", "end2");
    
    fsm.take(new StringEvent("goD"));
    expected.exit("C").effect("t7").enter("D").exit("D").effect("t8");
    assertSnapshotEquals(fsm, active);
    assertSequentialContextEquals(expected, fsm);
    
    fsm.take(new StringEvent("internal1"));
    expected.effect("self1");
    assertSnapshotEquals(fsm, active);
    assertSequentialContextEquals(expected, fsm);
    
    fsm.take(new StringEvent("internal2"));
    expected.effect("self2");
    assertSnapshotEquals(fsm, active);
    assertSequentialContextEquals(expected, fsm);
  }

  private void fireEnd(StateMachineExecutor fsm, SequentialContext expected) {
    fsm.take(new StringEvent("end"));
    expected.exit("ortho").effect("t9");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertSequentialContextEquals(expected, fsm);
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder(name());

    VertexBuilderReference end = builder
      .region()
        .finalState("end");
    
    OrthogonalStateBuilder<Context> ortho = orthogonal();

    builder
      .region()
        .initial()
          .transition("t0")
            .target(ortho);
    
    builder
      .region()
        .state(ortho)
          .entry((c) -> c.enter("ortho"))
          .exit((c) -> c.exit("ortho"))
          .selfTransition()
            .on("internal1")
            .effect((e,c) -> c.effect("self1"))
            .target()
          .selfTransition("self2")
            .on("internal2")
            .target()
          .transition()
            .on("end")
            .effect((e,c) -> c.effect("t9"))
            .target(end);

    return builder;
  }
  
  private OrthogonalStateBuilder<Context> orthogonal() {
    OrthogonalStateBuilder<Context> builder = new OrthogonalStateBuilder();
    
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
            .target("end1");
    
    builder
      .localTransition("t3")
        .on("goB")
        .target("B");
    
    builder
      .region("r1")
        .state("B")
          .transition("t4")
            .target("end1");
    
    builder
      .region("r2")
        .finalState("end2");
    
    builder
      .region("r2")
        .initial()
          .transition("t5")
            .target("C");
    
    builder
      .region("r2")
        .state("C")
          .transition("t6")
            .on("go2")
            .target("end2");
    
    builder
      .localTransition()
        .on("goD")
        .effect((e,c) -> c.effect("t7"))
        .target("D");
    
    builder
      .region("r2")
        .state("D")
          .transition("t8")
            .target("end2");
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final class Context extends SequentialContext {
  }

  private static final String STDOUT = "StateMachine: \"" + Test4.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: #6\n" +
"      Region: \"r2\"\n" +
"        FinalState: \"end2\"\n" +
"        PseudoState: #14 kind: INITIAL\n" +
"        State: \"C\"\n" +
"        State: \"D\"\n" +
"        Transition: #8 -L- #6 -> \"D\"\n" +
"        Transition: \"t5\" --- #14 -> \"C\"\n" +
"        Transition: \"t6\" --- \"C\" -> \"end2\"\n" +
"        Transition: \"t8\" --- \"D\" -> \"end2\"\n" +
"      Region: \"r1\"\n" +
"        PseudoState: #22 kind: INITIAL\n" +
"        State: \"A\"\n" +
"        State: \"B\"\n" +
"        FinalState: \"end1\"\n" +
"        Transition: \"t3\" -L- #6 -> \"B\"\n" +
"        Transition: \"t1\" --- #22 -> \"A\"\n" +
"        Transition: \"t2\" --- \"A\" -> \"end1\"\n" +
"        Transition: \"t4\" --- \"B\" -> \"end1\"\n" +
"    Transition: \"t0\" --- #4 -> #6\n" +
"    Transition: #9 -I- #6 -> #6\n" +
"    Transition: \"self2\" -I- #6 -> #6\n" +
"    Transition: #11 --- #6 -> \"end\"";
}
