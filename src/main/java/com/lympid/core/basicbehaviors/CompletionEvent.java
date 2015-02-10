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
package com.lympid.core.basicbehaviors;

/**
 * Provides a unique class and instance of an event labeled as the completion
 * event as defined in the UML superstructure version 2.4.1.
 *
 * @author Fabien Renaud
 */
public final class CompletionEvent implements Event {

  /**
   * The unique instance of the {@code CompletionEvent} class.
   */
  public static final Event INSTANCE = new CompletionEvent();

  /**
   * The private constructor for the singleton pattern.
   */
  private CompletionEvent() {
  }

  /**
   * Gets a printable representation of the event.
   *
   * @return {@code "CompletionEvent"}
   */
  @Override
  public String toString() {
    return "CompletionEvent";
  }
}
