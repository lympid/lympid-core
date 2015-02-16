
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

package com.lympid.core.behaviorstatemachines.composite;

import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.FinalStateTest;
import com.lympid.core.behaviorstatemachines.InitialPseudoStateTest;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.RegionTest;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.SimpleStateTest;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.StateMachineTester;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.TransitionTest;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexTest;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.EntryPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.ExitPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests entry/exit points sequence with a local transition.
 * The state machine auto starts.
 * @author Fabien Renaud 
 */
public class Test9 extends AbstractStateMachineTest {
  
  @Test
  public void model() {
    assertEquals(getClass().getSimpleName(), topLevelStateMachine().getName());
    Region region = StateMachineTester.assertTopLevelStateMachine(topLevelStateMachine());

    StateMachineTester.assertRegion(region, 3, 2, 
      new VertexTest[]{
        new InitialPseudoStateTest("#3"),
        new VertexTest("A", this::verifyA),
        new FinalStateTest("end")
      },
      new TransitionTest[]{
        new TransitionTest("#4", "#3", "A_entryPoint"),
        new TransitionTest("#14", "A_exitPoint", "#5")
      }
    );
  }
  
  private void verifyA(Vertex v) {
    State s = StateMachineTester.assertComposite(v);
    StateMachineTester.assertRegions(s.region(), 1, new RegionTest("7", null, 1, 2, 
      new VertexTest[] {
        new SimpleStateTest("Aa")
      },
      new TransitionTest[] {
        new TransitionTest("#9", "Aa", "A_exitPoint"),
        new TransitionTest("#11", "A_entryPoint", "Aa", TransitionKind.LOCAL)
      }
    ));
  }
  
  @Test
  public void runIt() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("A")
      .effect("t1").enter("Aa")
      .exit("Aa").effect("t2")
      .exit("A").effect("t3");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree(this).branch("#5").get());
    
    assertSequentialContextEquals(expected, ctx);
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder builder = new StateMachineBuilder(name());

    builder
      .region()
        .initial()
          .transition("t0")
            .target("A_entryPoint");
    
    builder
      .region()
        .finalState("end");
    
    builder
      .region()
        .state(compositeA("A"));

    return builder;
  }
  
  private CompositeStateBuilder compositeA(final String name) {
    CompositeStateBuilder builder = new CompositeStateBuilder(name);
        
    builder
      .connectionPoint()
        .entryPoint(new EntryPointBuilder<>("A_entryPoint")
          .transition("t1")
            .target("Aa"))
        .exitPoint(new ExitPointBuilder<>("A_exitPoint")
          .transition("t3")
            .target("end"));
    
    builder
      .region()
        .state("Aa")
          .transition("t2")
            .target("A_exitPoint");
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }

  private static final String STDOUT = "StateMachine: \"" + Test9.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    FinalState: \"end\"\n" +
"    State: \"A\"\n" +
"      EntryPoint: \"A_entryPoint\"\n" +
"      ExitPoint: \"A_exitPoint\"\n" +
"      Region: #7\n" +
"        State: \"Aa\"\n" +
"        Transition: \"t2\" --- \"Aa\" -> \"A_exitPoint\"\n" +
"        Transition: \"t1\" -L- \"A_entryPoint\" -> \"Aa\"\n" +
"    Transition: \"t0\" --- #3 -> \"A_entryPoint\"\n" +
"    Transition: \"t3\" --- \"A_exitPoint\" -> \"end\"";
}
