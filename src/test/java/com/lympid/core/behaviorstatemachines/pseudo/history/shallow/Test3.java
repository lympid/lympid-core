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
package com.lympid.core.behaviorstatemachines.pseudo.history.shallow;

import com.lympid.core.basicbehaviors.StringEvent;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import com.lympid.core.behaviorstatemachines.pseudo.history.HistoryTest3;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class Test3 extends HistoryTest3 {

  public Test3() {
    super(PseudoStateKind.SHALLOW_HISTORY);
  }
    
  @Test
  public void run_P_Aend() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    toA(fsm, expected);
    
    toP(fsm, expected, "A", "compo");
    resumeAend(fsm, expected);
    
    toBCEnd(fsm, expected);
  }
    
  @Test
  public void run_P_Bend() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
        
    toA(fsm, expected);
    
    fsm.take(new StringEvent("toB"));
    expected.exit("A").effect("t1");
    toB(fsm, expected);
    
    toP(fsm, expected, "B", "compo");
    resumeBend(fsm, expected);
    
    toCEnd(fsm, expected);
  }
    
  @Test
  public void run_P_Cend() {
    SequentialContext expected = new SequentialContext()
      .effect("t0").enter("compo");
    
    SequentialContext ctx = new SequentialContext();
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
        
    toAB(fsm, expected);
    
    fsm.take(new StringEvent("toC"));
    expected.exit("B").effect("t2");
    toC(fsm, expected);
    
    toP(fsm, expected, "C", "compo");
    resumeCend(fsm, expected);
    
    toEnd(fsm, expected);
  }

  @Override
  protected void resumeAa(StateMachineExecutor fsm, SequentialContext expected) {
    toAa(fsm, expected);
  }

  @Override
  protected void resumeAb(StateMachineExecutor fsm, SequentialContext expected) {
    toAa(fsm, expected);
    toAb(fsm, expected);
  }

  protected void resumeAend(StateMachineExecutor fsm, SequentialContext expected) {
    toA(fsm, expected);
  }

  @Override
  protected void resumeBa(StateMachineExecutor fsm, SequentialContext expected) {
    toBa(fsm, expected);
  }

  @Override
  protected void resumeBb(StateMachineExecutor fsm, SequentialContext expected) {
    toBa(fsm, expected);
    toBb(fsm, expected);
  }

  protected void resumeBend(StateMachineExecutor fsm, SequentialContext expected) {
    toB(fsm, expected);
  }

  @Override
  protected void resumeCa(StateMachineExecutor fsm, SequentialContext expected) {
    toCa(fsm, expected);
  }

  @Override
  protected void resumeCb(StateMachineExecutor fsm, SequentialContext expected) {
    toCa(fsm, expected);
    toCb(fsm, expected);
  }

  protected void resumeCend(StateMachineExecutor fsm, SequentialContext expected) {
    toC(fsm, expected);
  }
}
