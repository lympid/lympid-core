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
package com.lympid.core.behaviorstatemachines.builder;

import com.lympid.core.behaviorstatemachines.impl.MutableState;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Fabien Renaud
 */
public class VertexBuilderTest {

  @Test(expected = TransitionCreationException.class)
  public void connect_noSource() {
    SimpleStateBuilder<Object> builder = new SimpleStateBuilder<>("A");
    builder.setId("897234");
    try {
      builder.connect(new VertexSet());
    } catch (TransitionCreationException ex) {
      assertEquals("#897234", ex.getSource());
      assertNull(ex.getTarget());
      throw ex;
    }
  }

  @Test(expected = RuntimeException.class)
  public void connect_unregisteredTarget() {
    SimpleStateBuilder<Object> builderA = new SimpleStateBuilder<>("A");
    builderA.transition().target("B");

    MutableState stateA = new MutableState();
    stateA.setName("A");

    VertexSet vertices = new VertexSet();
    vertices.add(stateA);

    builderA.connect(vertices);
  }

  @Test(expected = RuntimeException.class)
  public void connect_noCommonAncestor() {
    SimpleStateBuilder<Object> builderA = new SimpleStateBuilder<>("A");
    builderA.transition().target("B");

    MutableState stateA = new MutableState();
    stateA.setName("A");
    MutableState stateB = new MutableState();
    stateB.setName("B");

    VertexSet vertices = new VertexSet();
    vertices.add(stateA);
    vertices.add(stateB);

    try {
      builderA.connect(vertices);
    } catch (RuntimeException ex) {
      assertTrue(ex.getCause() instanceof CommonAncestorException);
      throw ex;
    }
  }
}
