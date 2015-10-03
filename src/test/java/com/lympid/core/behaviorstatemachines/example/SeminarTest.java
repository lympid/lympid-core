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
import com.lympid.core.behaviorstatemachines.BiTransitionBehavior;
import com.lympid.core.behaviorstatemachines.BiTransitionConstraint;
import com.lympid.core.behaviorstatemachines.Region;
import com.lympid.core.behaviorstatemachines.RegionTest;
import com.lympid.core.behaviorstatemachines.State;
import com.lympid.core.behaviorstatemachines.StateBehavior;
import com.lympid.core.behaviorstatemachines.StateMachineTester;
import com.lympid.core.behaviorstatemachines.TransitionConstraint;
import com.lympid.core.behaviorstatemachines.Vertex;
import com.lympid.core.behaviorstatemachines.VertexTest;
import com.lympid.core.behaviorstatemachines.builder.ChoiceBuilder;
import com.lympid.core.behaviorstatemachines.builder.CompositeStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.ForkBuilder;
import com.lympid.core.behaviorstatemachines.builder.OrthogonalStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.ShallowHistoryBuilder;
import com.lympid.core.behaviorstatemachines.builder.SimpleStateBuilder;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.VertexBuilderReference;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Source: http://agilemodeling.com/artifacts/stateMachineDiagram.htm
 *
 * The fork segment validation does not pass because one of the fork outgoing
 * transitions target a history pseudo states which violates the rule "A fork
 * segment must always target a state" from the specification.
 *
 */
@Ignore
public class SeminarTest extends AbstractStateMachineTest {

  @Test
  public void model() {
    Region region = StateMachineTester.assertTopLevelStateMachine(topLevelStateMachine());
    StateMachineTester.assertRegion(region, 6, 9, // TODO: confirm 9 transitions with original diagram
      new VertexTest("Enrollment", (v) -> testEnrollmentState(v)),
      new VertexTest("Being Taught", (v) -> testBeingTaughState(v)),
      new VertexTest("Final Exams", (v) -> testFinalExams(v))
    );
  }

  private void testEnrollmentState(Vertex vertex) {
    StateMachineTester.assertComposite(vertex);
  }

  private void testBeingTaughState(Vertex vertex) {
    State state = StateMachineTester.assertOrthogonal(vertex);
    StateMachineTester.assertRegions(state.region(), 2,
      new RegionTest(null, "1", 6, 4), // TODO: confirm values with original diagram
      new RegionTest(null, "2", 2, 2) // TODO: confirm values with original diagram
    );

  }

  private void testFinalExams(Vertex vertex) {
    StateMachineTester.assertSimple(vertex);
  }

  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>("Seminar");

    builder
      .region()
        .initial()
          .transition()
            .target("Enrollment");

    VertexBuilderReference<Context> end = builder
      .region()
        .finalState();

    builder
      .region()
        .state(enrollmentState(builder, "Enrollment"))
          .transition()
            .on("cancelled")
            .target(end);

    builder
      .region()
        .state(beingTaughtState(builder, "Being Taught"))
          .transition()
            .on("classes end")
            .target("Final Exams")
          .transition()
            .on("student dropped")
            .target(new ChoiceBuilder<Context>()
              .transition()
                .guard(EmptySeminarGuard.class)
                .target(end)
              .transition()
                .guardElse(EmptySeminarGuard.class)
                .target("Being Taught")
            );

    builder
      .region()
        .state("Final Exams")
          .transition()
            .on("closed")
            .target(end);

    return builder;
  }

  private static CompositeStateBuilder<Context> enrollmentState(final StateMachineBuilder<Context> b, final String name) {
    CompositeStateBuilder<Context> builder = new CompositeStateBuilder<>(name);

    builder
      .region()
        .initial()
          .transition()
            .target("Proposed");

    builder
      .region()
        .state("Proposed")
          .transition()
            .on("scheduled")
            .target("Scheduled");

    builder
      .region()
        .state("Scheduled")
          .transition()
            .on("open")
            .target("Open For Enrollment");

    builder
      .region()
        .state("Open For Enrollment")
          .entry(new LogSize())
          .transition()
            .on("student enrolled")
            .guard(SeatAvailableGuard.class)
            .effect(AddStudent.class)
            .target("Open For Enrollment")
          .transition()
            .on("student enrolled")
            .guardElse(SeatAvailableGuard.class)
            .effect(AddToWaitingList.class)
            .target("Full")
          .transition()
            .on("closed")
            .target("Closed to Enrollment");

    builder
      .region()
        .state(fullState("Full"))
          .transition()
            .on("seminar split")
            .target("Open For Enrollment")
          .transition()
            .on("seat available")
            .target("Open For Enrollment")
          .transition()
            .on("closed")
            .target("Closed to Enrollment")
          .transition()
            .on("student dropped")
            .guard(SeatAvailableGuard.class)
            .effect(EnrolFromWaitingList.class)
            .target("Full")
          .transition()
            .on("student dropped")
            .guardElse(SeatAvailableGuard.class)
            .target("Full");

    builder
      .region()
        .state("Closed to Enrollment")
          .entry(new NotifyInstructor())
          .transition()
            .on("term started")
            .target("Being Taught");

    builder
      .region()
        .finalState("end1");

    return builder;
  }

  private static SimpleStateBuilder<Context> fullState(final String name) {
    SimpleStateBuilder<Context> builder = new SimpleStateBuilder<>(name);

//    builder
//      .on("enroll student")
//      .effect(() -> {
//        new AddToWaitingList().invoke();
//        new ConsiderSplit().invoke();
//      });
    return builder;
  }

  private static OrthogonalStateBuilder<Context> beingTaughtState(final StateMachineBuilder<Context> b, final String name) {
    OrthogonalStateBuilder<Context> builder = new OrthogonalStateBuilder<>(name);

    builder
      .region("1")
        .initial()
          .transition()
            .target("j1");

    builder
      .region("1")
        .junction("j1")
          .transition()
            .target(new ForkBuilder<Context>()
              .transition()
                .target(new ShallowHistoryBuilder<Context>()
                  .transition()
                  .target("Deliver course material")
                )
              .transition()
                .target("Research course material")
            );

    builder
      .region("1")
        .state("Deliver course material");

    builder
      .region("1")
        .state("Mark student work");

    builder
      .region("2")
        .state("Research course material")
          .transition()
            .on("Found new material")
            .target("Update course material");

    builder
      .region("2")
        .state("Update course material")
          .transition()
            .on("Research more material")
            .target("Research course material");

    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }

  public static class EmptySeminarGuard implements TransitionConstraint<Context> {

    @Override
    public boolean test(final Context ctx) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  }

  public static class SeatAvailableGuard implements BiTransitionConstraint<StringEvent, Context> {

    @Override
    public boolean test(final StringEvent event, final Context ctx) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  }

  public static class LogSize implements StateBehavior<Context> {

    @Override
    public void accept(final Context ctx) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  }

  public static class AddStudent implements BiTransitionBehavior<StringEvent, Context> {

    @Override
    public void accept(final StringEvent event, final Context ctx) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  }

  public static class AddToWaitingList implements BiTransitionBehavior<StringEvent, Context> {

    @Override
    public void accept(final StringEvent event, final Context ctx) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  }

  public static class ConsiderSplit implements StateBehavior<Context> {

    @Override
    public void accept(final Context ctx) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  }

  public static class EnrolFromWaitingList implements BiTransitionBehavior<StringEvent, Context> {

    @Override
    public void accept(final StringEvent event,final Context ctx) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  }

  public static class NotifyInstructor implements StateBehavior<Context> {

    @Override
    public void accept(final Context ctx) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  }

  public static final class Context {

  }

  private static final String STDOUT = "";
}
