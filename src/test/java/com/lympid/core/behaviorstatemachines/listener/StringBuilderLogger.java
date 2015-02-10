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

import static com.lympid.core.behaviorstatemachines.listener.StringBuilderLogger.LogLevel.DEBUG;
import static com.lympid.core.behaviorstatemachines.listener.StringBuilderLogger.LogLevel.ERROR;
import static com.lympid.core.behaviorstatemachines.listener.StringBuilderLogger.LogLevel.INFO;
import static com.lympid.core.behaviorstatemachines.listener.StringBuilderLogger.LogLevel.TRACE;
import static com.lympid.core.behaviorstatemachines.listener.StringBuilderLogger.LogLevel.WARN;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Ghetto implementation of SLF4J's logger with a StringBuffer for unit test
 * purposes.
 * This class does not implement Marker methods and is not memory-efficient.
 *
 * @author Fabien Renaud 
 */
public class StringBuilderLogger implements Logger {

  private final StringBuilder builder = new StringBuilder();
  private final LogLevel level;

  public StringBuilderLogger(final LogLevel level) {
    this.level = level;
  }

  @Override
  public String getName() {
    return StringBuilderLogger.class.getSimpleName();
  }

  @Override
  public boolean isTraceEnabled() {
    return isEnabled(TRACE);
  }

  @Override
  public void trace(String string) {
    write(TRACE, string);
  }

  @Override
  public void trace(String string, Object o) {
    write(TRACE, string, o);
  }

  @Override
  public void trace(String string, Object o, Object o1) {
    write(TRACE, string, o, o1);
  }

  @Override
  public void trace(String string, Object... os) {
    write(TRACE, string, os);
  }

  @Override
  public void trace(String string, Throwable thrwbl) {
    write(TRACE, string, thrwbl);
  }

  @Override
  public boolean isTraceEnabled(Marker marker) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void trace(Marker marker, String string) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void trace(Marker marker, String string, Object o) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void trace(Marker marker, String string, Object o, Object o1) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void trace(Marker marker, String string, Object... os) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void trace(Marker marker, String string, Throwable thrwbl) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isDebugEnabled() {
    return isEnabled(DEBUG);
  }

  @Override
  public void debug(String string) {
    write(DEBUG, string);
  }

  @Override
  public void debug(String string, Object o) {
    write(DEBUG, string, o);
  }

  @Override
  public void debug(String string, Object o, Object o1) {
    write(DEBUG, string, o, o1);
  }

  @Override
  public void debug(String string, Object... os) {
    write(DEBUG, string, os);
  }

  @Override
  public void debug(String string, Throwable thrwbl) {
    write(DEBUG, string, thrwbl);
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void debug(Marker marker, String string) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void debug(Marker marker, String string, Object o) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void debug(Marker marker, String string, Object o, Object o1) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void debug(Marker marker, String string, Object... os) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void debug(Marker marker, String string, Throwable thrwbl) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isInfoEnabled() {
    return isEnabled(INFO);
  }

  @Override
  public void info(String string) {
    write(INFO, string);
  }

  @Override
  public void info(String string, Object o) {
    write(INFO, string, o);
  }

  @Override
  public void info(String string, Object o, Object o1) {
    write(INFO, string, o, o1);
  }

  @Override
  public void info(String string, Object... os) {
    write(INFO, string, os);
  }

  @Override
  public void info(String string, Throwable thrwbl) {
    write(INFO, string, thrwbl);
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void info(Marker marker, String string) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void info(Marker marker, String string, Object o) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void info(Marker marker, String string, Object o, Object o1) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void info(Marker marker, String string, Object... os) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void info(Marker marker, String string, Throwable thrwbl) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isWarnEnabled() {
    return isEnabled(WARN);
  }

  @Override
  public void warn(String string) {
    write(WARN, string);
  }

  @Override
  public void warn(String string, Object o) {
    write(WARN, string, o);
  }

  @Override
  public void warn(String string, Object o, Object o1) {
    write(WARN, string, o, o1);
  }

  @Override
  public void warn(String string, Object... os) {
    write(WARN, string, os);
  }

  @Override
  public void warn(String string, Throwable thrwbl) {
    write(WARN, string, thrwbl);
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void warn(Marker marker, String string) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void warn(Marker marker, String string, Object o) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void warn(Marker marker, String string, Object o, Object o1) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void warn(Marker marker, String string, Object... os) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void warn(Marker marker, String string, Throwable thrwbl) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isErrorEnabled() {
    return isEnabled(ERROR);
  }

  @Override
  public void error(String string) {
    write(ERROR, string);
  }

  @Override
  public void error(String string, Object o) {
    write(ERROR, string, o);
  }

  @Override
  public void error(String string, Object o, Object o1) {
    write(ERROR, string, o, o1);
  }

  @Override
  public void error(String string, Object... os) {
    write(ERROR, string, os);
  }

  @Override
  public void error(String string, Throwable thrwbl) {
    write(ERROR, string, thrwbl);
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void error(Marker marker, String string) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void error(Marker marker, String string, Object o) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void error(Marker marker, String string, Object o, Object o1) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void error(Marker marker, String string, Object... os) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void error(Marker marker, String string, Throwable thrwbl) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  private boolean isEnabled(LogLevel level) {
    return level.ordinal() >= this.level.ordinal();
  }

  private void write(LogLevel level, String str, Object... os) {
    if (isEnabled(level)) {
      synchronized (this) {
        builder.append('[').append(level).append("] ");

        int osIndex = 0;
        int i = 0;
        while (i < str.length() - 1) {
          if (str.charAt(i) == '{' && str.charAt(i + 1) == '}' && osIndex < os.length) {
            builder.append(os[osIndex++]);
            i += 2;
          } else {
            builder.append(str.charAt(i));
            i++;
          }
        }
        if (i < str.length()) {
          builder.append(str.charAt(i));
        }

        builder.append('\n');

        if (osIndex == os.length - 1 && os[osIndex] instanceof Throwable) {
          Throwable thrw = (Throwable) os[osIndex];
          Writer result = new StringWriter();
          PrintWriter printWriter = new PrintWriter(result);
          thrw.printStackTrace(printWriter);
          builder.append(result.toString());
        }
      }
    }
  }

  @Override
  public synchronized String toString() {
    return builder.toString();
  }

  public enum LogLevel {

    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR
  }
}
