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
package com.lympid.core.behaviorstatemachines.pseudo.history.deep;

import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertSnapshotEquals;
import com.lympid.core.behaviorstatemachines.pseudo.history.HistoryTest7;

/**
 *
 * @author Fabien Renaud 
 */
public class Test7 extends HistoryTest7 {

  public Test7() {
    super(PseudoStateKind.DEEP_HISTORY);
  }
  
  @Override
  protected void resumeA(StateMachineExecutor fsm, SequentialContext expected) {
    expected.enter("A").enter("Aa").enter("Aaa").enter("Aaaa").enter("Aaaaa");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "Aa", "Aaa", "Aaaa", "Aaaaa"));
    assertSequentialContextEquals(expected, fsm);
  }
  
  @Override
  protected void resumeB(StateMachineExecutor fsm, SequentialContext expected) {
    expected.enter("A").enter("Aa").enter("Aaa").enter("Aaaa").enter("Aaaab");
    assertSnapshotEquals(fsm, new ActiveStateTree(this).branch("A", "Aa", "Aaa", "Aaaa", "Aaaab"));
    assertSequentialContextEquals(expected, fsm);
  }

}
