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
package com.lympid.core.behaviorstatemachines.example;

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.FinalStateTest;
import com.lympid.core.behaviorstatemachines.InitialPseudoStateTest;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.SimpleStateTest;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.StateMachineTester;
import com.lympid.core.behaviorstatemachines.TransitionTest;
import com.lympid.core.behaviorstatemachines.VertexTest;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import java.util.Random;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class OnOffTest extends AbstractStateMachineTest {

  @Test
  public void model() {
    assertEquals("OnOff", topLevelStateMachine().getName());
    Region region = StateMachineTester.assertTopLevelStateMachine(topLevelStateMachine());

    StateMachineTester.assertRegion(region, 4, 4,
      new VertexTest[]{
        new InitialPseudoStateTest("#4"),
        new SimpleStateTest("ON"),
        new SimpleStateTest("OFF"),
        new FinalStateTest("end")
      },
      new TransitionTest[]{
        new TransitionTest("#5", "#4", "OFF"),
        new TransitionTest("t1", "OFF", "ON"),
        new TransitionTest("t3", "OFF", "end"),
        new TransitionTest("t2", "ON", "OFF")
      }
    );
  }

  @Test
  public void straightRun_offEnd() {
    StateMachineExecutor fsm = fsm();
    fsm.go();

    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("OFF"));

    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }

  @Test
  public void straightRun_offOnOffEnd() {
    StateMachineExecutor fsm = fsm();
    fsm.go();

    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("OFF"));

    fsm.take(new StringEvent("on"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ON"));

    fsm.take(new StringEvent("off"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("OFF"));

    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }

  @Test
  public void repeatRun_offOnOffEnd() {
    StateMachineExecutor fsm = fsm();
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("OFF"));

    int repeat = new Random().nextInt(10) + 2;
    for (int i = 0; i < repeat; i++) {
      fsm.take(new StringEvent("on"));
      assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ON"));

      fsm.take(new StringEvent("off"));
      assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("OFF"));
    }

    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("#3"));
  }

  @Test
  public void simpleRun() {
    StateMachineExecutor fsm = fsm();
    fsm.go();
    
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("OFF"));
    System.out.println(fsm.snapshot().stateConfiguration());

    fsm.take(new StringEvent("off"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("OFF"));

    fsm.take(new StringEvent("on"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ON"));
    fsm.take(new StringEvent("on"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ON"));
    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("ON"));

    fsm.take(new StringEvent("off"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("OFF"));
    fsm.take(new StringEvent("off"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("OFF"));

    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    fsm.take(new StringEvent("on"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    fsm.take(new StringEvent("off"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
    fsm.take(new StringEvent("end"));
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("end"));
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Object> builder = new StateMachineBuilder("OnOff");

    builder
      .region()
        .finalState("end");

    builder
      .region()
        .initial()
          .transition()
            .target("OFF");

    builder
      .region()
        .state("OFF")
          .transition("t1")
            .on("on")
            .target("ON")
          .transition("t3")
            .on("end")
            .target("end");

    builder
      .region()
        .state("ON")
          .transition("t2")
            .on("off")
            .target("OFF");

    return builder;
  }

  @Override
  protected boolean sequentialContextInjection() {
    return false;
  }
  
  @Override
  public String stdOut() {
    return STDOUT;
  }

  private static final String STDOUT = "StateMachine: \"OnOff\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"OFF\"\n" +
"    State: \"ON\"\n" +
"    Transition: #5 --- #4 -> \"OFF\"\n" +
"    Transition: \"t1\" --- \"OFF\" -> \"ON\"\n" +
"    Transition: \"t3\" --- \"OFF\" -> \"end\"\n" +
"    Transition: \"t2\" --- \"ON\" -> \"OFF\"";
}
