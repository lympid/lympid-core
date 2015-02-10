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
import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.basicbehaviors.RelativeTimeEvent;
import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.BiTransitionBehavior;
import com.lympid.core.behaviorstatemachines.BiTransitionConstraint;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.common.Trigger;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * Provides an abstract builder and common functionality for creating external,
 * internal and local (aka self) transitions.
 *
 * @param <V> Type of the {@code VertexBuilder} this transition builder has for
 * source.
 * @param <C> Type of the state machine context.
 *
 * @see Transition
 * @see ErnalTransitionBuilder
 * @see PseudoErnalTransitionBuilder
 * @see SelfTransitionBuilder
 *
 * @author Fabien Renaud
 */
public abstract class AbstractTransitionBuilder<V extends VertexBuilder, C> implements TransitionBuilder<V, C> {

  private final String name;
  private String id;
  private final TransitionKind kind;
  private final V source;
  private final Collection<Trigger> triggers = new HashSet<>();
  private Object guard;
  private boolean guardElse;
  private Object effect;

  AbstractTransitionBuilder(final TransitionKind kind, final String name, final V source) {
    this.kind = kind;
    this.name = name;
    this.source = source;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(final String id) {
    this.id = id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setOn(final String event) {
    setOn(new StringEvent(event));
  }

  @Override
  public void setAfter(final long delay, final TimeUnit unit) {
    setOn(new RelativeTimeEvent(delay, unit));
  }

  @Override
  public void setOn(final Event event) {
    triggers.add(new Trigger(event));
  }

  @Override
  public void setGuard(final Class<? extends BiTransitionConstraint> guardClass) {
    this.guard = guardClass;
    this.guardElse = false;
  }

  @Override
  public void setGuardElse(final Class<? extends BiTransitionConstraint> guardClass) {
    this.guard = guardClass;
    this.guardElse = true;
  }

  @Override
  public void setGuard(final BiTransitionConstraint guard) {
    this.guard = guard;
    this.guardElse = false;
  }

  @Override
  public void setEffect(final Class<? extends BiTransitionBehavior> effectClass) {
    this.effect = effectClass;
  }

  @Override
  public void setEffect(final BiTransitionBehavior effect) {
    this.effect = effect;
  }

  @Override
  public V getSource() {
    return source;
  }

  @Override
  public BiTransitionConstraint getGuard() {
    if (guard instanceof Constraint) {
      return (BiTransitionConstraint) guard;
    }
    return guardElse
            ? (BiTransitionConstraint) ConstraintFactory.getNegation((Class<? extends Constraint>) guard)
            : (BiTransitionConstraint) ConstraintFactory.get((Class<? extends Constraint>) guard);
  }

  @Override
  public BiTransitionBehavior getEffect() {
    return BehaviorFactory.toBehavior(effect);
  }

  @Override
  public TransitionKind getKind() {
    return kind;
  }

  @Override
  public Collection<Trigger> getTriggers() {
    return triggers;
  }

}
