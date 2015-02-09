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
package com.lympid.core.behaviorstatemachines.validation;

import com.lympid.core.behaviorstatemachines.ConnectionPointReference;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;

/**
 *
 * @author Fabien Renaud
 */
public final class ConnectionPointReferenceConstraintException extends ConstraintException {

  private final ConnectionPointReference connectionPointReference;
  private final PseudoStateKind faultyPseudoStateKind;

  public ConnectionPointReferenceConstraintException(String s, ConnectionPointReference connectionPointReference, PseudoStateKind faultyPseudoStateKind) {
    super(s);
    this.connectionPointReference = connectionPointReference;
    this.faultyPseudoStateKind = faultyPseudoStateKind;
  }

  public ConnectionPointReference getConnectionPointReference() {
    return connectionPointReference;
  }

  public PseudoStateKind getFaultyPseudoStateKind() {
    return faultyPseudoStateKind;
  }

}
