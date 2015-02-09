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

import com.lympid.core.behaviorstatemachines.validation.StateConstraintException;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud
 */
public class OrthogonalStateBuilderTest {
  
  @Test(expected = StateConstraintException.class)
  public void notEnoughRegions_0() {
    OrthogonalStateBuilder builder = new OrthogonalStateBuilder();
    builder.setId("1123");
    builder.vertex(new VertexSet());
  }
  
  @Test(expected = StateConstraintException.class)
  public void notEnoughRegions_1() {
    OrthogonalStateBuilder builder = new OrthogonalStateBuilder();
    builder.setId("1123");
    
    builder.region("r1");
    
    builder.vertex(new VertexSet());
  }
  
  @Test
  public void enoughRegions() {
    OrthogonalStateBuilder builder = new OrthogonalStateBuilder();
    builder.setId("1123");
    
    builder.region("r1");
    builder.region("r2");
    
    builder.vertex(new VertexSet());
  }
}
