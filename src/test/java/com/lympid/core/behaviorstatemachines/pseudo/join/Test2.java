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
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.OrthogonalStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;

/**
 * Tests a fork vertex.
 * The orthogonal state is entered by a transition targeting its edge.
 * 
 * @author Fabien Renaud 
 */
public class Test2 extends AbstractStateMachineTest {
    
  private static final long WAIT_TIME = 10000;
  
  @Test
  public void run_go1_go2() throws InterruptedException {
    SequentialContext expected = new SequentialContext();    
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    
    fsm.take(new StringEvent("go1"));
    expected.exit("A").effect("t2").enter("B");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ortho", "B").branch("ortho", "C"));
    assertSequentialContextEquals(expected, fsm);
    
    ctx.latch.await();
    
    fsm.take(new StringEvent("go2"));
    expected.exit("C").effect("t5").enter("D")
      .exit("B").exit("D").activity("interrupted").exit("ortho").effect("t6").effect("t3"); // join execution order
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    end(fsm, expected);
  }
  
  @Test
  public void run_go2_go1() throws InterruptedException {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    
    fsm.take(new StringEvent("go2"));
    expected.exit("C").effect("t5").enter("D");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ortho", "A").branch("ortho", "D"));
    assertSequentialContextEquals(expected, fsm);
    
    ctx.latch.await();
    
    fsm.take(new StringEvent("go1"));
    expected.exit("A").effect("t2").enter("B")
      .exit("D").exit("B").activity("interrupted").exit("ortho").effect("t3").effect("t6"); // join execution order
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    end(fsm, expected);
  }

  private void begin(StateMachineExecutor fsm, SequentialContext expected) {
    expected
      .effect("t0").enter("ortho")
      .effect("t4").enter("C")
      .effect("t1").enter("A");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ortho", "A").branch("ortho", "C"));
    assertSequentialContextEquals(expected, fsm);
  }
  private void end(StateMachineExecutor fsm, SequentialContext expected) {
    expected.effect("t7");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertSequentialContextEquals(expected, fsm);
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
        .join("myJoin");
    
    builder
      .region()
        .join("myJoin") // double-call to increase coverage
          .transition()
            .effect((c) -> c.effect("t7"))
            .target(end);

    return builder;
  }
  
  private OrthogonalStateBuilder<Context> orthogonal(final StateMachineBuilder b, final String name) {
    OrthogonalStateBuilder<Context> builder = new OrthogonalStateBuilder(name);
    
    builder
      .activity(OrthoBehavior.class);
    
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
            .target("myJoin");
    
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
            .target("myJoin");
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final class Context extends SequentialContext {
    CountDownLatch latch = new CountDownLatch(1);
    AtomicBoolean activityStarted = new AtomicBoolean();
  }
  
  public static final class OrthoBehavior implements StateBehavior<Context> {

    @Override
    public void accept(Context c) {
      c.latch.countDown();
      try {
        Thread.sleep(WAIT_TIME);
      } catch (InterruptedException ex) {
        c.activity("interrupted");
      }
    }
    
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test2.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"ortho\"\n" +
"      Region: \"r2\"\n" +
"        State: \"D\"\n" +
"        PseudoState: #8 kind: INITIAL\n" +
"        State: \"C\"\n" +
"        Transition: \"t4\" --- #8 -> \"C\"\n" +
"        Transition: \"t5\" --- \"C\" -> \"D\"\n" +
"      Region: \"r1\"\n" +
"        PseudoState: #15 kind: INITIAL\n" +
"        State: \"A\"\n" +
"        State: \"B\"\n" +
"        Transition: \"t1\" --- #15 -> \"A\"\n" +
"        Transition: \"t2\" --- \"A\" -> \"B\"\n" +
"    PseudoState: \"myJoin\" kind: JOIN\n" +
"    Transition: \"t0\" --- #4 -> \"ortho\"\n" +
"    Transition: \"t6\" --- \"D\" -> \"myJoin\"\n" +
"    Transition: \"t3\" --- \"B\" -> \"myJoin\"\n" +
"    Transition: #22 --- \"myJoin\" -> \"end\"";
}
