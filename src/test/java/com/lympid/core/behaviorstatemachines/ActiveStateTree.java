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

import com.lympid.core.common.TreeNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Fabien Renaud 
 */
public final class ActiveStateTree {

  private final TreeNode<ActiveState> tree = new TreeNode<>();
  private final Map<Integer, TreeNode<ActiveState>> nodesById = new HashMap<>();
  private final Map<String, TreeNode<ActiveState>> nodesByName = new HashMap<>();
  private BranchBuilder rootBranch;

  public ActiveStateTree(final String... branch) {
    branch(branch);
  }

  public TreeNode<ActiveState> get() {
    return tree;
  }

  private BranchBuilder branch(final int id, final String name) {
    if (rootBranch == null) {
      createRoot(id, name);
    }

    if (tree.content().getId() != id) {
      throw new RuntimeException(); // TODO: custom exception
    }
    if (!Objects.equals(tree.content().getName(), name)) {
      throw new RuntimeException(); // TODO: custom exception
    }
    return rootBranch;
  }

  public BranchBuilder branch(final String name) {
    if (name.charAt(0) == '#') {
      return branch(Integer.parseInt(name.substring(1)), null);
    }
    return branch(0, name);
  }

  public ActiveStateTree branch(final String... branch) {
    if (branch.length != 0) {
      BranchBuilder bb = branch(branch[0]);
      for (int i = 1; i < branch.length; i++) {
        bb = bb.child(branch[i]);
      }
    }
    return this;
  }

  private void createRoot(final int id, final String name) {
    ActiveState as = new ActiveState(id, name);
    tree.setContent(as);
    if (id != 0) {
      nodesById.put(id, tree);
    }
    if (name != null) {
      nodesByName.put(name, tree);
    }
    this.rootBranch = new BranchBuilder(tree);
  }

  public final class BranchBuilder {

    private final TreeNode<ActiveState> parent;

    private BranchBuilder(final TreeNode<ActiveState> parent) {
      this.parent = parent;
    }

    private BranchBuilder child(final int id, final String name) {
      if (id != 0) {
        TreeNode<ActiveState> n = nodesById.get(id);
        if (n == null) {
          ActiveState as = new ActiveState(id, name);
          n = new TreeNode<>(as);
          parent.add(n);
          nodesById.put(id, n);
        }
        return new BranchBuilder(n);
      }

      if (name != null) {
        TreeNode<ActiveState> n = nodesByName.get(name);
        if (n == null) {
          ActiveState as = new ActiveState(id, name);
          n = new TreeNode<>(as);
          parent.add(n);
          nodesByName.put(name, n);
        }
        return new BranchBuilder(n);
      }

      return null;
    }

    private BranchBuilder child(final String name) {
      if (name.charAt(0) == '#') {
        return child(Integer.parseInt(name.substring(1)), null);
      }
      return child(0, name);
    }

  }
}
