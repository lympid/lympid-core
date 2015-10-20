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
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class PoolStateMachineExecutorTest implements StateMachineTest {
  
  private static final long DELAY = 50;
  private StateMachineShardPoolExecutor pool;
  private StateMachine machine;
  
  @Before
  public void setUp() {
    pool = new StateMachineShardPoolExecutor(1);
  }

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
    run(17, false);
  }
  
  private StateMachineExecutor<Context> fsm(final int id, final Context ctx) {
    return new PoolStateMachineExecutor.Builder(pool)
      .setId(id)
      .setStateMachine(topLevelStateMachine())
      .setContext(ctx)
      .setConfiguration(new ExecutorConfiguration().executor(AbstractStateMachineTest.THREAD_POOL))
      .build();
  }
  
  private StateMachineExecutor<Context> fsm(final int id, final StateMachineSnapshot snapshot) {
    return new PoolStateMachineExecutor.Builder(pool)
      .setId(id)
      .setStateMachine(topLevelStateMachine())
      .setSnapshot(snapshot)
      .setConfiguration(new ExecutorConfiguration().executor(AbstractStateMachineTest.THREAD_POOL))
      .build();
  }
  
  private void run(final int id, final boolean pause) throws InterruptedException {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    
    StateMachineExecutor<Context> fsm = fsm(id, ctx);
    fsm.go();
    
    pauseAndResume(expected, fsm, ctx, pause, this::run_Part2);
  }
  
  private void run_Part2(final SequentialContext expected, final StateMachineExecutor<Context> fsm,  final Context ctx, final boolean pause) throws InterruptedException {
    ctx.latchA.await();
    expected.effect("t0").enter("A");
    assertSequentialContextEquals(expected, fsm);
    
    pauseAndResume(expected, fsm, ctx, pause, this::run_Part3);
  }
  
  private void run_Part3(final SequentialContext expected, final StateMachineExecutor<Context> fsm,  final Context ctx, final boolean pause) throws InterruptedException {
    ctx.latchB.await();
    expected.exit("A").effect("t1").enter("B");
    assertSequentialContextEquals(expected, fsm);
    
    pauseAndResume(expected, fsm, ctx, pause, this::run_Part4);
  }
  
  private void run_Part4(final SequentialContext expected, final StateMachineExecutor<Context> fsm,  final Context ctx, final boolean pause) throws InterruptedException {
    ctx.latchC.await();
    expected.activity("something").exit("B").effect("t2").enter("C");
    assertSequentialContextEquals(expected, fsm);
    
    pauseAndResume(expected, fsm, ctx, pause, this::run_Part5);
  }
  
  private void run_Part5(final SequentialContext expected, final StateMachineExecutor<Context> fsm,  final Context ctx, final boolean pause) throws InterruptedException {
    fsm.take(new StringEvent("go"));
    ctx.latchEnd.await();
    Thread.sleep(2);
    expected.exit("C").effect("t3");
    assertSequentialContextEquals(expected, fsm);
  }
  
  private void pauseAndResume(final SequentialContext expected1, final StateMachineExecutor<Context> fsm1,  final Context ctx, final boolean pause, final FsmRunSequence sequence) throws InterruptedException {
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
      machine = topLevelMachineBuilder().instance();
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
          .entry((c) -> c.latchA.countDown())
          .transition("t1")
            .after(DELAY, TimeUnit.MILLISECONDS)
            .target("B");
    
    builder
      .region()
        .state("B")
          .entry((c) -> c.latchB.countDown())
          .activity((c) -> {
            try {
              Thread.sleep(DELAY);
              c.activity("something");
            } catch (Exception ex) {
              throw new RuntimeException(ex);
            }
          })
          .transition("t2")
            .target("C");
    
    builder
      .region()
        .state("C")
          .entry((c) -> c.latchC.countDown())
          .transition("t3")
            .on("go")
            .effect((e, c) -> c.latchEnd.countDown())
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
  
  protected static final class Context extends SequentialContext {
    
    private final CountDownLatch latchA;
    private final CountDownLatch latchB;
    private final CountDownLatch latchC;
    private final CountDownLatch latchEnd;
    
    public Context() {
      this.latchA = new CountDownLatch(1);
      this.latchB = new CountDownLatch(1);
      this.latchC = new CountDownLatch(1);
      this.latchEnd = new CountDownLatch(1);
    }
    
    public Context(final Context inst) {
      super(inst);
      this.latchA = new CountDownLatch((int) inst.latchA.getCount());
      this.latchB = new CountDownLatch((int) inst.latchB.getCount());
      this.latchC = new CountDownLatch((int) inst.latchC.getCount());
      this.latchEnd = new CountDownLatch((int) inst.latchEnd.getCount());
    }
    
    @Override
    public Context copy() {
      return new Context(this);
    }
  }

}
