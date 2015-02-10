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

import com.lympid.core.behaviorstatemachines.BiTransitionBehavior;
import com.lympid.core.behaviorstatemachines.BiTransitionConstraint;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.TransitionKind;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexUtils;
import com.lympid.core.behaviorstatemachines.Visitor;
import com.lympid.core.common.Trigger;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author Fabien Renaud
 */
public final class MutableTransition implements Transition {

  private final String id;
  private String name;
  private final TransitionKind kind;
  private final Set<Trigger> triggers = new HashSet<>();
  private final BiTransitionConstraint guard;
  private final BiTransitionBehavior effect;
  private final Vertex source;
  private final Vertex target;
  private Region container;

  public MutableTransition(Region container, Vertex source, Vertex target, BiTransitionConstraint guard, BiTransitionBehavior effect, TransitionKind kind, final String id) {
    this.id = id;
    this.container = container;
    this.source = source;
    this.target = target;
    this.guard = guard;
    this.effect = effect;
    this.kind = kind;
  }

  public MutableTransition(Region container, Vertex source, Vertex target, BiTransitionConstraint guard, BiTransitionBehavior effect, TransitionKind kind) {
    this(container, source, target, guard, effect, kind, UUID.randomUUID().toString());
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public TransitionKind kind() {
    return kind;
  }

  @Override
  public Collection<Trigger> triggers() {
    return triggers;
  }

  @Override
  public BiTransitionConstraint guard() {
    return guard;
  }

  @Override
  public BiTransitionBehavior effect() {
    return effect;
  }

  @Override
  public Vertex source() {
    return source;
  }

  @Override
  public Vertex target() {
    return target;
  }

  @Override
  public Region container() {
    return container;
  }

  public void setContainer(final Region container) {
    this.container = container;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visitOnEntry(this);
    visitor.visitOnExit(this);
  }

  @Override
  public String toString() {
    return VertexUtils.nameOrId(this);
  }
}
