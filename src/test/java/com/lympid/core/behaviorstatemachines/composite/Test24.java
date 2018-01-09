/*
 * Copyright 2015 Lympid.
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
package com.lympid.core.behaviorstatemachines.composite;

import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.impl.DefaultEntryException;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration.DefaultEntryRule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * Tests the default entry rule of an empty composite state.
 * 
 * @author Fabien Renaud 
 */
public class Test24 extends AbstractStateMachineTest {
  
  @Test(expected = DefaultEntryException.class)
  public void defaultEntryRule_initial() {
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    
    try {
      fsm.go();
    } catch (DefaultEntryException ex) {
      assertEquals(DefaultEntryRule.INITIAL, ex.getRule());
      assertNotNull(ex.getRegion());
      assertEquals("#7", ex.getRegion().toString());
      throw ex;
    }
  }
  
  @Test
  public void defaultEntryRule_none() {
    SequentialContext expected = new SequentialContext();
    SequentialContext ctx = new SequentialContext();
    
    ExecutorConfiguration config = new ExecutorConfiguration()
      .defaultEntryRule(DefaultEntryRule.NONE);
    
    StateMachineExecutor fsm = fsm(ctx, config);
    fsm.go();
    
    expected.effect("t0").enter("compo").exit("compo").effect("t1");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder builder = new StateMachineBuilder<>(name());
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target("compo");
            
    builder
      .region()
        .state(new CompositeStateBuilder<>("compo"))
          .transition("t1")
            .target("end");
    
    builder
      .region()
        .finalState("end");
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test24.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"compo\"\n" +
"      Region: #7\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"compo\"\n" +
"    Transition: \"t1\" --- \"compo\" -> \"end\"";
  
}
