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

import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.EntryPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.ExitPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.OrthogonalStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import org.junit.Test;

/**
 * Tests an entry point can be connect to a sub exit point
 * 
 * @author Fabien Renaud 
 */
public class Test6 extends AbstractStateMachineTest {
  
  @Test
  public void run() {
    SequentialContext expected = new SequentialContext();
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
  
    expected
      .effect("t0").enter("ortho")
      .effect("t1").enter("compo")
      .exit("compo").effect("t2")
      .exit("ortho").effect("t3");
    assertSequentialContextEquals(expected, ctx);
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("end").get());
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
          .transition("t3")
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
            .target("exitPoint2")
        );
    
    builder
      .region("r1");
    
    builder
      .region("r2")
        .state(compo("compo"));
    
    builder
      .region("r2")
        .finalState("end2");
    
    
    return builder;
  }
  
  private CompositeStateBuilder compo(final String name) {
    CompositeStateBuilder builder = new CompositeStateBuilder(name);
    
    builder
      .connectionPoint()
        .exitPoint(new ExitPointBuilder<>("exitPoint2")
          .transition("t2")
            .target("end2")
        );
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }

  private static final String STDOUT = "StateMachine: \"" + Test6.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"ortho\"\n" +
"      EntryPoint: \"entryPoint1\"\n" +
"      Region: \"r2\"\n" +
"        FinalState: \"end2\"\n" +
"        State: \"compo\"\n" +
"          ExitPoint: \"exitPoint2\"\n" +
"          Region: #9\n" +
"        Transition: \"t2\" --- \"exitPoint2\" -> \"end2\"\n" +
"        Transition: \"t1\" -L- \"entryPoint1\" -> \"exitPoint2\"\n" +
"      Region: \"r1\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"entryPoint1\"\n" +
"    Transition: \"t3\" --- \"ortho\" -> \"end\"";
}
