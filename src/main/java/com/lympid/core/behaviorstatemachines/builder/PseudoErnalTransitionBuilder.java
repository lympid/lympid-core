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

import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.TransitionBehavior;
import com.lympid.core.behaviorstatemachines.TransitionConstraint;
import com.lympid.core.behaviorstatemachines.TransitionKind;

/**
 * Implementation for building internal and/or external transition for
 * <strong>pseudo states</strong>. For building internal and/or external
 * transition for states, see {@link ErnalTransitionBuilder}.
 *
 * @param <V> {@code VertexBuilder} type which is the source of the transition.
 * @param <C> Type of the state machine context.
 *
 * @see Transition
 *
 * @author Fabien Renaud
 */
public final class PseudoErnalTransitionBuilder<V extends VertexBuilder<?, ?, C>, C> extends AbstractTransitionBuilder<V, C> implements PseudoTransitionGuard<V, C> {

  private Object target;

  PseudoErnalTransitionBuilder(final TransitionKind kind, final String name, final V source) {
    super(kind, name, source);
  }

  @Override
  public PseudoTransitionEffect<V, C> guard(final Class<? extends TransitionConstraint< C>> guardClass) {
    setGuard(guardClass);
    return this;
  }

  @Override
  public PseudoTransitionEffect<V, C> guardElse(final Class<? extends TransitionConstraint< C>> guardClass) {
    setGuardElse(guardClass);
    return this;
  }

  @Override
  public PseudoTransitionEffect<V, C> guard(final TransitionConstraint<C> guard) {
    setGuard(guard);
    return this;
  }

  @Override
  public TransitionTarget<V, C> effect(final Class<? extends TransitionBehavior< C>> effectClass) {
    setEffect(effectClass);
    return this;
  }

  @Override
  public TransitionTarget<V, C> effect(final TransitionBehavior<C> effect) {
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
