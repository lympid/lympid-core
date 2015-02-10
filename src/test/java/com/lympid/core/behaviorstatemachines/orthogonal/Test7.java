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
 * Tests an entry point can be connect to a sub sub entry point
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
      .effect("t0").enter("ortho")
      .effect("t1").enter("compo1").enter("compo2").effect("t2").enter("A");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, new ActiveStateTree("ortho", "compo1", "compo2", "A"));
    
    fsm.take(new StringEvent("go2"));
    expected
      .exit("A").effect("t3")
      .exit("compo2").effect("t4")
      .exit("compo1").effect("t5")
      .exit("ortho").effect("t6");
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
          .transition("t6")
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
      .region("r1");
    
    builder
      .region("r2")
        .state(compo1("compo1"))
          .transition("t5")
            .target("end2");
    
    builder
      .region("r2")
        .finalState("end2");
    
    
    return builder;
  }
  
  private CompositeStateBuilder compo1(final String name) {
    CompositeStateBuilder builder = new CompositeStateBuilder(name);
    
    builder
      .region()
        .state(compo2("compo2"))
          .transition("t4")
            .target("cend1");
    
    builder
      .region()
        .finalState("cend1");
    
    return builder;
  }
  
  private CompositeStateBuilder compo2(final String name) {
    CompositeStateBuilder builder = new CompositeStateBuilder(name);
    
    builder
      .connectionPoint()
        .entryPoint(new EntryPointBuilder<>("entryPoint2")
          .transition("t2")
            .target("A")
        );
    
    builder
      .region()
        .state("A")
          .transition("t3")
            .on("go2")
            .target("cend2");
    
    builder
      .region()
        .finalState("cend2");
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }

  private static final String STDOUT = "StateMachine: \"" + Test7.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    FinalState: \"end\"\n" +
"    State: \"ortho\"\n" +
"      EntryPoint: \"entryPoint1\"\n" +
"      Region: \"r2\"\n" +
"        State: \"compo1\"\n" +
"          Region: #10\n" +
"            State: \"compo2\"\n" +
"              EntryPoint: \"entryPoint2\"\n" +
"              Region: #13\n" +
"                State: \"A\"\n" +
"                FinalState: \"cend2\"\n" +
"                Transition: \"t3\" --- \"A\" -> \"cend2\"\n" +
"                Transition: \"t2\" -L- \"entryPoint2\" -> \"A\"\n" +
"            FinalState: \"cend1\"\n" +
"            Transition: \"t4\" --- \"compo2\" -> \"cend1\"\n" +
"        FinalState: \"end2\"\n" +
"        Transition: \"t5\" --- \"compo1\" -> \"end2\"\n" +
"        Transition: \"t1\" -L- \"entryPoint1\" -> \"entryPoint2\"\n" +
"      Region: \"r1\"\n" +
"    Transition: \"t0\" --- #3 -> \"entryPoint1\"\n" +
"    Transition: \"t6\" --- \"ortho\" -> \"end\"";
}
