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

package com.lympid.core.behaviorstatemachines.pseudo.join;

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.builder.OrthogonalStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import org.junit.Test;

/**
 * Tests a fork vertex.
 * The orthogonal state is entered by a transition targeting its edge.
 * 
 * @author Fabien Renaud 
 */
public class Test3 extends AbstractStateMachineTest {
    
  @Test
  public void run_go1_go2() {
    SequentialContext expected = new SequentialContext();    
    Context ctx = new Context(5);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected, ctx);
    
    fsm.take(new StringEvent("go1"));
    expected.exit("A").effect("t2").enter("B");
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("ortho", "B").branch("ortho", "C").get());
    assertSequentialContextEquals(expected, ctx);
    
    fsm.take(new StringEvent("go2"));
    expected.exit("C").effect("t5").enter("D");
    expected
      .effect("t9")  // 5  % 2 != 0 -> 16
      .effect("t8")  // 16 % 2 == 0 -> 8
      .effect("t8")  // 8  % 2 == 0 -> 4
      .effect("t8")  // 4  % 2 == 0 -> 2
      .effect("t8"); // 2  % 2 == 0 -> 1
    expected.exit("B").exit("D").exit("ortho").effect("t6").effect("t3"); // join execution order
    
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("end").get());
    end(fsm, expected, ctx);
  }
  
  @Test
  public void run_go2_go1() {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context(5);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected, ctx);
    
    fsm.take(new StringEvent("go2"));
    expected.exit("C").effect("t5").enter("D")
      .effect("t9"); // 5  % 2 != 0 -> 16
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("ortho", "A").branch("ortho", "D").get());
    assertSequentialContextEquals(expected, ctx);
    
    fsm.take(new StringEvent("go1"));
    expected.exit("A").effect("t2").enter("B")
      .effect("t8")  // 16 % 2 == 0 -> 8
      .effect("t8")  // 8  % 2 == 0 -> 4
      .effect("t8")  // 4  % 2 == 0 -> 2
      .effect("t8")  // 2  % 2 == 0 -> 1
      .exit("D").exit("B").exit("ortho").effect("t6").effect("t3"); // join execution order
    
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("end").get());
    end(fsm, expected, ctx);
  }

  private void begin(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    expected
      .effect("t0").enter("ortho")
      .effect("t4").enter("C")
      .effect("t1").enter("A");
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("ortho", "A").branch("ortho", "C").get());
    assertSequentialContextEquals(expected, ctx);
  }
  private void end(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    expected.effect("t7");
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("end").get());
    assertSequentialContextEquals(expected, ctx);
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder(name());

    VertexBuilderReference end = builder
      .region()
        .finalState("end");

    builder
      .region()
        .initial()
          .transition("t0")
            .target("ortho");
    
    builder
      .region()
        .state(orthogonal(builder, "ortho"));
    
    builder
      .region()
        .join("myJoin")
          .transition("t7")
            .guard((c) -> c.c == 1)
            .target(end);

    return builder;
  }
  
  private OrthogonalStateBuilder orthogonal(final StateMachineBuilder b, final String name) {
    OrthogonalStateBuilder<Context> builder = new OrthogonalStateBuilder(name);
        
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
            .target("myJoin")
          .selfTransition("t8")
            .guard((e, c) -> c.c % 2 == 0)
            .effect((e, c) -> c.c /= 2)
            .target();
    
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
            .target("myJoin")
          .selfTransition("t9")
            .guard((e, c) -> c.c % 2 != 0)
            .effect((e, c) -> c.c = 3 * c.c + 1)
            .target();
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final class Context extends SequentialContext {
    int c;
    
    Context(int c) {
      this.c = c;
    }

    @Override
    public int hashCode() {
      return c;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Context other = (Context) obj;
      if (this.c != other.c) {
        return false;
      }
      return true;
    }
    
    
  }

  private static final String STDOUT = "StateMachine: \"" + Test3.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: \"myJoin\" kind: JOIN\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"ortho\"\n" +
"      Region: \"r2\"\n" +
"        State: \"D\"\n" +
"        PseudoState: #8 kind: INITIAL\n" +
"        State: \"C\"\n" +
"        Transition: \"t4\" --- #8 -> \"C\"\n" +
"        Transition: \"t5\" --- \"C\" -> \"D\"\n" +
"        Transition: \"t9\" -I- \"D\" -> \"D\"\n" +
"      Region: \"r1\"\n" +
"        PseudoState: #16 kind: INITIAL\n" +
"        State: \"A\"\n" +
"        State: \"B\"\n" +
"        Transition: \"t1\" --- #16 -> \"A\"\n" +
"        Transition: \"t2\" --- \"A\" -> \"B\"\n" +
"        Transition: \"t8\" -I- \"B\" -> \"B\"\n" +
"    Transition: \"t0\" --- #4 -> \"ortho\"\n" +
"    Transition: \"t6\" --- \"D\" -> \"myJoin\"\n" +
"    Transition: \"t3\" --- \"B\" -> \"myJoin\"\n" +
"    Transition: \"t7\" --- \"myJoin\" -> \"end\"";
}
