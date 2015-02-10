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
package com.lympid.core.behaviorstatemachines;

import com.lympid.core.basicbehaviors.Behavior;
import com.lympid.core.common.UmlElement;
import java.util.Collection;
import java.util.List;

/**
 * A state machine owns one or more regions, which in turn own vertices and
 * transitions. The behaviored classifier context owning a state machine defines
 * which signal and call triggers are defined for the state machine, and which
 * attributes and operations are available in activities of the state machine.
 * Signal triggers and call triggers for the state machine are defined according
 * to the receptions and operations of this classifier. As a kind of behavior, a
 * state machine may have an associated behavioral feature (specification) and
 * be the method of this behavioral feature. In this case the state machine
 * specifies the behavior of this behavioral feature. The parameters of the
 * state machine in this case match the parameters of the behavioral feature and
 * provide the means for accessing (within the state machine) the behavioral
 * feature parameters. A state machine without a context classifier may use
 * triggers that are independent of receptions or operations of a classifier,
 * i.e., either just signal triggers or call triggers based upon operation
 * template parameters of the (parameterized) statemachine.
 *
 * @author Fabien Renaud
 */
public interface StateMachine extends Behavior, UmlElement, Visitable, RegionOwner {

  /**
   * The regions owned directly by the state machine.
   *
   * @return The regions owned directly by the state machine.
   */
  @Override
  List<? extends Region> region();

  /**
   * The connection points defined for this state machine. They represent the
   * interface of the state machine when used as part of submachine state.
   *
   * @return The connection points defined for this state machine.
   */
  Collection<? extends PseudoState> connectionPoint();

  /**
   * The state machines of which this is an extension.
   *
   * @return The state machines of which this is an extension.
   */
//  Collection<? extends StateMachine> extendedStateMachine();
  /**
   * Preprocessed information about the state machine.
   *
   * @return The state machine meta data
   */
  StateMachineMeta metadata();
}
