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
public class PoolStateMachineExecutorTest {
  
  private static final long DELAY = 10;

  @Test
  public void run1() throws InterruptedException {
    StateMachineShardPoolExecutor pool = new StateMachineShardPoolExecutor(1);
    PoolStateMachineExecutor fsm = new PoolStateMachineExecutor(pool);
    run(fsm);
  }

  @Test
  public void run2() throws InterruptedException {
    StateMachineShardPoolExecutor pool = new StateMachineShardPoolExecutor(1);
    PoolStateMachineExecutor fsm = new PoolStateMachineExecutor(pool, 12);
    run(fsm);
  }
  
  private void run(final StateMachineExecutor fsm) throws InterruptedException {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    
    fsm.configuration().executor(AbstractStateMachineTest.THREAD_POOL);
    fsm.setStateMachine(topLevelStateMachine().newInstance());
    fsm.setContext(ctx);
    fsm.go();
    
    ctx.latch01.await();
    expected.effect("t0");
    assertSequentialContextEquals(expected, ctx);
    ctx.latch02.countDown();
    
    ctx.latch11.await();
    expected.enter("A").exit("A").effect("t1");
    assertSequentialContextEquals(expected, ctx);
    ctx.latch12.countDown();
    
    
    ctx.latch21.await();
    expected.enter("B").exit("B").effect("t2");
    assertSequentialContextEquals(expected, ctx);
    ctx.latch22.countDown();
    
    
    fsm.take(new StringEvent("go"));
    ctx.latch31.await();
    expected.enter("C").exit("C").effect("t3");
    assertSequentialContextEquals(expected, ctx);
    ctx.latch32.countDown();
  }
  
  private StateMachineBuilder topLevelStateMachine() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder("noname");
    
    builder
      .region()
        .initial()
          .transition("t0")
            .effect((c) -> c.latch0())
            .target("A");
    
    builder
      .region()
        .state("A")
          .transition("t1")
            .after(DELAY, TimeUnit.MILLISECONDS)
            .effect((e, c) -> c.latch1())
            .target("B");
    
    builder
      .region()
        .state("B")
          .activity((c) -> {})
          .transition("t2")
            .effect((e, c) -> c.latch2())
            .target("C");
    
    builder
      .region()
        .state("C")
          .transition("t3")
            .on("go")
            .effect((e, c) -> c.latch3())
            .target("end");
    
    builder
      .region()
        .finalState("end");
    
    builder.accept(new SequentialContextInjector());
    
    return builder;
  }
  
  protected static final class Context extends SequentialContext {
    
    CountDownLatch latch01 = new CountDownLatch(1);
    CountDownLatch latch02 = new CountDownLatch(1);
    CountDownLatch latch11 = new CountDownLatch(1);
    CountDownLatch latch12 = new CountDownLatch(1);
    CountDownLatch latch21 = new CountDownLatch(1);
    CountDownLatch latch22 = new CountDownLatch(1);
    CountDownLatch latch31 = new CountDownLatch(1);
    CountDownLatch latch32 = new CountDownLatch(1);
    
    void latch0() {
      latch(latch01, latch02);
    }
    
    void latch1() {
      latch(latch11, latch12);
    }
    
    void latch2() {
      latch(latch21, latch22);
    }
    
    
    void latch3() {
      latch(latch31, latch32);
    }
    
    void latch(CountDownLatch latchBefore, CountDownLatch latchAfter) {
      latchBefore.countDown();
      try {
        latchAfter.await();
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

}
