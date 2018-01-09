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
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import java.util.Collection;
import java.util.function.Predicate;

/**
 *
 * @author Fabien Renaud 
 */
public class SequentialContextInjector implements Visitor {

  /**
   * Injects the name of the state as entry and exit actions of the state.
   * Does nothing to pseudo states.
   *
   * @param visitable Any vertex builder.
   */
  @Override
  public void visit(final VertexBuilder visitable) {
    if (visitable.getName() != null && visitable instanceof StateBuilder) {
      StateBuilder b = (StateBuilder) visitable;

      b.addEntry(new EntryActionSequenceBehavior(visitable.getName()), ADD_ENTRY_PREDICATE);
      b.addExit(new ExitActionSequenceBehavior(visitable.getName()), ADD_EXIT_PREDICATE);
    }
  }

  /**
   * Injects the name of the transition as an effect of the transition.
   *
   * @param visitable The visitable transition builder.
   */
  @Override
  public void visit(final TransitionBuilder visitable) {
    if (visitable.getName() != null && !(visitable.getEffect() instanceof TransitionSequenceBehavior)) {
      visitable.setEffect(new TransitionSequenceBehavior(visitable.getName(), visitable.getEffect()));
    }
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

  public static final class TransitionSequenceBehavior<E extends Event, C> implements BiTransitionBehavior<E, C> {

    private final String text;
    private final BiTransitionBehavior<E, C> prevEffect;

    public TransitionSequenceBehavior(final String text, final BiTransitionBehavior<E, C> prevEffect) {
      this.text = text;
      this.prevEffect = prevEffect;
    }

    @Override
    public void accept(final E event, final C context) {
      if (context instanceof SequentialContext) {
        ((SequentialContext) context).effect(text);
      }
      if (prevEffect != null) {
        prevEffect.accept(event, context);
      }
    }

  }

  public static final class EntryActionSequenceBehavior<C> implements StateBehavior<C> {

    private final String text;

    public EntryActionSequenceBehavior(final String text) {
      this.text = text;
    }

    @Override
    public void accept(final C context) {
      if (context instanceof SequentialContext) {
        ((SequentialContext) context).enter(text);
      }
    }

  }

  public static final class ExitActionSequenceBehavior<C> implements StateBehavior<C> {

    private final String text;

    public ExitActionSequenceBehavior(final String text) {
      this.text = text;
    }

    @Override
    public void accept(final C context) {
      if (context instanceof SequentialContext) {
        ((SequentialContext) context).exit(text);
      }
    }

  }

  private static final Predicate<Collection<Object>> ADD_ENTRY_PREDICATE = coll -> {
    for (Object o : coll) {
      if (o instanceof EntryActionSequenceBehavior) {
        return false;
      }
    }
    return true;
  };

  private static final Predicate<Collection<Object>> ADD_EXIT_PREDICATE = coll -> {
    for (Object o : coll) {
      if (o instanceof ExitActionSequenceBehavior) {
        return false;
      }
    }
    return true;
  };
}
