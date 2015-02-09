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

import com.lympid.core.basicbehaviors.Behavior;
import java.util.function.Consumer;

/**
 * Represents a state behavior that takes a context as argument and returns no
 * result. This {@code StateBehavior} is expected to operate via side-effects.
 * 
 * In a behavorial state machine, a {@code StateBehavior} can be used as:
 * <ul>
 *   <li>an entry behavior</li>
 *   <li>an exit behavior</li>
 *   <li>an activity behavior</li>
 * </ul>
 *
 * @param <C> Type of the state machine context
 * 
 * @see Consumer
 * @author Fabien Renaud
 */
public interface StateBehavior<C> extends Behavior, Consumer<C> {

}
