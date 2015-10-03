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
 * Implementation for building internal and/or external transition for
 * <strong>states</strong>. For building internal and/or external transition for
 * pseudo states, see {@link PseudoErnalTransitionBuilder}.
 *
 * @param <V> {@code VertexBuilder} type which is the source of the transition.
 * @param <C> Type of the state machine context.
 * @param <E> {@code Event} type which lead to this step in the transition.
 *
 * @see Transition
 *
 * @author Fabien Renaud
 */
public final class ErnalTransitionBuilder<V extends VertexBuilder<?, ?, C>, C, E extends Event> extends AbstractTransitionBuilder<V, C> implements TransitionTrigger<V, C, E> {

  private Object target;

  ErnalTransitionBuilder(final TransitionKind kind, final String name, final V source) {
    super(kind, name, source);
  }

  @Override
  public TransitionTrigger<V, C, StringEvent> on(final String event) {
    setOn(event);
    return (TransitionTrigger<V, C, StringEvent>) this;
  }

  @Override
  public TransitionTrigger<V, C, RelativeTimeEvent> after(final long delay, final TimeUnit unit) {
    setAfter(delay, unit);
    return (TransitionTrigger<V, C, RelativeTimeEvent>) this;
  }

  @Override
  public <EE extends Event> TransitionTrigger<V, C, EE> on(final EE event) {
    setOn(event);
    return (TransitionTrigger<V, C, EE>) this;
  }

  @Override
  public TransitionEffect<V, C, E> guard(final Class<? extends BiTransitionConstraint<E, C>> guardClass) {
    setGuard(guardClass);
    return this;
  }

  @Override
  public TransitionEffect<V, C, E> guardElse(final Class<? extends BiTransitionConstraint<E, C>> guardClass) {
    setGuardElse(guardClass);
    return this;
  }

  @Override
  public TransitionEffect<V, C, E> guard(final BiTransitionConstraint<E, C> guard) {
    setGuard(guard);
    return this;
  }

  @Override
  public TransitionTarget<V, C> effect(final Class<? extends BiTransitionBehavior<E, C>> effectClass) {
    setEffect(effectClass);
    return this;
  }

  @Override
  public TransitionTarget<V, C> effect(final BiTransitionBehavior<E, C> effect) {
    setEffect(effect);
    return this;
  }

  @Override
  public V target(final String name) {
    this.target = name;
    return getSource();
  }

  @Override
  public V target(final VertexBuilderReference reference) {
    this.target = reference;
    return getSource();
  }

  @Override
  public String getTargetAsString() {
    return target instanceof String ? (String) target : null;
  }

  @Override
  public VertexBuilderReference getTargetAsVertexBuilder() {
    return target instanceof VertexBuilderReference ? (VertexBuilderReference) target : null;
  }
}
