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

package com.lympid.core.behaviorstatemachines.pseudo.junction;

import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import org.junit.Test;

/**
 * Test the full tree of possible path is traversed before picking one enabled.
 * 
 * @author Fabien Renaud 
 */
public class Test5 extends AbstractStateMachineTest {
  
  @Test
  public void t1_t4() {
    validRun(1, 4);
  }
  
  @Test
  public void t1_t5() {
    validRun(1, 5);
  }
  
  @Test
  public void t2_t6() {
    validRun(2, 6);
  }
  
  @Test
  public void t2_t7() {
    validRun(2, 7);
  }
  
  @Test
  public void t3_t8() {
    validRun(3, 8);
  }
  
  @Test
  public void t3_t9() {
    validRun(3, 9);
  }
  
  private void validRun(int i , int j) {
    SequentialContext expected = new SequentialContext()
      .effect("t0").effect("t" + i).effect("t" + j).effect("t" + (j + 6));
    
    Context ctx = new Context(j);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end").get());
    assertSequentialContextEquals(expected, ctx);
  }
  
  @Test(expected = RuntimeException.class)
  public void invalid_neg1() {
    invalidRun(-1);
  }
  
  @Test(expected = RuntimeException.class)
  public void invalid_0() {
    invalidRun(0);
  }
  
  @Test(expected = RuntimeException.class)
  public void invalid_1() {
    invalidRun(1);
  }
  
  @Test(expected = RuntimeException.class)
  public void invalid_2() {
    invalidRun(2);
  }
  
  private void invalidRun(int j) {
    Context ctx = new Context(j);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    builder
      .region()
        .finalState("end");
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target("j0");
    
    builder
      .region()
        .junction("j0")
          .transition("t1")
            .target("j1")
          .transition("t2")
            .target("j2")
          .transition("t3")
            .target("j3");
    
    builder
      .region()
        .junction("j1")
          .transition("t4")
            .target("j4")
          .transition("t5")
            .target("j5");
    
    builder
      .region()
        .junction("j2")
          .transition("t6")
            .target("j6")
          .transition("t7")
            .target("j7");
    
    builder
      .region()
        .junction("j3")
          .transition("t8")
            .target("j8")
          .transition("t9")
            .target("j9");
    
    for (int i = 0; i < 6; i++) {
      final int j = 4 + i;
      builder
        .region()
          .junction("j" + j)
            .transition("t1" + i)
              .guard((c) -> { return c.c == j; })
              .target("end");
    }
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final class Context extends SequentialContext {
    int c;

    public Context(int c) {
      this.c = c;
    }
    
    public Context() {
    }
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test5.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: \"j6\" kind: JUNCTION\n" +
"    PseudoState: \"j2\" kind: JUNCTION\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: \"j7\" kind: JUNCTION\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    PseudoState: \"j3\" kind: JUNCTION\n" +
"    PseudoState: \"j8\" kind: JUNCTION\n" +
"    PseudoState: \"j0\" kind: JUNCTION\n" +
"    PseudoState: \"j9\" kind: JUNCTION\n" +
"    PseudoState: \"j4\" kind: JUNCTION\n" +
"    PseudoState: \"j1\" kind: JUNCTION\n" +
"    PseudoState: \"j5\" kind: JUNCTION\n" +
"    Transition: \"t0\" --- #4 -> \"j0\"\n" +
"    Transition: \"t1\" --- \"j0\" -> \"j1\"\n" +
"    Transition: \"t2\" --- \"j0\" -> \"j2\"\n" +
"    Transition: \"t3\" --- \"j0\" -> \"j3\"\n" +
"    Transition: \"t4\" --- \"j1\" -> \"j4\"\n" +
"    Transition: \"t5\" --- \"j1\" -> \"j5\"\n" +
"    Transition: \"t6\" --- \"j2\" -> \"j6\"\n" +
"    Transition: \"t7\" --- \"j2\" -> \"j7\"\n" +
"    Transition: \"t8\" --- \"j3\" -> \"j8\"\n" +
"    Transition: \"t9\" --- \"j3\" -> \"j9\"\n" +
"    Transition: \"t10\" --- \"j4\" -> \"end\"\n" +
"    Transition: \"t11\" --- \"j5\" -> \"end\"\n" +
"    Transition: \"t12\" --- \"j6\" -> \"end\"\n" +
"    Transition: \"t13\" --- \"j7\" -> \"end\"\n" +
"    Transition: \"t14\" --- \"j8\" -> \"end\"\n" +
"    Transition: \"t15\" --- \"j9\" -> \"end\"";
}
