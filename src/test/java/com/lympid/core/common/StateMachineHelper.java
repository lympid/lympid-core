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

import com.lympid.core.behaviorstatemachines.ConnectionPointReference;
import com.lympid.core.behaviorstatemachines.FinalState;
import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.SimpleVisitor;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.StateMachineTest;
import com.lympid.core.behaviorstatemachines.Transition;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Fabien Renaud
 */
public final class StateMachineHelper {

  private static final Map<StateMachineTest, Map<String, String>> ID_TO_NAME = new HashMap<>();
  private static final Map<StateMachineTest, Map<String, String>> NAME_TO_ID = new HashMap<>();

  private StateMachineHelper() {
  }

  public static String nameToId(final StateMachineTest test, final String name) {
    assert name != null;
    assert !name.isEmpty();
    
    Map<String, String> mapping = NAME_TO_ID.get(test);
    if (mapping == null) {
      register(test);
      mapping = NAME_TO_ID.get(test);
    }
    return mapping.get(name);
  }

  public static String idToName(final StateMachineTest test, final String id) {
    Map<String, String> mapping = ID_TO_NAME.get(test);
    if (mapping == null) {
      register(test);
      mapping = ID_TO_NAME.get(test);
    }
    return mapping.get(id);
  }

  private static void register(final StateMachineTest test) {
    IdToNameVisitor visitor = new IdToNameVisitor();
    test.topLevelStateMachine().accept(visitor);

    synchronized (ID_TO_NAME) {
      ID_TO_NAME.put(test, visitor.idToName);
    }
    synchronized (NAME_TO_ID) {
      NAME_TO_ID.put(test, visitor.nameToId);
    }
  }

  private static final class IdToNameVisitor extends SimpleVisitor {

    private final Map<String, String> idToName = new HashMap<>();
    private final Map<String, String> nameToId = new HashMap<>();

    @Override
    public void visit(ConnectionPointReference visitable) {
      register(visitable);
    }

    @Override
    public void visit(State visitable) {
      register(visitable);
    }

    @Override
    public void visit(PseudoState visitable) {
      register(visitable);
    }

    @Override
    public void visit(FinalState visitable) {
      register(visitable);
    }

    @Override
    public void visit(Region visitable) {
      register(visitable);
    }

    @Override
    public void visit(StateMachine visitable) {
      register(visitable);
    }

    @Override
    public void visit(Transition visitable) {
      register(visitable);
    }

    private void register(final UmlElement e) {
      idToName.put(e.getId(), e.getName());
      
      if (e.getName() != null && !e.getName().isEmpty()) {
        String val = nameToId.get(e.getName());
        if (val == null) {
          nameToId.put(e.getName(), ":" + e.getId() + ":");
        } else {
          nameToId.put(e.getName(), val + e.getId() + ":");
        }
      }
    }
  }
}
