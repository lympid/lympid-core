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
package com.lympid.core.behaviorstatemachines.listener;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Fabien Renaud 
 */
public final class StringBufferLoggerTester {
  
  private StringBufferLoggerTester() {
  }
  
  public static void assertLogEquals(final String expectedLog, final StringBuilderLogger actualLog) {
    String actual = filterStringBufferLog(actualLog);
    String expected = stripFromExecutorField(expectedLog.trim());
    
    assertEquals(expected.length(), actual.length());
    if (!expected.equals(actual)) {
      /*
       * Activities run in background so it is unknown when their logs will
       * display. Try to strip the activity statements from the log see if it
       * helps passing the test this time.
       */
      String actualNoActivity = stripFromActivityStatements(actual);
      String expectedNoActivity = stripFromActivityStatements(expected);
      assertEquals(expectedNoActivity, actualNoActivity);
    }
  }
  
  private static String stripFromExecutorField(final String text) {
    return text.replaceAll("executor=\"[0-9]+\" ", "");
  }

  private static String filterStringBufferLog(StringBuilderLogger actualLog) {
    String str = stripFromExecutorField(actualLog.toString().trim());
    return str.replaceAll("\r", "").replaceAll("\tat .+\n?", "");
  }
  
  private static String stripFromActivityStatements(final String text) {
    return text.replaceAll("\\[.+STATE_ACTIVITY.+\n?", "");
  }
}
