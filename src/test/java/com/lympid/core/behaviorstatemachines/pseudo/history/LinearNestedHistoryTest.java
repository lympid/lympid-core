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

import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;

/**
 * Tests terminating a state machine within a deep composite state out of a deep
 * simple state.
 * Depth of the source simple state: 4
 * Depth of the target terminate pseudo state: 4
 * The terminate pseudo state parent state is always an ancestor of the simple
 * state.
 *
 * @author Fabien Renaud 
 */
public abstract class LinearNestedHistoryTest extends AbstractHistoryTest {

  private final int depthNodeA;
  private final int depthNodeB;
  private final int depthHistoryNode;
  private final int maxDepth;
  private String targetA;
  private String targetB;
  
  public LinearNestedHistoryTest(final PseudoStateKind historyKind, final int depthNodeA, final int depthNodeB, final int depthHistoryNode) {
    super(historyKind);
    this.depthNodeA = depthNodeA;
    this.depthNodeB = depthNodeB;
    this.depthHistoryNode = depthHistoryNode;
    this.maxDepth = Math.max(depthNodeA, depthNodeB);
    if (maxDepth < 1) {
      throw new RuntimeException();
    }
    
    this.targetA = createTargetName('a', depthNodeA);
    this.targetB = createTargetName('b', depthNodeB);
    
    setStdOut(historyKind);
  }
  
  private String createTargetName(final char c, final int depth) {
    StringBuilder target = new StringBuilder(depth + 1);
    target.append('A');
    for (int i = 0; i < depth - 1; i++) {
      target.append('a');
    }
    target.append(c);
    return target.toString();
  }  

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder builder = new StateMachineBuilder(name());
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target(targetA);
    
    builder
      .region()
        .state(composite("A", 1))
          .transition("t3")
            .on("pause")
            .target("P");
    
    builder
      .region()
        .state("P")
          .transition("t4")
            .on("resume")
            .target("history");
    
    builder
      .region()
        .finalState("end");
    
    return builder;
  }
  
  private CompositeStateBuilder composite(final String name, final int depth) {
    final CompositeStateBuilder builder = new CompositeStateBuilder(name);
    
    if (depth == depthNodeA) {
      builder
        .region()
          .state(targetA)
            .transition("t1")
              .on("go")
              .target(targetB);
    }
    
    if (depth == depthNodeB) {
      builder
        .region()
          .state(targetB)
            .transition("t2")
              .on("end")
              .target("end");
    }
    
    if (depth == depthHistoryNode) {
      history(builder, "history");
    }
    
    if (depth != maxDepth) {
      builder
        .region()
          .state(composite(name + "a", depth + 1));
    }
    
    return builder;
  }
}
