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
import com.lympid.core.behaviorstatemachines.BiTransitionBehavior;

/**
 *
 * @author Fabien Renaud 
 */
public class TransitionEffectInjector implements Visitor {

  private final Object effect;

  public TransitionEffectInjector(final BiTransitionBehavior effect) {
    this.effect = effect;
  }

  public TransitionEffectInjector(final Class<? extends BiTransitionBehavior> effectClass) {
    this.effect = effectClass;
  }

  /**
   * Does nothing.
   *
   * @param visitable Any vertex builder.
   */
  @Override
  public void visit(final VertexBuilder visitable) {
  }

  /**
   * Injects the class behavior as an effect of the transition.
   *
   * @param visitable The visitable transition builder.
   */
  @Override
  public void visit(final TransitionBuilder visitable) {
    BiTransitionBehavior eff = BehaviorFactory.toBehavior(effect);
    visitable.setEffect(new TransitionBehaviorDecorator(eff, visitable.getEffect()));
  }

  @Override
  public void visit(StateMachineBuilder visitable) {
  }

  @Override
  public void visit(RegionBuilder visitable) {
  }

  @Override
  public void visit(ConnectionPointReferenceBuilder visitable) {
  }

  private static final class TransitionBehaviorDecorator<E extends Event, C> implements BiTransitionBehavior<E, C> {

    private final BiTransitionBehavior<E,C> effect;
    private final BiTransitionBehavior<E,C> prevEffect;

    public TransitionBehaviorDecorator(BiTransitionBehavior<E,C> eff, BiTransitionBehavior<E,C> prevEffect) {
      this.effect = eff;
      this.prevEffect = prevEffect;
    }

    @Override
    public void accept(E event, C context) {
      effect.accept(event, context);
      if (prevEffect != null) {
        prevEffect.accept(event, context);
      }
    }
  }

}
