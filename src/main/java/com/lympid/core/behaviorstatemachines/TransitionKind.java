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

/**
 * TransitionKind is an enumeration of the following literal values: external, internal,
 * local.
 * @author Fabien Renaud
 */
public enum TransitionKind {

  /**
   * Implies that the transition, if triggered, will exit the composite (source)
   * state.
   */
  EXTERNAL,
  /**
   * Implies that the transition, if triggered, occurs without exiting or
   * entering the source state. Thus, it does not cause a state change. This
   * means that the entry or exit condition of the source state will not be
   * invoked. An internal transition can be taken even if the state machine is
   * in one or more regions nested within this state.
   */
  INTERNAL,
  /**
   * Implies that the transition, if triggered, will not exit the composite
   * (source) state, but it will apply to any state within the composite state,
   * and these will be exited and entered.
   */
  LOCAL
}
