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

import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import org.junit.Test;

/**
 * Tests termination in a composite state prevents the completion transition
 * of the composite state to be fired.
 * @author Fabien Renaud 
 */
public class Test4 extends AbstractStateMachineTest {
  
  @Test
  public void run() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("A").get());
    assertSequentialContextEquals(expected, ctx);
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder builder = new StateMachineBuilder(name());
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target("terminate");
    
    builder
      .region()
        .state(compositeA("A"))
          .transition("t1")
            .target("end");
    
    builder
      .region()
        .finalState("end");
    
    return builder;
  }
  
  private CompositeStateBuilder compositeA(final String name) {
    final CompositeStateBuilder builder = new CompositeStateBuilder(name);
    
    builder
      .region()
        .terminate("terminate");
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test4.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"A\"\n" +
"      Region: #7\n" +
"        PseudoState: \"terminate\" kind: TERMINATE\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"terminate\"\n" +
"    Transition: \"t1\" --- \"A\" -> \"end\"";
}
