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

/**
 *
 * @author Fabien Renaud
 */
public abstract class UmlElementNotFoundException extends RuntimeException {

  private final String id;
  private final String name;

  public UmlElementNotFoundException(String id) {
    this(id, null);
  }

  public UmlElementNotFoundException(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public UmlElementNotFoundException(String id, String name, String message) {
    super(message);
    this.id = id;
    this.name = name;
  }

  public UmlElementNotFoundException(String id, String name, String message, Throwable cause) {
    super(message, cause);
    this.id = id;
    this.name = name;
  }

  public UmlElementNotFoundException(String id, String name, Throwable cause) {
    super(cause);
    this.id = id;
    this.name = name;
  }

  public UmlElementNotFoundException(String id, String name, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.id = id;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
