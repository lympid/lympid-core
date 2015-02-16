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
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotHistoryEquals;
import com.lympid.core.behaviorstatemachines.pseudo.history.HistoryTest9;

/**
 *
 * @author Fabien Renaud 
 */
public class Test9 extends HistoryTest9 {
  
  public Test9() {
    super(PseudoStateKind.SHALLOW_HISTORY);
  }
  
  @Override
  protected void resume_B1A_B2A_sub1(StateMachineExecutor fsm, SequentialContext expected, SequentialContext ctx) {
    assertSnapshotHistoryEquals(fsm, "#7", new ActiveStateTree(this).branch("B"));
    
    fsm.take(new StringEvent("resume"));
    expected
      .exit("P").effect("t13").enter("compo").enter("B")
      .effect("t6").enter("B2A")
      .effect("t9").enter("sub1").effect("t0").enter("Z")
      .effect("t4").enter("B1").enter("B1A");
    assertSnapshotEquals(fsm, new ActiveStateTree(this)
      .branch("compo", "B", "B1", "B1A")
      .branch("compo", "B", "B2A")
      .branch("compo", "B", "sub1", "Z")
    );
    assertSequentialContextEquals(expected, ctx);
  }
}
