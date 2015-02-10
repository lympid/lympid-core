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
package com.lympid.core.behaviorstatemachines;

/**
 *
 * @author Fabien Renaud 
 */
public final class RegionTest {

  private final String id;
  private final String name;
  private final int expectedNumberOfVertices;
  private final int expectedNumberOfTransitions;
  private final VertexTest[] vertexTests;
  private final TransitionTest[] transitionTests;

  public RegionTest(final String id, final String name, final int expectedNumberOfVertices, final int expectedNumberOfTransitions, final VertexTest[] vertexTests, final TransitionTest[] transitionTests) {
    assert expectedNumberOfVertices >= 0;
    assert expectedNumberOfTransitions >= 0;
    this.id = id;
    this.name = name;
    this.expectedNumberOfVertices = expectedNumberOfVertices;
    this.expectedNumberOfTransitions = expectedNumberOfTransitions;
    this.vertexTests = vertexTests;
    this.transitionTests = transitionTests;
  }

  public RegionTest(final String id, final String name, final int expectedNumberOfVertices, final int expectedNumberOfTransitions, final VertexTest... vertexTests) {
    this(id, name, expectedNumberOfVertices, expectedNumberOfTransitions, vertexTests, null);
  }

  public RegionTest(final String id, final String name, final int expectedNumberOfVertices, final int expectedNumberOfTransitions, final TransitionTest... transitionTests) {
    this(id, name, expectedNumberOfVertices, expectedNumberOfTransitions, null, transitionTests);
  }

  public RegionTest(final String id, final String name, final int expectedNumberOfVertices, final int expectedNumberOfTransitions) {
    this(id, name, expectedNumberOfVertices, expectedNumberOfTransitions, null, null);
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getExpectedNumberOfVertices() {
    return expectedNumberOfVertices;
  }

  public int getExpectedNumberOfTransitions() {
    return expectedNumberOfTransitions;
  }

  public VertexTest[] getVertexTests() {
    return vertexTests;
  }

  public TransitionTest[] getTransitionTests() {
    return transitionTests;
  }

}
