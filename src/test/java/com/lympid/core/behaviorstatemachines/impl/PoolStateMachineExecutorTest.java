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
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.StateMachineSnapshot;
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
public class PoolStateMachineExecutorTest {
  
  private static final long DELAY = 50;
  private StateMachineShardPoolExecutor pool;
  
  @Before
  public void setUp() {
    pool = new StateMachineShardPoolExecutor(1);
  }

  @Test
  public void run1() throws InterruptedException {
    run(new PoolStateMachineExecutor(pool), false);
  }

  @Test
  public void run1_pause() throws InterruptedException {
    run(new PoolStateMachineExecutor(pool), true);
  }

  @Test
  public void run2() throws InterruptedException {
    run(new PoolStateMachineExecutor(pool, 17), false);
  }
  
  private void run(final StateMachineExecutor<Context> fsm, final boolean pause) throws InterruptedException {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    
    fsm.configuration().executor(AbstractStateMachineTest.THREAD_POOL);
    fsm.setStateMachine(topLevelStateMachine().newInstance());
    fsm.setContext(ctx);
    fsm.go();
    
    pauseAndResume(fsm, pause);
    fsm.snapshot().context().latchA.await();
    expected.effect("t0").enter("A");
    assertSequentialContextEquals(expected, fsm);
    
    pauseAndResume(fsm, pause);
    fsm.snapshot().context().latchB.await();
    expected.exit("A").effect("t1").enter("B");
    assertSequentialContextEquals(expected, fsm);
    
    pauseAndResume(fsm, pause);
    fsm.snapshot().context().latchC.await();
    expected.activity("something").exit("B").effect("t2").enter("C");
    assertSequentialContextEquals(expected, fsm);
    
    pauseAndResume(fsm, pause);
    fsm.take(new StringEvent("go"));
    fsm.snapshot().context().latchEnd.await();
    Thread.sleep(2);
    expected.exit("C").effect("t3");
    assertSequentialContextEquals(expected, fsm);
  }
  
  private void pauseAndResume(final StateMachineExecutor<Context> fsm, final boolean pause) throws InterruptedException {
    if (pause) {
      StateMachineSnapshot snapshot = fsm.pause();
      fsm.take(new StringEvent("go"));
      fsm.resume(snapshot);
      Thread.sleep(10); // time for the queue to resume the execution of the state machine
    }
  }
  
  private StateMachineBuilder topLevelStateMachine() {
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
      this.latchA = inst.latchA;
      this.latchB = inst.latchB;
      this.latchC = inst.latchC;
      this.latchEnd = inst.latchEnd;
    }
    
    @Override
    public Context copy() {
      return new Context(this);
    }
  }

}
