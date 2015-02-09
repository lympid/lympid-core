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
import org.junit.Test;

/**
 *
 * @author Fabien Renaud
 */
public abstract class HistoryTest7 extends LinearNestedHistoryTest {

  private String stdout;
  
  public HistoryTest7(PseudoStateKind historyKind) {
    super(historyKind, 4, 4, 1);
  }
  
  @Test
  public void run_unpaused() {
    SequentialContext expected = new SequentialContext()
      .effect("t0");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected, ctx);    
    fireGo(fsm, expected, ctx);    
    fireEnd(fsm, expected, ctx);
  }
  
  @Test
  public void run_pause_A() {
    SequentialContext expected = new SequentialContext()
      .effect("t0");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected, ctx);
    pauseAndResumeA(fsm, expected, ctx);
    fireGo(fsm, expected, ctx);
    fireEnd(fsm, expected, ctx);
  }
  
  @Test
  public void run_pause_B() {
    SequentialContext expected = new SequentialContext()
      .effect("t0");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    begin(fsm, expected, ctx);    
    fireGo(fsm, expected, ctx);    
    pauseAndResumeB(fsm, expected, ctx);    
    fireEnd(fsm, expected, ctx);
  }

  protected final void fireEnd(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    fsm.take(new StringEvent("end"));
    expected.exit("Aaaab").exit("Aaaa").exit("Aaa").exit("Aa").exit("A").effect("t2");
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
  }

  protected final void fireGo(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    fsm.take(new StringEvent("go"));
    expected.exit("Aaaaa").effect("t1").enter("Aaaab");
    assertStateConfiguration(fsm, new ActiveStateTree("A", "Aa", "Aaa", "Aaaa", "Aaaab"));
    assertSequentialContextEquals(expected, ctx);
  }

  protected final void begin(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    expected.enter("A").enter("Aa").enter("Aaa").enter("Aaaa").enter("Aaaaa");
    assertStateConfiguration(fsm, new ActiveStateTree("A", "Aa", "Aaa", "Aaaa", "Aaaaa"));
    assertSequentialContextEquals(expected, ctx);
  }
  
  protected final void pauseAndResumeA(final StateMachineExecutor fsm, final SequentialContext expected, final SequentialContext ctx) {
    fsm.take(new StringEvent("pause"));
    expected.exit("Aaaaa").exit("Aaaa").exit("Aaa").exit("Aa").exit("A").effect("t3").enter("P");
    assertStateConfiguration(fsm, new ActiveStateTree("P"));
    assertSequentialContextEquals(expected, ctx);
    
    fsm.take(new StringEvent("resume"));
    expected.exit("P").effect("t4");
    resumeA(fsm, expected, ctx);
  }
  
  protected final void pauseAndResumeB(final StateMachineExecutor fsm, final SequentialContext expected, final SequentialContext ctx) {
    fsm.take(new StringEvent("pause"));
    expected.exit("Aaaab").exit("Aaaa").exit("Aaa").exit("Aa").exit("A").effect("t3").enter("P");
    assertStateConfiguration(fsm, new ActiveStateTree("P"));
    assertSequentialContextEquals(expected, ctx);
    
    fsm.take(new StringEvent("resume"));
    expected.exit("P").effect("t4");
    resumeB(fsm, expected, ctx);
  }
  
  protected abstract void resumeA(final StateMachineExecutor fsm, final SequentialContext expected, final SequentialContext ctx);
  
  protected abstract void resumeB(final StateMachineExecutor fsm, final SequentialContext expected, final SequentialContext ctx);
    
  @Override
  public String stdOut() {
    return stdout;
  }
  
  @Override
  protected final void setStdOut(final PseudoStateKind historyKind) {
    stdout = "StateMachine: \"" + name() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: #3 kind: INITIAL\n" +
"    State: \"A\"\n" +
"      Region: #7\n" +
"        PseudoState: \"history\" kind: " + historyKind + "\n" +
"        State: \"Aa\"\n" +
"          Region: #10\n" +
"            State: \"Aaa\"\n" +
"              Region: #12\n" +
"                State: \"Aaaa\"\n" +
"                  Region: #14\n" +
"                    State: \"Aaaaa\"\n" +
"                    State: \"Aaaab\"\n" +
"                    Transition: \"t1\" --- \"Aaaaa\" -> \"Aaaab\"\n" +
"    State: \"P\"\n" +
"    FinalState: \"end\"\n" +
"    Transition: \"t0\" --- #3 -> \"Aaaaa\"\n" +
"    Transition: \"t3\" --- \"A\" -> \"P\"\n" +
"    Transition: \"t2\" --- \"Aaaab\" -> \"end\"\n" +
"    Transition: \"t4\" --- \"P\" -> \"history\"";
  }
}
