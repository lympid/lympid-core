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

import com.lympid.core.basicbehaviors.CompletionEvent;
import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.basicbehaviors.TimeEvent;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.Transition;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexUtils;
import com.lympid.core.common.Trigger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author Fabien Renaud
 */
public abstract class MutableVertex implements Vertex {

  private final String id;
  private List<Transition> outgoing = Collections.EMPTY_LIST;
  private Map<Event, List<Transition>> outgoingByEvent = Collections.EMPTY_MAP;
  private List<TimeEvent> outgoingTimeEvents = Collections.EMPTY_LIST;
  private final Set<Transition> incoming = new HashSet<>();
  private String name;
  private Region container;

  protected MutableVertex(final String id) {
    this.id = id;
  }

  protected MutableVertex() {
    this(UUID.randomUUID().toString());
  }

  @Override
  public final String getId() {
    return id;
  }

  @Override
  public final String getName() {
    return name;
  }

  public final void setName(String name) {
    this.name = name;
  }

  @Override
  public Collection<Transition> outgoing() {
    return outgoing;
  }

  @Override
  public Collection<Transition> outgoing(final Event event) {
    Collection<Transition> transitions = outgoingByEvent.get(event);
    return transitions == null ? Collections.emptyList() : transitions;
  }

  @Override
  public Collection<? extends TimeEvent> outgoingTimeEvents() {
    return outgoingTimeEvents;
  }

  @Override
  public Collection<Transition> incoming() {
    return incoming;
  }

  @Override
  public Region container() {
    return container;
  }

  public void setContainer(Region container) {
    this.container = container;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final MutableVertex other = (MutableVertex) obj;
    return (this.id == null ? other.id == null : this.id.equals(other.id));
  }

  @Override
  public String toString() {
    return VertexUtils.nameOrId(this);
  }

  public void setOutgoing(final Collection<Transition> outgoing) {
    if (outgoing.isEmpty()) {
      this.outgoing = Collections.EMPTY_LIST;
      this.outgoingByEvent = Collections.EMPTY_MAP;
      this.outgoingTimeEvents = Collections.EMPTY_LIST;
    } else {
      this.outgoing = new ArrayList<>(outgoing.size());
      this.outgoingByEvent = new HashMap<>();
      this.outgoingTimeEvents = new LinkedList<>();

      for (Transition t : outgoing) {
        addOutgoingTransition(t);
      }

      /*
       * Early optimization
       */
      this.outgoingByEvent = optimize(outgoingByEvent);
      this.outgoingTimeEvents = outgoingTimeEvents.isEmpty()
              ? Collections.EMPTY_LIST
              : new ArrayList<>(outgoingTimeEvents);
    }
  }

  private void addOutgoingTransition(final Transition transition) {
    outgoing.add(transition);
    if (transition.triggers().isEmpty()) {
      addOutgoingTransition(transition, CompletionEvent.INSTANCE);
    } else {
      for (Trigger tr : transition.triggers()) {
        addOutgoingTransition(transition, tr.event());
      }
    }
  }

  private void addOutgoingTransition(final Transition transition, final Event event) {
    if (event instanceof TimeEvent) {
      TimeEvent timeEvent = (TimeEvent) event;
      if (!outgoingTimeEvents.contains(timeEvent)) {
        outgoingTimeEvents.add(timeEvent);
      }
    }
    List<Transition> list = outgoingByEvent.get(event);
    if (list == null) {
      list = new LinkedList<>();
      outgoingByEvent.put(event, list);
    }
    list.add(transition);
  }

  private Map<Event, List<Transition>> optimize(Map<Event, List<Transition>> map) {
    final Map<Event, List<Transition>> newMap = new HashMap<>(map.size());
    for (Entry<Event, List<Transition>> e : map.entrySet()) {
      newMap.put(e.getKey(), new ArrayList<>(e.getValue()));
    }
    return newMap;
  }
}
