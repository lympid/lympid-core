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

import java.util.Iterator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class TreeNodeTest {

  private TreeNode<String> node;

  @Before
  public void setUp() {
    node = new TreeNode<>();
  }

  /**
   * Test of copy method, of class TreeNode.
   */
  @Test
  public void testCopy() {
    TreeNode<String> tree = new TreeNode<>("root");
    TreeNode<String> child1 = new TreeNode<>("child1");
    TreeNode<String> child2 = new TreeNode<>("child2");
    TreeNode<String> child3 = new TreeNode<>("child3");
    TreeNode<String> child11 = new TreeNode<>("child11");
    TreeNode<String> child12 = new TreeNode<>("child12");
    TreeNode<String> child31 = new TreeNode<>("child31");

    tree.add(child1);
    tree.add(child2);
    tree.add(child3);
    child1.add(child11);
    child1.add(child12);
    child3.add(child31);

    TreeNode<String> copy = tree.copy();
    assertTrue(copy != tree);
    assertTrue(tree.equals(copy));
  }

  /**
   * Test of add method, of class TreeNode.
   */
  @Test
  public void testAdd() {
    assertFalse(node.add(null));
  }

  /**
   * Test of parent method, of class TreeNode.
   */
  @Test
  public void testParent() {
    assertNull(node.parent());

    TreeNode<String> parent = new TreeNode<>("p");
    node.setParent(parent);
    assertEquals(parent, node.parent());
  }

  /**
   * Test of children method, of class TreeNode.
   */
  @Test
  public void testChildren() {
    assertTrue(node.isLeaf());
    assertFalse(node.hasChildren());
    assertEquals(0, node.size());

    TreeNode<String> child1 = new TreeNode<>("data1");
    TreeNode<String> child2 = new TreeNode<>("data2");

    node.add(child1);
    assertFalse(node.isLeaf());
    assertTrue(node.hasChildren());
    assertEquals(1, node.size());

    node.add(child2);
    assertFalse(node.isLeaf());
    assertTrue(node.hasChildren());
    assertEquals(2, node.size());

    node.remove(child1);
    assertFalse(node.isLeaf());
    assertTrue(node.hasChildren());
    assertEquals(1, node.size());

    node.remove(child2);
    assertTrue(node.isLeaf());
    assertFalse(node.hasChildren());
    assertEquals(0, node.size());
  }

  /**
   * Test of iterator method, of class TreeNode.
   */
  @Test
  public void testIterator() {
    TreeNode<String> node1 = new TreeNode<>("data");
    TreeNode<String> node2 = new TreeNode<>("data");
    node.add(node1);
    node.add(node2);
    assertEquals(2, node.size());

    /*
     * Iterator returns a shallow copy of the children.
     */
    int i = 0;
    Iterator<TreeNode<String>> it = node.iterator();
    while (it.hasNext()) {
      TreeNode<String> child = it.next();
      assertTrue(child == node.children().get(i++));
      it.remove();
      assertEquals(2, node.size());
    }
  }

  /**
   * Test of hashCode method, of class TreeNode.
   */
  @Test
  public void testHashCode() {
    TreeNode<String> node1 = new TreeNode<>("data");
    TreeNode<String> node2 = new TreeNode<>("data");

    /*
     * Nodes with the same content, no parent and no children have the same
     * hashcode.
     */
    assertEquals(node1.hashCode(), node2.hashCode());

    /*
     * Nodes with different parents have not the same hashcode.
     */
    node.add(node1);
    assertNotEquals(node1.hashCode(), node2.hashCode());
    node.add(node2);
    assertEquals(node1.hashCode(), node2.hashCode());

    node.remove(node1);
    assertNotEquals(node1.hashCode(), node2.hashCode());
    node.remove(node2);
    assertEquals(node1.hashCode(), node2.hashCode());

    /*
     * Nodes with different children have not the same hashcode.
     */
    node1.add(node);
    assertNotEquals(node1.hashCode(), node2.hashCode());
    node1.remove(node);

    /*
     * The content of the parent node matters
     */
    TreeNode<String> root = new TreeNode<>("root");
    node.add(node1);
    assertNotEquals(node1.hashCode(), node2.hashCode());
    root.add(node2);
    assertNotEquals(node1.hashCode(), node2.hashCode());
  }

  /**
   * Test of equals method, of class TreeNode.
   */
  @Test
  public void testEquals() {
    Object obj = null;
    assertFalse(node.equals(obj));

    obj = new Object();
    assertFalse(node.equals(obj));

    TreeNode<String> node1 = new TreeNode<>("data");
    TreeNode<String> node2 = new TreeNode<>("data");
    assertTrue(node1.equals(node1));
    assertTrue(node1.equals(node2));

    TreeNode<String> child = new TreeNode<>("child");
    node1.add(child);
    assertFalse(node1.equals(node2));

    node1.remove(child);
    assertTrue(node1.equals(node2));

    node.add(node1);
    assertFalse(node1.equals(node2));
    node.add(node2);
    assertTrue(node1.equals(node2));
    
    node.setContent("root");

    node.remove(node1);
    assertFalse(node1.equals(node2));
    node.remove(node2);
    assertTrue(node1.equals(node2));

    node1.setParent(node);
    assertFalse(node1.equals(node2));
    node2.setParent(node);
    assertTrue(node1.equals(node2));
    node.remove(node1);
    node.remove(node2);
    
    /*
     * The content of the parent node matters
     */
    TreeNode<String> another = new TreeNode<>("another");
    node.add(node1);
    another.add(node2);
    assertFalse(node1.equals(node2));
  }

  /**
   * Test of toString method, of class TreeNode.
   */
  @Test
  public void testToString() {
    assertEquals("null", node.toString());

    node.setContent("hello");
    assertEquals("hello", node.toString());

    assertEquals("hi", new TreeNode<>("hi").toString());
  }

}
