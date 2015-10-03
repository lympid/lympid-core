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

import com.lympid.core.behaviorstatemachines.PseudoStateKind;
import com.lympid.core.behaviorstatemachines.pseudo.history.HistoryTest5;

/**
 *
 * @author Fabien Renaud 
 */
public class Test5 extends HistoryTest5 {
  
  public Test5() {
    super(PseudoStateKind.DEEP_HISTORY);
  }
}
