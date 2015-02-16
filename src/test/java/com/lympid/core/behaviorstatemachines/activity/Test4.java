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

package com.lympid.core.behaviorstatemachines.activity;

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
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests activities in an orthogonal state.
 * 
 * @author Fabien Renaud 
 */
public class Test4 extends AbstractStateMachineTest {
  
  @Test
  public void run_end() throws InterruptedException {
    SequentialContext expected = new SequentialContext();
    
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected, ctx);
    
    expected.exit("B").exit("A");
    fireEnd(fsm, expected, ctx);
  }
  
  @Test
  public void run_go1_end() throws InterruptedException {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected, ctx);
    fireGo1(fsm, expected, ctx, "B");
    
    expected.exit("B");
    fireEnd(fsm, expected, ctx);
  }
  
  @Test
  public void run_go1_go2_end() throws InterruptedException {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected, ctx);
    fireGo1(fsm, expected, ctx, "B");
    fireGo2(fsm, expected, ctx, "end1");
    fireEnd(fsm, expected, ctx);
  }
  
  @Test
  public void run_go2_end() throws InterruptedException {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected, ctx);
    fireGo2(fsm, expected, ctx, "A");
    
    expected.exit("A");
    fireEnd(fsm, expected, ctx);
  }
  
  @Test
  public void run_go2_go1_end() throws InterruptedException {
    SequentialContext expected = new SequentialContext();    
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected, ctx);
    fireGo2(fsm, expected, ctx, "A");
    fireGo1(fsm, expected, ctx, "end2");
    fireEnd(fsm, expected, ctx);
  }

  private void begin(StateMachineExecutor fsm, SequentialContext expected, Context ctx) {
    expected
      .effect("t0").enter("ortho")
      .effect("t3").enter("B")
      .effect("t1").enter("A");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ortho", "A").branch("ortho", "B").get());
    assertSequentialContextEquals(expected, ctx);
  }

  private void fireGo1(StateMachineExecutor fsm, SequentialContext expected, Context ctx, String otherRegionState) {
    fsm.take(new StringEvent("go1"));
    expected.exit("A").effect("t2");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ortho", "end1").branch("ortho", otherRegionState).get());
    assertSequentialContextEquals(expected, ctx);
  }

  private void fireGo2(StateMachineExecutor fsm, SequentialContext expected, Context ctx, String otherRegionState) {
    fsm.take(new StringEvent("go2"));
    expected.exit("B").effect("t4");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ortho", otherRegionState).branch("ortho", "end2").get());
    assertSequentialContextEquals(expected, ctx);
  }

  private void fireEnd(StateMachineExecutor fsm, SequentialContext expected, Context ctx) throws InterruptedException {
    ctx.latch.await();
    
    fsm.take(new StringEvent("end"));
    expected.exit("ortho").effect("t5");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end").get());
    assertSequentialContextEquals(expected, ctx);
    assertEquals(1, ctx.c);
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
        .state(orthogonal("ortho"))
          .transition("t5")
            .on("end")
            .target(end);

    return builder;
  }
  
  private OrthogonalStateBuilder<Context> orthogonal(final String name) {
    OrthogonalStateBuilder<Context> builder = new OrthogonalStateBuilder(name);
    builder
      .activity((c) -> {
        c.c++;
        c.latch.countDown();
      });
    
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
      .region("r2")
        .finalState("end2");
    
    builder
      .region("r2")
        .initial()
          .transition("t3")
            .target("B");
    
    builder
      .region("r2")
        .state("B")
          .transition("t4")
            .on("go2")
            .target("end2");
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final class Context extends SequentialContext {
    CountDownLatch latch = new CountDownLatch(1);
    volatile int c;
  }

  private static final String STDOUT = "StateMachine: \"" + Test4.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"ortho\"\n" +
"      Region: \"r2\"\n" +
"        State: \"B\"\n" +
"        FinalState: \"end2\"\n" +
"        PseudoState: #10 kind: INITIAL\n" +
"        Transition: \"t3\" --- #10 -> \"B\"\n" +
"        Transition: \"t4\" --- \"B\" -> \"end2\"\n" +
"      Region: \"r1\"\n" +
"        FinalState: \"end1\"\n" +
"        PseudoState: #16 kind: INITIAL\n" +
"        State: \"A\"\n" +
"        Transition: \"t1\" --- #16 -> \"A\"\n" +
"        Transition: \"t2\" --- \"A\" -> \"end1\"\n" +
"    Transition: \"t0\" --- #4 -> \"ortho\"\n" +
"    Transition: \"t5\" --- \"ortho\" -> \"end\"";
}
