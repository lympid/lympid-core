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

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Fabien Renaud
 */
public class StateMachineShardPoolExecutor {

  private final Worker[] pool;

  public StateMachineShardPoolExecutor(final int poolSize) {
    this(poolSize, Executors.defaultThreadFactory());
  }

  public StateMachineShardPoolExecutor(final int poolSize, final ThreadFactory threadFactory) {
    this.pool = new Worker[poolSize];
    for (int i = 0; i < poolSize; i++) {
      Worker worker = new Worker();
      pool[i] = worker;
      worker.start();
    }
  }

  void start(final PoolStateMachineExecutor executor) {
    queue(executor).add(new StateMachineStart(executor));
  }

  void take(final PoolStateMachineExecutor executor, final Event event) {
    queue(executor).add(new StateMachineEvent(executor, event));
  }

  void take(final PoolStateMachineExecutor executor, final Event event, final State state) {
    queue(executor).add(new StateMachineStateEvent(executor, event, state));
  }

  void takeCompletionEvent(final PoolStateMachineExecutor executor) {
    queue(executor).addFirst(new StateMachineCompletionEvent(executor));
  }

  private LinkedBlockingDeque<Runnable> queue(final StateMachineExecutor executor) {
    int shard = executor.getId() % pool.length;
    return pool[shard].queue;
  }

  void resume(final PoolStateMachineExecutor executor, final StateMachineSnapshot snapshot) {
    queue(executor).add(new StateMachineResumeRunnable(executor, snapshot));
  }

  Future<StateMachineSnapshot> pause(final PoolStateMachineExecutor executor) {
    StateMachinePauseRunnable runnable = new StateMachinePauseRunnable(executor);
    queue(executor).addFirst(runnable);
    return runnable;
  }

  Future<StateMachineSnapshot> snapshot(final PoolStateMachineExecutor executor) {
    StateMachineSnapshotRunnable runnable = new StateMachineSnapshotRunnable(executor);
    queue(executor).addFirst(runnable);
    return runnable;
  }

  private static final class Worker extends Thread {

    private final LinkedBlockingDeque<Runnable> queue = new LinkedBlockingDeque<>();

    private Worker() {
    }

    @Override
    public void run() {
      Thread t = Thread.currentThread();
      while (!t.isInterrupted()) {
        try {
          queue.takeFirst().run();
        } catch (InterruptedException ex) {
          ex.printStackTrace(); // FIXME
        }
      }
    }

  }

  private static final class StateMachineStart implements Runnable {

    private final PoolStateMachineExecutor executor;

    public StateMachineStart(final PoolStateMachineExecutor executor) {
      this.executor = executor;
    }

    @Override
    public void run() {
      executor.doStart();
    }

  }

  private static final class StateMachineEvent implements Runnable {

    private final PoolStateMachineExecutor executor;
    private final Event event;

    public StateMachineEvent(final PoolStateMachineExecutor executor, final Event event) {
      this.executor = executor;
      this.event = event;
    }

    @Override
    public void run() {
      executor.doTake(event);
    }

  }

  private static final class StateMachineStateEvent implements Runnable {

    private final PoolStateMachineExecutor executor;
    private final Event event;
    private final State state;

    public StateMachineStateEvent(final PoolStateMachineExecutor executor, final Event event, final State state) {
      this.executor = executor;
      this.event = event;
      this.state = state;
    }

    @Override
    public void run() {
      executor.doTake(event, state);
    }

  }

  private static final class StateMachineCompletionEvent implements Runnable {

    private final PoolStateMachineExecutor executor;

    public StateMachineCompletionEvent(final PoolStateMachineExecutor executor) {
      this.executor = executor;
    }

    @Override
    public void run() {
      executor.doTakeCompletionEvent();
    }

  }

  private static final class StateMachineResumeRunnable implements Runnable {

    private final PoolStateMachineExecutor executor;
    private final StateMachineSnapshot snapshot;

    public StateMachineResumeRunnable(final PoolStateMachineExecutor executor, final StateMachineSnapshot snapshot) {
      this.executor = executor;
      this.snapshot = snapshot;
    }

    @Override
    public void run() {
      executor.doResume(snapshot);
    }

  }
  
  private static abstract class StateMachineSnapshotFuture implements Future<StateMachineSnapshot>, Runnable {

    private final CountDownLatch latch = new CountDownLatch(1);
    private final AtomicInteger status = new AtomicInteger();
    private StateMachineSnapshot snapshot;
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      return status.compareAndSet(0, -1);
    }

    @Override
    public boolean isCancelled() {
      return status.get() == -1;
    }

    @Override
    public boolean isDone() {
      return status.get() == 1;
    }

    @Override
    public StateMachineSnapshot get() throws InterruptedException, ExecutionException {
      latch.await();
      return snapshot;
    }

    @Override
    public StateMachineSnapshot get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      latch.await(timeout, unit);
      return snapshot;
    }

    @Override
    public void run() {
      if (isCancelled()) {
        latch.countDown();
        return;
      }
      
      snapshot = snapshot();
      latch.countDown();
      status.set(1);
    }
    
    abstract StateMachineSnapshot snapshot();
    
  }

  private static final class StateMachinePauseRunnable extends StateMachineSnapshotFuture {

    private final PoolStateMachineExecutor executor;

    public StateMachinePauseRunnable(final PoolStateMachineExecutor executor) {
      this.executor = executor;
    }

    @Override
    StateMachineSnapshot snapshot() {
      return executor.doPause();
    }

  }

  private static final class StateMachineSnapshotRunnable extends StateMachineSnapshotFuture implements Runnable {

    private final PoolStateMachineExecutor executor;

    public StateMachineSnapshotRunnable(final PoolStateMachineExecutor executor) {
      this.executor = executor;
    }

    @Override
    StateMachineSnapshot snapshot() {
      return executor.doSnapshot();
    }

  }
}
