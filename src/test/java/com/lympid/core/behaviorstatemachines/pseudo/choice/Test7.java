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
package com.lympid.core.behaviorstatemachines.pseudo.choice;

import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class Test7 extends AbstractStateMachineTest {
  
  @Test
  public void run() {
    SequentialContext expected = new SequentialContext();
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    expected
      .effect("t0").enter("A")
      .exit("A").effect("t1").enter("B")
      .exit("B").effect("t2");
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("end").get());
    assertSequentialContextEquals(expected, ctx);
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder builder = new StateMachineBuilder(name());
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target("choice1");
    
    builder
      .region()
        .state(new CompositeStateBuilder("A"))
          .region()
            .choice("choice1")
              .transition("t1")
                .target("choice2");
    
    builder
      .region()
        .state(new CompositeStateBuilder("B"))
          .region()
            .choice("choice2")
              .transition("t2")
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
  
  private static final String STDOUT = "StateMachine: \"" + Test7.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"A\"\n" +
"      Region: #6\n" +
"        PseudoState: \"choice1\" kind: CHOICE\n" +
"    State: \"B\"\n" +
"      Region: #10\n" +
"        PseudoState: \"choice2\" kind: CHOICE\n" +
"    Transition: \"t0\" --- #3 -> \"choice1\"\n" +
"    Transition: \"t1\" --- \"choice1\" -> \"choice2\"\n" +
"    Transition: \"t2\" --- \"choice2\" -> \"end\"";
}
