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

import com.lympid.core.common.UmlElement;
import java.util.Collection;

/**
 * A region is an orthogonal part of either a composite state or a state
 * machine. It contains states and transitions.
 *
 * @author Fabien Renaud
 */
public interface Region extends UmlElement, Visitable {

  /**
   * The StateMachine that owns the Region. If a Region is owned by a
   * StateMachine, then it cannot also be owned by a State.
   *
   * @return The StateMachine that owns the Region.
   */
  StateMachine stateMachine();

  /**
   * The State that owns the Region. If a Region is owned by a State, then it
   * cannot also be owned by a StateMachine.
   *
   * @return The State that owns the Region.
   */
  State state();

  /**
   * The set of transitions owned by the region. Note that internal transitions
   * are owned by a region, but applies to the source state.
   *
   * @return The set of transitions owned by the region.
   */
  Collection<? extends Transition> transition();

  /**
   * The set of vertices that are owned by this region.
   *
   * @return The set of vertices that are owned by this region.
   */
  Collection<? extends Vertex> subVertex();

  /**
   * The region of which this region is an extension.
   *
   * @return The region of which this region is an extension.
   */
//  Region extendedRegion();
  /**
   * Returns the StateMachine in which this Region is defined.
   *
   * @return The StateMachine in which this Region is defined.
   */
//  StateMachine containingStateMachine();
  /**
   * Returns the initial pseudo state of the region if it exists.
   *
   * @return The initial pseudo state of the region or null.
   */
  PseudoState initial();

  /**
   * Returns the shallow history pseudo state of the region if it exists.
   *
   * @return The shallow history pseudo state of the region or null.
   */
  PseudoState shallowHistory();

  /**
   * Returns the deep history pseudo state of the region if it exists.
   *
   * @return The deep history pseudo state of the region or null.
   */
  PseudoState deepHistory();

}
