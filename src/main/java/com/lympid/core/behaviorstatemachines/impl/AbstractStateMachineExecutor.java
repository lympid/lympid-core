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
import com.lympid.core.basicbehaviors.RelativeTimeEvent;
import com.lympid.core.basicbehaviors.TimeEvent;
import com.lympid.core.behaviorstatemachines.FinalState;
import com.lympid.core.behaviorstatemachines.PseudoState;
import static com.lympid.core.behaviorstatemachines.PseudoStateKind.CHOICE;
import static com.lympid.core.behaviorstatemachines.PseudoStateKind.DEEP_HISTORY;
import static com.lympid.core.behaviorstatemachines.PseudoStateKind.ENTRY_POINT;
import static com.lympid.core.behaviorstatemachines.PseudoStateKind.EXIT_POINT;
import static com.lympid.core.behaviorstatemachines.PseudoStateKind.FORK;
import static com.lympid.core.behaviorstatemachines.PseudoStateKind.INITIAL;
import static com.lympid.core.behaviorstatemachines.PseudoStateKind.JOIN;
import static com.lympid.core.behaviorstatemachines.PseudoStateKind.JUNCTION;
import static com.lympid.core.behaviorstatemachines.PseudoStateKind.SHALLOW_HISTORY;
import static com.lympid.core.behaviorstatemachines.PseudoStateKind.TERMINATE;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.StateMachine;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.Transition;
import static com.lympid.core.behaviorstatemachines.TransitionKind.EXTERNAL;
import static com.lympid.core.behaviorstatemachines.TransitionKind.INTERNAL;
import static com.lympid.core.behaviorstatemachines.TransitionKind.LOCAL;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexUtils;
import com.lympid.core.common.TreeNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Fabien Renaud
 */
public abstract class AbstractStateMachineExecutor implements StateMachineExecutor {

  private static final AtomicInteger ID_GENERATOR = new AtomicInteger();
  private final int id;
  private StateMachine machine;
  private StateMachineState machineState;
  private Object context;
  private ExecutorConfiguration configuration = ExecutorConfiguration.DEFAULT;
  private ExecutorListener listeners = ExecutorListener.DEFAULT;

  public AbstractStateMachineExecutor(final int id) {
    this.id = id;
  }

  public AbstractStateMachineExecutor() {
    this(ID_GENERATOR.incrementAndGet());
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setStateMachine(final StateMachine machine) {
    this.machine = machine;
    this.machineState = createMachineState(machine);
  }
  
  @Override
  public StateMachine stateMachine() {
    return machine;
  }

  protected StateMachineState createMachineState(final StateMachine machine) {
    return StateMachineStateFactory.get(machine.metadata());
  }

  @Override
  public ExecutorConfiguration configuration() {
    if (configuration == ExecutorConfiguration.DEFAULT) {
      configuration = new ExecutorConfiguration();
    }
    return configuration;
  }

  @Override
  public void setListeners(final ExecutorListener listeners) {
    this.listeners = listeners;
  }

  @Override
  public ExecutorListener listeners() {
    if (listeners == ExecutorListener.DEFAULT) {
      listeners = new ExecutorListener();
    }
    return listeners;
  }

  @Override
  public void setContext(final Object context) {
    this.context = context;
  }

  @Override
  public void go() {
    if ((machine.metadata().hasActivities() || machine.metadata().hasTimeEvents()) && configuration.executor() == null) {
      throw new RuntimeException(); // TODO: custom exception
    }

    if (configuration.autoStart()) {
      start();
    }
  }

  @Override
  public StateMachineSnapshot snapshot() {
    return new StateMachineSnapshot(machine, machineState, context);
  }

  @Override
  public void take(final Event event) {
    if (!machineState.hasStarted()) {
      start();
    }
    if (machineState.isTerminated()) {
      onEventDenied(event);
      return;
    }

    /*
     * Fire all the transition paths found for the given event applied to the
     * active state configuration.
     */
    fireManyAndBeyond(event, transitionPaths(event, machineState.activeStates()));
  }

  protected void take(final Event event, final State state) {
    if (machineState.isTerminated() || !machineState.isActive(state)) {
      onEventDenied(event);
      return;
    }

    fireOneAndBeyond(event, transitionPath(event, state));
  }

  protected void takeCompletionEvent() {
    if (machineState.isTerminated()) {
      return;
    }

    postFire();
  }

  protected Future scheduleEvent(final TimeEvent event, final State state, final long delay) {
    return configuration.executor().schedule(new RunnableEvent(event, state), delay, TimeUnit.MILLISECONDS);
  }

  protected void start() {
    PseudoState initial = machine.region().get(0).initial();
    TreeNode<Transition> path = transitionPath(CompletionEvent.INSTANCE, initial);
    if (path.children().isEmpty()) {
      throw new RuntimeException(); // TODO: custom exception
    }

    machineState.start();

    if (listeners.hasMachineStartedListener()) {
      listeners.onMachineStarted(this, machine, context);
    }

    fireOneAndBeyond(CompletionEvent.INSTANCE, path);
  }

  private void postFire() {
    /*
     * Completion events have priority over any over events that might be in the
     * queue.
     */
    if (!machineState.isTerminated() && machine.metadata().hasCompletionEvents()) {
      internalTakeCompletionEvents();
    }

    /*
     * Schedules all time events at once
     */
    if (!machineState.isTerminated() && machine.metadata().hasTimeEvents()) {
      scheduleTimeEvents(machineState.activeStates());
    }
  }

  private void internalTakeCompletionEvents() {
    while (machineState.hasCompletedStates()) {
      int stateHashBefore = machineState.completedStates().hashCode();
      int contextHashBefore = Objects.hashCode(context);

      fireMany(CompletionEvent.INSTANCE, transitionPaths(CompletionEvent.INSTANCE, machineState.completedStates()));

      int stateHashAfter = machineState.completedStates().hashCode();
      int contextHashAfter = Objects.hashCode(context);

      /*
       * Infinite loop detection.
       *
       * Transitions with no triggers can still have guards. If the condition
       * satisfying the guard is never met, this loop will keep trying to fire
       * the transition unaware of what and when the guard may be satisfied.
       * Instead of letting this loop run infinitely, let it process as many
       * transitions as possible until an iteration shows no changes in the set
       * of completed states. When such 'event' happens, it is assumed the set
       * hasn't changed and that firing transitions for that set of states will
       * not result in any changes in the next few iterations.
       */
      if ((stateHashBefore == stateHashAfter && contextHashBefore == contextHashAfter) || machineState.isTerminated()) { // possible infinite loop detected.
        break;
      }
    }
  }

  private void scheduleTimeEvents(final StateConfiguration<?> stateConfig) {
    assert stateConfig.state() != null;
    stateConfig.forEach(this::scheduleTimeEvents);
    scheduleTimeEvents(stateConfig.state());
  }

  private void scheduleTimeEvents(final State state) {
    StateStatus status = machineState.status(state);
    if (status.hasEventTimers()) {
      return;
    }

    final Collection<? extends TimeEvent> timeEvents = state.outgoingTimeEvents();
    if (timeEvents.isEmpty()) {
      status.setEventTimers(Collections.EMPTY_LIST);
      return;
    }

    final List<Future> futures = new ArrayList<>(timeEvents.size());
    final long past = status.getActivationTime() - System.currentTimeMillis();

    for (TimeEvent timeEvent : timeEvents) {
      if (timeEvent instanceof RelativeTimeEvent) {
        long actualDelay = past + timeEvent.time();
        if (actualDelay <= 0) {
          System.err.println("The actual delay is <= 0: " + actualDelay + " State: " + state); // TODO: no stderr
        }
        futures.add(scheduleEvent(timeEvent, state, actualDelay));
      } else {
        throw new UnsupportedOperationException("Unsupported time event: " + timeEvent.getClass().getCanonicalName());
      }
    }
    status.setEventTimers(futures);
  }

  private void fireManyAndBeyond(final Event event, final Collection<TreeNode<Transition>> paths) {
    if (fireMany(event, paths)) {
      postFire();
    } else {
      onEventDenied(event);
      // TODO: deferred triggers
    }
  }

  private void fireOneAndBeyond(final Event event, final TreeNode<Transition> path) {
    if (fireOne(event, path)) {
      postFire();
    } else {
      onEventDenied(event);
      // TODO: deferred triggers
    }
  }

  /**
   * Fire all transition trees.
   *
   * Each transition in the given collection must belong to distinct orthogonal
   * regions.
   *
   * @param event The event that triggered those transitions.
   * @param paths A collection of transitions that can be fired in parallel.
   * @return true when the event actually resulted in firing a transition.
   */
  private boolean fireMany(final Event event, final Collection<TreeNode<Transition>> paths) {
    if (paths.isEmpty()) {
      return false;
    }

    boolean accepted = false;
    for (TreeNode<Transition> path : paths) {
      if (path.hasChildren()) {

        if (!accepted) {
          onEventAccepted(event);
          accepted = true;
        }

        fire(event, path.children().get(0));

        if (machineState.isTerminated()) {
          if (listeners.hasMachineTerminatedListener()) {
            listeners.onMachineTerminated(this, machine, context);
          }
          break;
        }
      }
    }
    return accepted;
  }

  private boolean fireOne(final Event event, final TreeNode<Transition> path) {
    if (path.isLeaf()) {
      return false;
    }

    onEventAccepted(event);
    fire(event, path.children().get(0));

    if (machineState.isTerminated()) {
      if (listeners.hasMachineTerminatedListener()) {
        listeners.onMachineTerminated(this, machine, context);
      }
    }

    return true;
  }

  private void fire(final Event event, final TreeNode<Transition> transitionNode) {
    Transition transition = transitionNode.content();

    if (listeners.hasTransitionStartedListener()) {
      listeners.onTransitionStarted(this, machine, context, event, transition);
    }

    switch (transition.kind()) {
      case EXTERNAL:
      case LOCAL:
        leave(transition);
        transitionEffect(event, transition);
        enter(transition, transition.target(), transitionNode.children());
        break;
      case INTERNAL:
        transitionEffect(event, transition);
        break;
      default:
        throw new UnsupportedOperationException("Unknown transition kind: " + transition.kind());
    }

    if (listeners.hasTransitionEndedListener()) {
      listeners.onTransitionEnded(this, machine, context, event, transition);
    }
  }

  /*
   *
   * Leave methods
   *
   */
  private void leave(final Transition transition) {
    if (transition.source() instanceof State) {
      machineState.removeCompletedState((State) transition.source());
    }
    leave(transition.container());
  }

  private void leave(final Region region) {
    final StateConfiguration stateConfig = machineState.activeStates(region);
    if (stateConfig != null) {
      leaveNode(stateConfig);
    }
  }

  private void leaveNode(final StateConfiguration<?> stateConfig) {
    final Region region = stateConfig.state().container();
    if (region.deepHistory() != null) {
      machineState.saveDeepHistory(region);
    } else if (region.shallowHistory() != null) {
      machineState.saveShallowHistory(region);
    }

    stateConfig.forEach(this::leaveNode);
    leaveState(stateConfig.state());
  }

  private void leaveState(final State state) {
    machineState.deactivate(state);

    if (!state.exit().isEmpty()) {
      try {

        if (listeners.hasStateExitBeforeExecution()) {
          listeners.onStateExitBeforeExecution(this, machine, context, state);
        }

        for (StateBehavior b : state.exit()) {
          b.accept(context);
        }

        if (listeners.hasStateExitAfterExecution()) {
          listeners.onStateExitAfterExecution(this, machine, context, state);
        }

      } catch (Exception ex) {
        if (listeners.hasStateExitException()) {
          listeners.onStateExitException(this, machine, context, state, ex);
        }
      }
    }
  }

  /*
   *
   * Enter methods
   *
   */
  private void enter(final Transition incomingTransition, final Vertex v, final List<TreeNode<Transition>> path) {
    entry(activationPath(v));

    if (v instanceof State) {
      enterState((State) v);
    } else {
      enterPseudoState(incomingTransition, (PseudoState) v, path);
    }
  }

  private void enterHistory(final StateConfiguration<?> stateConfig) {
    assert stateConfig.state() != null;
    if (stateConfig.state() instanceof FinalState) {
      throw new RuntimeException(); // TODO: custom exception
    }
    
    entry(stateConfig.state());
    if (stateConfig.isEmpty()) {
      enterState(stateConfig.state());
    } else {
      stateConfig.forEach(this::enterHistory);
    }
  }

  private void entry(final List<State> path) {
    if (!path.isEmpty()) {
      for (State s : path) {
        entry(s);
      }
    }
  }

  private void entry(final State state) {
    machineState.activate(state);

    if (!state.entry().isEmpty()) {
      try {

        if (listeners.hasStateEnterBeforeExecution()) {
          listeners.onStateEnterBeforeExecution(this, machine, context, state);
        }

        for (StateBehavior b : state.entry()) {
          b.accept(context);
        }

        if (listeners.hasStateEnterAfterExecution()) {
          listeners.onStateEnterAfterExecution(this, machine, context, state);
        }

      } catch (Exception ex) {
        if (listeners.hasStateEnterException()) {
          listeners.onStateEnterException(this, machine, context, state, ex);
        }
      }
    }

    if (state.doActivity() != null) {
      doActivity(state);
    }
  }

  private void enterState(final State state) {
    if (state.isSimple()) {
      if (state instanceof FinalState) {
        enterFinalState((FinalState) state);
      }
    } else {
      assert state.isComposite() || state.isSubMachineState();
      for (Region r : state.region()) {
        enter(r);
      }
    }
  }

  private void doActivity(final State state) {
    Future<?> f = configuration.executor().submit(new RunnableActivity(this, state));
    machineState.setActivity(state, f);
  }

  private void enterFinalState(final FinalState state) {
    State parent = parentState(state);
    if (parent == null) { // top level state machine
      terminate();
    } else {
      machineState.completedOne(parent);
    }
  }

  private void enter(final Region region) {
    /*
     * Semantic variation point (default entry rule)
     *
     * If a transition terminates on an enclosing state and the enclosed regions
     * do not have an initial pseudostate, the interpretation of this situation
     * is a semantic variation point. In some interpretations, this is
     * considered an ill-formed model. That is, in those cases the initial
     * pseudostate is mandatory.
     *
     * An alternative interpretation allows this situation and it means that,
     * when such a transition is taken, the state machine stays in the composite
     * state, without entering any of the regions or their substates.
     */
    if (region.initial() == null) {
      if (configuration.defaultEntryRule() == ExecutorConfiguration.DefaultEntryRule.INITIAL) {
        throw new DefaultEntryException(region, configuration.defaultEntryRule(), "Can not enter a region which has no initial vertex.");
      }
      /*
       * The region isn't entered, so it is transiently completed.
       */
      assert configuration.defaultEntryRule() == ExecutorConfiguration.DefaultEntryRule.NONE;
      machineState.completedOne(region.state());
    } else {
      PseudoState initial = region.initial();
      TreeNode<Transition> path = transitionPath(CompletionEvent.INSTANCE, initial);
      enterPseudoState(initial, path.children());
    }
  }

  private void enterPseudoState(final PseudoState pseudoState, final List<TreeNode<Transition>> paths) {
    enterPseudoState(null, pseudoState, paths);
  }

  private void enterPseudoState(final Transition incomingTransition, final PseudoState pseudoState, final List<TreeNode<Transition>> paths) {
    /*
     * Section 15.3.14 Transition: - [5] Transitions outgoing pseudostates may
     * not have a trigger (except for those coming out of the initial
     * pseudostate). Therefore, there is always at least one transition to fire.
     * None results in an exception.
     */
    switch (pseudoState.kind()) {
      case CHOICE:
        /*
         * Section 15.3.8 Pseudostate: - [8] In a complete statemachine, a
         * choice vertex must have at least one incoming and one outgoing
         * transition. # Semantics: If more than one of the guards evaluates to
         * true, an arbitrary one is selected. If none of the guards evaluates
         * to true, then the model is considered ill-formed.
         */
        TreeNode<Transition> newPath = transitionPath(CompletionEvent.INSTANCE, pseudoState);
        if (newPath.isLeaf()) {
          throw new RuntimeException(); // TODO: custom exception
        }
        fire(CompletionEvent.INSTANCE, newPath.children().get(0));
        break;
      case FORK:
        /*
         * Section 15.3.8 Pseudostate: - [5] In a complete statemachine, a fork
         * vertex must have at least two outgoing transitions and exactly one
         * incoming transition.
         *
         * Section 15.3.14 Transition: - [1] A fork segment must not have guards
         * or triggers.
         *
         * In addition, outgoing transitions of a fork can only be external.
         */
        /*
         * There are one pseudo state to leave (leave once), many transitions to
         * fire and many states to enter.
         */
        leave(pseudoState.container());
        for (TreeNode<Transition> node : paths) {
          Transition t = node.content();
          assert t.source() == pseudoState : "Source of the fork transition and current pseudo state do not match!";

          transitionEffect(CompletionEvent.INSTANCE, t);
          enter(t, t.target(), node.children());
        }
        break;
      case JOIN:
        for (Transition t : pseudoState.incoming()) {
          if (t != incomingTransition) {
            transitionEffect(CompletionEvent.INSTANCE, t);
          }
        }
        machineState.clearJoin(pseudoState);
      case INITIAL:
      case JUNCTION:
      case ENTRY_POINT:
      case EXIT_POINT:
        fire(CompletionEvent.INSTANCE, paths.get(0));
        break;
      case SHALLOW_HISTORY:
      case DEEP_HISTORY:
        StateConfiguration stateConfig = machineState.restore(pseudoState.container());
        if (stateConfig == null) {
          fire(CompletionEvent.INSTANCE, paths.get(0));
        } else {
          enterHistory(stateConfig);
        }
        break;
      case TERMINATE:
        terminate();
        break;
      default:
        throw new UnsupportedOperationException("Unknown pseudo state kind: " + pseudoState.kind());
    }
  }

  private List<State> activationPath(final Vertex vertex) {
    final LinkedList<State> path = new LinkedList<>();
    if (vertex instanceof State && !machineState.isActive((State) vertex)) {
      path.add((State) vertex);
    }

    State parent = parentState(vertex);
    while (parent != null && !machineState.isActive(parent)) {
      path.addFirst(parent);
      parent = parentState(parent);
    }
    return path;
  }

  private Collection<TreeNode<Transition>> transitionPaths(final Event event, final StateConfiguration stateConfig) {
    final Collection<TreeNode<Transition>> allPaths = new LinkedList<>();
    transitionPaths(event, stateConfig, allPaths);
    return allPaths;
  }

  private void transitionPaths(final Event event, final StateConfiguration<?> stateConfig, final Collection<TreeNode<Transition>> allPaths) {
    if (stateConfig.isEmpty()) {
       transitionPath(event, stateConfig.state(), allPaths);
       return;
    }
    
    int sizeBefore = allPaths.size();
    stateConfig.forEach((s) -> transitionPaths(event, s, allPaths));
    int sizeAfter = allPaths.size();
    
    if (sizeAfter == sizeBefore) {
       transitionPath(event, stateConfig.state(), allPaths);
    }
  }

  private Collection<TreeNode<Transition>> transitionPaths(final Event event, final Collection<State> states) {
    final Collection<TreeNode<Transition>> allPaths = new LinkedList<>();
    for (State s : states) {
      transitionPath(event, s, allPaths);
    }
    return allPaths;
  }

  private void transitionPath(final Event event, final Vertex vertex, final Collection<TreeNode<Transition>> allPaths) {
    TreeNode<Transition> path = transitionPath(event, vertex);
    if (path.hasChildren()) {
      allPaths.add(path);
    }
  }

  private TreeNode<Transition> transitionPath(final Event event, final Vertex vertex) {
    final TreeNode<Transition> paths = new TreeNode<>();
    transitionPath(event, vertex, paths);
    return paths;
  }

  private boolean transitionPath(final Event event, final Vertex vertex, final TreeNode<Transition> paths) {
    final Collection<? extends Transition> candidates = vertex.outgoing(event);

    boolean found = false;
    for (Transition t : candidates) {
      if (transitionGuard(event, t)) {
        TreeNode<Transition> tn = new TreeNode<>(t);

        if (t.target() instanceof State) {
          found = paths.add(tn);
        } else {
          PseudoState ps = (PseudoState) t.target();

          switch (ps.kind()) {
            /*
             * Terminating the state machine means the executor must go no
             * farther that vertex. Reaching a choice vertex requires to fire
             * all transitions to go to that vertex first and then figure out
             * where to go from there (dynamic conditional branching).
             */
            case CHOICE:
            case TERMINATE:
              found = paths.add(tn);
              break;
            /*
             * For history vertices, either: - an history does not exist in
             * which case the outgoing transition of the history vertex will be
             * fired; if such\ transition does not exist, the transition might
             * not be enabled or the code may throw an exception depending on
             * the running configuration. - an history exists in which case we
             * want to reach directly that history pseudo vertex.
             */
            case SHALLOW_HISTORY:
            case DEEP_HISTORY:
              StateConfiguration stateConfig = machineState.restore(ps.container());
              if (stateConfig == null) {
                if (transitionPath(CompletionEvent.INSTANCE, t.target(), tn)) {
                  found = paths.add(tn);
                } else {
                  switch (configuration.defaultHistoryFailover()) {
                    case DISABLE_TRANSITION:
                      break; // Transition has not been added. Nothing else to do.
                    case EXCEPTION:
                      throw new DefaultHistoryEntryException(ps, configuration.defaultHistoryFailover(), "History vertex is unreacheable because this region never had an activate state before and the vertex does not have a valid outgoing transition.");
                    default:
                      throw new UnsupportedOperationException("Unknown default history failover value: " + configuration.defaultHistoryFailover());
                  }
                }
              } else {
                found = paths.add(tn);
              }
              break;
            case JOIN:
              if (machineState.joinReached(ps, t) && transitionPath(CompletionEvent.INSTANCE, t.target(), tn)) {
                found = paths.add(tn);
              }
              break;
            default:
              if (transitionPath(CompletionEvent.INSTANCE, t.target(), tn)) {
                found = paths.add(tn);
              }
              break;
          }
        }
      }
    }
    return found;
  }

  /**
   * Finds the immediate parent state of the given vertex.
   *
   * There are four possibilities. 1) the given vertex is a sub state of a
   * composite state. 2) the given vertex belongs to the topmost region of a sub
   * state machine, in which case the state containing the state machine is
   * returned. 3) the given vertex belongs to the topmost region of the top
   * level state machine in which case it has no parent and null is returned. 4)
   * the vertex is an entry or exit point and its parent state is immediately
   * known via the state() method.
   *
   * @param childVertex The vertex of which to find the parent state.
   * @return The parent containing state or null when such does not exist.
   */
  private State parentState(final Vertex childVertex) {
    if (childVertex.container() == null) {
      assert VertexUtils.connectionPoint(childVertex) : "Only entry points and exit points can not belong to a region.";
      return ((PseudoState) childVertex).state();
    }
    return childVertex.container().state();
  }

  private boolean transitionGuard(final Event event, final Transition transition) {
    if (transition.guard() == null) {
      return true;
    }

    boolean result;
    try {

      if (listeners.hasTransitionGuardBeforeExecutionListener()) {
        listeners.onTransitionGuardBeforeExecution(this, machine, context, event, transition);
      }

      result = transition.guard().test(event, context);

      if (listeners.hasTransitionGuardAfterExecutionListener()) {
        listeners.onTransitionGuardAfterExecution(this, machine, context, event, transition);
      }

    } catch (Exception ex) {
      if (listeners.hasTransitionGuardExceptionListener()) {
        listeners.onTransitionGuardException(this, machine, context, event, transition, ex);
      }
      result = false;
    }
    return result;
  }

  private void transitionEffect(final Event event, final Transition transition) {
    if (transition.effect() == null) {
      return;
    }

    try {

      if (listeners.hasTransitionEffectBeforeExecutionListener()) {
        listeners.onTransitionEffectBeforeExecution(this, machine, context, event, transition);
      }

      transition.effect().accept(event, context);

      if (listeners.hasTransitionEffectAfterExecutionListener()) {
        listeners.onTransitionEffectAfterExecution(this, machine, context, event, transition);
      }

    } catch (Exception ex) {
      if (listeners.hasTransitionEffectExceptionListener()) {
        listeners.onTransitionEffectException(this, machine, context, event, transition, ex);
      }
    }
  }

  private void terminate() {
    machineState.terminate();

    /*
     * The listener for termination must be invoked after the listener for
     * transition completion.
     */
  }

  private void onEventAccepted(final Event event) {
    if (listeners.hasEventAcceptedListener()) {
      listeners.onEventAccepted(this, machine, context, event);
    }
  }

  private void onEventDenied(final Event event) {
    if (listeners.hasEventDeniedListener()) {
      listeners.onEventDenied(this, machine, context, event);
    }
  }

  private void onEventDeferred(final Event event) {
    if (listeners.hasEventDeferredListener()) {
      listeners.onEventDeferred(this, machine, context, event);
    }
  }

  private final class RunnableActivity implements Runnable {

    private final AbstractStateMachineExecutor executor;
    private final State state;

    public RunnableActivity(final AbstractStateMachineExecutor executor, final State state) {
      this.executor = executor;
      this.state = state;
    }

    @Override
    public void run() {
      try {

        if (listeners.hasStateActivityBeforeExecution()) {
          listeners.onStateActivityBeforeExecution(executor, machine, context, state);
        }

        state.doActivity().accept(context);

        if (listeners.hasStateActivityAfterExecution()) {
          listeners.onStateActivityAfterExecution(executor, machine, context, state);
        }

      } catch (Exception ex) {
        if (listeners.hasStateActivityException()) {
          listeners.onStateActivityException(executor, machine, context, state, ex);
        }
      }

      if (machineState.activityCompleted(state)) {
        takeCompletionEvent();
      }
    }

  }

  private final class RunnableEvent implements Runnable {

    private final State state;
    private final Event event;

    public RunnableEvent(final Event event, final State state) {
      this.state = state;
      this.event = event;
    }

    @Override
    public void run() {
      take(event, state);
    }

  }
}
