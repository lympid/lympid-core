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
package com.lympid.core.common;

/**
 * Interface of a UML element.
 *
 * @author Fabien Renaud
 */
public interface UmlElement {

  /**
   * Gets the unique identifier of the {@code UmlElement}. This identifier must
   * be distinct from any other identifier this element may interact and be in
   * relation with.
   *
   * @return The non-null unique identifier of the {@code UmlElement}.
   */
  String getId();

  /**
   * Gets the name of the {@code UmlElement}. The name is not guaranteed to be
   * unique.
   *
   * @return The name of the {@code UmlElement} or null.
   */
  String getName();
}
