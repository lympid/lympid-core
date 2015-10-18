/*
 * Copyright 2015 Lympid.
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
package com.lympid.core.behaviorstatemachines.impl;

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.StateMachineSnapshot;
import com.lympid.core.behaviorstatemachines.StateMachineTest;
import com.lympid.core.behaviorstatemachines.builder.SequentialContextInjector;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import static com.lympid.core.common.TestUtils.assertSequentialContextEquals;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class LockStateMachineExecutorTest implements StateMachineTest {
  
  private static final long DELAY = 50;
  private StateMachine machine;

  @Test
  public void run1() throws InterruptedException {
    run(0, false);
  }
  
  @Test
  public void run1_pause() throws InterruptedException {
    run(0, true);
  }

  @Test
  public void run2() throws InterruptedException {
    run(12, false);
  }
  
  @Test(expected = RuntimeException.class)
  public void go_noExecutor() {
    StateMachineExecutor fsm = new LockStateMachineExecutor.Builder()
      .setStateMachine(topLevelStateMachine())
      .setContext(new Context())
      .build();
    fsm.go();
  }
  
  private StateMachineExecutor<Context> fsm(final int id, final Context ctx) {
    ExecutorConfiguration config = new ExecutorConfiguration()
      .executor(AbstractStateMachineTest.THREAD_POOL);
    
    return new LockStateMachineExecutor.Builder()
      .setId(id)
      .setStateMachine(topLevelStateMachine())
      .setContext(ctx)
      .setConfiguration(config)
      .build();
  }
  
  private StateMachineExecutor<Context> fsm(final int id, final StateMachineSnapshot snapshot) {
    ExecutorConfiguration config = new ExecutorConfiguration()
      .executor(AbstractStateMachineTest.THREAD_POOL);
    
    return new LockStateMachineExecutor.Builder()
      .setId(id)
      .setStateMachine(topLevelStateMachine())
      .setSnapshot(snapshot)
      .setConfiguration(config)
      .build();
  }
  
  private void run(final int id, final boolean pause) throws InterruptedException {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    
    StateMachineExecutor<Context> fsm = fsm(id, ctx);
    fsm.go();
    
    expected.effect("t0").enter("A");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));
    
    pauseAndResume(expected, fsm, ctx, pause, this::runPart2);
  }
  
  private void runPart2(final SequentialContext expected, final StateMachineExecutor<Context> fsm, final Context ctx, final boolean pause) throws InterruptedException {
    ctx.latch1.await(10 * DELAY, TimeUnit.MILLISECONDS);
    Thread.sleep(DELAY);
    expected.exit("A").effect("t1").enter("B");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("B"));
    
    pauseAndResume(expected, fsm, ctx, pause, this::runPart3);
  }
  
  private void runPart3(final SequentialContext expected, final StateMachineExecutor<Context> fsm, final Context ctx, final boolean pause) throws InterruptedException {
    ctx.latch2.countDown();
    Thread.sleep(DELAY);
    expected.exit("B").effect("t2").enter("C");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("C"));
    
    pauseAndResume(expected, fsm, ctx, pause, this::runPart4);
  }
  
  
  private void runPart4(final SequentialContext expected, final StateMachineExecutor<Context> fsm, final Context ctx, final boolean pause) throws InterruptedException {
    fsm.take(new StringEvent("go"));
    expected.exit("C").effect("t3");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }
  
  private void pauseAndResume(final SequentialContext expected1, final StateMachineExecutor fsm1, final Context ctx, final boolean pause, final FsmRunSequence sequence) throws InterruptedException {
    if (!pause) {
      sequence.run(expected1, fsm1, ctx, pause);
      return;
    }
    
    fsm1.pause();
    StateMachineSnapshot<Context> snapshot1 = fsm1.snapshot();
    SequentialContext expected2 = expected1.copy();
    
    /*
     * First state machine
     */
    fsm1.take(new StringEvent("go"));
    fsm1.resume();
    sequence.run(expected1, fsm1, ctx, pause);

    /*
     * Second/cloned state machine
     */
    StateMachineExecutor fsm2 = fsm(fsm1.getId(), snapshot1);
    assertSnapshotEquals(snapshot1, fsm2);

    fsm2.take(new StringEvent("go"));
    assertSnapshotEquals(snapshot1, fsm2);

    fsm2.resume();
    assertSnapshotEquals(snapshot1, fsm2);

    sequence.run(expected2, fsm2, snapshot1.context(), pause);
  }

  @Override
  public StateMachine topLevelStateMachine() {
    if (machine == null) {
      machine = topLevelMachineBuilder().newInstance();
    }
    return machine;
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder("noname");
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target("A");
    
    builder
      .region()
        .state("A")
          .transition("t1")
            .after(DELAY, TimeUnit.MILLISECONDS)
            .effect((e, c) -> c.latch1.countDown())
            .target("B");
    
    builder
      .region()
        .state("B")
          .activity((c) -> {
            try {
              c.latch2.await();
            } catch (InterruptedException ex) {
              throw new RuntimeException(ex);
            }
          })
          .transition("t2")
            .target("C");
    
    builder
      .region()
        .state("C")
          .transition("t3")
            .on("go")
            .target("end");
    
    builder
      .region()
        .finalState("end");
    
    builder.accept(new SequentialContextInjector());
    
    return builder;
  }
  
  private static interface FsmRunSequence {
    
    void run(SequentialContext expected, StateMachineExecutor fsm, Context ctx, boolean pause) throws InterruptedException;
    
  }
  
  private static final class Context extends SequentialContext {
    
    final CountDownLatch latch1;
    final CountDownLatch latch2;
    
    public Context() {
      this.latch1 = new CountDownLatch(1);
      this.latch2 = new CountDownLatch(1);
    }
    
    public Context(final Context inst) {
      super(inst);
      this.latch1 = new CountDownLatch((int) inst.latch1.getCount());
      this.latch2 = new CountDownLatch((int) inst.latch2.getCount());
    }
    
    @Override
    public Context copy() {
      return new Context(this);
    }
  }

}
