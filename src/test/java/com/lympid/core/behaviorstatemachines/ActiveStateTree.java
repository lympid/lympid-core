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

import com.lympid.core.common.StateMachineHelper;
import com.lympid.core.common.TreeNode;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Fabien Renaud
 */
public final class ActiveStateTree {

  private final StateMachineTest test;
  private final TreeNode<String> tree = new TreeNode<>();
  private final Map<String, TreeNode<String>> nodes = new HashMap<>();
  private BranchBuilder rootBranch;

  public ActiveStateTree(final StateMachineTest test) {
    this.test = test;
  }

  public TreeNode<String> tree() {
    return tree;
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

  private BranchBuilder branch(final String name) {
    if (name.charAt(0) == '#') {
      return branchId(name.substring(1));
    }

    String id = StateMachineHelper.nameToId(test, name);
    return branchId(id);
  }

  private BranchBuilder branchId(final String id) {
    if (rootBranch == null) {
      createRoot(id);
    }

    if (!tree.content().equals(id)) {
      throw new RuntimeException(); // TODO: custom exception
    }
    return rootBranch;
  }

  private void createRoot(final String id) {
    assert id != null;

    tree.setContent(id);
    nodes.put(id, tree);
    this.rootBranch = new BranchBuilder(tree);
  }

  public final class BranchBuilder {

    private final TreeNode<String> parent;

    private BranchBuilder(final TreeNode<String> parent) {
      this.parent = parent;
    }

    private BranchBuilder child(final String name) {
      if (name.charAt(0) == '#') {
        return childId(name.substring(1));
      }

      String id = StateMachineHelper.nameToId(test, name);
      return childId(id);
    }

    private BranchBuilder childId(final String id) {
      assert id != null;

      TreeNode<String> n = nodes.get(id);
      if (n == null) {
        n = new TreeNode<>(id);
        parent.add(n);
        nodes.put(id, n);
      }
      return new BranchBuilder(n);
    }

  }
}
