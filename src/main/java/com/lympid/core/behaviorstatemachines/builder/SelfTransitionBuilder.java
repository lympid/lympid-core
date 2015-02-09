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
import com.lympid.core.basicbehaviors.RelativeTimeEvent;
import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.BiTransitionBehavior;
import com.lympid.core.behaviorstatemachines.BiTransitionConstraint;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import java.util.concurrent.TimeUnit;

/**
 * Implementation for building internal (aka self) transitions for
 * composite/orthogonal states.
 *
 * @param <V> {@code StateBuilder} type which is the source of the transition.
 * @param <C> Type of the state machine context.
 * @param <E> {@code Event} type which lead to this step in the transition.
 * 
 * @see Transition
 * 
 * @author Fabien Renaud
 */
public final class SelfTransitionBuilder<V extends StateBuilder, C, E extends Event> extends AbstractTransitionBuilder<V, C> implements SelfTransitionTrigger<V, C, E> {

  SelfTransitionBuilder(final TransitionKind kind, final String name, final V source) {
    super(kind, name, source);
  }

  @Override
  public SelfTransitionTrigger<V, C, StringEvent> on(final String event) {
    setOn(event);
    return (SelfTransitionTrigger<V, C, StringEvent>) this;
  }

  @Override
  public SelfTransitionGuard<V, C, RelativeTimeEvent> after(final long delay, final TimeUnit unit) {
    setAfter(delay, unit);
    return (SelfTransitionGuard<V, C, RelativeTimeEvent>) this;
  }

  @Override
  public SelfTransitionTrigger<V, C, E> on(final Event event) {
    setOn(event);
    return this;
  }

  @Override
  public SelfTransitionEffect<V, C, E> guard(final Class<? extends BiTransitionConstraint<E, C>> guardClass) {
    setGuard(guardClass);
    return this;
  }

  @Override
  public SelfTransitionEffect<V, C, E> guardElse(final Class<? extends BiTransitionConstraint<E, C>> guardClass) {
    setGuardElse(guardClass);
    return this;
  }

  @Override
  public SelfTransitionEffect<V, C, E> guard(final BiTransitionConstraint<E, C> guard) {
    setGuard(guard);
    return this;
  }

  @Override
  public SelfTransitionTarget<V> effect(final Class<? extends BiTransitionBehavior<E, C>> effectClass) {
    setEffect(effectClass);
    return this;
  }

  @Override
  public SelfTransitionTarget<V> effect(final BiTransitionBehavior<E, C> effect) {
    setEffect(effect);
    return this;
  }

  @Override
  public V target() {
    return getSource();
  }

  @Override
  public String getTargetAsString() {
    return null;
  }

  @Override
  public VertexBuilderReference getTargetAsVertexBuilder() {
    return target();
  }
}
