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

import java.util.concurrent.TimeUnit;

/**
 * Defines a time event that happens in the future, starting from <em>now</em>.
 *
 * @author Fabien Renaud
 */
public final class RelativeTimeEvent implements TimeEvent {

  private final long delay;

  /**
   * Instantiates an event that will take place in the given amount of time in
   * the future.
   *
   * @param delay Time to wait before this event becomes active.
   * @param unit The time unit for the {@code delay}.
   */
  public RelativeTimeEvent(final long delay, final TimeUnit unit) {
    if (delay <= 0) {
      throw new IllegalArgumentException("delay must be a strictly positive number. Got: " + delay);
    }
    this.delay = unit.toMillis(delay);
  }

  /**
   * Gets the time to wait since this event has been created before it becomes
   * active.
   *
   * @return The time to wait, in milliseconds.
   */
  @Override
  public long time() {
    return delay;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 83 * hash + (int) (this.delay ^ (this.delay >>> 32));
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
    final RelativeTimeEvent other = (RelativeTimeEvent) obj;
    return this.delay == other.delay;
  }

  /**
   * Gets a string representation of the time event.
   *
   * @return A string representation of the time event.
   */
  @Override
  public String toString() {
    return delay + " ms";
  }

}
