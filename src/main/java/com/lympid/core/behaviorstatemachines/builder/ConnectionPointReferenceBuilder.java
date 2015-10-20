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
package com.lympid.core.behaviorstatemachines.builder;

import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.common.UmlElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Fabien Renaud
 */
public class ConnectionPointReferenceBuilder<C> implements Visitable {

  private final String namePrefix;
  private final Map<String, ExitPointBuilder<C>> exitPoints = new HashMap<>();
  private String id;

  public ConnectionPointReferenceBuilder(final String stateName) {
    this.namePrefix = stateName + "::";
  }

  public ExitPointBuilder<C> exitPoint(final String simpleName) {
    return exitPointFull(namePrefix + simpleName);
  }

  private ExitPointBuilder<C> exitPointFull(final String fullName) {
    ExitPointBuilder<C> point = exitPoints.get(fullName);
    if (point == null) {
      point = new ExitPointBuilder<>(fullName);
      exitPoints.put(fullName, point);
    }
    return point;
  }

  /**
   * Replaces the exit point reference of the sub machine state with the actual
   * exit point of the sub state machine.
   *
   * The merge is performed by name.
   *
   * @param exitPoint The exit point from the sub state machine.
   */
  void mergeExitPoint(final PseudoState exitPoint) {
    assert exitPoint.kind() == PseudoStateKind.EXIT_POINT : "Parameter must be an exit point";
    for (Entry<String, ExitPointBuilder<C>> e : exitPoints.entrySet()) {
      if (exitPoint.getName().endsWith(e.getKey())) {
        e.getValue().setId(exitPoint.getId());
        return;
      }
    }
    throw new ConnectionPointBindingException(exitPoint.getName(), exitPoints.keySet(), "The exit point of the sub state machine has not been defined in the sub machine state.");
  }

  void connect(final VertexSet vertices) {
    for (Entry<String, ExitPointBuilder<C>> e : exitPoints.entrySet()) {
      ExitPointBuilder<C> exitPoint = e.getValue();
      String name = exitPoint.getName();
      exitPoint.setName(e.getKey());
      exitPoint.connect(vertices);
      exitPoint.setName(name);
    }
  }

  /**
   * Gets the {@link UmlElement} unique identifier of the connection point
   * reference.
   *
   * @return The unique identifier of the connection point reference.
   */
  String getId() {
    return id;
  }

  /**
   * Sets the {@link UmlElement} id of the connection point reference.
   *
   * @param id A unique id across the whole state machine.
   */
  void setId(final String id) {
    this.id = id;
  }

  /**
   * Gets the {@link UmlElement} name of the connection point reference.
   *
   * @return null
   */
  String getName() {
    return null;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
    for (ExitPointBuilder e : exitPoints.values()) {
      e.accept(visitor);
    }
  }
}
