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
package com.lympid.core.behaviorstatemachines;

/**
 *
 * @author Fabien Renaud 
 */
public final class TransitionTest {

  private final String transition;
  private final String source;
  private final String target;
  private final TransitionKind kind;

  public TransitionTest(final String transition, final String source, final String target, final TransitionKind kind) {
    this.transition = transition;
    this.source = source;
    this.target = target;
    this.kind = kind;
  }

  public TransitionTest(final String transition, final String source, final String target) {
    this(transition, source, target, TransitionKind.EXTERNAL);
  }

  public String getTransition() {
    return transition;
  }

  public String getSource() {
    return source;
  }

  public String getTarget() {
    return target;
  }

  public TransitionKind getKind() {
    return kind;
  }

}
