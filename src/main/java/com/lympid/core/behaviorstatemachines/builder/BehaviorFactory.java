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

import com.lympid.core.basicbehaviors.Behavior;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides factory functionality for any {@link Behavior}.
 * @author Fabien Renaud
 */
public final class BehaviorFactory {

  /**
   * Registry of all the behaviors that have been instantiated by the factory.
   */
  private static final Map<Class<? extends Behavior>, Behavior> REGISTRY = new HashMap<>();

  /**
   * Private empty constructor for utility-like class.
   */
  private BehaviorFactory() {
  }

  /**
   * Turns a collection of objects into a type safe list of behaviors.
   *
   * @param <T> Type of {@code Behavior} this method must return.
   * @param coll A mixed collection of {@code Behavior} types and/or
   * {@code Behavior} instances.
   * @return A list of instances of {@code Behavior}s.
   *
   * @see #toBehavior
   */
  public static final <T extends Behavior> List<T> toBehaviorList(final Collection coll) {
    if (coll.isEmpty()) {
      return Collections.EMPTY_LIST;
    }

    final List<T> items = new ArrayList<>(coll.size());
    for (Object o : coll) {
      items.add(toBehavior(o));
    }
    return items;
  }

  /**
   * Turns an {@code Object} into a specified type of {@code Behavior}.
   *
   * This method only accepts two types of parameters:
   * <ul>
   * <li>an instance of a {@code Behavior}, in which case the method just
   * returns and casts it to the expected return type.</li>
   * <li>a {@code Class} type that whose class implements a
   * {@code Behavior}</li>
   * </ul>
   *
   * Any other types of argument will result in a {@code ClassCastException}.
   *
   * @param <T> Type of {@code Behavior} this method must return.
   * @param o A {@code Behavior} instance or the type of a class that implements
   * a {@code Behavior}
   * @return A {@code Behavior} instance cast to the specified return type.
   */
  public static final <T extends Behavior> T toBehavior(final Object o) {
    if (o instanceof Behavior) {
      return (T) o;
    }
    return (T) get((Class<? extends Behavior>) o);
  }

  /**
   * Gets the singleton instance for the given {@code Behavior} class.
   * When such instance does not exist, it is created and registered.
   *
   * The method safely returns null when the {@code behaviorClass} argument is
   * null.
   *
   * This method is thread-safe.
   *
   * @param behaviorClass A {@code Class} type that implements {@code Behavior}.
   * @return The singleton instance of the {@code Behavior} class.
   */
  private static Behavior get(final Class<? extends Behavior> behaviorClass) {
    if (behaviorClass == null) {
      return null;
    }
    synchronized (REGISTRY) {
      Behavior b = REGISTRY.get(behaviorClass);
      if (b == null) {
        try {
          b = behaviorClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
          throw new RuntimeException(behaviorClass.getCanonicalName() + " must have a public default constructor.", ex);
        }
        REGISTRY.put(behaviorClass, b);
      }
      return b;
    }
  }
}
