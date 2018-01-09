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

package com.lympid.core.behaviorstatemachines.time.relative;

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.OrthogonalStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Tests parallel time transitions in an orthogonal state.
 * 
 * @author Fabien Renaud 
 */
public class Test6 extends AbstractStateMachineTest {
  
  private static final long DELAY_REGION_1 = 10;
  private static final long DELAY_REGION_2 = 50;
    
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
  public void run_timer1_end() throws InterruptedException {
    SequentialContext expected = new SequentialContext();
    
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    waitTimer1(fsm, expected, ctx, "C");
    
    expected.exit("C");
    fireEnd(fsm, expected);
  }
  
  @Test
  public void run_timer1_timer2_end() throws InterruptedException {
    SequentialContext expected = new SequentialContext();
    
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected);
    waitTimer1(fsm, expected, ctx, "C");
    waitTimer2(fsm, expected, ctx, "end1");
    fireEnd(fsm, expected);
  }

  private void begin(StateMachineExecutor fsm, SequentialContext expected) {
    expected
      .effect("t0").enter("ortho")
      .effect("t4").enter("C")
      .effect("t1").enter("A");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ortho", "A").branch("ortho", "C"));
    assertSequentialContextEquals(expected, fsm);
  }

  private void waitTimer1(StateMachineExecutor fsm, SequentialContext expected, Context ctx, String otherRegionState) throws InterruptedException {
    ctx.latch1.await(DELAY_REGION_2, TimeUnit.MILLISECONDS);
    expected.exit("A").effect("t2").enter("B").exit("B").effect("t3");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ortho", "end1").branch("ortho", otherRegionState));
    assertSequentialContextEquals(expected, fsm);
  }

  private void waitTimer2(StateMachineExecutor fsm, SequentialContext expected, Context ctx, String otherRegionState) throws InterruptedException {
    ctx.latch2.await(DELAY_REGION_2, TimeUnit.MILLISECONDS);
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
    StateMachineBuilder builder = new StateMachineBuilder<>(name());

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
        .state(orthogonal("ortho"))
          .transition("t7")
            .on("end")
            .target(end);

    return builder;
  }
  
  private OrthogonalStateBuilder orthogonal(final String name) {
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
            .after(DELAY_REGION_1, TimeUnit.MILLISECONDS)
            .effect((e, c) -> c.latch1.countDown())
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
            .after(DELAY_REGION_2, TimeUnit.MILLISECONDS)
            .effect((e, c) -> c.latch2.countDown())
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
  
  private static final class Context extends SequentialContext {
    CountDownLatch latch1 = new CountDownLatch(1);
    CountDownLatch latch2 = new CountDownLatch(1);
  }

  private static final String STDOUT = "StateMachine: \"" + Test6.class.getSimpleName() + "\"\n" +
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
