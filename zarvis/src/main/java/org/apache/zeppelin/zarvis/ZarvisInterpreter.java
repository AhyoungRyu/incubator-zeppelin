/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zeppelin.zarvis;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.zeppelin.interpreter.*;
import org.apache.zeppelin.interpreter.InterpreterResult.Code;
import org.apache.zeppelin.interpreter.thrift.InterpreterCompletion;
import org.apache.zeppelin.scheduler.Scheduler;
import org.apache.zeppelin.scheduler.SchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Zarvis interpreter for Zeppelin.
 */
public class ZarvisInterpreter extends Interpreter {
  static final Logger LOGGER = LoggerFactory.getLogger(ZarvisInterpreter.class);

  static {
    Interpreter.register(
        "zarvis",
        "zarvis",
        ZarvisInterpreter.class.getName(),
        new InterpreterPropertyBuilder()
            .add("zeppelin.zarvis.data.dir", "zarvis", "data path for zarvis")
            .build());
  }

  private Zarvis zarvis;

  public ZarvisInterpreter(Properties property) {
    super(property);
  }

  @Override
  public void open() {
    zarvis = new Zarvis(getProperty("zeppelin.zarvis.data.dir"));
  }

  @Override
  public void close() {}

  @Override
  public InterpreterResult interpret(String st, InterpreterContext interpreterContext) {
    try {
      return zarvis.talk(st);
    } catch (IOException | java.lang.RuntimeException e) {
      LOGGER.error("Exception in Zarvis while interpret ", e);
      return new InterpreterResult(Code.ERROR, InterpreterUtils.getMostRelevantMessage(e));
    }
  }

  @Override
  public void cancel(InterpreterContext context) {}

  @Override
  public FormType getFormType() {
    return FormType.SIMPLE;
  }

  @Override
  public int getProgress(InterpreterContext context) {
    return 0;
  }

  @Override
  public Scheduler getScheduler() {
    return SchedulerFactory.singleton().createOrGetParallelScheduler(
        ZarvisInterpreter.class.getName() + this.hashCode(), 5);
  }

  @Override
  public List<InterpreterCompletion> completion(String buf, int cursor) {
    return null;
  }
}
