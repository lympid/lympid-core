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

package com.lympid.core.behaviorstatemachines.orthogonal;

import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.builder.EntryPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.ExitPointBuilder;
import com.lympid.core.behaviorstatemachines.builder.OrthogonalStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Local transitions outgoing an orthogonal state and targeting an exit point of
 * the same orthogonal state are not supported yet.
 * 
 * @author Fabien Renaud
 */
public class Test10  {
  
  private static final String EXCEPTION_MESSAGE = "Local transitions which source and target are the same orthogonal state are not supported yet.";
  
  @Test(expected = UnsupportedOperationException.class)
  public void unsupportedTransition() {
    try {
      topLevelMachineBuilder().newInstance();
    } catch (UnsupportedOperationException ex) {
      assertEquals(EXCEPTION_MESSAGE, ex.getMessage());
      throw ex;
    }
  }
  
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<SequentialContext> builder = new StateMachineBuilder<>(Test10.class.getSimpleName());

    builder
      .region()
        .initial()
          .transition("t0")
            .target("ortho");
    
    builder
      .region()
        .finalState("end");
    
    builder
      .region()
        .state(ortho("ortho"));

    return builder;
  }
  
  private OrthogonalStateBuilder ortho(final String name) {
    OrthogonalStateBuilder builder = new OrthogonalStateBuilder(name);
    
    builder
      .localTransition("t1")
        .target("exitPoint");
    
    builder
      .connectionPoint()
        .exitPoint(new ExitPointBuilder<>("exitPoint")
          .transition("t2")
            .target("end"));
    
    builder.region("r1");
    builder.region("r2");
    
    return builder;
  }
}
