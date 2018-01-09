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

package com.lympid.core.behaviorstatemachines.simple;

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration;
import com.lympid.core.behaviorstatemachines.simple.Test1.Context;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;

/**
 * Tests an initial transition with no effect.
 * @author Fabien Renaud 
 */
public class Test1 extends AbstractStateMachineTest<Context> {
  
  @Test
  public void run_WithAutoStart() {
    ExecutorConfiguration config = new ExecutorConfiguration()
      .autoStart(true);
    StateMachineExecutor<Context> fsm = fsm(config);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }
  
  @Test
  public void run_WithoutAutoStart() {
    ExecutorConfiguration config = new ExecutorConfiguration()
      .autoStart(false);
    
    StateMachineExecutor<Context> fsm = fsm(config);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this));
    
    fsm.take(new Event() {});
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }
  
  @Override
  public StateMachineBuilder<Context> topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    VertexBuilderReference<Context> end = builder
      .region()
        .finalState("end");
    
    builder
      .region()
        .initial()
          .transition()
            .target(end);
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }

  public static final class Context {
  }

  private static final String STDOUT = "StateMachine: \"" + Test1.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    Transition: #5 --- #4 -> \"end\"";
}
