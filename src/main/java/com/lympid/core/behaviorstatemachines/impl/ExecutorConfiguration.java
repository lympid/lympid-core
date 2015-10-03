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

import java.util.concurrent.ScheduledExecutorService;

/**
 * State machine processor configuration.
 *
 * @author Fabien Renaud
 */
public class ExecutorConfiguration {

  public static final ExecutorConfiguration DEFAULT = new ExecutorConfiguration();

  private boolean autoStart = true;
  private DefaultEntryRule defaultEntryRule = DefaultEntryRule.INITIAL;
  private DefaultHistoryFailover defaultHistoryFailover = DefaultHistoryFailover.EXCEPTION;
  private ScheduledExecutorService executor;

  ExecutorConfiguration() {
  }

  /**
   * When the state machine processor is instantiated, it always prepares the
   * initial pseudo state of the top level state machine. That initial pseudo
   * state never has behaviors attached to it and this operation has thus no
   * effect on external systems.
   *
   * However, that initial pseudo state may have outgoing transitions without
   * triggers. This configuration determines whether those should automatically
   * be fired when found. Once fired, such a transition will change the state of
   * the state machine which will then have one or more active states or might
   * even run till its final state depending on the definition of the state
   * machine.
   *
   * This parameter is mutually exclusive with autoCompletion.
   *
   * Default is true.
   *
   * @param autoStart Set to false to NOT auto fire outgoing transitions which
   * have no trigger of the initial pseudo state of the top level state machine.
   * @return Returns the current configuration instance.
   */
  public ExecutorConfiguration autoStart(final boolean autoStart) {
    this.autoStart = autoStart;
    return this;
  }

  /**
   * Returns whether or not to automatically start the state machine.
   *
   * Default is true.
   *
   * @return true to auto fire outgoing transitions of the initial pseudo state
   * of the top level state machine which have no triggers; false otherwise.
   */
  boolean autoStart() {
    return autoStart;
  }

  /**
   * Sets the default entry rule.
   *
   * Default is INITIAL.
   *
   * @param defaultEntryRule A default entry rule.
   * @return Returns the current configuration instance.
   */
  public ExecutorConfiguration defaultEntryRule(DefaultEntryRule defaultEntryRule) {
    this.defaultEntryRule = defaultEntryRule;
    return this;
  }

  /**
   * Gets the default entry rule to use when a transition terminates on an
   * enclosing state and the enclosed regions do not have an initial
   * pseudostate.
   *
   * Default is INITIAL.
   *
   * @return The default entry rule.
   */
  DefaultEntryRule defaultEntryRule() {
    return defaultEntryRule;
  }

  /**
   * Sets the default history failover rule.
   *
   * Default is EXCEPTION.
   *
   * @param defaultHistoryFailover The default history failover rule.
   * @return Returns the current configuration instance.
   */
  public ExecutorConfiguration defaultHistoryFailover(DefaultHistoryFailover defaultHistoryFailover) {
    this.defaultHistoryFailover = defaultHistoryFailover;
    return this;
  }

  /**
   * Gets the default history failover rule to use when a history pseudostate is
   * reached without any history to restore and no outgoing transition to fire.
   *
   * Default is EXCEPTION.
   *
   * @return The default history failover rule.
   */
  DefaultHistoryFailover defaultHistoryFailover() {
    return defaultHistoryFailover;
  }

  /**
   * Sets the scheduled executor service for the state machine to use.
   *
   * If set, this will be used for: - activities: activities will always be run
   * in background - kick off timers for transactions which accept time events.
   * - orthogonal states: orthogonal states will be executed concurrently with
   * the given scheduled executor service under the condition that the
   * 'concurrentOrthogonal' property has been enabled.
   *
   * @param executor The scheduled executor service for the state machine.
   * @return Returns the current configuration instance.
   */
  public ExecutorConfiguration executor(final ScheduledExecutorService executor) {
    this.executor = executor;
    return this;
  }

  /**
   * Gets the scheduled executor service to run tasks in background/parallel.
   *
   * @return A scheduled executor service or null.
   */
  ScheduledExecutorService executor() {
    return executor;
  }

  /**
   * Rules for when a transition terminates on an enclosing state and the
   * enclosed regions do not have an initial pseudostate.
   */
  public enum DefaultEntryRule {

    /**
     * If a transition terminates on an enclosing state and the enclosed regions
     * do not have an initial pseudostate, this is considered an ill-formed
     * model. That is, in those cases the initial pseudostate is mandatory.
     *
     * Using this rule means a composite state must have an initial pseudo state
     * when it has an incoming transition targeting its edge.
     */
    INITIAL,
    /**
     * If a transition terminates on an enclosing state and the enclosed regions
     * do not have an initial pseudostate, the state machine stays in the
     * composite state, without entering any of the regions or their substates.
     *
     * Using this rule means a composite state is not required to have an
     * initial pseudo state when it has an incoming transition targeting its
     * edge. However, its region nor any of its substates will be entered and
     * additional transitions might be required to leave the state.
     */
    NONE
  }

  /**
   * Rules for handling improper shallow or deep history vertices about to be
   * entered.
   *
   * From UML superstructure 2.4.1, chapter 15.3.11 State
   *
   * [When] the transtion terminates on a shallow [or deep] history pseudostate,
   * [if] the most rencently active substate [prior to this entry] is the final
   * state or if this is the first entry into this state, [...] the default
   * entry state is entered. This is the substate that is target of the
   * transition originating from the history pseudostate. (If no such transition
   * is specified, the situation is ill-defined and its handling is not
   * defined.)
   *
   * This enumeration provides various ways of handling this ill-defined
   * situation.
   */
  public enum DefaultHistoryFailover {

    /**
     * Throws a custom runtime exception.
     */
    EXCEPTION,
    /**
     * Prevents the transition targeting the history pseudostate to be enabled.
     * That is, the transition will never be fired until a valid history for the
     * state becomes available again and an attempt to fire the transition is
     * made again.
     */
    DISABLE_TRANSITION
    // more?
  }
}
