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
package com.lympid.core.behaviorstatemachines.pseudo.history;

import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.DeepHistoryPseudoStateTest;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.ShallowHistoryPseudoStateTest;
import com.lympid.core.behaviorstatemachines.TransitionBehavior;
import com.lympid.core.behaviorstatemachines.VertexTest;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;

/**
 *
 * @author Fabien Renaud 
 */
abstract class AbstractHistoryTest<C> extends AbstractStateMachineTest<C> {

  private final PseudoStateKind historyKind;

  protected AbstractHistoryTest(final PseudoStateKind historyKind) {
    if (historyKind != PseudoStateKind.SHALLOW_HISTORY && historyKind != PseudoStateKind.DEEP_HISTORY) {
      throw new RuntimeException("Invalid pseudo state history kind. Expects " + PseudoStateKind.SHALLOW_HISTORY + " or " + PseudoStateKind.DEEP_HISTORY + " but got: " + historyKind);
    }
    this.historyKind = historyKind;
  }
  
  protected final void history(final CompositeStateBuilder<C> builder, final String name) {
    if (historyKind == PseudoStateKind.SHALLOW_HISTORY) {
      builder
        .region()
          .shallowHistory(name);
    } else {
      builder
        .region()
          .deepHistory(name);
    }
  }
  
  protected final void history(final CompositeStateBuilder<C> builder) {
    if (historyKind == PseudoStateKind.SHALLOW_HISTORY) {
      builder
        .region()
          .shallowHistory();
    } else {
      builder
        .region()
          .deepHistory();
    }
  }
  
  protected final void historyTransitionTo(final CompositeStateBuilder<C> builder, final String target, final String historyName, final String transitionName) {
    if (historyKind == PseudoStateKind.SHALLOW_HISTORY) {
      builder
        .region()
          .shallowHistory(historyName)
            .transition(transitionName)
              .target(target);
    } else {
      builder
        .region()
          .deepHistory(historyName)
            .transition(transitionName)
              .target(target);
    }
  }
  
  protected final VertexBuilderReference historyTransitionTo(final CompositeStateBuilder<C> builder, final String target, final TransitionBehavior<C> effect) {
    if (historyKind == PseudoStateKind.SHALLOW_HISTORY) {
      builder
        .region()
          .shallowHistory()
            .transition()
              .effect(effect)
              .target(target);
      
      return builder
        .region()
          .shallowHistory();
    }
    
    builder
      .region()
        .deepHistory()
          .transition()
            .effect(effect)
            .target(target);

    return builder
      .region()
        .deepHistory();
  }
  
  protected final VertexTest historyVertexTest(final String name) {
    return historyKind == PseudoStateKind.SHALLOW_HISTORY
      ? new ShallowHistoryPseudoStateTest(name)
      : new DeepHistoryPseudoStateTest(name);
  }
  
  abstract void setStdOut(PseudoStateKind historyKind);
  
}
