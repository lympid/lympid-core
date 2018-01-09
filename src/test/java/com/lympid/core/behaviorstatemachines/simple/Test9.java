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

package com.lympid.core.behaviorstatemachines.simple;

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.BiTransitionBehavior;
import com.lympid.core.behaviorstatemachines.BiTransitionConstraint;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import com.lympid.core.behaviorstatemachines.impl.ExecutorListener;
import com.lympid.core.behaviorstatemachines.listener.StringBufferLogger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests an external transition with 1 trigger, 1 guard and 1 effect.
 * The state machine auto starts.
 * @author Fabien Renaud 
 */
public class Test9 extends AbstractStateMachineTest {
  
  @Test
  public void run() {    
    final StringBufferLogger log = new StringBufferLogger();
    
    Context context = new Context();
    context.counter = 1;
    assertFalse(context.fired);
    StateMachineExecutor fsm = fsm(context);
    setListeners(fsm.listeners(), log);
    fsm.go();
    
    /*
     * Machine has started and is on state A.
     */
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));
    
    context.counter = 1;
    /*
     * An unknown event has no effect
     */
    fsm.take(new Event() { @Override public String toString() { return "unknown"; }});
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A"));
    assertFalse(context.fired);
    
    fsm.take(new StringEvent("go"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    assertTrue(context.fired);
    
    assertEquals(MAIN_LOG, log.mainBuffer());
    assertEquals(ACTIVITY_LOG, log.activityBuffer());
  }
  
  private void setListeners(final ExecutorListener listener, final StringBufferLogger log) {
    listener.add(log);
    listener.add(null);
    listener.remove(null);
    listener.remove(log);
    listener.add(log);
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder builder = new StateMachineBuilder<>(name());
    
    VertexBuilderReference end = builder
      .region()
        .finalState("end");
    
    builder
      .region()
        .initial()
          .transition()
            .target("A");
    
    builder
      .region()
        .state("A")
          .transition()
            .on("go")
            .guard((BiTransitionConstraint<Event, Context>)(e, c) -> { return c.counter > 0; })
            .effect((BiTransitionBehavior<Event, Context>)(e, c) -> { c.fired = true; })
            .target(end);
    
    return builder;
  }
  
  @Override
  protected String executorName() {
    return "Simple." + getClass().getSimpleName();
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final class Context {
    int counter;
    boolean fired;
    
    @Override
    public String toString() {
      return "";
    }
  }
  
  private static final String MAIN_LOG = "tag=\"MACHINE_STARTED\" executor=\"Simple.Test9\" context=\"\"\n" +
"tag=\"EVENT_ACCEPTED\" executor=\"Simple.Test9\" event=\"CompletionEvent\" context=\"\"\n" +
"tag=\"TRANSITION_STARTED\" executor=\"Simple.Test9\" event=\"CompletionEvent\" transition=\"#5\" source=\"#4\" target=\"A\" context=\"\"\n" +
"tag=\"STATE_ENTER\" executor=\"Simple.Test9\" state=\"A\" context=\"\"\n" +
"tag=\"STATE_ENTER_BEFORE_EXECUTION\" executor=\"Simple.Test9\" state=\"A\" context=\"\"\n" +
"tag=\"STATE_ENTER_AFTER_EXECUTION\" executor=\"Simple.Test9\" state=\"A\" context=\"\"\n" +
"tag=\"TRANSITION_ENDED\" executor=\"Simple.Test9\" event=\"CompletionEvent\" transition=\"#5\" source=\"#4\" target=\"A\" context=\"\"\n" +
"tag=\"EVENT_DENIED\" executor=\"Simple.Test9\" event=\"unknown\" context=\"\"\n" +
"tag=\"TRANSITION_GUARD_BEFORE_EXECUTION\" executor=\"Simple.Test9\" event=\"go\" transition=\"#7\" source=\"A\" target=\"end\" context=\"\"\n" +
"tag=\"TRANSITION_GUARD_AFTER_EXECUTION\" executor=\"Simple.Test9\" event=\"go\" transition=\"#7\" source=\"A\" target=\"end\" context=\"\"\n" +
"tag=\"EVENT_ACCEPTED\" executor=\"Simple.Test9\" event=\"go\" context=\"\"\n" +
"tag=\"TRANSITION_STARTED\" executor=\"Simple.Test9\" event=\"go\" transition=\"#7\" source=\"A\" target=\"end\" context=\"\"\n" +
"tag=\"STATE_EXIT\" executor=\"Simple.Test9\" state=\"A\" context=\"\"\n" +
"tag=\"STATE_EXIT_BEFORE_EXECUTION\" executor=\"Simple.Test9\" state=\"A\" context=\"\"\n" +
"tag=\"STATE_EXIT_AFTER_EXECUTION\" executor=\"Simple.Test9\" state=\"A\" context=\"\"\n" +
"tag=\"TRANSITION_EFFECT_BEFORE_EXECUTION\" executor=\"Simple.Test9\" event=\"go\" transition=\"#7\" source=\"A\" target=\"end\" context=\"\"\n" +
"tag=\"TRANSITION_EFFECT_AFTER_EXECUTION\" executor=\"Simple.Test9\" event=\"go\" transition=\"#7\" source=\"A\" target=\"end\" context=\"\"\n" +
"tag=\"STATE_ENTER\" executor=\"Simple.Test9\" state=\"end\" context=\"\"\n" +
"tag=\"TRANSITION_ENDED\" executor=\"Simple.Test9\" event=\"go\" transition=\"#7\" source=\"A\" target=\"end\" context=\"\"\n" +
"tag=\"MACHINE_TERMINATED\" executor=\"Simple.Test9\" context=\"\"\n";
  private static final String ACTIVITY_LOG = "";
  
  private static final String STDOUT = "StateMachine: \"" + Test9.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"A\"\n" +
"    Transition: #5 --- #4 -> \"A\"\n" +
"    Transition: #7 --- \"A\" -> \"end\"";
}
