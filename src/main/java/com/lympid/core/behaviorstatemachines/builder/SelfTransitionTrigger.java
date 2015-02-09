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
package com.lympid.core.behaviorstatemachines.builder;

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.basicbehaviors.RelativeTimeEvent;
import com.lympid.core.basicbehaviors.StringEvent;
import java.util.concurrent.TimeUnit;

/**
 * Provides an interface for building an internal (aka self) transition with
 * triggers, guard, effect and target.
 *
 * @param <V> {@code StateBuilder} type which is the source of the transition.
 * @param <C> Type of the state machine context.
 * @param <E> {@code Event} type which lead to this step in the transition.
 *
 * @see SelfTransitionGuard
 * @author Fabien Renaud
 */
public interface SelfTransitionTrigger<V extends StateBuilder, C, E extends Event> extends SelfTransitionGuard<V, C, E> {

  /**
   * Adds a trigger based on {@code StringEvent} for the transition.
   *
   * @param event The event of the trigger.
   * @return An interface to add more triggers, set the guard, effect and target
   * of the transition.
   *
   * @see StringEvent
   */
  SelfTransitionTrigger<V, C, StringEvent> on(String event);

  /**
   * Adds a trigger with any {@code Event} for the transition.
   *
   * @param <EE> The event of the trigger.
   * @param event The event of the trigger.
   * @return An interface to add more triggers, set the guard, effect and target
   * of the transition.
   */
  <EE extends Event> SelfTransitionTrigger<V, C, EE> on(EE event);

  /**
   * Sets a relative time event.
   *
   * @param delay The amount of time the transition has to wait before getting
   * fired.
   * @param unit The unit of the amount time.
   * @return An interface to set the guard, effect and target of the transition.
   *
   * @see RelativeTimeEvent
   */
  SelfTransitionGuard<V, C, RelativeTimeEvent> after(long delay, TimeUnit unit);
}
