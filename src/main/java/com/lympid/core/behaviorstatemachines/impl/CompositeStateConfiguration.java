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
package com.lympid.core.behaviorstatemachines.impl;

import com.lympid.core.behaviorstatemachines.State;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Fabien Renaud
 */
public class CompositeStateConfiguration implements MutableStateConfiguration<CompositeStateConfiguration> {

  private State state;
  private CompositeStateConfiguration parent;
  private CompositeStateConfiguration child;

  public CompositeStateConfiguration() {
  }

  private CompositeStateConfiguration(final CompositeStateConfiguration config) {
    this.parent = config.parent;
    this.state = config.state;
    if (config.child != null) {
      this.child = new CompositeStateConfiguration(config.child);
    }
  }

  @Override
  public CompositeStateConfiguration parent() {
    return parent;
  }

  @Override
  public void setState(final State state) {
    assert this.state == null;
    assert child == null;
    this.state = state;
  }

  @Override
  public CompositeStateConfiguration addChild(final State state) {
    assert child == null;
    CompositeStateConfiguration config = new CompositeStateConfiguration();
    config.parent = this;
    config.state = state;
    this.child = config;
    return config;
  }

  @Override
  public void removeChild(final CompositeStateConfiguration state) {
    assert child == state;
    child = null;
    state.parent = null;
  }

  @Override
  public void clear() {
    assert child == null;
    this.state = null;
  }

  @Override
  public State state() {
    return state;
  }

  @Override
  public List<CompositeStateConfiguration> children() {
    if (child == null) {
      return Collections.EMPTY_LIST;
    }

    List<CompositeStateConfiguration> list = new ArrayList<>(1);
    list.add(child);
    return list;
  }

  @Override
  public int size() {
    return child == null ? 0 : 1;
  }

  @Override
  public boolean isEmpty() {
    return child == null;
  }

  @Override
  public void forEach(Consumer<CompositeStateConfiguration> consumer) {
    if (child != null) {
      consumer.accept(child);
    }
  }

  @Override
  public StateConfiguration copy() {
    return new CompositeStateConfiguration(this);
  }

}
