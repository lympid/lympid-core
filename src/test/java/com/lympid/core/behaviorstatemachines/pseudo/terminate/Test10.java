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

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import org.junit.Test;

/**
 * Tests terminating a state machine within a deep composite state out of a deep
 * simple state.
 * Depth of the source simple state: 4
 * Depth of the target terminate pseudo state: 0
 * The terminate pseudo state parent state is always an ancestor of the simple
 * state.
 *
 * @author Fabien Renaud 
 */
public class Test10 extends AbstractStateMachineTest {
  
  @Test
  public void run() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A").enter("Aa").enter("Aaa").enter("Aaaa").enter("Aaaaa")
      .exit("Aaaaa").exit("Aaaa").exit("Aaa").exit("Aa").exit("A").effect("t1");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree(this));
    assertSequentialContextEquals(expected, ctx);
    
    fsm.take(new StringEvent("go"));
    assertStateConfiguration(fsm, new ActiveStateTree(this));
    assertSequentialContextEquals(expected, ctx);
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder builder = new StateMachineBuilder(name());
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target("Aaaaa");
    
    builder
      .region()
        .state(compositeA("A"))
          .transition("t2")
            .on("go")
            .target("end");
    
    builder
      .region()
        .terminate("terminate");
    
    builder
      .region()
        .finalState("end");
    
    return builder;
  }
  
  private CompositeStateBuilder compositeA(final String name) {
    final CompositeStateBuilder builder = new CompositeStateBuilder(name);
    
    builder
      .region()
        .state(compositeAa("Aa"));
    
    return builder;
  }
  
  private CompositeStateBuilder compositeAa(final String name) {
    final CompositeStateBuilder builder = new CompositeStateBuilder(name);
    
    builder
      .region()
        .state(compositeAaa("Aaa"));
    
    return builder;
  }
  
  private CompositeStateBuilder compositeAaa( final String name) {
    final CompositeStateBuilder builder = new CompositeStateBuilder(name);
    
    builder
      .region()
        .state(compositeAaaa("Aaaa"));
    
    return builder;
  }
  
  private CompositeStateBuilder compositeAaaa(final String name) {
    final CompositeStateBuilder builder = new CompositeStateBuilder(name);
    
    builder
      .region()
        .state("Aaaaa")
          .transition("t1")
            .target("terminate");
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test10.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"A\"\n" +
"      Region: #7\n" +
"        State: \"Aa\"\n" +
"          Region: #9\n" +
"            State: \"Aaa\"\n" +
"              Region: #11\n" +
"                State: \"Aaaa\"\n" +
"                  Region: #13\n" +
"                    State: \"Aaaaa\"\n" +
"    PseudoState: \"terminate\" kind: TERMINATE\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"Aaaaa\"\n" +
"    Transition: \"t2\" --- \"A\" -> \"end\"\n" +
"    Transition: \"t1\" --- \"Aaaaa\" -> \"terminate\"";
}
