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

import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.impl.MutableRegion;
import com.lympid.core.behaviorstatemachines.impl.MutableStateMachine;
import com.lympid.core.behaviorstatemachines.impl.StateMachineMetaVisitor;
import com.lympid.core.behaviorstatemachines.validation.AllValidatorVisitor;
import com.lympid.core.common.UmlElement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Provides a builder for a behavioral {@link StateMachine}.
 *
 * @param <C> Type of the state machine context.
 *
 * @see StateMachine
 *
 * @author Fabien Renaud
 */
public class StateMachineBuilder<C> implements Visitable {

  private final String name;
  private String id;
  private final Map<String, RegionBuilder<C>> regionBuilders = new HashMap<>();
  private MutableStateMachine machine;

  /**
   * Instantiates a state machine builder.
   *
   * @param name The name of the state machine.
   */
  public StateMachineBuilder(final String name) {
    this.name = name;
  }

  /**
   * Gets the {@link UmlElement} unique identifier of the state machine.
   *
   * @return The unique identifier of the state machine.
   */
  String getId() {
    return id;
  }

  /**
   * Sets the {@link UmlElement} id of the state machine.
   *
   * @param id A unique id across the whole state machine.
   */
  void setId(final String id) {
    this.id = id;
  }

  /**
   * Gets the {@link UmlElement} name of the state machine.
   *
   * @return The name of the state machine.
   */
  String getName() {
    return name;
  }

  /**
   * Gets the unnamed region builder of the state machine.
   *
   * @return The singleton instance of the unnamed region builder for that state
   * machine.
   * @see Region
   */
  public RegionBuilder<C> region() {
    return region("__region", false);
  }

  /**
   * Gets a region builder by name.
   *
   * @param name The name of the region.
   * @return The singleton instance of the named region builder for that state
   * machine.
   *
   * @see Region
   */
  public RegionBuilder<C> region(final String name) {
    return region(name, true);
  }

  /**
   * Gets a region builder by key.
   *
   * <p>
   * When the region builder does not exist, it is created and registered in
   * order to be reused for future calls. {@code key} is used as name for the
   * region builder when {@code asName} is true.</p>
   *
   * @param key The key for the region builder. May be the name of the region.
   * @param asName Set to true to use the key as name for the region builder.
   * @return A singleton instance of a region builder.
   *
   * @see Region
   */
  private RegionBuilder<C> region(final String key, final boolean asName) {
    RegionBuilder<C> builder = regionBuilders.get(key);
    if (builder == null) {
      builder = asName ? new RegionBuilder<>(key) : new RegionBuilder<>();
      regionBuilders.put(key, builder);
    }
    return builder;
  }

  /**
   * <strong>Builds and validates</strong> the state machine
   * <strong>once</strong>.
   *
   * <p>
   * This operation transforms all the data collected via the builders into an
   * actual {@link StateMachine} that can be executed. Validation and metadata
   * are collected as part of that build.</p>
   *
   * <p>
   * The newly built machine is then cached and calling further this method will
   * always return the same previously built instance of the state machine.</p>
   *
   * @return A singleton instance of the state machine generated by this
   * builder.
   */
  public StateMachine instance() {
    if (machine == null) {
      accept(new IdMakerVisitor(new IncrementIdProvider()));
      MutableStateMachine m = build();

      m.accept(new AllValidatorVisitor());

      StateMachineMetaVisitor metaVisitor = new StateMachineMetaVisitor();
      m.accept(metaVisitor);
      m.setMetadata(metaVisitor.getMeta());
      this.machine = m;
    }
    return machine;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
    for (RegionBuilder b : regionBuilders.values()) {
      b.accept(visitor);
    }
  }

  /**
   * Turns the state machine builder into an actual state machine instance. No
   * validation of any kind is performed here.
   *
   * @return A non-validated state machine.
   */
  MutableStateMachine build() {
    VertexSet vertices = new VertexSet();
    MutableStateMachine m = new MutableStateMachine(getId());
    m.setName(name);

    /*
     * First, create all vertices.
     */
    List<Region> regions = new LinkedList<>();
    for (RegionBuilder b : regionBuilders.values()) {
      MutableRegion r = b.region(vertices);
      r.setStateMachine(m);
      regions.add(r);
    }
    m.setRegions(regions);

    buildConnectionPoints(m, vertices);

    /*
     * Then, connect them with transitions.
     */
    for (RegionBuilder b : regionBuilders.values()) {
      b.connect(vertices);
    }

    return m;
  }

  /**
   * Builds the connection points for this state machine.
   *
   * @param machine The state machine being built.
   * @param vertices Collection of all the vertices in the state machine.
   */
  protected void buildConnectionPoints(final MutableStateMachine machine, final VertexSet vertices) {
  }
}
