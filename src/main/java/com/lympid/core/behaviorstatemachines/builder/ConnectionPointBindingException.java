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
package com.lympid.core.behaviorstatemachines.builder;

import java.util.Collection;

/**
 *
 * @author Fabien Renaud
 */
public class ConnectionPointBindingException extends RuntimeException {

  private final String name;
  private final Collection<String> candidates;

  public ConnectionPointBindingException(String name, Collection<String> candidates, String message) {
    super(message);
    this.name = name;
    this.candidates = candidates;
  }

  public String getName() {
    return name;
  }

  public Collection<String> getCandidates() {
    return candidates;
  }

}
