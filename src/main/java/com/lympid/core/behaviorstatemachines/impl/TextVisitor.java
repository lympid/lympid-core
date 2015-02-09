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
package com.lympid.core.behaviorstatemachines.impl;

import com.lympid.core.behaviorstatemachines.ConnectionPointReference;
import com.lympid.core.behaviorstatemachines.FinalState;
import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.Visitor;
import com.lympid.core.common.UmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fabien Renaud
 */
public final class TextVisitor implements Visitor {

  private static final String NEW_LINE = System.getProperty("line.separator");
  private final List<String> lines = new ArrayList<>();
  private String text;
  private int depth;

  @Override
  public void visitOnEntry(ConnectionPointReference v) {
    add(padding().append("ConnectionPointReference: ").append(nameOrId(v)));
    ++depth;
  }

  @Override
  public void visitOnExit(ConnectionPointReference v) {
    --depth;
  }

  @Override
  public void visitOnEntry(State v) {
    add(padding().append("State: ").append(nameOrId(v)));
    ++depth;
  }

  @Override
  public void visitOnExit(State v) {
    --depth;
  }

  @Override
  public void visitOnEntry(PseudoState v) {
    switch (v.kind()) {
      case ENTRY_POINT:
        add(padding().append("EntryPoint: ").append(nameOrId(v)));
        break;
      case EXIT_POINT:
        add(padding().append("ExitPoint: ").append(nameOrId(v)));
        break;
      case CHOICE:
      case DEEP_HISTORY:
      case FORK:
      case INITIAL:
      case JOIN:
      case JUNCTION:
      case SHALLOW_HISTORY:
      case TERMINATE:
        add(padding().append("PseudoState: ").append(nameOrId(v)).append(" kind: ").append(v.kind()));
        break;
    }
    ++depth;
  }

  @Override
  public void visitOnExit(PseudoState v) {
    --depth;
  }

  @Override
  public void visitOnEntry(FinalState v) {
    add(padding().append("FinalState: ").append(nameOrId(v)));
    ++depth;
  }

  @Override
  public void visitOnExit(FinalState v) {
    --depth;
  }

  @Override
  public void visitOnEntry(Region v) {
    add(padding().append("Region: ").append(nameOrId(v)));
    ++depth;
  }

  @Override
  public void visitOnExit(Region v) {
    --depth;
  }

  @Override
  public void visitOnEntry(StateMachine v) {
    add(padding().append("StateMachine: ").append(nameOrId(v)));
    ++depth;
  }

  @Override
  public void visitOnExit(StateMachine v) {
    --depth;
  }

  @Override
  public void visitOnEntry(Transition v) {
    StringBuilder b = padding().append("Transition: ").append(nameOrId(v));
    switch (v.kind()) {
      case EXTERNAL:
        b.append(" --- ");
        break;
      case INTERNAL:
        b.append(" -I- ");
        break;
      case LOCAL:
        b.append(" -L- ");
        break;
    }
    add(b.append(nameOrId(v.source())).append(" -> ").append(nameOrId(v.target())));
    ++depth;
  }

  @Override
  public void visitOnExit(Transition v) {
    --depth;
  }

  private StringBuilder padding() {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < depth; i++) {
      b.append("  ");
    }
    return b;
  }

  private <T extends UmlElement> String nameOrId(T element) {
    if (element.getName() != null) {
      return "\"" + element.getName() + "\"";
    }
    return "#" + element.getId();
  }

  private void add(final StringBuilder b) {
    lines.add(b.toString());
  }

  @Override
  public String toString() {
    if (text == null) {
      StringBuilder b = new StringBuilder();
      for (String l : lines) {
        b.append(l).append(NEW_LINE);
      }
      text = b.toString();
    }
    return text;
  }
}
