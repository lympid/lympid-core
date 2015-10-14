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
package com.lympid.core.basicbehaviors;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

/**
 * Defines a time event that happens in the future, starting from <em>now</em>.
 *
 * @author Fabien Renaud
 */
public final class FunctionRelativeTimeEvent<C> implements TimeEvent<C> {

  private final Function<C, Duration> delay;
  private transient long lastTime = -1;

  /**
   * Instantiates an event that will take place in the given amount of time in
   * the future.
   *
   * @param delay Time to wait before this event becomes active.
   */
  public FunctionRelativeTimeEvent(final Function<C, Duration> delay) {
    if (delay == null) {
      throw new IllegalArgumentException("The delay function is null");
    }
    this.delay = delay;
  }

  /**
   * Gets the time to wait since this event has been created before it becomes
   * active.
   *
   * @return The time to wait, in milliseconds.
   */
  @Override
  public long time(final C context) {
    lastTime = delay.apply(context).toMillis();
    return lastTime;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 73 * hash + Objects.hashCode(this.delay);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final FunctionRelativeTimeEvent<?> other = (FunctionRelativeTimeEvent<?>) obj;
    return Objects.equals(this.delay, other.delay);
  }

  /**
   * Gets a string representation of the time given by the last call to #time.
   * If #time has never been called before calling this method, it will return
   * "() ms"
   *
   * @return A string representation of the time event.
   */
  @Override
  public String toString() {
    return lastTime == -1 ? "() ms" : (lastTime + " ms");
  }

}
