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
package com.lympid.core.behaviorstatemachines.activity;

import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.impl.ExecutorConfiguration.DefaultEntryRule;
import com.lympid.core.behaviorstatemachines.impl.StateMachineSnapshot;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;

/**
 * Tests the default entry rule of an empty composite state.
 * 
 * @author Fabien Renaud
 */
public class Test6 extends AbstractStateMachineTest {
    
  @Test
  public void run() throws InterruptedException {
    SequentialContext expected = new SequentialContext();
    
    Context ctx = new Context();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.configuration().defaultEntryRule(DefaultEntryRule.NONE);    
    fsm.go();
    
    expected.effect("t0").enter("compo");
    StateMachineSnapshot<Context> snapshot = fsm.snapshot();
    assertSequentialContextEquals(expected, snapshot.context());
    assertStateConfiguration(snapshot, new ActiveStateTree("compo"));
    
    ctx.latch.await();
    
    expected.activity("someactivity").exit("compo").effect("t1");
    snapshot = fsm.snapshot();
    assertSequentialContextEquals(expected, snapshot.context());
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder(name());
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target("compo");
            
    builder
      .region()
        .state(new CompositeStateBuilder<>("compo"))
          .activity(Activity.class)
          .transition("t1")
            .effect((e, c) -> c.latch.countDown())
            .target("end");
    
    builder
      .region()
        .finalState("end");
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final class Context extends SequentialContext {
    CountDownLatch latch = new CountDownLatch(1);
  }
  
  public static final class Activity implements StateBehavior<Context> {

    @Override
    public void accept(Context ctx) {
        ctx.activity("someactivity");
    }
    
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test6.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"compo\"\n" +
"      Region: #7\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"compo\"\n" +
"    Transition: \"t1\" --- \"compo\" -> \"end\"";
  
}
