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
package com.lympid.core.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Fabien Renaud
 */
public class TreeNode<T> implements Iterable<TreeNode<T>> {

  private T content;
  private TreeNode parent;
  private final List<TreeNode<T>> children;

  public TreeNode(final T content) {
    this.content = content;
    this.children = new LinkedList<>();
  }

  private TreeNode(final T content, final int size) {
    this.content = content;
    this.children = new ArrayList<>(size);
  }

  public TreeNode() {
    this(null);
  }

  public TreeNode<T> copy() {
    final TreeNode<T> node = new TreeNode<>(content, children.size());
    for (TreeNode<T> c : children()) {
      node.add(c.copy());
    }
    return node;
  }

  public T content() {
    return content;
  }

  public void setContent(final T content) {
    this.content = content;
  }

  public boolean add(final TreeNode<T> node) {
    if (node == null) {
      return false;
    }
    node.setParent(this);
    return children.add(node);
  }

  public boolean remove(final TreeNode<T> node) {
    node.setParent(null);
    return children.remove(node);
  }

  void setParent(final TreeNode<T> parent) {
    this.parent = parent;
  }

  public TreeNode<T> parent() {
    return parent;
  }

  public List<TreeNode<T>> children() {
    return children;
  }

  public int size() {
    return children.size();
  }

  public boolean hasChildren() {
    return children.size() > 0;
  }

  public boolean isLeaf() {
    return children.isEmpty();
  }

  @Override
  public Iterator<TreeNode<T>> iterator() {
    return new ArrayList<>(children).iterator();
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 11 * hash + Objects.hashCode(this.content);
    hash = 11 * hash + Objects.hashCode(this.children);
    if (this.parent != null) {
      hash = 11 * hash + Objects.hashCode(this.parent.content);
    }
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TreeNode<?> other = (TreeNode<?>) obj;
    if (!Objects.equals(this.content, other.content)) {
      return false;
    }
    if (this.parent == null && other.parent != null) {
      return false;
    }
    if (this.parent != null && other.parent == null) {
      return false;
    }
    if (this.parent != null && !Objects.equals(this.parent.content, other.parent.content)) {
      return false;
    }
    if (this.isLeaf()) {
      return other.isLeaf();
    }
    return Objects.deepEquals(this.children, other.children);
  }

  @Override
  public String toString() {
    return String.valueOf(content);
  }
}
