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

import org.apache.zeppelin.interpreter.InterpreterResult;
import org.markdown4j.Markdown4jProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Slide based on markdown
 */
public class Slide {
  private final Logger logger = LoggerFactory.getLogger(Slide.class);
  List<StringBuffer> pages = new LinkedList<StringBuffer>();
  int currentPageIdx = -1;
  File slideFile;

  public Slide(File slideFile) throws IOException {
    this.slideFile = slideFile;
    load(slideFile);
  }

  public InterpreterResult next() {
    currentPageIdx++;
    if (currentPageIdx >= pages.size()) {
      return new InterpreterResult(InterpreterResult.Code.ERROR, "End of the page");
    }

    StringBuffer page = pages.get(currentPageIdx);
    return loadPage(page);
  }

  public InterpreterResult get() {
    return loadPage(pages.get(currentPageIdx));
  }

  public InterpreterResult previous() {
    currentPageIdx--;
    if (currentPageIdx < 0) {
      return new InterpreterResult(InterpreterResult.Code.ERROR, "Beginning of the page");
    }

    StringBuffer page = pages.get(currentPageIdx);
    return loadPage(page);
  }

  public InterpreterResult jumpToPage(int idx) {
    currentPageIdx = idx - 1;
    return next();
  }

  public int size() {
    return pages.size();
  }

  public void refresh() throws IOException {
    load(slideFile);
  }



  private InterpreterResult loadPage(StringBuffer page) {
    InterpreterResult parsed = new InterpreterResult(
        InterpreterResult.Code.SUCCESS, page.toString());
    if (parsed.type() == InterpreterResult.Type.TEXT) {
      Markdown4jProcessor md = new Markdown4jProcessor();
      try {
        String markdown = md.process(parsed.message());
        return new InterpreterResult(
            InterpreterResult.Code.SUCCESS,
            InterpreterResult.Type.HTML,
            markdown);
      } catch (IOException e) {
        e.printStackTrace();
        return new InterpreterResult(InterpreterResult.Code.ERROR, e.getMessage());
      }
    } else {
      return parsed;
    }
  }


  private void load(File file) throws IOException {
    pages.clear();
    // Open the file
    FileInputStream fstream = new FileInputStream(file);
    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

    String strLine;
    StringBuffer page = new StringBuffer();

    while ((strLine = br.readLine()) != null)   {
      if (strLine != null && strLine.length() > 10 && strLine.matches("[=]+")) {
        // page separater detected
        pages.add(page);
        page = new StringBuffer();
      } else {
        page.append(strLine);
        page.append("\n");
      }
    }
    pages.add(page);

    br.close();
  }
}
