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

import java.util.Collection;

/**
 * Connection point references of a submachine state can be used as
 * sources/targets of transitions. They represent entries into or exits out of
 * the submachine state machine referenced by the submachine state.
 *
 * @author Fabien Renaud
 */
public interface ConnectionPointReference extends Vertex {

  /**
   * The entryPoint kind pseudostates corresponding to this connection point.
   *
   * @return The entryPoint kind pseudostates corresponding to this connection
   * point.
   */
  Collection<? extends PseudoState> entry();

  /**
   * The exitPoints kind pseudostates corresponding to this connection point.
   *
   * @return The exitPoints kind pseudostates corresponding to this connection
   * point.
   */
  Collection<? extends PseudoState> exit();

  /**
   * The State in which the connection point references are defined.
   *
   * @return The State in which the connection point references are defined.
   */
  State state();
}
