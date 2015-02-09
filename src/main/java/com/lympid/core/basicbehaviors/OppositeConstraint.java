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

/**
 * Provides an interface for building a {@link Constraint} which is the binary
 * opposite of another {@code Constraint}.
 *
 * The binary opposite is defined such as when constraint A returns true
 * (respectively false), the opposite of A always return false (respectively
 * true).
 *
 * @param <T> Type of the {@code Constraint} this has to be the opposite of.
 * @author Fabien Renaud
 */
public interface OppositeConstraint<T> extends Constraint {

  /**
   * Gets the opposite constraint of this {@code OppositeConstraint}.
   *
   * @return The opposite constraint of this {@code OppositeConstraint}.
   */
  T opposite();
}
