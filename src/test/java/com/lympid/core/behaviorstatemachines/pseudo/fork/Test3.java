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

package com.lympid.core.behaviorstatemachines.pseudo.fork;

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
 * Tests a fork vertex belonging to one of the regions of the orthogonal state.
 * 
 * @author Fabien Renaud 
 */
public class Test3 extends AbstractStateMachineTest {
  
  @Test
  public void run_end() {
    SequentialContext expected = new SequentialContext();
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    
    expected.exit("A").exit("C");
    fireEnd(fsm, expected);
  }
  
  @Test
  public void run_go1_end() {
    SequentialContext expected = new SequentialContext();    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    fireGo1(fsm, expected, "C");
    
    expected.exit("C");
    fireEnd(fsm, expected);
  }
  
  @Test
  public void run_go1_go2_end() {
    SequentialContext expected = new SequentialContext();    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    fireGo1(fsm, expected, "C");
    fireGo2(fsm, expected, "end1");
    fireEnd(fsm, expected);
  }
  
  @Test
  public void run_go2_end() {
    SequentialContext expected = new SequentialContext();    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    fireGo2(fsm, expected, "A");
    
    expected.exit("A");
    fireEnd(fsm, expected);
  }
  
  @Test
  public void run_go2_go1_end() {
    SequentialContext expected = new SequentialContext();    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    fireGo2(fsm, expected, "A");
    fireGo1(fsm, expected, "end2");
    fireEnd(fsm, expected);
  }

  private void begin(StateMachineExecutor fsm, SequentialContext expected) {
    expected
      .effect("t0").enter("ortho").enter("E");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ortho", "E"));
    assertSequentialContextEquals(expected, fsm);
    
    fsm.take(new StringEvent("doFork"));
    expected
      .exit("E").effect("tE")
        .effect("t1").enter("A")
        .effect("t4").enter("C");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ortho", "A").branch("ortho", "C"));
    assertSequentialContextEquals(expected, fsm);
  }

  private void fireGo1(StateMachineExecutor fsm, SequentialContext expected, String otherRegionState) {
    fsm.take(new StringEvent("go1"));
    expected.exit("A").effect("t2").enter("B").exit("B").effect("t3");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ortho", "end1").branch("ortho", otherRegionState));
    assertSequentialContextEquals(expected, fsm);
  }

  private void fireGo2(StateMachineExecutor fsm, SequentialContext expected, String otherRegionState) {
    fsm.take(new StringEvent("go2"));
    expected.exit("C").effect("t5").enter("D").exit("D").effect("t6");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ortho", otherRegionState).branch("ortho", "end2"));
    assertSequentialContextEquals(expected, fsm);
  }

  private void fireEnd(StateMachineExecutor fsm, SequentialContext expected) {
    fsm.take(new StringEvent("end"));
    expected.exit("ortho").effect("t7");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertSequentialContextEquals(expected, fsm);
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Object> builder = new StateMachineBuilder(name());

    VertexBuilderReference end = builder
      .region()
        .finalState("end");

    builder
      .region()
        .initial()
          .transition("t0")
            .target("E");
    
    builder
      .region()
        .state(orthogonal("ortho"))
          .transition("t7")
            .on("end")
            .target(end);

    return builder;
  }
  
  private OrthogonalStateBuilder orthogonal(final String name) {
    OrthogonalStateBuilder<Object> builder = new OrthogonalStateBuilder(name);
    
    builder
      .region("r1")
        .finalState("end1");
    
    VertexBuilderReference end1 = builder
      .region("r1")
        .finalState("end1"); // double call is on purpose to increase coverage
    
    builder
      .region("r1")
        .state("E")
          .transition("tE")
            .on("doFork")
            .target("myFork");
    
    builder
      .region("r1")
        .fork("myFork")
          .transition("t1")
            .target("A")
          .transition("t4")
            .target("C");
        
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
            .target(end1);
    
    builder
      .region("r2")
        .finalState("end2");
        
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

  private static final String STDOUT = "StateMachine: \"" + Test3.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"ortho\"\n" +
"      Region: \"r2\"\n" +
"        State: \"D\"\n" +
"        FinalState: \"end2\"\n" +
"        State: \"C\"\n" +
"        Transition: \"t5\" --- \"C\" -> \"D\"\n" +
"        Transition: \"t6\" --- \"D\" -> \"end2\"\n" +
"      Region: \"r1\"\n" +
"        State: \"B\"\n" +
"        FinalState: \"end1\"\n" +
"        State: \"E\"\n" +
"        PseudoState: \"myFork\" kind: FORK\n" +
"        State: \"A\"\n" +
"        Transition: \"tE\" --- \"E\" -> \"myFork\"\n" +
"        Transition: \"t1\" --- \"myFork\" -> \"A\"\n" +
"        Transition: \"t2\" --- \"A\" -> \"B\"\n" +
"        Transition: \"t3\" --- \"B\" -> \"end1\"\n" +
"    Transition: \"t0\" --- #4 -> \"E\"\n" +
"    Transition: \"t7\" --- \"ortho\" -> \"end\"\n" +
"    Transition: \"t4\" --- \"myFork\" -> \"C\"";
}
