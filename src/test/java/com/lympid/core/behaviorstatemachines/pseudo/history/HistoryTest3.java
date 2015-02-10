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

package com.lympid.core.behaviorstatemachines.pseudo.history;

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import org.junit.Test;

/**
 * Tests the shallow history is shallow and reenters sub composite states.
 * @author Fabien Renaud 
 */
public abstract class HistoryTest3 extends AbstractHistoryTest {
  
  private String stdout;
  
  protected HistoryTest3(final PseudoStateKind historyKind) {
    super(historyKind);
    setStdOut(historyKind);
  }
    
  @Test
  public void run_unpaused() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    toA(fsm, expected, ctx);
    
    toBCEnd(fsm, expected, ctx);
  }
    
  @Test
  public void run_pause_Aa() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    toAa(fsm, expected, ctx);
    
    pauseAndResume(fsm, expected, ctx, "Aa", "A", "compo");
    resumeAa(fsm, expected, ctx);
    
    toAb(fsm, expected, ctx);
    toAend(fsm, expected, ctx);
    toBCEnd(fsm, expected, ctx);
  }
    
  @Test
  public void run_pause_Ab() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    toAa(fsm, expected, ctx);
    toAb(fsm, expected, ctx);
    
    pauseAndResume(fsm, expected, ctx, "Ab", "A", "compo");
    resumeAb(fsm, expected, ctx);
    
    toAend(fsm, expected, ctx);
    toBCEnd(fsm, expected, ctx);
  }
    
  @Test
  public void run_pause_Ba() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
        
    toA(fsm, expected, ctx);
    
    fsm.take(new StringEvent("toB"));
    expected.exit("A").effect("t1");
    toBa(fsm, expected, ctx);
    
    pauseAndResume(fsm, expected, ctx, "Ba", "B", "compo");
    resumeBa(fsm, expected, ctx);
    
    toBb(fsm, expected, ctx);
    toBend(fsm, expected, ctx);
    toCEnd(fsm, expected, ctx);
  }
    
  @Test
  public void run_pause_Bb() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
        
    toA(fsm, expected, ctx);
    
    fsm.take(new StringEvent("toB"));
    expected.exit("A").effect("t1");
    toBa(fsm, expected, ctx);
    toBb(fsm, expected, ctx);
    
    pauseAndResume(fsm, expected, ctx, "Bb", "B", "compo");
    resumeBb(fsm, expected, ctx);
    
    toBend(fsm, expected, ctx);
    toCEnd(fsm, expected, ctx);
  }
    
  @Test
  public void run_pause_Ca() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
        
    toAB(fsm, expected, ctx);
    
    fsm.take(new StringEvent("toC"));
    expected.exit("B").effect("t2");
    toCa(fsm, expected, ctx);
    
    pauseAndResume(fsm, expected, ctx, "Ca", "C", "compo");
    resumeCa(fsm, expected, ctx);
    
    toCb(fsm, expected, ctx);
    toCend(fsm, expected, ctx);
    toEnd(fsm, expected, ctx);
  }
    
  @Test
  public void run_pause_Cb() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
        
    toAB(fsm, expected, ctx);
    
    fsm.take(new StringEvent("toC"));
    expected.exit("B").effect("t2");
    toCa(fsm, expected, ctx);
    toCb(fsm, expected, ctx);
    
    pauseAndResume(fsm, expected, ctx, "Cb", "C", "compo");    
    resumeCb(fsm, expected, ctx);
    
    toCend(fsm, expected, ctx);
    toEnd(fsm, expected, ctx);
  }
  
  protected abstract void resumeAa(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx);

  protected abstract void resumeAb(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx);

  protected abstract void resumeBa(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx);

  protected abstract void resumeBb(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx);

  protected abstract void resumeCa(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx);

  protected abstract void resumeCb(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx);

  protected final void toAB(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    toA(fsm, expected, ctx);
    
    fsm.take(new StringEvent("toB"));
    expected.exit("A").effect("t1");
    toB(fsm, expected, ctx);
  }

  protected final void toBCEnd(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    fsm.take(new StringEvent("toB"));
    expected.exit("A").effect("t1");
    toB(fsm, expected, ctx);
    
    toCEnd(fsm, expected, ctx);
  }
  
  protected final void toCEnd(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    fsm.take(new StringEvent("toC"));
    expected.exit("B").effect("t2");
    toC(fsm, expected, ctx);
    
    toEnd(fsm, expected, ctx);
  }

  protected final void toC(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    toCa(fsm, expected, ctx);
    toCb(fsm, expected, ctx);
    toCend(fsm, expected, ctx);
  }

  protected final void toB(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    toBa(fsm, expected, ctx);
    toBb(fsm, expected, ctx);
    toBend(fsm, expected, ctx);
  }

  protected final void toA(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    toAa(fsm, expected, ctx);
    toAb(fsm, expected, ctx);
    toAend(fsm, expected, ctx);
  }

  protected final void toEnd(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    fsm.take(new StringEvent("toEnd"));
    expected.exit("C").exit("compo").effect("t3");
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
  }

  protected final void toCend(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    fsm.take(new StringEvent("toCend"));
    expected.exit("Cb").effect("t2_C");
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "C", "Cend"));
    assertSequentialContextEquals(expected, ctx);
  }

  protected final void toCb(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    fsm.take(new StringEvent("toCb"));
    expected.exit("Ca").effect("t1_C").enter("Cb");
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "C", "Cb"));
    assertSequentialContextEquals(expected, ctx);
  }

  protected final void toCa(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    expected.enter("C").effect("t0_C").enter("Ca");
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "C", "Ca"));
    assertSequentialContextEquals(expected, ctx);
  }

  protected final void toBend(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    fsm.take(new StringEvent("toBend"));
    expected.exit("Bb").effect("t2_B");
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "B", "Bend"));
    assertSequentialContextEquals(expected, ctx);
  }

  protected final void toBb(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    fsm.take(new StringEvent("toBb"));
    expected.exit("Ba").effect("t1_B").enter("Bb");
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "B", "Bb"));
    assertSequentialContextEquals(expected, ctx);
  }

  protected final void toBa(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    expected.enter("B").effect("t0_B").enter("Ba");
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "B", "Ba"));
    assertSequentialContextEquals(expected, ctx);
  }

  protected final void toAend(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    fsm.take(new StringEvent("toAend"));
    expected.exit("Ab").effect("t2_A");
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "A", "Aend"));
    assertSequentialContextEquals(expected, ctx);
  }

  protected final void toAb(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    fsm.take(new StringEvent("toAb"));
    expected.exit("Aa").effect("t1_A").enter("Ab");
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "A", "Ab"));
    assertSequentialContextEquals(expected, ctx);
  }

  protected final void toAa(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    expected.enter("A").effect("t0_A").enter("Aa");
    assertStateConfiguration(fsm, new ActiveStateTree("compo", "A", "Aa"));
    assertSequentialContextEquals(expected, ctx);
  }

  protected final void pauseAndResume(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx, String... exits) {
    fsm.take(new StringEvent("pause"));
    for (String e : exits) {
      expected.exit(e);
    }
    expected.effect("t4").enter("P");
    assertStateConfiguration(fsm, new ActiveStateTree("P"));
    assertSequentialContextEquals(expected, ctx);
    
    fsm.take(new StringEvent("resume"));
    expected.exit("P").effect("t5").enter("compo");
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder builder = new StateMachineBuilder(name());

    builder
      .region()
        .finalState("end");

    builder
      .region()
        .initial()
          .transition("t0")
            .target("A");
    
    builder
      .region()
        .state(composite("compo"))
          .transition("t4")
            .on("pause")
            .target("P");

    builder
      .region()
        .state("P")
          .transition("t5")
            .on("resume")
            .target("history");
    
    return builder;
  }
  
  private CompositeStateBuilder composite(final String name) {
    CompositeStateBuilder builder = new CompositeStateBuilder(name);
  
    history(builder, "history");
    
    builder
      .region()
        .state(subComposite("A"))
          .transition("t1")
            .on("toB")
            .target("B");
  
    builder
      .region()
        .state(subComposite("B"))
          .transition("t2")
            .on("toC")
            .target("C");
  
    builder
      .region()
        .state(subComposite("C"))
          .transition("t3")
            .on("toEnd")
            .target("end");
    
    return builder;
  }
  
  private CompositeStateBuilder subComposite(final String name) {
    CompositeStateBuilder builder = new CompositeStateBuilder(name);
    
    builder
      .region()
        .initial()
          .transition("t0_" + name)
            .target(name + "a");
    
    builder
      .region()
        .state(name + "a")
          .transition("t1_" + name)
            .on("to" + name + "b")
            .target(name + "b");
    
    builder
      .region()
        .state(name + "b")
          .transition("t2_" + name)
            .on("to" + name + "end")
            .target(name + "end");
    
    builder
      .region()
        .finalState(name + "end");
    
    return builder;
  }
  
  @Override
  public String stdOut() {
    return stdout;
  }

  @Override
  final void setStdOut(final PseudoStateKind historyKind) {
    stdout = "StateMachine: \"" + name() + "\"\n" +
"  Region: #2\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: #4 kind: INITIAL\n" +
"    State: \"compo\"\n" +
"      Region: #8\n" +
"        PseudoState: \"history\" kind: " + historyKind + "\n" +
"        State: \"C\"\n" +
"          Region: #32\n" +
"            PseudoState: #33 kind: INITIAL\n" +
"            State: \"Ca\"\n" +
"            State: \"Cb\"\n" +
"            FinalState: \"Cend\"\n" +
"            Transition: \"t0_C\" --- #33 -> \"Ca\"\n" +
"            Transition: \"t1_C\" --- \"Ca\" -> \"Cb\"\n" +
"            Transition: \"t2_C\" --- \"Cb\" -> \"Cend\"\n" +
"        State: \"B\"\n" +
"          Region: #22\n" +
"            PseudoState: #23 kind: INITIAL\n" +
"            State: \"Ba\"\n" +
"            State: \"Bb\"\n" +
"            FinalState: \"Bend\"\n" +
"            Transition: \"t0_B\" --- #23 -> \"Ba\"\n" +
"            Transition: \"t1_B\" --- \"Ba\" -> \"Bb\"\n" +
"            Transition: \"t2_B\" --- \"Bb\" -> \"Bend\"\n" +
"        State: \"A\"\n" +
"          Region: #12\n" +
"            PseudoState: #13 kind: INITIAL\n" +
"            State: \"Aa\"\n" +
"            State: \"Ab\"\n" +
"            FinalState: \"Aend\"\n" +
"            Transition: \"t0_A\" --- #13 -> \"Aa\"\n" +
"            Transition: \"t1_A\" --- \"Aa\" -> \"Ab\"\n" +
"            Transition: \"t2_A\" --- \"Ab\" -> \"Aend\"\n" +
"        Transition: \"t1\" --- \"A\" -> \"B\"\n" +
"        Transition: \"t2\" --- \"B\" -> \"C\"\n" +
"    State: \"P\"\n" +
"    Transition: \"t0\" --- #4 -> \"A\"\n" +
"    Transition: \"t4\" --- \"compo\" -> \"P\"\n" +
"    Transition: \"t3\" --- \"C\" -> \"end\"\n" +
"    Transition: \"t5\" --- \"P\" -> \"history\"";
  }

}
