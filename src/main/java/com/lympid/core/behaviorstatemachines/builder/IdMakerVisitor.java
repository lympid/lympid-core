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
package com.lympid.core.behaviorstatemachines.builder;

/**
 * Visitor to set unique identifiers for the various builders.
 *
 * @author Fabien Renaud
 */
final class IdMakerVisitor implements Visitor {

  private final IdProvider provider;

  public IdMakerVisitor(final IdProvider provider) {
    this.provider = provider;
  }

  @Override
  public void visit(final StateMachineBuilder visitable) {
    visitable.setId(provider.nextId());
  }

  @Override
  public void visit(final RegionBuilder visitable) {
    visitable.setId(provider.nextId());
  }

  @Override
  public void visit(final VertexBuilder visitable) {
    visitable.setId(provider.nextId());
  }

  @Override
  public void visit(final TransitionBuilder visitable) {
    visitable.setId(provider.nextId());
  }

  @Override
  public void visit(final ConnectionPointReferenceBuilder visitable) {
    visitable.setId(provider.nextId());
  }

}
