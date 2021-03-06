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

import com.lympid.core.behaviorstatemachines.PseudoState;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration.DefaultHistoryFailover;

/**
 *
 * @author Fabien Renaud
 */
public class DefaultHistoryEntryException extends RuntimeException {

  private final PseudoState historyVertex;
  private final DefaultHistoryFailover failOver;

  public DefaultHistoryEntryException(final PseudoState historyVertex, final DefaultHistoryFailover failOver, final String message) {
    super(message);
    this.historyVertex = historyVertex;
    this.failOver = failOver;
  }

  public PseudoState getHistoryVertex() {
    return historyVertex;
  }

  public DefaultHistoryFailover getFailOver() {
    return failOver;
  }

}
