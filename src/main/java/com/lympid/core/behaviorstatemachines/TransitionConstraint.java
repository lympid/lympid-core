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
package com.lympid.core.behaviorstatemachines;

import com.lympid.core.basicbehaviors.Event;

/**
 * Represents a transition constraint (guard) that accepts only a context
 * argument. This type of transition constraint should only be used when it is
 * known that the event does not carry any information that could be useful to
 * the implementer of the constraint. This operation must NOT operate via
 * side-effects.
 *
 * In a behavioral state machine, a {@code TransitionConstraint} can be used as
 * a guard for any transitions outgoing a pseudo state. For transition guards
 * outgoing states, see {@link BiTransitionConstraint}.
 *
 * @param <C> Type of the state machine context.
 *
 * @see java.util.function.Predicate
 * @author Fabien Renaud
 */
public interface TransitionConstraint<C> extends BiTransitionConstraint<Event, C> {

  /**
   * Evaluates the predicate with the context. This method must NOT have
   * side-effects
   *
   * @param ctx The context to use.
   * @return true when the guard has been fully validated.
   */
  boolean test(C ctx);

  @Override
  @Deprecated
  default boolean test(Event event, C ctx) {
    return test(ctx);
  }
}
