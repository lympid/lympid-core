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

import com.lympid.core.behaviorstatemachines.State;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Fabien Renaud
 */
public class OrthogonalStateConfiguration implements MutableStateConfiguration<OrthogonalStateConfiguration> {

  private OrthogonalStateConfiguration parent;
  private State state;
  private final List<OrthogonalStateConfiguration> children;
  private Iterator<OrthogonalStateConfiguration> iterator;

  public OrthogonalStateConfiguration() {
    this.children = new LinkedList<>();
  }

  private OrthogonalStateConfiguration(final OrthogonalStateConfiguration config) {
    this.parent = config.parent;
    this.state = config.state;
    this.children = new ArrayList<>(config.children.size());
    for (OrthogonalStateConfiguration stateConfig : config.children) {
      this.children.add(new OrthogonalStateConfiguration(stateConfig));
    }
  }

  @Override
  public OrthogonalStateConfiguration parent() {
    return parent;
  }

  @Override
  public State state() {
    return state;
  }

  @Override
  public List<OrthogonalStateConfiguration> children() {
    return children;
  }

  @Override
  public void setState(final State state) {
    assert this.state == null;
    assert children.isEmpty();
    this.state = state;
  }

  @Override
  public OrthogonalStateConfiguration addChild(final State state) {
    OrthogonalStateConfiguration config = new OrthogonalStateConfiguration();
    config.parent = this;
    config.state = state;
    children.add(config);
    return config;
  }

  @Override
  public void removeChild(final OrthogonalStateConfiguration state) {
    if (iterator == null) {
      children.remove(state);
    } else {
      iterator.remove();
    }
    state.parent = null;
  }

  @Override
  public void clear() {
    assert children.isEmpty();
    this.state = null;
  }

  @Override
  public int size() {
    return children.size();
  }

  @Override
  public boolean isEmpty() {
    return children.isEmpty();
  }

  @Override
  public void forEach(final Consumer<OrthogonalStateConfiguration> consumer) {
    if (!children.isEmpty()) {
      iterator = children.iterator();
      while (iterator.hasNext()) {
        consumer.accept(iterator.next());
      }
      iterator = null;
    }
  }

  @Override
  public MutableStateConfiguration copy() {
    return new OrthogonalStateConfiguration(this);
  }
}
