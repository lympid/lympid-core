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

import com.lympid.core.behaviorstatemachines.StateBehavior;

/**
 * Provides an interface for building:
 * <ul>
 * <li>entry behaviors</li>
 * <li>exit behaviors</li>
 * <li>an activity behavior</li>
 * <li>internal transitions</li>
 * <li>external transitions</li>
 * </ul>
 * for a simple or submachine state.
 *
 * @param <V> A {@link SimpleStateBuilder} or {@link SubMachineStateBuilder}.
 * @param <C> Type of the state machine context.
 *
 * @see StateExit
 * @author Fabien Renaud
 */
public interface StateEntry<V extends StateBuilder<?, C>, C> extends StateExit<V, C> {

  /**
   * Adds an entry behavior to the simple/submachine state.
   *
   * @param entry The entry behavior.
   * @return An interface to add more entry behaviors to the simple/submachine
   * state.
   */
  StateEntry<V, C> entry(StateBehavior<C> entry);

  /**
   * Adds an entry behavior to the simple/submachine state.
   *
   * The instance of the entry behavior is provided by the
   * {@link BehaviorFactory}.
   *
   * @param entry The {@code StateBehavior} class of the entry behavior.
   * @return An interface to add more entry behaviors to the simple/submachine
   * state.
   */
  StateEntry<V, C> entry(Class<? extends StateBehavior<C>> entry);
}
