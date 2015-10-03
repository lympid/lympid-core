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
package com.lympid.core.behaviorstatemachines.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class MutableRegionTest {
  
  private MutableRegion region;
  
  @Before
  public void setUp() {
    region = new MutableRegion();
  }
  
  @Test
  public void testEquals() {
    Object obj = null;
    assertFalse(region.equals(obj));
    
    obj = new Object();
    assertFalse(region.equals(obj));
  }
  
  @Test
  public void testToString() {
    assertEquals("#" + region.getId(), region.toString());
    
    region.setName("abababcc");
    assertEquals(region.getName(), region.toString());
  }
}
