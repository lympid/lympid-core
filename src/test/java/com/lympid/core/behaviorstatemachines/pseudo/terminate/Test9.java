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
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.pseudo.terminate.Test9.Context;
import org.junit.Test;

import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;

/**
 * Tests terminating a state machine within a deep composite state out of a deep
 * simple state.
 * Depth of the source simple state: 4
 * Depth of the target terminate pseudo state: 1
 * The terminate pseudo state parent state is always an ancestor of the simple
 * state.
 *
 * @author Fabien Renaud 
 */
public class Test9 extends AbstractStateMachineTest<Context> {
  
  @Test
  public void run() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A").enter("Aa").enter("Aaa").enter("Aaaa").enter("Aaaaa")
      .exit("Aaaaa").exit("Aaaa").exit("Aaa").exit("Aa").effect("t1");
    
    Context ctx = new Context();
    StateMachineExecutor<Context> fsm = fsm(ctx);
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));
    assertSequentialContextEquals(expected, fsm);
    
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));
    assertSequentialContextEquals(expected, fsm);
  }

  @Override
  public StateMachineBuilder<Context> topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
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
        .finalState("end");
    
    return builder;
  }
  
  private CompositeStateBuilder<Context> compositeA(final String name) {
    final CompositeStateBuilder<Context> builder = new CompositeStateBuilder<>(name);
    
    builder
      .region()
        .state(compositeAa("Aa"));
    
    builder
      .region()
        .terminate("terminate");
    
    return builder;
  }
  
  private CompositeStateBuilder<Context> compositeAa(final String name) {
    final CompositeStateBuilder<Context> builder = new CompositeStateBuilder<>(name);
    
    builder
      .region()
        .state(compositeAaa("Aaa"));
    
    return builder;
  }
  
  private CompositeStateBuilder<Context> compositeAaa(final String name) {
    final CompositeStateBuilder<Context> builder = new CompositeStateBuilder<>(name);
    
    builder
      .region()
        .state(compositeAaaa("Aaaa"));
    
    return builder;
  }
  
  private CompositeStateBuilder<Context> compositeAaaa(final String name) {
    final CompositeStateBuilder<Context> builder = new CompositeStateBuilder<>(name);
    
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

  public static final class Context extends SequentialContext {
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test9.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"A\"\n" +
"      Region: #7\n" +
"        PseudoState: \"terminate\" kind: TERMINATE\n" +
"        State: \"Aa\"\n" +
"          Region: #9\n" +
"            State: \"Aaa\"\n" +
"              Region: #11\n" +
"                State: \"Aaaa\"\n" +
"                  Region: #13\n" +
"                    State: \"Aaaaa\"\n" +
"        Transition: \"t1\" --- \"Aaaaa\" -> \"terminate\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"Aaaaa\"\n" +
"    Transition: \"t2\" --- \"A\" -> \"end\"";
}
