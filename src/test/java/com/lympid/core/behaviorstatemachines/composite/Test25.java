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
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.composite.Test25.Context;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration.DefaultEntryRule;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;

/**
 * 
 * @author Fabien Renaud 
 */
public class Test25 extends AbstractStateMachineTest<Context> {
  
  @Test
  public void run() {
    SequentialContext expected = new SequentialContext();
    ExecutorConfiguration config = new ExecutorConfiguration()
      .defaultEntryRule(DefaultEntryRule.NONE);
    
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx, config);
    fsm.go();
    
    expected
      .effect("t0").enter("compo")
      .effect("t1").enter("A")
      .exit("A").effect("t2")
      .exit("compo").effect("t3");
    assertSequentialContextEquals(expected, fsm);
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }

  @Override
  public StateMachineBuilder<Context> topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target("compo");
            
    builder
      .region()
        .state(compo("compo"))
          .localTransition("t1")
            .guard((e, c) -> c.c == 0)
            .target("A")
          .transition("t3")
            .guard((e, c) -> c.c == 1)
            .target("end");
    
    builder
      .region()
        .finalState("end");
    
    return builder;
  }
  
  private CompositeStateBuilder<Context> compo(final String name) {
    CompositeStateBuilder<Context> builder = new CompositeStateBuilder<>(name);
    
    builder
      .region()
        .state("A")
          .transition("t2")
            .effect((e, c) -> c.c++)
            .target("end1");
    
    builder
      .region()
        .finalState("end1");
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  public static final class Context extends SequentialContext {
    int c;
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test25.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"compo\"\n" +
"      Region: #8\n" +
"        FinalState: \"end1\"\n" +
"        State: \"A\"\n" +
"        Transition: \"t1\" -L- \"compo\" -> \"A\"\n" +
"        Transition: \"t2\" --- \"A\" -> \"end1\"\n" +
"    Transition: \"t0\" --- #3 -> \"compo\"\n" +
"    Transition: \"t3\" --- \"compo\" -> \"end\"";
  
}
