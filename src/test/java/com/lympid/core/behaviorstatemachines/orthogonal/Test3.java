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
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.builder.OrthogonalStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import org.junit.Test;

/**
 * 
 * @author Fabien Renaud 
 */
public class Test3 extends AbstractStateMachineTest {
   
  private static final int PARALLEL_REGIONS = 2;
  private static final int DEPTH = 2;
  
  @Test
  public void run_noevents() {
    SequentialContext expected = new SequentialContext();    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected, ctx);
  }
  
  @Test
  public void run_e_f_b_c() {
    SequentialContext expected = new SequentialContext();    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected, ctx);
    
    fsm.take(new StringEvent("goE"));
    expected.exit("E").effect("t9");
    ActiveStateTree tree = new ActiveStateTree()
      .branch("ortho", "D", "endE")
      .branch("ortho", "D", "F")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
    
    fsm.take(new StringEvent("goF"));
    expected.exit("F").effect("t11")
      .exit("D").effect("t12");
    tree = new ActiveStateTree()
      .branch("ortho", "endD")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
    
    fsm.take(new StringEvent("goB"));
    expected.exit("B").effect("t3");
    tree = new ActiveStateTree()
      .branch("ortho", "endD")
      .branch("ortho", "A", "endB")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
    
    fsm.take(new StringEvent("goC"));
    expected.exit("C").effect("t5")
      .exit("A").effect("t6")
      .exit("ortho").effect("t13");
    tree = new ActiveStateTree("end");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
  }
  
  @Test
  public void run_f_e_b_c() {
    SequentialContext expected = new SequentialContext();    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected, ctx);
    
    fsm.take(new StringEvent("goF"));
    expected.exit("F").effect("t11");
    ActiveStateTree tree = new ActiveStateTree()
      .branch("ortho", "D", "E")
      .branch("ortho", "D", "endF")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
    
    fsm.take(new StringEvent("goE"));
    expected.exit("E").effect("t9")
      .exit("D").effect("t12");
    tree = new ActiveStateTree()
      .branch("ortho", "endD")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
    
    fsm.take(new StringEvent("goB"));
    expected.exit("B").effect("t3");
    tree = new ActiveStateTree()
      .branch("ortho", "endD")
      .branch("ortho", "A", "endB")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
    
    fsm.take(new StringEvent("goC"));
    expected.exit("C").effect("t5")
      .exit("A").effect("t6")
      .exit("ortho").effect("t13");
    tree = new ActiveStateTree("end");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
  }
  
  @Test
  public void run_e_f_c_b() {
    SequentialContext expected = new SequentialContext();    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected, ctx);
    
    fsm.take(new StringEvent("goE"));
    expected.exit("E").effect("t9");
    ActiveStateTree tree = new ActiveStateTree()
      .branch("ortho", "D", "endE")
      .branch("ortho", "D", "F")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
    
    fsm.take(new StringEvent("goF"));
    expected.exit("F").effect("t11")
      .exit("D").effect("t12");
    tree = new ActiveStateTree()
      .branch("ortho", "endD")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
    
    fsm.take(new StringEvent("goC"));
    expected.exit("C").effect("t5");
    tree = new ActiveStateTree()
      .branch("ortho", "endD")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "endC");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
    
    fsm.take(new StringEvent("goB"));
    expected.exit("B").effect("t3")
      .exit("A").effect("t6")
      .exit("ortho").effect("t13");
    tree = new ActiveStateTree("end");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
  }
  
  @Test
  public void run_e_b_f_c() {
    SequentialContext expected = new SequentialContext();    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected, ctx);
    
    fsm.take(new StringEvent("goE"));
    expected.exit("E").effect("t9");
    ActiveStateTree tree = new ActiveStateTree()
      .branch("ortho", "D", "endE")
      .branch("ortho", "D", "F")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
    
    fsm.take(new StringEvent("goB"));
    expected.exit("B").effect("t3");
    tree = new ActiveStateTree()
      .branch("ortho", "D", "endE")
      .branch("ortho", "D", "F")
      .branch("ortho", "A", "endB")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
    
    fsm.take(new StringEvent("goF"));
    expected.exit("F").effect("t11")
      .exit("D").effect("t12");
    tree = new ActiveStateTree()
      .branch("ortho", "endD")
      .branch("ortho", "A", "endB")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
    
    fsm.take(new StringEvent("goC"));
    expected.exit("C").effect("t5")
      .exit("A").effect("t6")
      .exit("ortho").effect("t13");
    tree = new ActiveStateTree("end");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
  }

  private void begin(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    expected
      .effect("t0").enter("ortho")
        .effect("t7").enter("D")
          .effect("t8").enter("E")
          .effect("t10").enter("F")
        .effect("t1").enter("A")
          .effect("t2").enter("B")
          .effect("t4").enter("C");
    ActiveStateTree tree = new ActiveStateTree()
      .branch("ortho", "D", "E")
      .branch("ortho", "D", "F")
      .branch("ortho", "A", "B")
      .branch("ortho", "A", "C");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, tree);
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder builder = new StateMachineBuilder(name());

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
  
  private OrthogonalStateBuilder orthogonal(final String name, int depth) {
    OrthogonalStateBuilder builder = new OrthogonalStateBuilder(name);
    
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
