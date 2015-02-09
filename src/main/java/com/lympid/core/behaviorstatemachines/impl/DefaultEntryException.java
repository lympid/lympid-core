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

import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration.DefaultEntryRule;

/**
 *
 * @author Fabien Renaud
 */
public class DefaultEntryException extends RuntimeException {

  private final Region region;
  private final DefaultEntryRule rule;

  DefaultEntryException(final Region region, final DefaultEntryRule rule, final String message) {
    super(message);
    this.region = region;
    this.rule = rule;
  }

  public Region getRegion() {
    return region;
  }

  public DefaultEntryRule getRule() {
    return rule;
  }

}
