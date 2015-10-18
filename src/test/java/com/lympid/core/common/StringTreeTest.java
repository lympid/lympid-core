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
package com.lympid.core.common;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Fabien Renaud
 */
public class StringTreeTest {
  
  @Test
  public void testDepth0() {
    StringTree st = new StringTree(null);
    assertNull(st.state());
    assertNull(st.children());
    assertEquals("", st.toString());
    
    assertEquals(new StringTree(null).hashCode(), st.hashCode());
    assertEquals(new StringTree(null), st);
    
    assertFalse(st.equals(null));
    assertFalse(st.equals(new Object()));
    assertNotEquals(new StringTree("a"), st);
  }
  
  @Test
  public void testDepth1() {
    StringTree st = new StringTree("Abcd");
    assertEquals("Abcd", st.state());
    assertNull(st.children());
    assertEquals("Abcd", st.toString());
    assertEquals(new StringTree("Abcd"), st);
    assertNotEquals(new StringTree("Acd"), st);
  }
  
  @Test
  public void testDepth2() {
    List<StringTree> children = new ArrayList<>();
    children.add(new StringTree("a"));
    children.add(new StringTree("b"));
    children.add(new StringTree("c"));
    children.add(new StringTree("d"));
    StringTree root = new StringTree("root");
    root.setChildren(children);
    
    assertEquals("root", root.state());
    assertNotNull(root.children());
    assertEquals(4, root.children().size());
    assertEquals("a", root.children().get(0).state());
    assertNull("a", root.children().get(0).children());
    assertEquals("b", root.children().get(1).state());
    assertNull("a", root.children().get(1).children());
    assertEquals("c", root.children().get(2).state());
    assertNull("a", root.children().get(2).children());
    assertEquals("d", root.children().get(3).state());
    assertNull("a", root.children().get(3).children());
    
    assertEquals("root [a, b, c, d]", root.toString());
    
    List<StringTree> children1 = new ArrayList<>();
    children1.add(new StringTree("a"));
    children1.add(new StringTree("b"));
    children1.add(new StringTree("c"));
    children1.add(new StringTree("d"));
    StringTree root1 = new StringTree("root");
    root1.setChildren(children1);
    assertEquals(root.hashCode(), root1.hashCode());
    assertEquals(root, root1);
    
    List<StringTree> children2 = new ArrayList<>();
    children2.add(new StringTree("a"));
    children2.add(new StringTree("b"));
    children2.add(new StringTree("d"));
    StringTree root2 = new StringTree("root");
    root2.setChildren(children2);
    assertNotEquals(root.hashCode(), root2.hashCode());
    assertNotEquals(root, root2);
  }
}
