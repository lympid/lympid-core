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

package com.lympid.core.behaviorstatemachines.pseudo.terminate;

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration;
import com.lympid.core.behaviorstatemachines.impl.SyncStateMachineExecutor;
import java.util.Random;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests an initial transition with no effect targeting a terminate pseudo state.
 * @author Fabien Renaud 
 */
public class Test1 extends AbstractStateMachineTest {
  
  @Test
  public void run_WithAutoStart() {
    ExecutorConfiguration config = new ExecutorConfiguration()
      .autoStart(true);
    
    StateMachineExecutor fsm = fsm(config);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this));
  }
  
  @Test
  public void run_WithoutAutoStart() {
    final int executorId = new Random().nextInt();
    StateMachine machine = topLevelStateMachine();
    
    ExecutorConfiguration config = new ExecutorConfiguration()
      .autoStart(true);
    
    StateMachineExecutor fsm = new SyncStateMachineExecutor.Builder()
      .setId(executorId)
      .setStateMachine(machine)
      .setConfiguration(config)
      .build();
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this));
    
    fsm.take(new Event() {});
    assertSnapshotEquals(fsm, new ActiveStateTree(this));
    
    assertEquals(executorId, fsm.getId());
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder builder = new StateMachineBuilder(name());
    
    VertexBuilderReference term = builder
      .region()
        .terminate();
    
    builder
      .region()
        .initial()
          .transition()
            .target(term);
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test1.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: TERMINATE\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    Transition: #5 --- #4 -> #3";
}
