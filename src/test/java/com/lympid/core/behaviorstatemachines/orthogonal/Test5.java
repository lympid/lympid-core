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
package com.lympid.core.behaviorstatemachines.orthogonal;

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.EntryPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.OrthogonalStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import org.junit.Test;

/**
 * Tests an entry point can be connect to a sub entry point
 * 
 * @author Fabien Renaud 
 */
public class Test5 extends AbstractStateMachineTest {
  
  @Test
  public void run() {
    SequentialContext expected = new SequentialContext();
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
  
    expected
      .effect("t0").enter("ortho")
      .effect("t1").enter("compo").effect("t2").enter("B");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, new ActiveStateTree("ortho", "compo", "B"));
    
    fsm.take(new StringEvent("go2"));
    expected
      .exit("B").effect("t3")
      .exit("compo").effect("t4")
      .exit("ortho").effect("t7");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder builder = new StateMachineBuilder(name());
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target("entryPoint1");
    
    builder
      .region()
        .state(ortho("ortho"))
          .transition("t7")
            .target("end");
    
    builder
      .region()
        .finalState("end");
    
    return builder;
  }
  
  private OrthogonalStateBuilder ortho(final String name) {
    OrthogonalStateBuilder builder = new OrthogonalStateBuilder(name);
    
    builder
      .connectionPoint()
        .entryPoint(new EntryPointBuilder<>("entryPoint1")
          .transition("t1")
            .target("entryPoint2")
        );
    
    builder
      .region("r1")
        .initial()
          .transition("t5")
            .target("A");
    
    builder
      .region("r1")
        .state("A")
          .transition("t6")
            .on("go1")
            .target("end1");
    
    builder
      .region("r1")
        .finalState("end1");
    
    builder
      .region("r2")
        .state(compo("compo"))
          .transition("t4")
            .target("end2");
    
    builder
      .region("r2")
        .finalState("end2");
    
    
    return builder;
  }
  
  private CompositeStateBuilder compo(final String name) {
    CompositeStateBuilder builder = new CompositeStateBuilder(name);
    
    builder
      .connectionPoint()
        .entryPoint(new EntryPointBuilder<>("entryPoint2")
          .transition("t2")
            .target("B")
        );
    
    builder
      .region()
        .state("B")
          .transition("t3")
            .on("go2")
            .target("cEnd");
    
    builder
      .region()
        .finalState("cEnd");
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }

  private static final String STDOUT = "StateMachine: \"" + Test5.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"ortho\"\n" +
"      EntryPoint: \"entryPoint1\"\n" +
"      Region: \"r2\"\n" +
"        FinalState: \"end2\"\n" +
"        State: \"compo\"\n" +
"          EntryPoint: \"entryPoint2\"\n" +
"          Region: #10\n" +
"            State: \"B\"\n" +
"            FinalState: \"cEnd\"\n" +
"            Transition: \"t3\" --- \"B\" -> \"cEnd\"\n" +
"            Transition: \"t2\" -L- \"entryPoint2\" -> \"B\"\n" +
"        Transition: \"t4\" --- \"compo\" -> \"end2\"\n" +
"        Transition: \"t1\" -L- \"entryPoint1\" -> \"entryPoint2\"\n" +
"      Region: \"r1\"\n" +
"        FinalState: \"end1\"\n" +
"        PseudoState: #19 kind: INITIAL\n" +
"        State: \"A\"\n" +
"        Transition: \"t5\" --- #19 -> \"A\"\n" +
"        Transition: \"t6\" --- \"A\" -> \"end1\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"entryPoint1\"\n" +
"    Transition: \"t7\" --- \"ortho\" -> \"end\"";
}
