/*
 * Copyright 2015 Lympid.
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
package com.lympid.core.behaviorstatemachines.impl;

import com.lympid.core.behaviorstatemachines.StateMachineMeta;

/**
 * Factory class for {@link StateMachineState}.
 *
 * @author Fabien Renaud
 */
public final class StateMachineStateFactory {

  private StateMachineStateFactory() {
  }

  /**
   * Determines what implementation of {@code StateMachineState} the state
   * machine described by the specified {@code StateMachineMeta} needs.
   *
   * @param meta The meta data about the state machine
   *
   * @return A new instance of a {@code StateMachineState} supporting all
   * features the state machine needs.
   */
  public static StateMachineState get(final StateMachineMeta meta) {
    if (meta.hasOrthogonalStates()) {
      return new OrthogonalStateMachineState(meta);
    }

    if (meta.hasCompositeStates() || meta.hasSubmachineStates()) {
      return new CompositeStateMachineState(meta);
    }

    if (meta.hasSimpleStates()) {
      return new SimpleStateMachineState(meta);
    }

    return new SimpleStateMachineState(meta); // TODO?
  }
}
