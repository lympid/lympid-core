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
package com.lympid.core.common;

import java.util.Random;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud 
 */
public class MutableIntegerTest {

  private static final Random RND = new Random();
  private int n;
  private int m;

  @Before
  public void setUp() {
    n = RND.nextInt();
    m = RND.nextInt();
  }

  /**
   * Test of get method, of class MutableInteger.
   */
  @Test
  public void testGet_0() {
    MutableInteger inst = new MutableInteger();
    assertEquals(0, inst.get());
  }

  @Test
  public void testGet_n() {
    MutableInteger inst = new MutableInteger(n);
    assertEquals(n, inst.get());
  }

  /**
   * Test of getAndAdd method, of class MutableInteger.
   */
  @Test
  public void testGetAndAdd() {
    MutableInteger inst = new MutableInteger(n);
    assertEquals(n, inst.getAndAdd(m));
    assertEquals(n + m, inst.get());
  }

  /**
   * Test of addAndGet method, of class MutableInteger.
   */
  @Test
  public void testAddAndGet() {
    MutableInteger inst = new MutableInteger(n);
    assertEquals(n + m, inst.addAndGet(m));
  }

  /**
   * Test of set method, of class MutableInteger.
   */
  @Test
  public void testSet() {
    MutableInteger inst = new MutableInteger();
    inst.set(n);
    assertEquals(n, inst.get());
  }

  /**
   * Test of incrementAndGet method, of class MutableInteger.
   */
  @Test
  public void testIncrementAndGet() {
    MutableInteger inst = new MutableInteger(n);
    assertEquals(n + 1, inst.incrementAndGet());
  }

  /**
   * Test of decrementAndGet method, of class MutableInteger.
   */
  @Test
  public void testDecrementAndGet() {
    MutableInteger inst = new MutableInteger(n);
    assertEquals(n - 1, inst.decrementAndGet());
  }

  /**
   * Test of getAndIncrement method, of class MutableInteger.
   */
  @Test
  public void testGetAndIncrement() {
    MutableInteger inst = new MutableInteger(n);
    assertEquals(n, inst.getAndIncrement());
    assertEquals(n + 1, inst.get());
  }

  /**
   * Test of getAndDecrement method, of class MutableInteger.
   */
  @Test
  public void testGetAndDecrement() {
    MutableInteger inst = new MutableInteger(n);
    assertEquals(n, inst.getAndDecrement());
    assertEquals(n - 1, inst.get());
  }

  /**
   * Test of toString method, of class MutableInteger.
   */
  @Test
  public void testToString() {
    MutableInteger inst = new MutableInteger(n);
    assertEquals(Integer.toString(n), inst.toString());
  }

  /**
   * Test of intValue method, of class MutableInteger.
   */
  @Test
  public void testIntValue() {
    MutableInteger inst = new MutableInteger(n);
    assertEquals(n, inst.intValue());
  }

  /**
   * Test of longValue method, of class MutableInteger.
   */
  @Test
  public void testLongValue() {
    MutableInteger inst = new MutableInteger(n);
    assertEquals((long) n, inst.longValue());
  }

  /**
   * Test of floatValue method, of class MutableInteger.
   */
  @Test
  public void testFloatValue() {
    MutableInteger inst = new MutableInteger(n);
    assertEquals((float) n, inst.floatValue(), 0);
  }

  /**
   * Test of doubleValue method, of class MutableInteger.
   */
  @Test
  public void testDoubleValue() {
    MutableInteger inst = new MutableInteger(n);
    assertEquals((double) n, inst.doubleValue(), 0);
  }

}
