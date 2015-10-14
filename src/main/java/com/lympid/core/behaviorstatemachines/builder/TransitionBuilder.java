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

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.basicbehaviors.OppositeBiTransitionConstraint;
import com.lympid.core.behaviorstatemachines.BiTransitionBehavior;
import com.lympid.core.behaviorstatemachines.BiTransitionConstraint;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.common.Trigger;
import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Interface for building transitions.
 *
 * @param <V> Type of the {@code VertexBuilder} this transition builder has for
 * source.
 * @param <C> Type of the state machine context.
 *
 * @see Transition
 * @see AbstractTransitionBuilder
 *
 * @author Fabien Renaud
 */
public interface TransitionBuilder<V extends VertexBuilder, C> {

  /**
   * Sets the id of the transition.
   *
   * @param id The id of the transition.
   */
  void setId(String id);

  /**
   * Sets a trigger based on {@code StringEvent} for the transition.
   *
   * @param event The event of the trigger.
   */
  void setOn(String event);

  /**
   * Sets a time event.
   *
   * @param delay The amount of time the transition has to wait before getting
   * fired.
   * @param unit The unit of the amount time.
   */
  void setAfter(long delay, TimeUnit unit);

  /**
   * Sets a time event.
   *
   * @param delayFunction A function that given the context of the state machine
   * returns the {@link Duration} the the transition has to wait before getting
   * fired.
   */
  void setAfter(Function<C, Duration> delayFunction);

  /**
   * Sets a trigger with any {@code Event} for the transition.
   *
   * @param event The event of the trigger.
   */
  void setOn(Event event);

  /**
   * Sets a guard for the transition.
   *
   * @param guardClass A class implementing a {@link BiTransitionConstraint}.
   */
  void setGuard(Class<? extends BiTransitionConstraint> guardClass);

  /**
   * Sets a guard that is the binary opposite of the given class.
   *
   * @param guardClass A class implementing a {@link BiTransitionConstraint}.
   *
   * @see OppositeBiTransitionConstraint
   */
  void setGuardElse(Class<? extends BiTransitionConstraint> guardClass);

  /**
   * Sets a guard for the transition.
   *
   * @param guard The constraint for the guard.
   */
  void setGuard(BiTransitionConstraint guard);

  /**
   * Sets an effect for the transition.
   *
   * @param effectClass A class implementing a {@link BiTransitionBehavior}.
   */
  void setEffect(Class<? extends BiTransitionBehavior> effectClass);

  /**
   * Sets an effect for the transition.
   *
   * @param effect The behavior for the effect.
   */
  void setEffect(BiTransitionBehavior effect);

  /**
   * Gets the {@code VertexBuilder} source of the transition.
   *
   * @return The {@code VertexBuilder} that is source of the transition.
   */
  V getSource();

  /**
   * Gets an instance of the guard to use in the built {@code StateMachine}.
   *
   * @return A constraint to use as guard for this transition or null.
   */
  BiTransitionConstraint getGuard();

  /**
   * Gets an instance of the effect to use in the built {@code StateMachine}.
   *
   * @return A behavior to use as an effect for this transitio or null.
   */
  BiTransitionBehavior getEffect();

  /**
   * Gets the transition kind.
   *
   * @return The kind of the transition. This is never null.
   */
  TransitionKind getKind();

  /**
   * Gets a collection of triggers for the transition.
   *
   * @return The collection of triggers for the transition or an empty
   * collection.
   */
  Collection<Trigger> getTriggers();

  /**
   * Gets the name of the target for the transition when defined as such.
   *
   * <p>
   * When undefined, it is then guaranteed {@link #getTargetAsVertexBuilder()}
   * will return a non-null value.</p>
   *
   * @return The target name or null.
   */
  String getTargetAsString();

  /**
   * Gets the {@code VertexBuilderReference} which is target of the transition
   * when defined as such.
   *
   * <p>
   * When undefined, it is then guaranteed {@link #getTargetAsString()} will
   * return a non-null value.</p>
   *
   * @return A {@code VertexBuilderReference} or null.
   */
  VertexBuilderReference getTargetAsVertexBuilder();

  /**
   * Gets the {@link com.lympid.core.common.UmlElement} unique identifier of the
   * transition.
   *
   * @return The unique identifier of the transition.
   */
  String getId();

  /**
   * Gets the {@link com.lympid.core.common.UmlElement} name of the transition.
   *
   * @return The name of the transition.
   */
  String getName();
}
