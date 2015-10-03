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

import com.lympid.core.basicbehaviors.CompletionEvent;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.TransitionKind;

/**
 *
 * @param <C> Type of the state machine context.
 *
 * @see State
 *
 * @author Fabien Renaud
 */
public final class SimpleStateBuilder<C> extends StateBuilder<SimpleStateBuilder<C>, C> implements StateEntry<SimpleStateBuilder<C>, C> {

  public SimpleStateBuilder(final String name) {
    super(name);
  }

  public SimpleStateBuilder() {
    super();
  }

  @Override
  public final StateEntry<SimpleStateBuilder<C>, C> entry(final StateBehavior<C> entry) {
    addEntry(entry);
    return this;
  }

  @Override
  public final StateEntry<SimpleStateBuilder<C>, C> entry(final Class<? extends StateBehavior<C>> entry) {
    addEntry(entry);
    return this;
  }

  @Override
  public final StateExit<SimpleStateBuilder<C>, C> exit(final StateBehavior<C> exit) {
    addExit(exit);
    return this;
  }

  @Override
  public final StateExit<SimpleStateBuilder<C>, C> exit(final Class<? extends StateBehavior<C>> exit) {
    addExit(exit);
    return this;
  }

  @Override
  public final StateTransitionSource<SimpleStateBuilder<C>, TransitionTrigger<SimpleStateBuilder<C>, C, CompletionEvent>, C> activity(final StateBehavior<C> activity) {
    setActivity(activity);
    return this;
  }

  @Override
  public final StateTransitionSource<SimpleStateBuilder<C>, TransitionTrigger<SimpleStateBuilder<C>, C, CompletionEvent>, C> activity(final Class<? extends StateBehavior<C>> activity) {
    setActivity(activity);
    return this;
  }

  @Override
  public TransitionTrigger<SimpleStateBuilder<C>, C, CompletionEvent> transition(final String name) {
    ErnalTransitionBuilder transition = new ErnalTransitionBuilder(TransitionKind.EXTERNAL, name, this);
    outgoing().add(transition);
    return transition;
  }

  @Override
  public TransitionTrigger<SimpleStateBuilder<C>, C, CompletionEvent> transition() {
    return transition(null);
  }

  @Override
  public SelfTransitionTrigger<SimpleStateBuilder<C>, C, CompletionEvent> selfTransition(String name) {
    SelfTransitionBuilder transition = new SelfTransitionBuilder(TransitionKind.INTERNAL, name, this);
    outgoing().add(transition);
    return transition;
  }

  @Override
  public SelfTransitionTrigger<SimpleStateBuilder<C>, C, CompletionEvent> selfTransition() {
    return selfTransition(null);
  }

}
