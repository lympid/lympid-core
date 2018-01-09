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

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import com.lympid.core.behaviorstatemachines.composite.Test23.Context;
import com.lympid.core.behaviorstatemachines.impl.DefaultEntryException;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration.DefaultEntryRule;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests the default entry rule of a composite state.
 * 
 * @author Fabien Renaud 
 */
public class Test23 extends AbstractStateMachineTest<Context> {
  
  @Test(expected = DefaultEntryException.class)
  public void defaultEntryRule_initial() {
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    
    try {
      fsm.go();
    } catch (DefaultEntryException ex) {
      assertEquals(DefaultEntryRule.INITIAL, ex.getRule());
      assertNotNull(ex.getRegion());
      assertEquals("#6", ex.getRegion().toString());
      throw ex;
    }
  }
  
  @Test
  public void defaultEntryRule_none() {
    SequentialContext expected = new SequentialContext();
    Context ctx = new Context();
    
    ExecutorConfiguration config = new ExecutorConfiguration()
      .defaultEntryRule(DefaultEntryRule.NONE);

    StateMachineExecutor<Context> fsm = fsm(ctx, config);
    fsm.go();
    
    expected.effect("t0").enter("compo");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo"));
    
    fsm.take(new StringEvent("toA"));
    expected.effect("t1").enter("A");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("compo", "A"));
    
    fsm.take(new StringEvent("end"));
    expected.exit("A").exit("compo").effect("t2");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("#3"));
  }

  @Override
  public StateMachineBuilder<Context> topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    VertexBuilderReference<Context> end = builder
      .region()
        .finalState();
    
    CompositeStateBuilder<Context> compo = new CompositeStateBuilder<>("compo");
    
    compo
      .region()
        .state("A")
          .transition("t2")
            .on("end")
            .target(end);
    
    compo
      .localTransition("t1")
        .on("toA")
        .target("A");
    
    builder
      .region()
        .state(compo);
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target(compo);
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }

  public static final class Context extends SequentialContext {
  }

  private static final String STDOUT = "StateMachine: \"" + Test23.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: #3\n" +
"    State: \"compo\"\n" +
"      Region: #6\n" +
"        State: \"A\"\n" +
"        Transition: \"t1\" -L- \"compo\" -> \"A\"\n" +
"    PseudoState: #9 kind: INITIAL\n" +
"    Transition: \"t2\" --- \"A\" -> #3\n" +
"    Transition: \"t0\" --- #9 -> \"compo\"";
  
}
