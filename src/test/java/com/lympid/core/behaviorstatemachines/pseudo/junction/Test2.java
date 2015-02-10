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

package com.lympid.core.behaviorstatemachines.pseudo.junction;

import com.lympid.core.basicbehaviors.Event;
import com.lympid.core.behaviorstatemachines.AbstractStateMachineTest;
import com.lympid.core.behaviorstatemachines.ActiveStateTree;
import com.lympid.core.behaviorstatemachines.BiTransitionBehavior;
import com.lympid.core.behaviorstatemachines.SequentialContext;
import com.lympid.core.behaviorstatemachines.StateMachineExecutor;
import static com.lympid.core.behaviorstatemachines.StateMachineProcessorTester.assertStateConfiguration;
import com.lympid.core.behaviorstatemachines.TransitionConstraint;
import com.lympid.core.behaviorstatemachines.builder.StateMachineBuilder;
import com.lympid.core.behaviorstatemachines.builder.TransitionEffectInjector;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests the junction pseudo state realizes static conditional branching. 
 * This means that the effect fired on the incoming transition of the pseudo
 * state has no effect on which outgoing transition of the junction pseudo state
 * will be selected.
 * 
 * @author Fabien Renaud 
 */
public class Test2 extends AbstractStateMachineTest {
  
  @Test
  public void run_t1_t4_t10_t18_t20() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t1")
      .effect("t4")
      .effect("t10")
      .effect("t18")
      .effect("t20");
    
    Context ctx = new Context(0, 1, 2);
    Context expectedCtx = new Context(ctx, 6);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
    
    assertEquals(expectedCtx, ctx);
  }
  
  @Test(expected = RuntimeException.class)
  public void run_t1_t4_t10_t18() {
    StateMachineExecutor fsm = fsm(new Context(0, 1, 3));
    fsm.go();
  }
  
  @Test(expected = RuntimeException.class)
  public void run_t1_t4() {
    StateMachineExecutor fsm = fsm(new Context(0, 0, 2));
    fsm.go();
  }
  
  @Test
  public void run_t1_t5_t11_t18_t20() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t1")
      .effect("t5")
      .effect("t11")
      .effect("t18")
      .effect("t20");
    
    Context ctx = new Context(3, 1, 2);
    Context expectedCtx = new Context(ctx, 6);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
    
    assertEquals(expectedCtx, ctx);
  }
  
  @Test(expected = RuntimeException.class)
  public void run_t1_t5_t11_t18() {
    StateMachineExecutor fsm = fsm(new Context(3, 1, 3));
    fsm.go();
  }
  
  @Test(expected = RuntimeException.class)
  public void run_t1_t5() {
    StateMachineExecutor fsm = fsm(new Context(3, 2, 2));
    fsm.go();
  }
  
  @Test
  public void run_t2_t6_t12_t18_t20() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t2")
      .effect("t6")
      .effect("t12")
      .effect("t18")
      .effect("t20");
    
    Context ctx = new Context(4, 1, 2);
    Context expectedCtx = new Context(ctx, 6);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
    
    assertEquals(expectedCtx, ctx);
  }
  
  @Test(expected = RuntimeException.class)
  public void run_t2_t6_t12_t18() {
    StateMachineExecutor fsm = fsm(new Context(4, 1, 0));
    fsm.go();
  }
  
  @Test
  public void run_t2_t6_t13_t20() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t2")
      .effect("t6")
      .effect("t13")
      .effect("t20");
    
    Context ctx = new Context(1, 1, 2);
    Context expectedCtx = new Context(ctx, 5);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
    
    assertEquals(expectedCtx, ctx);
  }
  
  @Test(expected = RuntimeException.class)
  public void run_t2_t6_t13() {
    StateMachineExecutor fsm = fsm(new Context(1, 1, 3));
    fsm.go();
  }
  
  @Test
  public void run_t2_t7_t14_t20() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t2")
      .effect("t7")
      .effect("t14")
      .effect("t20");
    
    Context ctx = new Context(4, 2, 2);
    Context expectedCtx = new Context(ctx, 5);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
    
    assertEquals(expectedCtx, ctx);
  }
  
  @Test(expected = RuntimeException.class)
  public void run_t2_t7_t14() {
    StateMachineExecutor fsm = fsm(new Context(4, 2, 0));
    fsm.go();
  }
  
  @Test
  public void run_t2_t7_t15_t19_t20() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t2")
      .effect("t7")
      .effect("t15")
      .effect("t19")
      .effect("t20");
    
    Context ctx = new Context(1, 2, 2);
    Context expectedCtx = new Context(ctx, 6);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
    
    assertEquals(expectedCtx, ctx);
  }
  
  @Test(expected = RuntimeException.class)
  public void run_t2_t7_t15_t19() {
    StateMachineExecutor fsm = fsm(new Context(1, 2, 0));
    fsm.go();
  }
  
  @Test(expected = RuntimeException.class)
  public void run_t2() {
    StateMachineExecutor fsm = fsm(new Context(4, 0, 0));
    fsm.go();
  }
  
  @Test
  public void run_t3_t8_t16_t19_t20() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t3")
      .effect("t8")
      .effect("t16")
      .effect("t19")
      .effect("t20");
    
    Context ctx = new Context(2, 1, 2);
    Context expectedCtx = new Context(ctx, 6);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
    
    assertEquals(expectedCtx, ctx);
  }
  
  @Test(expected = RuntimeException.class)
  public void run_t3_t8_t16_t19() {
    StateMachineExecutor fsm = fsm(new Context(2, 1, -1));
    fsm.go();
  }
  
  @Test(expected = RuntimeException.class)
  public void run_t3_t8() {
    StateMachineExecutor fsm = fsm(new Context(2, 0, 2));
    fsm.go();
  }
  
  @Test
  public void run_t3_t9_t17_t19_t20() {
    SequentialContext expected = new SequentialContext()
      .effect("t0")
      .effect("t3")
      .effect("t9")
      .effect("t17")
      .effect("t19")
      .effect("t20");
    
    Context ctx = new Context(5, 1, 2);
    Context expectedCtx = new Context(ctx, 6);
    StateMachineExecutor fsm = fsm(ctx);
    fsm.go();
    
    assertStateConfiguration(fsm, new ActiveStateTree("end"));
    assertSequentialContextEquals(expected, ctx);
    
    assertEquals(expectedCtx, ctx);
  }
  
  @Test(expected = RuntimeException.class)
  public void run_t3_t9_t17_t19() {
    StateMachineExecutor fsm = fsm(new Context(5, 1, -1));
    fsm.go();
  }
  
  @Test(expected = RuntimeException.class)
  public void run_t3_t9() {
    StateMachineExecutor fsm = fsm(new Context(5, 0, 2));
    fsm.go();
  }
  
  @Override
  public StateMachineBuilder topLevelMachineBuilder() {
    StateMachineBuilder<Context> builder = new StateMachineBuilder<>(name());
    
    builder
      .region()
        .finalState("end");
    
    builder
      .region()
        .junction("j0");
    
    builder
      .region()
        .junction("j0") // this is done on purpose to increase coverage
          .transition("t1")
            .guard(ConstraintCMod3IsZero.class)
            .target("j1")
          .transition("t2")
            .guard(ConstraintCMod3IsOne.class)
            .target("j2")
          .transition("t3")
            .guard(ConstraintCMod3IsTwo.class)
            .target("j3");
    
    builder
      .region()
        .junction("j1")
          .transition("t4")
            .guard(ConstraintCMod2IsZero.class)
            .target("j4")
          .transition("t5")
            .guard(ConstraintCMod2IsOne.class)
            .target("j5");
    
    builder
      .region()
        .junction("j2")
          .transition("t6")
            .guard((c) -> { return c.d == 1; })
            .target("j6")
          .transition("t7")
            .guard((c) -> { return c.d == 2; })
            .target("j7");
    
    builder
      .region()
        .junction("j3")
          .transition("t8")
            .guard(ConstraintCMod2IsZero.class)
            .target("j8")
          .transition("t9")
            .guard(ConstraintCMod2IsOne.class)
            .target("j9");
    
    builder
      .region()
        .junction("j4")
          .transition("t10")
            .guard(ConstraintDIsOne.class)
            .target("j10");
    
    builder
      .region()
        .junction("j5")
          .transition("t11")
            .guard(ConstraintDIsOne.class)
            .target("j10");
    
    builder
      .region()
        .junction("j6")
          .transition("t12")
            .guard(ConstraintCMod2IsZero.class)
            .target("j10")
          .transition("t13")
            .guard(ConstraintCMod2IsOne.class)
            .target("jend");
    
    builder
      .region()
        .junction("j7")
          .transition("t14")
            .guard(ConstraintCMod2IsZero.class)
            .target("jend")
          .transition("t15")
            .guard(ConstraintCMod2IsOne.class)
            .target("j16");
    
    builder
      .region()
        .junction("j8")
          .transition("t16")
            .guard(ConstraintDIsOne.class)
            .target("j16");
    
    builder
      .region()
        .junction("j9")
          .transition("t17")
            .guard(ConstraintDIsOne.class)
            .target("j16");
    
    builder
      .region()
        .junction("j10")
          .transition("t18")
            .target("jend");
    
    builder
      .region()
        .junction("j16")
          .transition("t19")
            .target("jend");
    
    builder
      .region()
        .junction("jend")
          .transition("t20")
            .guard((c) -> { return c.e == 2; })
            .target("end");
    
    builder
      .region()
        .initial()
          .transition("t0")
            .target("j0");
    
    builder.accept(new TransitionEffectInjector(Modifier.class));
    
    return builder;
  }

  @Override
  public String stdOut() {
    return STDOUT;
  }
  
  private static final class Context extends SequentialContext {
    
    private int c;
    private int d;
    private int e;

    public Context(int c, int d, int e) {
      this.c = c;
      this.d = d;
      this.e = e;
    }
    
    public Context(Context ctx, int offset) {
      this.c = ctx.c + offset;
      this.d = ctx.d + offset;
      this.e = ctx.e + offset;
    }

    @Override
    public int hashCode() {
      int hash = 3;
      hash = 11 * hash + this.c;
      hash = 11 * hash + this.d;
      hash = 11 * hash + this.e;
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Context other = (Context) obj;
      if (this.c != other.c) {
        return false;
      }
      if (this.d != other.d) {
        return false;
      }
      return this.e == other.e;
    }
    
  }
  
  public static final class ConstraintCMod3IsZero implements TransitionConstraint<Context> {

    @Override
    public boolean test(final Context ctx) {
      return ctx.c % 3 == 0;
    }
    
  }
  
  public static final class ConstraintCMod3IsOne implements TransitionConstraint<Context> {

    @Override
    public boolean test(final Context ctx) {
      return ctx.c % 3 == 1;
    }
    
  }
  
  public static final class ConstraintCMod3IsTwo implements TransitionConstraint<Context> {

    @Override
    public boolean test(final Context ctx) {
      return ctx.c % 3 == 2;
    }
    
  }
  
  public static final class ConstraintCMod2IsZero implements TransitionConstraint<Context> {

    @Override
    public boolean test(final Context ctx) {
      return ctx.c % 2 == 0;
    }
    
  }
  
  public static final class ConstraintCMod2IsOne implements TransitionConstraint<Context> {

    @Override
    public boolean test(final Context ctx) {
      return ctx.c % 2 == 1;
    }
    
  }
  
  public static final class ConstraintDIsOne implements TransitionConstraint<Context> {

    @Override
    public boolean test(final Context ctx) {
      return ctx.d == 1;
    }
    
  }
  
  public static final class Modifier implements BiTransitionBehavior<Event, Context> {

    @Override
    public void accept(Event event, Context input) {
      input.c++;
      input.d++;
      input.e++;
    }
    
  }
  
  private static final String STDOUT = "StateMachine: \"" + Test2.class.getSimpleName() + "\"\n" +
"  Region: #2\n" +
"    PseudoState: \"j2\" kind: JUNCTION\n" +
"    PseudoState: \"j16\" kind: JUNCTION\n" +
"    PseudoState: \"j7\" kind: JUNCTION\n" +
"    PseudoState: \"jend\" kind: JUNCTION\n" +
"    PseudoState: \"j3\" kind: JUNCTION\n" +
"    PseudoState: #37 kind: INITIAL\n" +
"    PseudoState: \"j8\" kind: JUNCTION\n" +
"    PseudoState: \"j4\" kind: JUNCTION\n" +
"    PseudoState: \"j9\" kind: JUNCTION\n" +
"    PseudoState: \"j5\" kind: JUNCTION\n" +
"    FinalState: \"end\"\n" +
"    PseudoState: \"j0\" kind: JUNCTION\n" +
"    PseudoState: \"j1\" kind: JUNCTION\n" +
"    PseudoState: \"j10\" kind: JUNCTION\n" +
"    PseudoState: \"j6\" kind: JUNCTION\n" +
"    Transition: \"t1\" --- \"j0\" -> \"j1\"\n" +
"    Transition: \"t2\" --- \"j0\" -> \"j2\"\n" +
"    Transition: \"t3\" --- \"j0\" -> \"j3\"\n" +
"    Transition: \"t4\" --- \"j1\" -> \"j4\"\n" +
"    Transition: \"t5\" --- \"j1\" -> \"j5\"\n" +
"    Transition: \"t6\" --- \"j2\" -> \"j6\"\n" +
"    Transition: \"t7\" --- \"j2\" -> \"j7\"\n" +
"    Transition: \"t8\" --- \"j3\" -> \"j8\"\n" +
"    Transition: \"t9\" --- \"j3\" -> \"j9\"\n" +
"    Transition: \"t10\" --- \"j4\" -> \"j10\"\n" +
"    Transition: \"t11\" --- \"j5\" -> \"j10\"\n" +
"    Transition: \"t12\" --- \"j6\" -> \"j10\"\n" +
"    Transition: \"t13\" --- \"j6\" -> \"jend\"\n" +
"    Transition: \"t14\" --- \"j7\" -> \"jend\"\n" +
"    Transition: \"t15\" --- \"j7\" -> \"j16\"\n" +
"    Transition: \"t16\" --- \"j8\" -> \"j16\"\n" +
"    Transition: \"t17\" --- \"j9\" -> \"j16\"\n" +
"    Transition: \"t18\" --- \"j10\" -> \"jend\"\n" +
"    Transition: \"t19\" --- \"j16\" -> \"jend\"\n" +
"    Transition: \"t20\" --- \"jend\" -> \"end\"\n" +
"    Transition: \"t0\" --- #37 -> \"j0\"";
}
