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

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Fabien Renaud
 */
public class StringTree implements Serializable {

  private final String value;
  private List<StringTree> children;

  public StringTree(final String value) {
    this.value = value;
  }

  public String state() {
    return value;
  }

  public List<StringTree> children() {
    return children;
  }

  public void setChildren(final List<StringTree> children) {
    this.children = children;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 41 * hash + Objects.hashCode(this.value);
    hash = 41 * hash + Objects.hashCode(this.children);
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
    final StringTree other = (StringTree) obj;
    if (!Objects.equals(this.value, other.value)) {
      return false;
    }
    return Objects.equals(this.children, other.children);
  }

  @Override
  public String toString() {
    if (value == null) {
      return "";
    }

    if (children == null) {
      return value;
    }

    return value + " " + children;
  }
}
