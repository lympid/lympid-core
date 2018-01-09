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

import org.junit.Before;
import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 *
 * @author Fabien Renaud 
 */
public class RegionBuilderTest {

  private static final String VERTEX_NAME = "A";
  private static final CompositeStateBuilder<Object> FIRST_VERTEX = new CompositeStateBuilder<>(VERTEX_NAME);
  private RegionBuilder region;

  @Before
  public void setUp() {
    StateMachineBuilder<Object> builder = new StateMachineBuilder<>("abc");
    builder.region("regionName").state(FIRST_VERTEX);
    region = builder.region("regionName");
  }
  
  @Test
  public void idAndName() {
    assertEquals(null, region.getId());
    String newId = "24fag4efr";
    region.setId(newId);
    assertEquals(newId, region.getId());
    
    assertEquals("regionName", region.getName());
  }
  
  @Test
  public void vertexRegistry() {
    SimpleStateBuilder<Object> stateBuilder1 = new SimpleStateBuilder<>("B");
    SimpleStateBuilder<Object> stateBuilder2 = region.state(stateBuilder1);
    SimpleStateBuilder<Object> stateBuilder3 = region.state(stateBuilder1);
    assertTrue(stateBuilder1 == stateBuilder2);
    assertTrue(stateBuilder1 == stateBuilder3);
  }
  
  @Test
  public void singletonByName_simple() {
    singletonByName(region::state);
  }

  @Test
  public void singletonByName_deepHistory() {
    singletonByName(region::deepHistory);
  }

  @Test
  public void singletonByName_finalState() {
    singletonByName(region::finalState);
  }
  
  @Test
  public void singletonByName_initial() {
    singletonByName(region::initial);
  }
  
  @Test
  public void singletonByName_choice() {
    singletonByName(region::choice);
  }

  @Test
  public void singletonByName_fork() {
    singletonByName(region::fork);
  }

  @Test
  public void singletonByName_junction() {
    singletonByName(region::junction);
  }

  @Test
  public void singletonByName_join() {
    singletonByName(region::join);
  }

  @Test
  public void singletonByName_shallowHistory() {
    singletonByName(region::shallowHistory);
  }

  @Test
  public void singletonByName_terminate() {
    singletonByName(region::terminate);
  }
  
  private void singletonByName(Function<String, VertexBuilderReference> consumer) {
    VertexBuilderReference ref1 = consumer.apply("B");
    VertexBuilderReference ref2 = consumer.apply("B");
    assertNotNull(ref1);
    assertTrue(ref1 == ref2);
  }

  @Test(expected = DuplicateVertexBuilderNameException.class)
  public void duplicateVertexName_simple() {
    duplicateVertexName(region::state);
  }

  @Test(expected = DuplicateVertexBuilderNameException.class)
  public void duplicateVertexName_deepHistory() {
    duplicateVertexName(region::deepHistory);
  }

  @Test(expected = DuplicateVertexBuilderNameException.class)
  public void duplicateVertexName_finalState() {
    duplicateVertexName(region::finalState);
  }

  @Test(expected = DuplicateVertexBuilderNameException.class)
  public void duplicateVertexName_initial() {
    duplicateVertexName(region::initial);
  }

  @Test(expected = DuplicateVertexBuilderNameException.class)
  public void duplicateVertexName_choice() {
    duplicateVertexName(region::choice);
  }

  @Test(expected = DuplicateVertexBuilderNameException.class)
  public void duplicateVertexName_fork() {
    duplicateVertexName(region::fork);
  }

  @Test(expected = DuplicateVertexBuilderNameException.class)
  public void duplicateVertexName_junction() {
    duplicateVertexName(region::junction);
  }

  @Test(expected = DuplicateVertexBuilderNameException.class)
  public void duplicateVertexName_join() {
    duplicateVertexName(region::join);
  }

  @Test(expected = DuplicateVertexBuilderNameException.class)
  public void duplicateVertexName_shallowHistory() {
    duplicateVertexName(region::shallowHistory);
  }

  @Test(expected = DuplicateVertexBuilderNameException.class)
  public void duplicateVertexName_terminate() {
    duplicateVertexName(region::terminate);
  }
  
  private void duplicateVertexName(Consumer<String> consumer) {
    try {
      consumer.accept(VERTEX_NAME);
    } catch (DuplicateVertexBuilderNameException ex) {
      assertEquals(VERTEX_NAME, ex.getVertexName());
      assertEquals(FIRST_VERTEX, ex.getExistingVertex());
      throw ex;
    }
  }
}
