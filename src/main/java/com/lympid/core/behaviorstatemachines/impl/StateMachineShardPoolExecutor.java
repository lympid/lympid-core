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
import com.lympid.core.behaviorstatemachines.StateMachineSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Fabien Renaud
 */
public final class StateMachineShardPoolExecutor<C> {

  private final LinkedBlockingDeque<Runnable>[] queues;
  private final ThreadPoolExecutor[] pools;

  public StateMachineShardPoolExecutor(final int poolSize) {
    this(poolSize, Executors.defaultThreadFactory());
  }

  public StateMachineShardPoolExecutor(final int poolSize, final ThreadFactory threadFactory) {
    this.queues = new LinkedBlockingDeque[poolSize];
    this.pools = new ThreadPoolExecutor[poolSize];
    for (int i = 0; i < poolSize; i++) {
      queues[i] = new LinkedBlockingDeque<>();
      pools[i] = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, queues[i], threadFactory);
      pools[i].prestartAllCoreThreads();
    }
  }

  /**
   * Shuts down all the thread pools this {@link StateMachineShardPoolExecutor}
   * instance holds.
   *
   * @see ThreadPoolExecutor#shutdown
   */
  public void shutdown() {
    for (ThreadPoolExecutor pool : pools) {
      pool.shutdown();
    }
  }

  /**
   * Attempts to stop all actively executing tasks, halts the processing of
   * waiting tasks, and returns a list of the tasks that were awaiting execution
   * for all the thread pools this instance holds.
   *
   * @return list of tasks that never commenced execution
   * @see ThreadPoolExecutor#shutdownNow
   */
  public List<Runnable> shutdownNow() {
    final List<Runnable> list = new ArrayList<>();
    for (ThreadPoolExecutor pool : pools) {
      list.addAll(pool.shutdownNow());
    }
    return list;
  }

  /**
   * Returns true if all thread pools have been shut down.
   *
   * @return true if all thread pools have been shut down
   * @see ThreadPoolExecutor#isShutdown
   */
  public boolean isShutdown() {
    for (ThreadPoolExecutor pool : pools) {
      if (!pool.isShutdown()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns true if all thread pools have completed following shut down. Note
   * that {@link #isTerminated} is never true unless either {@link #shutdown} or
   * {@link #shutdownNow} was called first.
   *
   * @return true if all thread pools have terminated
   * @see ThreadPoolExecutor#isTerminated
   */
  public boolean isTerminated() {
    for (ThreadPoolExecutor pool : pools) {
      if (!pool.isTerminated()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Blocks until all tasks of all the thread pools hold by this instance have
   * completed execution after a shutdown request, or the timeout occurs, or the
   * current thread is interrupted, whichever happens first.
   *
   * @param timeout the maximum time to wait
   * @param unit the time unit of the timeout argument
   * @return true if all the threads pools terminated and false if the timeout
   * elapsed before termination for any of them
   * @throws InterruptedException if interrupted while waiting
   * @see ThreadPoolExecutor#awaitTermination
   */
  public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
    int failed = 0;
    for (ThreadPoolExecutor pool : pools) {
      if (!pool.awaitTermination(timeout, unit)) {
        failed++;
      }
    }
    return failed == 0;
  }

  /**
   * Returns true if this executor is in the process of terminating after
   * {@link StateMachineShardPoolExecutor#shutdown} or
   * {@link StateMachineShardPoolExecutor#shutdownNow} but has not completely
   * terminated.
   *
   * @return true if at least one thread pool is terminating and all the other
   * thread pools are either terminating or terminated.
   * @see ThreadPoolExecutor#isTerminating
   */
  public boolean isTerminating() {
    int terminated = 0;
    int terminating = 0;
    for (ThreadPoolExecutor pool : pools) {
      if (pool.isTerminating()) {
        terminating++;
      } else if (pool.isTerminated()) {
        terminated++;
      }
    }

    return terminating > 0 && pools.length == terminating + terminated;
  }

  /**
   * Tries to remove from the work queues all {@link Event} tasks that have been
   * cancelled.
   *
   * @see ThreadPoolExecutor#purge
   */
  public void purge() {
    for (ThreadPoolExecutor pool : pools) {
      pool.purge();
    }
  }

  void go(final PoolStateMachineExecutor<C> executor) {
    queue(executor).add(new StateMachineGo<>(executor));
  }

  void take(final PoolStateMachineExecutor<C> executor, final Event event) {
    queue(executor).add(new StateMachineEvent<>(executor, event));
  }

  void take(final PoolStateMachineExecutor<C> executor, final Event event, final State state) {
    queue(executor).add(new StateMachineStateEvent<>(executor, event, state));
  }

  void takeCompletionEvent(final PoolStateMachineExecutor<C> executor) {
    queue(executor).addFirst(new StateMachineCompletionEvent<>(executor));
  }

  private LinkedBlockingDeque<Runnable> queue(final StateMachineExecutor<C> executor) {
    int shard = executor.getId() % queues.length;
    return queues[shard];
  }

  void resume(final PoolStateMachineExecutor<C> executor) {
    queue(executor).add(new StateMachineResumeRunnable<>(executor));
  }

  void pause(final PoolStateMachineExecutor<C> executor) {
    queue(executor).addFirst(new StateMachinePauseRunnable<>(executor));
  }

  Future<StateMachineSnapshot<C>> snapshot(final PoolStateMachineExecutor<C> executor) {
    StateMachineSnapshotRunnable<C> runnable = new StateMachineSnapshotRunnable<>(executor);
    queue(executor).addFirst(runnable);
    return runnable;
  }

  private static final AtomicInteger COUNTER = new AtomicInteger();

  private static final class StateMachineGo<C> implements Runnable {

    private final int id = COUNTER.incrementAndGet();
    private final PoolStateMachineExecutor<C> executor;

    public StateMachineGo(final PoolStateMachineExecutor<C> executor) {
      this.executor = executor;
      System.out.println(getClass().getSimpleName() + "#" + id + " scheduled");
    }

    @Override
    public void run() {
      System.out.println(getClass().getSimpleName() + "#" + id + " running");
      executor.doGo();
    }

  }

  private static final class StateMachineEvent<C> implements Runnable {

    private final int id = COUNTER.incrementAndGet();
    private final PoolStateMachineExecutor<C> executor;
    private final Event event;

    public StateMachineEvent(final PoolStateMachineExecutor<C> executor, final Event event) {
      this.executor = executor;
      this.event = event;
      System.out.println(getClass().getSimpleName() + "#" + id + " scheduled");
    }

    @Override
    public void run() {

      System.out.println(getClass().getSimpleName() + "#" + id + " running");
      executor.doTake(event);
    }

  }

  private static final class StateMachineStateEvent<C> implements Runnable {

    private final int id = COUNTER.incrementAndGet();
    private final PoolStateMachineExecutor<C> executor;
    private final Event event;
    private final State state;

    public StateMachineStateEvent(final PoolStateMachineExecutor<C> executor, final Event event, final State state) {
      this.executor = executor;
      this.event = event;
      this.state = state;
      System.out.println(getClass().getSimpleName() + "#" + id + " scheduled");
    }

    @Override
    public void run() {

      System.out.println(getClass().getSimpleName() + "#" + id + " running");
      executor.doTake(event, state);
    }

  }

  private static final class StateMachineCompletionEvent<C> implements Runnable {

    private final int id = COUNTER.incrementAndGet();
    private final PoolStateMachineExecutor<C> executor;

    public StateMachineCompletionEvent(final PoolStateMachineExecutor<C> executor) {
      this.executor = executor;
      System.out.println(getClass().getSimpleName() + "#" + id + " scheduled");
    }

    @Override
    public void run() {
      executor.doTakeCompletionEvent();
    }

  }

  private static final class StateMachinePauseRunnable<C> implements Runnable {

    private final int id = COUNTER.incrementAndGet();
    private final PoolStateMachineExecutor<C> executor;

    public StateMachinePauseRunnable(final PoolStateMachineExecutor<C> executor) {
      this.executor = executor;
      System.out.println(getClass().getSimpleName() + "#" + id + " scheduled");
    }

    @Override
    public void run() {

      System.out.println(getClass().getSimpleName() + "#" + id + " running");
      executor.doPause();
    }

  }

  private static final class StateMachineResumeRunnable<C> implements Runnable {

    private final int id = COUNTER.incrementAndGet();
    private final PoolStateMachineExecutor<C> executor;

    public StateMachineResumeRunnable(final PoolStateMachineExecutor<C> executor) {
      this.executor = executor;
      System.out.println(getClass().getSimpleName() + "#" + id + " scheduled");
    }

    @Override
    public void run() {

      System.out.println(getClass().getSimpleName() + "#" + id + " running");
      executor.doResume();
    }

  }

  private static abstract class StateMachineSnapshotFuture<C> implements Future<StateMachineSnapshot>, Runnable {

    private final CountDownLatch latch = new CountDownLatch(1);
    private final AtomicInteger status = new AtomicInteger();
    private StateMachineSnapshot<C> snapshot;

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
    public StateMachineSnapshot<C> get() throws InterruptedException, ExecutionException {
      latch.await();
      return snapshot;
    }

    @Override
    public StateMachineSnapshot<C> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
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

    abstract StateMachineSnapshot<C> snapshot();

  }

  private static final class StateMachineSnapshotRunnable<C> extends StateMachineSnapshotFuture implements Runnable {

    private final PoolStateMachineExecutor<C> executor;

    public StateMachineSnapshotRunnable(final PoolStateMachineExecutor<C> executor) {
      this.executor = executor;
    }

    @Override
    StateMachineSnapshot<C> snapshot() {
      return executor.doSnapshot();
    }

  }
}
