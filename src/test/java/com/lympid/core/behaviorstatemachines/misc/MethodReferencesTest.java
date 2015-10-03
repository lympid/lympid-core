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
package com.lympid.core.behaviorstatemachines.misc;

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;

/**
 *
 * @author Fabien Renaud 
 */
public class MethodReferencesTest extends AbstractStateMachineTest {

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    final StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    builder
      .region()
        .initial("start")
          .transition("t0")
            .target("A");
    
    builder
      .region()
        .state("A")
          .transition("t1")
            .effect(MachineActions::t1Effect)
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
  
  public static final class MachineActions {
    
    public static void t1Effect(Event event, Context context) {
      
    }
    
  }
  
  private static final class Context {
    
  }
  
  private static final String STDOUT = "StateMachine: \"MethodReferencesTest\"\n" +
"  Region: #2\n" +
"    PseudoState: \"start\" kind: INITIAL\n" +
"    State: \"A\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- \"start\" -> \"A\"\n" +
"    Transition: \"t1\" --- \"A\" -> \"end\"";
}
