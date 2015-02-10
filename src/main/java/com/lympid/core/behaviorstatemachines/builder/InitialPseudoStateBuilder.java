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

import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.TransitionKind;

/**
 *
 * @param <C> Type of the state machine context.
 *
 * @see PseudoState
 * @see PseudoStateKind#INITIAL
 *
 * @author Fabien Renaud
 */
public final class InitialPseudoStateBuilder<C> extends InitialPseudoStateReference<InitialPseudoStateBuilder<C>, C> implements PseudoStateTransitionSource<PseudoTransitionEffect<InitialPseudoStateReference<?, C>, C>> {

  InitialPseudoStateBuilder(final String name) {
    super(name);
  }

  InitialPseudoStateBuilder() {
    super();
  }

  @Override
  public PseudoTransitionEffect<InitialPseudoStateReference<?, C>, C> transition(final String name) {
    PseudoErnalTransitionBuilder transition = new PseudoErnalTransitionBuilder(TransitionKind.EXTERNAL, name, this);
    outgoing().add(transition);
    return transition;
  }

  @Override
  public PseudoTransitionEffect<InitialPseudoStateReference<?, C>, C> transition() {
    return transition(null);
  }

}
