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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * @author Fabien Renaud
 */
public class SimpleStateConfiguration implements MutableStateConfiguration<SimpleStateConfiguration> {

  private State state;
  
  public SimpleStateConfiguration() {
  }

  public SimpleStateConfiguration(final State state) {
    assert state != null;
    this.state = state;
  }

  @Override
  public SimpleStateConfiguration parent() {
    return null;
  }

  @Override
  public State state() {
    return state;
  }

  @Override
  public List<SimpleStateConfiguration> children() {
    return Collections.EMPTY_LIST;
  }

  @Override
  public void setState(final State state) {
    assert this.state == null;
    this.state = state;
  }

  @Override
  public SimpleStateConfiguration addChild(final State state) {
    throw new IllegalStateException();
  }

  @Override
  public void removeChild(SimpleStateConfiguration state) {
    throw new IllegalStateException();
  }

  @Override
  public void clear() {
    this.state = null;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public void forEach(final Consumer<SimpleStateConfiguration> consumer) {
  }

  @Override
  public MutableStateConfiguration copy() {
    SimpleStateConfiguration config = new SimpleStateConfiguration();
    config.state = state;
    return config;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.state);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final SimpleStateConfiguration other = (SimpleStateConfiguration) obj;
    return Objects.equals(this.state, other.state);
  }

  @Override
  public String toString() {
    return String.valueOf(state);
  }

}
