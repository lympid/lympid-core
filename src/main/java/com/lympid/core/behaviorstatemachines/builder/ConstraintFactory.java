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
package com.lympid.core.behaviorstatemachines.builder;

import com.lympid.core.basicbehaviors.Constraint;
import com.lympid.core.basicbehaviors.OppositeBiTransitionConstraint;
import com.lympid.core.basicbehaviors.OppositeConstraint;
import com.lympid.core.behaviorstatemachines.BiTransitionConstraint;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides factory functionality for any {@link Constraint}.
 *
 * @author Fabien Renaud
 */
public final class ConstraintFactory {

  /**
   * Registry of all the {@link Constraint} that have been instantiated by the
   * factory.
   */
  private static final Map<Class<? extends Constraint>, Constraint> REGISTRY_REGULAR = new HashMap<>();
  /**
   * Registry of all the {@link OppositeConstraint} that have been instantiated
   * by the factory.
   */
  private static final Map<Class<? extends Constraint>, OppositeConstraint> REGISTRY_OPPOSITE = new HashMap<>();

  /**
   * Private empty constructor for utility-like class.
   */
  private ConstraintFactory() {
  }

  /**
   * Gets the singleton instance for the given {@code Constraint} class. When
   * such instance does not exist, it is created and registered.
   *
   * The method safely returns null when the {@code constraintClass} argument is
   * null.
   *
   * This method is thread-safe.
   *
   * @param constraintClass A {@code Class} type that implements
   * {@code Constraint}.
   * @return The singleton instance of the {@code Constraint} class.
   */
  public static Constraint get(final Class<? extends Constraint> constraintClass) {
    if (constraintClass == null) {
      return null;
    }
    return getRegularConstraint(constraintClass);
  }

  /**
   * Gets the singleton instance for the {@code OppositeConstraint} matching the
   * given {@code Constraint} class. When such instance does not exist, it is
   * created and registered.
   *
   * The method safely returns null when the {@code constraintClass} argument is
   * null.
   *
   * This method is thread-safe.
   *
   * @param constraintClass A {@code Class} type that implements
   * {@code Constraint}.
   * @return The singleton instance of the {@code OppositeConstraint} class
   * matching the {@code Constraint}.
   */
  public static OppositeConstraint getNegation(final Class<? extends Constraint> constraintClass) {
    if (constraintClass == null) {
      return null;
    }
    synchronized (REGISTRY_OPPOSITE) {
      OppositeConstraint c = REGISTRY_OPPOSITE.get(constraintClass);
      if (c == null) {
        Constraint regular = getRegularConstraint(constraintClass);
        if (regular instanceof BiTransitionConstraint) {
          c = new OppositeBiTransitionConstraint((BiTransitionConstraint) regular);
        } else {
          throw new UnsupportedOperationException();
        }
        REGISTRY_OPPOSITE.put(constraintClass, c);
      }
      return c;
    }
  }

  /**
   * Gets the singleton instance for the given {@code Constraint} class. When
   * such instance does not exist, it is created and registered.
   *
   * The method safely returns null when the {@code constraintClass} argument is
   * null.
   *
   * This method is thread-safe.
   *
   * @param constraintClass A {@code Class} type that implements
   * {@code Constraint}.
   * @return The singleton instance of the {@code Constraint} class.
   */
  private static Constraint getRegularConstraint(final Class<? extends Constraint> constraintClass) {
    synchronized (REGISTRY_REGULAR) {
      Constraint c = REGISTRY_REGULAR.get(constraintClass);
      if (c == null) {
        try {
          c = constraintClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
          throw new RuntimeException(constraintClass.getCanonicalName() + " must have a public default constructor!", ex);
        }
        REGISTRY_REGULAR.put(constraintClass, c);
      }
      return c;
    }
  }
}
