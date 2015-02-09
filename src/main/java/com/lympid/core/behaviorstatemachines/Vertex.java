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
import com.lympid.core.basicbehaviors.TimeEvent;
import com.lympid.core.common.UmlElement;
import java.util.Collection;

/**
 * A vertex is an abstraction of a node in a state machine graph. In general, it
 * can be the source or destination of any number
 * of transitions.
 *
 * @author Fabien Renaud
 * 
 * @see State
 * @see PseudoState
 */
public interface Vertex extends UmlElement, Visitable {

  /**
   * Specifies the transitions departing from this vertex.
   *
   * @return All the transitions departing from this vertex.
   */
  Collection<? extends Transition> outgoing();

  /**
   * Specifies the transitions departing from this vertex for a given event.
   *
   * @param event An event.
   * @return All the transitions departing from this vertex that match the given
   * event.
   */
  Collection<? extends Transition> outgoing(Event event);

  /**
   * Specifies the time transitions departing from this vertex.
   *
   * @return All the time transitions departing from this vertex.
   */
  Collection<? extends TimeEvent> outgoingTimeEvents();

  /**
   * Specifies the transitions entering this vertex.
   *
   * @return All the transitions entering this vertex.
   */
  Collection<? extends Transition> incoming();

  /**
   * Gets the region that contains this vertex.
   *
   * @return The region that contains this vertex.
   */
  Region container();

//  StateMachine containingStateMachine();
}
