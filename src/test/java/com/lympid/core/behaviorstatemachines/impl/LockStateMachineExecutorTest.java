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
    LockStateMachineExecutor fsm = new LockStateMachineExecutor();
    run(fsm);
  }

  @Test
  public void run2() throws InterruptedException {
    LockStateMachineExecutor fsm = new LockStateMachineExecutor(12);
    run(fsm);
  }
  
  @Test(expected = RuntimeException.class)
  public void go_fail() {
    LockStateMachineExecutor fsm = new LockStateMachineExecutor();
    fsm.setStateMachine(topLevelStateMachine());
    fsm.setContext(new Context());
    fsm.go();
  }
  
  private void run(final StateMachineExecutor fsm) throws InterruptedException {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    
    fsm.configuration().executor(AbstractStateMachineTest.THREAD_POOL);
    fsm.setStateMachine(topLevelStateMachine());
    fsm.setContext(ctx);
    fsm.go();
    
    expected.effect("t0").enter("A");
    assertSequentialContextEquals(expected, ctx);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A").get());
    
    ctx.latch1.await(10 * DELAY, TimeUnit.MILLISECONDS);
    Thread.sleep(DELAY);
    expected.exit("A").effect("t1").enter("B");
    assertSequentialContextEquals(expected, ctx);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("B").get());
    
    ctx.latch2.countDown();
    Thread.sleep(DELAY);
    expected.exit("B").effect("t2").enter("C");
    assertSequentialContextEquals(expected, ctx);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("C").get());
    
    fsm.take(new StringEvent("go"));
    expected.exit("C").effect("t3");
    assertSequentialContextEquals(expected, ctx);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end").get());
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
  
  private static final class Context extends SequentialContext {
    
    CountDownLatch latch1 = new CountDownLatch(1);
    CountDownLatch latch2 = new CountDownLatch(1);
  }

}
