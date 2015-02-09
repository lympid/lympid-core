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
 * <li>exit behaviors</li>
 * <li>an activity behavior</li>
 * <li>local transitions</li>
 * <li>internal transitions</li>
 * <li>external transitions</li>
 * </ul>
 * for a composite or orthogonal state.
 *
 * @param <V> A {@link CompositeStateBuilder} or {@link OrthogonalStateBuilder}.
 * @param <C> Type of the state machine context.
 *
 * @see CompositeStateActivity
 * @author Fabien Renaud
 */
public interface CompositeStateExit<V extends StateBuilder<?, C>, C> extends CompositeStateActivity<V, C> {

  /**
   * Adds an exit behavior to the composite/orthogonal state.
   *
   * @param exit The exit behavior.
   * @return An interface to add more exit behaviors to the composite/orthogonal
   * state.
   */
  CompositeStateExit<V, C> exit(StateBehavior<C> exit);

  /**
   * Adds an exit behavior to the composite/orthogonal state.
   *
   * The instance of the exit behavior is provided by the
   * {@link BehaviorFactory}.
   *
   * @param exit The exit behavior.
   * @return An interface to add more exit behaviors to the composite/orthogonal
   * state.
   */
  CompositeStateExit<V, C> exit(Class<? extends StateBehavior<C>> exit);
}
