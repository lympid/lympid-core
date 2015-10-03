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

import com.lympid.core.basicbehaviors.Constraint;
import com.lympid.core.basicbehaviors.Event;
import java.util.function.BiPredicate;

/**
 * Represents a transition constraint (guard) that accepts an event and a
 * context argument. This operation must NOT operate via side-effects.
 *
 * In a behavioral state machine, a {@code BiTransitionConstraint} can be used
 * as a guard for any transitions outgoing a state. For transition guards
 * outgoing pseudo states, see {@link TransitionConstraint}.
 *
 * @param <E> the event
 * @param <C> the state machine context
 *
 * @see java.util.function.BiPredicate
 * @author Fabien Renaud
 */
public interface BiTransitionConstraint<E extends Event, C> extends Constraint, BiPredicate<E, C> {

}
