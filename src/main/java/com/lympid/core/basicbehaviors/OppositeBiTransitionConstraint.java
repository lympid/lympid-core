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
package com.lympid.core.basicbehaviors;

import com.lympid.core.behaviorstatemachines.BiTransitionConstraint;

/**
 *
 * @author Fabien Renaud
 */
public final class OppositeBiTransitionConstraint<E extends Event, C> implements OppositeConstraint<BiTransitionConstraint<E, C>>, BiTransitionConstraint<E, C> {

  private final BiTransitionConstraint<E, C> constraint;

  public OppositeBiTransitionConstraint(final BiTransitionConstraint<E, C> constraint) {
    this.constraint = constraint;
  }

  @Override
  public boolean test(final E event, final C context) {
    return !constraint.test(event, context);
  }

  public BiTransitionConstraint<E, C> opposite() {
    return constraint;
  }

}
