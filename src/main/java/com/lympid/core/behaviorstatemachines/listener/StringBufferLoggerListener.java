/*
 * Copyright 2015 Lympid.
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

import com.lympid.core.behaviorstatemachines.impl.ExecutorEvent;

/**
 *
 * @author Fabien Renaud
 */
public final class StringBufferLoggerListener extends AbstractStringLoggerListener {

  private final StringBuffer buffer = new StringBuffer();
  private final StringBuffer activityBuffer = new StringBuffer();

  public String mainBuffer() {
    return buffer.toString();
  }

  public String activityBuffer() {
    return activityBuffer.toString();
  }
  
  @Override
  protected void log(final ExecutorEvent tag, final String s) {
    buffer.append(s).append('\n');
  }
  
  @Override
  protected void log(final ExecutorEvent tag, final String s, final Exception exception) {
    buffer.append(s).append(' ').append(exception).append('\n');
  }
  
  @Override
  protected void logActivity(final ExecutorEvent tag, final String s) {
    activityBuffer.append(s).append('\n');
  }
  
  @Override
  protected void logActivity(final ExecutorEvent tag, final String s, final Exception exception) {
    activityBuffer.append(s).append(' ').append(exception).append('\n');
  }
}
